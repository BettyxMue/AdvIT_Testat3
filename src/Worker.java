import java.net.*;
import java.util.*;

public class Worker extends Thread {

    // Deklaration der benötigten Variablen
    private DatagramSocket socket;
    private Queue q;
    private int id;
    Map<MyFile, FileMonitor> monitor;
    private String path;

    /**
     * Konstruktor eines Workers
     *
     * @param id      - Id des Workers
     * @param socket  - der verwendete DatagramSocket
     * @param q       - die verwendete Job-Warteschlange
     * @param monitor - die verwendete Map zur Zuweisung eines Datei-Monitors zu einem File
     * @param path    - der Pfad der entsprechenden Datei
     * @Return Worker-Objekt
     */
    public Worker(int id, DatagramSocket socket, Queue q, Map<MyFile, FileMonitor> monitor, String path) {
        this.id = id;
        this.socket = socket;
        this.q = q;
        this.monitor = monitor;
        this.path = path;
    }

    /**
     * Dies ist die Methode, die eine Worker nach seinem Start ausführt.
     *
     * @Return void
     */
    public void run() {

        System.out.println("ATTENTION: Worker " + this.id + " is running...");

        // Überprüfung auf Status des Servers --> siehe Server.java
        while (Server.running) {

            // Entnehmen eines Auftrags aus der Job-Warteschlange
            DatagramPacket dp = q.remove();
            System.out.println("SUCCESS: Worker " + this.id + " received the job from the queue!");

            try {
                // Holen der Ziel-Adresse sowie des Ziel-Ports für das zu sendende DP
                InetAddress clientAddress = dp.getAddress();
                int clientPort = dp.getPort();

                // Holes des Befehls aus dem empfangenen DP
                String command = new String(dp.getData(), 0, dp.getLength());
                // Übergabe des Befehls an die entsprechende Methode zur Verarbeitung
                String s = process(command);

                // Erstellung eines DPs zur Antwort an den Client
                DatagramPacket sendDp = new DatagramPacket(s.getBytes(), s.getBytes().length, clientAddress, clientPort);
                // Senden des Antwort-DPs
                socket.send(sendDp);

            } catch (Exception e) {
                // bei Auftreten einer Exception: Fehlerausgabe
                System.err.println("ERROR: " + e);
            }
        }
    }

