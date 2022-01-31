import java.net.*;
import java.io.*;
import java.util.*;

public class Client {

    // Initialisierung der benötigten Variablen
    public final static int MAXSIZE = 65507;
    public final static int DEFAULT_PORT = 5999;
    public final static String HOSTNAME = "localhost";
    public static DatagramSocket socket;
    public static InetAddress host;
    public static final int timeout = 300000;

    /**
     * Diese Methode holt den Benutzer-Input in Bezug auf den ausgewählten Client-Modi.
     *
     * @Return int
     */
    public static int getMode() {

        System.out.println("Available mode:\n1 - manual user input\n2 - prepared automatic input");
        System.out.print("Please choose one of the modes above:\n> ");
        // Deklaration der Benutzer-Eingabe
        BufferedReader modeIn = new BufferedReader(new InputStreamReader(System.in));
        int mode = -1;
        String line = null;

        try {
            // Einlesen der Benutzer-Eingabe
            line = modeIn.readLine();
            // Umwandlung der Benutzer-Eingabe in ein Integer
            mode = Integer.parseInt(line.trim());

        } catch (IOException e) {
            // bei Auftreten einer IOException: Fehlerausgabe
            System.err.println("ERROR: " + e);
        }
        // Rückgabe des ausgewählten Modus als Integer
        return mode;
    }

