import java.net.*;
import java.io.*;
import java.util.*;

public class Server {

    // Deklaration und Initialisierung der global benötigten Variablen
    public static final int MAXSIZE = 65507;
    private static final int DEFAULT_PORT = 5999;
    // Definition des Pfades zur Ablage der generierten Dateien / Nachrichten
    private static final String PATH = System.getProperty("user.home") + "/Desktop/Messages/";
    // Achtung: Die Deklaration dieses Pfades funktioniert in dieser Form nur in bestimmten Sprachen sowie Betriebs-
    //          systemen. Für andere Sprachen als Deutsch, Englisch sowie einem anderen System als Windows muss dies
    //          ggf. umgestellt werden.

    // dient der späteren Abfrage vom Serverstatus
    public static boolean running = true;

    private static DatagramSocket serverSocket = null;
    private static Worker[] workers;
    private static Queue requestQueue;
    // dient der Zuteilung eines Monitors zu jeden File
    private static Map<MyFile, FileMonitor> monitor = new HashMap<>();

    /**
     * Dies ist der Einstiegspunkt des Klasse "Server.java".
     *
     * @param args - Verwendung zur Überprüfung des Ports
     * @Return void
     */
    public static void main(String[] args) {

        // Setzen des Ports auf den oben initialisierten Standardport
        int port = DEFAULT_PORT;
        DatagramPacket dp = null;
        // Festlegung der Anzahl von Workern
        workers = new Worker[5];
        requestQueue = new Queue();

        // Überprüfung auf Länge des mitgegebenen Arrays "args", welches unter anderem die Portnummer beinhaltet
        if (args.length > 0) {

            try {
                // Überführung der Portnummer in eine Zahl im Integer-Format zur Validierung
                port = Integer.parseInt(args[0]);

                // Überprüfung, ob der angegebene Port nicht zwischen 0 und 65.536 liegt
                if (port < 0 || port >= 65536) {
                    // trifft dies zu: Ausgabe einer Fehlermeldung und Beenden des Programms
                    System.err.println("FAILED: The port must be between 0 and 65535. Exiting program...");
                    return;
                }
            } catch (NumberFormatException e) {
                // bei Auftreten einer NumberFormatException durch das Parsen: Fehlerausgabe und Beenden des Programms
                System.err.println("FAILED: " + e + "\nSomething failed to work. Exiting program...");
                return;
            }
        }

        try {
            // Zuweisung des Ports zum Serversocket
            serverSocket = new DatagramSocket(DEFAULT_PORT);
            System.out.println("SUCCESS: Server was startet on port " + DEFAULT_PORT + "!");

            // Initialisierung eines Pointers auf den Ordner
            File folder = new File(PATH);
            // Überprüfung auf Exitenz eines entsprechenden Ordners
            if(!folder.exists()) {
                // existiert keiner, wird einer am entsprechenden Pfad erstellt
                System.out.println("ATTENTION: Message Folder at the corresponding path does not exist.\nCreating " +
                        "one...");
                folder.mkdirs();
            }

            // Start der einzelnen Worker und Mitgabe des Sockets, der entsprechenden Job-Warteschlange, des
            // verwendeten Pfades sowie der Monitor-Map
            for(int i = 0; i < workers.length; i++) {
                workers[i] = new Worker(i + 1, serverSocket, requestQueue, monitor, PATH);
                workers[i].start();
                System.out.println("SUCCESS: Worker "+ (i + 1) +" was started!");
            }

            // Endlosschleife zum dauerhaften entgegennehmen der Connections
            while (true) {

                try {
                    // Erstellung eines DatagramPackets mit der max. Größe eines DPs
                    dp = new DatagramPacket(new byte[MAXSIZE], MAXSIZE);
                    // Entgegennehmen des einkommenden DPs
                    serverSocket.receive(dp);
                    // Hinzufügen des einkommenden DPs (neue Aufgabe) zur Job-Warteschlange für die Worker
                    requestQueue.add(dp);

                } catch (Exception e) {
                    // bei Auftreten einer Exception: Fehlerausgabe und Beenden des Programms
                    System.err.println("ERROR: " + e + "\nATTENTION: Shutting down server!");
                    System.exit(1);
                }
            }
        } catch (Exception e) {
            // bei Auftreten einer Exception: Fehlerausgabe und Beenden des Programms
            System.err.println("ERROR: " + e + "\nCould not initialize the server! Exiting program...");
            System.exit(1);
        }
    }
}