    /**
     * Diese Methode ist dafür zuständig den Befehl, welcher vom Nutzer eingegeben wurde, zu bearbeiten.
     *
     * @param command - der Befehl aus der Benutzer-Eingabe
     * @return String
     */
    public String process(String command) {

        // Initialisierung der benötigten Variablen
        String answer = "ERROR: An unknown error occurred.";
        String fileName = "";
        String newData = "";
        int lineNo = -1;
        MyFile f = null;
        String param[] = null;
        String param2[] = null;
        DatagramPacket dp2 = null;
        FileMonitor currentMonitor = null;

        // Wird kein Befehl auf Grund von "EXIT" übergeben, würde er sonst beim Aufrufen von "split" in einen Fehler
        // laufen. Durch vorherige Überprüfung soll dies vermieden werden.
        if (command == null) {
            return "FAILED: Something failed on the client-side.";
        }

        try {
            // Aufspaltung des Befehls in ein Array mit den einzelnen Eingaben
            String[] piece = command.split(" ", 2);

            if (piece.length < 2) {
                // Ist das Array kürzer als 2, wird ein Fehler geworfen.
                return "FAILED: A wrong command was used!";
            }

            // Überprüfung, ob der Befehl das Wort "read" enthält
            if (piece[0].equalsIgnoreCase("READ")) {

                try {
                    // Aufspaltung des Befehls in 2 Teile
                    param = command.split(" ", 2);

                    // Überprüfung, ob der 2. Teil des Befehls leer ist
                    if (param[1] != null) {
                        // wenn nicht, dann wird dieser Teil weiter unterteilt in 2 Teile (Dateiname, Zeilennummer)
                        param2 = param[1].split(",", 2);

                        // Überprüfung, ob der Dateiname vom Benutzer eingegeben wurde
                        if (param2[0].length() > 0) {
                            // Speicherung des Dateinamens durch Split des Befehls
                            fileName = param2[0].trim();

                            try {
                                // Speicherung der Zeilennummer durch Split des Befehls
                                lineNo = Integer.parseInt(param2[1].trim());

                                // Überprüfung, ob eine positive Zahl < 0 als Zeilennummer angegeben wurde
                                if (lineNo < 0){
                                    answer = "ERROR: Bad line number input. Please choose a line number greater than 0.";

                                } else {
                                    // Erstellung eines Files mit eingegebenen Namen und vorgegebenen Pfad
                                    f = new MyFile(path, fileName);

                                    // Überprüfung, ob für die entsprechende Datei ein Monitor bereits zugewiesen ist
                                    if (!monitor.containsKey(f)) {
                                        // wenn nicht, wird ein Monitor für diese Datei erstellt
                                        currentMonitor = new FileMonitor();
                                        monitor.put(f, currentMonitor);
                                    }
                                    System.out.println("ATTENTION: Worker " + this.id + " starts reading...");

                                    // lege den Thread schlafen, um Parallelität zeigen zu können
                                    sleep(5000);

                                    // Lesen des Inhalts der angefragten Zeile aus der entsprechenden Datei
                                    answer = f.read(f, lineNo, monitor);
                                    System.out.println("ATTENTION: Worker " + this.id + " stops reading...");
                                }

                            } catch (InterruptedException e) {
                                // automatische Fehlermeldung für thread.sleep()
                                throw new Exception(e);

                            } catch (IllegalArgumentException e) {
                                // Fehlermeldung, wenn der Benutzer keine Zahl als Zeilennummer angibt
                                answer = "ERROR: Bad line number input. Line number has to be an integer number greater than 0.";
                            }

                        } else {
                            // Fehlermeldung, wenn der Benutzer keinen Dateinamen eingegeben hat
                            answer = "ERROR: Invalid command. Please enter a valid filename.";
                        }

                    } else {
                        // Fehlermeldung, wenn der Benutzer einen benötigten Parameter nicht eingegeben hat
                        answer = "ERROR: Invalid command. Please enter the missing parameters.";
                    }

                } catch (Exception e) {
                    // bei Auftreten einer Exception: Fehlerausgabe durch Weiterreichen der Exception
                    answer = "ERROR: " + e;
                }

                // Überprüfung, ob der Befehl das Wort "write" enthält
            } else if (piece[0].equalsIgnoreCase("WRITE")) {

                try {
                    // Aufspaltung des Befehls in 2 Teile
                    param = command.split(" ", 2);

                    // Überprüfung, ob der 2. Teil des Befehls leer ist
                    if (param[1] != null) {
                        // wenn nicht, dann wird dieser Teil weiter unterteilt in 3 Teile (Dateiname, Zeilennummer,
                        // neuer Inhalt)
                        param2 = param[1].split(",", 3);

                        // Überprüfung, ob der Dateiname vom Benutzer eingegeben wurde
                        if (param2[0].length() > 0) {
                            // Speicherung des Dateinamens durch Split des Befehls
                            fileName = param2[0].trim();

                            try {
                                // Speicherung der Zeilennummer durch Split des Befehls
                                lineNo = Integer.parseInt(param2[1].trim());

                                // Überprüfung, ob eine positive Zahl < 0 als Zeilennummer angegeben wurde
                                if (lineNo < 0){
                                    answer = "ERROR: Bad line number input. Please choose a line number greater than 0.";

                                } else {
                                    // Speicherung des neuen Inhalts durch Split des Befehls
                                    newData = param2[2].trim();
                                    // Erstellung eines Files mit eingegebenen Namen und vorgegebenen Pfad
                                    f = new MyFile(path, fileName + ".txt");

                                    // Überprüfung, ob im angegebenen Pfad bereits eine Datei entsprechend des Datei-Objekts
                                    // existiert
                                    if (!f.exists()) {
                                        // wenn nicht, wird sie erstellt
                                        f.createNewFile();
                                        System.out.println("ATTENTION: No corresponding file could be found! Creating one...");
                                    }

                                    // Überprüfung, ob für die entsprechende Datei ein Monitor bereits zugewiesen ist
                                    if (!monitor.containsKey(f)) {
                                        // wenn nicht, wird ein Monitor für diese Datei erstellt
                                        currentMonitor = new FileMonitor();
                                        monitor.put(f, currentMonitor);
                                    }

                                    // lege den Thread schlafen, um Parallelität zeigen zu können
                                    sleep(5000);

                                    System.out.println("ATTENTION: Worker " + this.id + " starts writing...");
                                    // Schreiben der eingegebenen Daten in die angegebenen Zeile der entsprechenden Datei
                                    answer = f.write(f, lineNo, newData, monitor);
                                    System.out.println("ATTENTION: Worker " + this.id + " stops writing...");
                                }

                            } catch (InterruptedException e) {
                                // automatische Fehlermeldung für thread.sleep()
                                throw new Exception(e);

                            } catch (IllegalArgumentException e) {
                                // Fehlermeldung, wenn der Benutzer keine Zahl als Zeilennummer angibt
                                answer = "ERROR: Bad line number input. Line number has to be an integer number greater than 0.";
                            }

                        } else {
                            // Fehlermeldung, wenn der Benutzer keinen Dateinamen eingegeben hat
                            answer = "ERROR: Invalid command. Please enter a valid filename.";
                        }

                    } else {
                        // Fehlermeldung, wenn der Benutzer einen benötigten Parameter nicht eingegeben hat
                        answer = "ERROR: Invalid command. Please enter the missing parameters in a correct format.";
                    }

                } catch (Exception e) {
                    // bei Auftreten einer Exception: Fehlerausgabe
                    answer = "ERROR: Something went wrong with the WRITE command!";
                }

            } else {
                // wenn das eingegebene Kommando weder "READ" noch "WRITE" ist:
                //      Fehlerausgabe durch Werfen einer Exception
                answer = "ERROR: The given command is unknown!";
            }

        } catch (Exception e) {
            // bei Auftreten einer Exception: Fehlerausgabe
            System.err.println("ERROR: " + e);
        }

        // Rückgabe der endgültigen Ausgabe / Antwort
        return answer;
    }
}