    /**
     * Diese main-Methode dient dem Start der Client-Klasse, um deren verschiedene Prozeduren zu starten.
     *
     * @param args - String-Array (mit Argumenten / Parametern) der main-Methode
     * @Return void
     */
    public static void main(String[] args) {

        // Deklaration und Vorinitialsierung der verwendeten Variablen
        BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
        String hostname = HOSTNAME;
        String s = null;
        int port = DEFAULT_PORT;

        try {
            // Initialisierung des Sockets sowie der Ziel-Adresse der DPs
            socket = new DatagramSocket();
            host = InetAddress.getByName(hostname);

            // Überprüfung des vom Benutzer eingegebenen Modus
            switch (getMode()) {

                // ist die Eingabe = 1, so wird der manuelle Client-Modus gestartet
                case 1:
                    try {

                        System.out.println("*************************************************************************************");
                        System.out.println("ATTENTION: You have chosen mode 1!");
                        System.out.println("Type a command to send:\n'READ file, lineNO' OR 'WRITE file, lineNo, data' OR 'EXIT'");

                        // Dauerschleife zum Einlesen der Benutzer-Eingabe
                        while (true) {

                            System.out.print("> ");
                            // Einlesen der Benutzer-Eingabe
                            s = userIn.readLine();

                            // Überprüfung, ob die Eingabe leer ist oder "exit" eingegeben wurde
                            if (s == null || s.equalsIgnoreCase("EXIT")) {
                                // wenn ja, wird die Schleife abgebrochen und der Client beendet sich
                                break;
                            }

                            // Erstellung des Byte-Arrays der Benutzer-Eingabe
                            byte[] data = s.getBytes();
                            // Speicherung der Länge des Byte-Arrays der Benutzer-Eingabe
                            int length = s.length();
                            // Überprüfung, ob die Größe des Byte-Arrays der Benutzer-Eingabe den max. Wert eines
                            // DatagramPackets überschreitet
                            if (length > MAXSIZE) {
                                // wenn ja, wird eine neue Exception geworfen
                                throw new Exception("Data too large to send");
                            }

                            // Erstellung des DPs mit den oben gesammelten Daten
                            DatagramPacket outPacket = new DatagramPacket(data, length, host, port);
                            // Senden des DPs
                            socket.send(outPacket);

                            // setze den Timeout des Clients auf 5 Minuten
                            socket.setSoTimeout(timeout);

                            try {
                                // Initialisierung eines Byte-Arrays mit der max. Größe eines DPs
                                byte[] buffer = new byte[MAXSIZE];
                                // Speicherung des Antwort-DPs
                                DatagramPacket inPacket = new DatagramPacket(buffer, buffer.length);
                                // Empfangen des Antwort-DPs
                                socket.receive(inPacket);

                                // Ausgabe der Antwort für den Benutzer
                                String answer = new String(inPacket.getData(), 0, inPacket.getLength());
                                System.out.println("SUCCESS: Answer received: <" + answer + ">");
                                System.out.println("*************************************************************************************");

                            } catch (SocketTimeoutException e) {
                                // erhält Client nach 5 Minuten keine Antwort vom Server: Fehlerausgabe + Beenden des
                                // clients
                                System.err.println("ERROR: " + e + "\nNo connection to server available. The client " +
                                        "timed out after " + timeout + " milliseconds and will be closed now...");
                                System.exit(1);
                            }
                        }

                        // wird mittels "exit" oder null aus der While-Schleife ausgebrochen, so wird im Folgenden noch
                        // überprüft, ob die Streams leer sind
                        try {
                            if (socket != null) {
                                // wenn ja, wird der verwendete Socket geschlossen
                                socket.close();
                            }
                            if (userIn != null) {
                                // wenn ja, wird der Benutzer-Input-Stream geschlossen
                                userIn.close();
                            }

                        } catch (Exception e) {
                            // bei Auftreten einer Exception: Fehlerausgabe
                            System.err.println("ERROR: " + e);
                        }

                    } catch (Exception e) {
                        // bei Auftreten einer Exception: Fehlerausgabe
                        System.err.println("ERROR: " + e);
                    }
                    break;

                // ist die Eingabe = 2, so wird der automatische Client-Modus gestartet
                case 2:

                    System.out.println("*************************************************************************************");
                    System.out.println("ATTENTION: You have chosen mode 2! The automatic tests will start now...");

                    // setze den Timeout des Clients auf 5 Minuten
                    socket.setSoTimeout(timeout);

                    // Start der verschiedenen Testfälle mittels eigener Methoden
                    automaticParallelReadingFromSameFile();
                    automaticParallelReadFromDifferentFiles();
                    automaticParallelWritingInSameDocument();
                    automaticParallelWritingInDifferentDocument();
                    automaticParallelReadingAndWritingInSameDocument();
                    automaticParallelReadAndWriteInDifferentFiles();
                    automaticReadFromNonExistentFile();
                    automaticWriteInNonExistentFile();
                    automaticReadNonExistentLine();
                    automaticWriteNonExistentLine();
                    automaticOverwriteExistingLine();
                    automaticIncompleteReadCommand();
                    automaticIncompleteWriteCommand();
                    automaticImproperReadCommand();
                    automaticImproperWriteCommand();
                    automaticReadNegativeLine();
                    automaticWriteNegativeLine();
                    automaticUnknownCommand();
                    automaticMoreRequestsThanWorker();

                    System.out.println("*************************************************************************************");
                    System.out.println("SUCCESS: All tests have passed! Exiting the client...");
                    break;

                default:
                    // ist die Eingabe weder 1 noch 2, so wird eine Exception geworfen
                    throw new Exception("Wrong choice of mode! Please start the client again and choose mode 1 or 2.");
            }

        } catch (SocketTimeoutException e) {
            // erhält Client nach 5 Minuten keine Antwort vom Server: Fehlerausgabe + Beenden des
            // clients
            System.err.println("ERROR: " + e + "\nNo connection to server available. The client " +
                    "timed out after " + timeout + " milliseconds and will be closed now...");
            System.exit(1);

        } catch (SocketException e) {
            // bei Auftreten einer SocketException: Fehlerausgabe
            System.err.println("ERROR: " + e);

        } catch (UnknownHostException e) {
            // bei Auftreten einer UnknownHostException: Fehlerausgabe
            System.err.println("ERROR: " + e);

        } catch (IOException e) {
            // bei Auftreten einer IOException: Fehlerausgabe
            System.err.println("ERROR: " + e);

        } catch (Exception e) {
            // bei Auftreten einer Exception: Fehlerausgabe
            System.err.println("ERROR: " + e);
        }
    }

    /**
     * Diese Methode ist zuständig, um während der automatische Client läuft, die eingegebenen Befehle entgegenzunehmen,
     * zu senden und auf deren Antwort zu warten, sodass diese korrekt dem Benutzer angezeigt werden.
     *
     * @param commands - eine Liste aus mitgegebenen Befehlen des automatischen Clients
     * @Return void
     */
    public static void sendAutomaticCommand(List<String> commands) {

        try {

            for (String command : commands) {

                System.out.println("Send: <" + command + ">");
                // Erstellung eines DPs aus den vordefinierten Befehlen
                byte[] buffer = command.getBytes();
                DatagramPacket outPacket = new DatagramPacket(buffer, buffer.length, host, DEFAULT_PORT);
                // Senden dieser Befehle
                socket.send(outPacket);
            }

            for (String command : commands) {

                // Erstellung eines DPs für den Empfang der automatischen Antworten
                byte[] buffer = new byte[MAXSIZE];
                DatagramPacket inPacket = new DatagramPacket(buffer, buffer.length);
                // Warten auf die Antwort aller vorher gesendeten Befehle
                socket.receive(inPacket);
                // Ausgabe der Antwort an den Benutzer
                String answer = new String(inPacket.getData(), 0, inPacket.getLength());
                System.out.println("SUCCESS: Answer received: <" + answer + ">");
            }

        } catch (SocketTimeoutException e) {
            // erhält Client nach 5 Minuten keine Antwort vom Server: Fehlerausgabe + Beenden des
            // clients
            System.err.println("ERROR: " + e + "\nNo connection to server available. The client " +
                    "will be closed now...");
            System.exit(1);

        } catch (Exception e) {
            // bei Auftreten einer Exception: Fehlerausgabe
            System.err.println("ERROR: " + e);
        }
    }

    /**
     * Diese Methode testet, was passiert, wenn der Client aus einer nicht vorhandenen Datei lesen soll.
     *
     * @Return void
     */
    public static void automaticReadFromNonExistentFile() {
        System.out.println("*************************************************************************************");
        System.out.println("ATTENTION: Starting test for reading a non existent file...");

        List<String> commands = new ArrayList();
        commands.add("READ test3,50");
        sendAutomaticCommand(commands);
    }

    /**
     * Diese Methode testet, ob die Schreiber-Priorität eingehalten wird.
     *
     * @Return void
     */
    public static void automaticParallelReadingAndWritingInSameDocument() {
        System.out.println("*************************************************************************************");
        System.out.println("ATTENTION: Starting test for parallel reading and writing in the same document...");

        List<String> commands = new ArrayList();
        commands.add("READ test1,2");
        commands.add("WRITE test1,3,SURPRISE");
        commands.add("READ test1,3");
        commands.add("READ test1,4");
        commands.add("WRITE test1,5,SURPRISE in line 5");
        commands.add("READ test1,5");
        sendAutomaticCommand(commands);
    }

    /**
     * Diese Methode testet das parallele Lesen von Workern in der gleichen Datei.
     *
     * @Return void
     */
    public static void automaticParallelReadingFromSameFile() {
        System.out.println("*************************************************************************************");
        System.out.println("ATTENTION: Starting test for parallel reading from a file...");

        List<String> commands = new ArrayList();
        commands.add("READ test1,1");
        commands.add("READ test1,2");
        commands.add("READ test1,3");
        commands.add("READ test1,4");
        commands.add("READ test1,5");
        sendAutomaticCommand(commands);
    }

    /**
     * Diese Methode testet das parallele Schreiben von Workern in die gleiche Datei.
     *
     * @Return void
     */
    public static void automaticParallelWritingInSameDocument() {
        System.out.println("*************************************************************************************");
        System.out.println("ATTENTION: Starting test for parallel writing into the same file...");

        List<String> commands = new ArrayList();
        commands.add("WRITE test1,3,I am tired...");
        commands.add("WRITE test1,5,Me too...");
        sendAutomaticCommand(commands);
    }

    /**
     * Diese Methode testet das parallele Schreiben von Workern in verschiedene Dateien.
     *
     * @Return void
     */
    public static void automaticParallelWritingInDifferentDocument() {
        System.out.println("*************************************************************************************");
        System.out.println("ATTENTION: Starting test for parallel writing in different files...");

        List<String> commands = new ArrayList();
        commands.add("WRITE test1,3,Random Data");
        commands.add("WRITE test2,5,Random Data in line 5");
        sendAutomaticCommand(commands);
    }

    /**
     * Diese Methode testet das parallele Lesen von Workern aus verschiedenen Dateien.
     *
     * @Return void
     */
    public static void automaticParallelReadFromDifferentFiles() {
        System.out.println("*************************************************************************************");
        System.out.println("ATTENTION: Starting test for reading from different files...");

        List<String> commands = new ArrayList();
        commands.add("READ test1,1");
        commands.add("READ test2,3");
        commands.add("READ test1,5");
        sendAutomaticCommand(commands);
    }

    /**
     * Diese Methode testet das parallele Lesen von Workern einer Datei und dabei das gleichzeitige von Workern in eine
     * andere Datei.
     *
     * @Return void
     */
    public static void automaticParallelReadAndWriteInDifferentFiles() {
        System.out.println("*************************************************************************************");
        System.out.println("ATTENTION: Starting test for reading parallel from a file and writing into another file...");

        List<String> commands = new ArrayList();
        commands.add("READ test1,1");
        commands.add("WRITE test2,3,Trying to access in parallel...");
        commands.add("READ test1,5");
        commands.add("READ test2,3");
        sendAutomaticCommand(commands);
    }

    /**
     * Diese Methode testet, was passiert, wenn eine Zeile gelesen werden soll, die in der entsprechenden Datei nicht
     * vorhanden ist.
     *
     * @Return void
     */
    public static void automaticReadNonExistentLine() {
        System.out.println("*************************************************************************************");
        System.out.println("ATTENTION: Starting test for reading a non existent line...");

        List<String> commands = new ArrayList();
        commands.add("READ test1,50");
        commands.add("READ test2,30");
        sendAutomaticCommand(commands);
    }

    /**
     * Diese Methode testet, was passiert, wenn mehr Befehle gesendet werden, als Worker vorhanden sind.
     *
     * @Return void
     */
    public static void automaticMoreRequestsThanWorker() {
        System.out.println("*************************************************************************************");
        System.out.println("ATTENTION: Starting test with more requests than worker...");

        List<String> commands = new ArrayList();
        commands.add("READ test1,1");
        commands.add("READ test1,2");
        commands.add("READ test1,3");
        commands.add("READ test1,4");
        commands.add("READ test1,5");
        commands.add("READ test1,6");
        commands.add("READ test1,7");
        commands.add("READ test1,8");
        commands.add("READ test1,9");
        commands.add("READ test1,10");
        commands.add("READ test1,11");
        commands.add("READ test1,12");
        commands.add("READ test1,13");
        sendAutomaticCommand(commands);
    }

    /**
     * Diese Methode testet, was passiert, wenn der Client versucht in eine noch nicht existierende Datei zu schreiben.
     *
     * @return void
     */
    public static void automaticWriteInNonExistentFile(){
        System.out.println("*************************************************************************************");
        System.out.println("ATTENTION: Starting test for writing in a non existent file...");

        List<String> commands = new ArrayList();
        commands.add("WRITE test100,1,Will probably work...");
        sendAutomaticCommand(commands);
    }

    /**
     * Diese Methode testet, was passiert, wenn der Client versucht eine noch nicht beschriebene Zeile zu beschreiben.
     *
     * @return void
     */
    public static void automaticWriteNonExistentLine(){
        System.out.println("*************************************************************************************");
        System.out.println("ATTENTION: Starting test for writing in a non existent line...");

        List<String> commands = new ArrayList();
        commands.add("WRITE test1,25,I am the last line");
        commands.add("WRITE test2,10,I am the last line");
        sendAutomaticCommand(commands);
    }

    /**
     * Diese Methode testet, was passiert, wenn der Client versucht eine bereits existierende Zeile zu überschreiben.
     *
     * @return void
     */
    public static void automaticOverwriteExistingLine(){
        System.out.println("*************************************************************************************");
        System.out.println("ATTENTION: Starting test to overwrite an already existing line...");

        List<String> commands = new ArrayList();
        commands.add("WRITE test1,1,BYE!");
        commands.add("WRITE test2,1,HELLO!");
        sendAutomaticCommand(commands);
    }

    /**
     * Diese Methode testet, was passiert, wenn der Client einen unvollständigen Lese-Befehl erhält.
     *
     * @return void
     */
    public static void automaticIncompleteReadCommand(){
        System.out.println("*************************************************************************************");
        System.out.println("ATTENTION: Starting test with incomplete read command...");

        List<String> commands = new ArrayList();
        commands.add("READ ,1");
        sendAutomaticCommand(commands);
    }

    /**
     * Diese Methode testet, was passiert, wenn der Client einen unvollständigen Schreib-Befehl erhält.
     *
     * @return void
     */
    public static void automaticIncompleteWriteCommand(){
        System.out.println("*************************************************************************************");
        System.out.println("ATTENTION: Starting test with incomplete write command...");

        List<String> commands = new ArrayList();
        commands.add("WRITE ,5,TEST");
        sendAutomaticCommand(commands);
    }

    /**
     * Diese Methode testet, was passiert, wenn der Client einen unzureichenden Lese-Befehl erhält.
     *
     * @return void
     */
    public static void automaticImproperReadCommand(){
        System.out.println("*************************************************************************************");
        System.out.println("ATTENTION: Starting test with improper read command...");

        List<String> commands = new ArrayList();
        commands.add("READ test1, ");
        sendAutomaticCommand(commands);
    }

    /**
     * Diese Methode testet, was passiert, wenn der Client einen unzureichenden Schreib-Befehl erhält.
     *
     * @return void
     */
    public static void automaticImproperWriteCommand(){
        System.out.println("*************************************************************************************");
        System.out.println("ATTENTION: Starting test with improper write command...");

        List<String> commands = new ArrayList();
        commands.add("WRITE test2, ,TEST2");
        sendAutomaticCommand(commands);
    }

    /**
     * Diese Methode testet, was passiert, wenn der Client einen Lese-Befehl mit einer negativen Zeilenangabe erhält.
     *
     * @return void
     */
    public static void automaticReadNegativeLine(){
        System.out.println("*************************************************************************************");
        System.out.println("ATTENTION: Starting test to read a negative line number...");

        List<String> commands = new ArrayList();
        commands.add("READ test1,-1");
        commands.add("READ test2,-100");
        sendAutomaticCommand(commands);
    }

    /**
     * Diese Methode testet, was passiert, wenn der Client einen Schreib-Befehl mit einer negativen Zeilenangabe
     * erhält.
     *
     * @return void
     */
    public static void automaticWriteNegativeLine(){
        System.out.println("*************************************************************************************");
        System.out.println("ATTENTION: Starting test to write in a negative line number...");

        List<String> commands = new ArrayList();
        commands.add("WRITE test1,-10,Will not work...");
        commands.add("WRITE test2,-5,Will not work either...");
        sendAutomaticCommand(commands);
    }

    /**
     * Diese Methode testet, was passiert, wenn der Client einen unbekannten Befehl erhält.
     *
     * @return void
     */
    public static void automaticUnknownCommand(){
        System.out.println("*************************************************************************************");
        System.out.println("ATTENTION: Starting test with unknown command...");

        List<String> commands = new ArrayList();
        commands.add("Go to sleep already!");
        sendAutomaticCommand(commands);
    }
}