import java.net.*;
import java.io.*;
import java.util.*;

public class Client {

    public final static int MAXSIZE = 65507;
    public final static int DEFAULT_PORT = 5999;
    public final static String HOSTNAME = "localhost";
    public static DatagramSocket socket;
    public static InetAddress host;

    /**
     * This class gets the input of the user concerning the chosen client-mode.
     *
     * @Return int
     */
    public static int getMode () {

        System.out.println("Available mode:\n1 - manual user input\n2 - prepared automatic input");
        System.out.print("Please choose one of the modes above:\n> ");
        BufferedReader modeIn = new BufferedReader(new InputStreamReader(System.in));
        int mode = -1;
        String line = null;

        try {

            line = modeIn.readLine();
            mode = Integer.parseInt(line.trim());

        } catch (IOException e) {
            System.err.println("ERROR: " + e);
        }

        return mode;
    }

    /**
     * This is the main-methode of the client class for starting the procedures.
     *
     * @param args
     * @Return void
     */
    public static void main(String[] args) {

        BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
        String hostname = HOSTNAME;
        String s = null;
        int port = DEFAULT_PORT;

        if (args.length > 1) {

            try {
                hostname = args[0];
                port = Integer.parseInt(args[1]);

                if (port < 1 || port > 65535) {
                    throw new Exception();
                }
            } catch (Exception e) {
                System.out.println("ATTENTION: usage of FileClient " + hostname + ":" + port);
            }
        }

        try {
            socket = new DatagramSocket();
            host = InetAddress.getByName(hostname);

            switch (getMode()) {

                case 1:

                    try {

                        System.out.println("*************************************************************************************");
                        System.out.println("ATTENTION: You have chosen mode 1!");
                        System.out.println("Type a command to send:\n'READ file, lineNO' OR 'WRITE file, lineNo, data' OR 'EXIT'");

                        while (true) {

                            System.out.println("> ");
                            s = userIn.readLine();

                            if (s == null || s.equalsIgnoreCase("EXIT")) {
                                break;
                            }

                            byte[] data = s.getBytes();

                            int length = s.length();
                            if (length > MAXSIZE) {
                                throw new Exception("Data too large to send");
                            }

                            DatagramPacket outPacket = new DatagramPacket(data, length, host, port);
                            socket.send(outPacket);

                            byte[] buffer = new byte[MAXSIZE];
                            DatagramPacket inPacket = new DatagramPacket(buffer, buffer.length);
                            socket.receive(inPacket);

                            String answer = new String(inPacket.getData(), 0, inPacket.getLength());
                            System.out.println("SUCCESS: Answer received: <" + answer + ">");
                            System.out.println("*************************************************************************************");
                        }

                        socket.close();

                    } catch (Exception e) {
                        System.err.println("ERROR: " + e);
                    }
                    break;

                case 2:

                    System.out.println("*************************************************************************************");
                    System.out.println("ATTENTION: You have chosen mode 2! The automatic tests will start now...");

                    automaticParallelReading();
                    automaticSequentiellWritingInSameDocument();
                    automaticParallelWritingInDifferentDocument();
                    automaticWriterPriority();
                    automaticReadParallelAndWriteInDifferentFiles();
                    automaticMoreRequestsThanWorker();
                    automaticReadFromDifferentFiles();
                    automaticReadNonExistentLine();

                    System.out.println("*************************************************************************************");
                    System.out.println("SUCCESS: All tests have passed!");
                    break;

                default:

                    throw new Exception("Wrong choose of mode! Please choose mode 1 or 2.");
            }
        } catch (Exception e) {
            System.err.println("ERROR: " + e);
        }
    }

    /**
     *
     *
     * @Return void
     */
    public static void sendAutomaticCommand(List<String> commands) {

        try {

            //create and send datagrams for all requests
            for(String command: commands) {

                System.out.println("Send: <" + command + ">");
                byte[] buffer = command.getBytes();
                DatagramPacket outPacket = new DatagramPacket(buffer, buffer.length, host, 5999);
                socket.send(outPacket);
            }

            //receive all answers to the commands
            for(String command: commands) {

                byte[] buffer = new byte[MAXSIZE];
                DatagramPacket inPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(inPacket);
                String answer = new String(inPacket.getData(), 0, inPacket.getLength());
                System.out.println("SUCCESS: Answer received: <" + answer + ">");
            }

        } catch(Exception e) {
            System.err.println("ERROR: " + e);
        }
    }

    /**
     * This method is responsible for testing parallel reading in the same file
     *
     * @Return void
     */
    public static void automaticParallelReading() {
        System.out.println("*************************************************************************************");
        System.out.println("ATTENTION: Starting test for reading parallel from a file...");

        List<String> commands = new ArrayList();
        commands.add("READ test1,2");
        commands.add("READ test1,3");
        commands.add("READ test1,4");
        commands.add("READ test1,5");

        sendAutomaticCommand(commands);
    }

    /**
     * This method is responsible for testing parallel writing to the same file
     *
     * @Return void
     */
    public static void automaticSequentiellWritingInSameDocument() {
        System.out.println("*************************************************************************************");
        System.out.println("ATTENTION: Starting test for writing into the same file...");

        List<String> commands = new ArrayList();
        commands.add("WRITE test1,3,New data 1");
        commands.add("WRITE test1,5,New data in line 5");

        sendAutomaticCommand(commands);

    }

    /**
     * This method is responsible for testing parallel writing to different files
     *
     * @Return void
     */
    public static void automaticParallelWritingInDifferentDocument() {
        System.out.println("*************************************************************************************");
        System.out.println("ATTENTION: Starting test for writing in different files...");

        List<String> commands = new ArrayList();
        commands.add("WRITE test1,3,New data 1");
        commands.add("WRITE test2,5,New data in line 5");
        sendAutomaticCommand(commands);
    }

    /**
     * This method is responsible for testing if the writer-priority is active in the server
     *
     * @Return void
     */
    public static void automaticWriterPriority() {
        System.out.println("*************************************************************************************");
        System.out.println("ATTENTION: Starting test for Writer Priority...");

        List<String> commands = new ArrayList();
        commands.add("READ test1,2");
        commands.add("WRITE test1,3,New data 1");
        commands.add("READ test1,3");
        commands.add("READ test1,4");
        commands.add("WRITE test1,5,New data in line 5");
        commands.add("READ test1,5");
        sendAutomaticCommand(commands);
    }

    /**
     * This method is responsible for testing parallel reading from different files
     *
     * @Return void
     */
    public static void automaticReadFromDifferentFiles() {
        System.out.println("*************************************************************************************");
        System.out.println("ATTENTION: Starting test for reading from different files...");

        List<String> commands = new ArrayList();
        commands.add("READ test1,2");
        commands.add("READ test2,3");
        commands.add("READ test1,5");
        sendAutomaticCommand(commands);
    }

    /**
     * This method is responsible for testing parallel reading and writing to different files
     *
     * @Return void
     */
    public static void automaticReadParallelAndWriteInDifferentFiles() {
        System.out.println("*************************************************************************************");
        System.out.println("ATTENTION: Starting test for reading parallel from a file and writing into another file...");

        List<String> commands = new ArrayList();
        commands.add("READ test1,2");
        commands.add("WRITE test2,3,Trying to access in parallel...");
        commands.add("READ test1,5");
        sendAutomaticCommand(commands);
    }

    /**
     * This method is testing the activity of the server when more commands were send than there are worker in the pool
     *
     * @Return void
     */
    public static void automaticMoreRequestsThanWorker() {
        System.out.println("*************************************************************************************");
        System.out.println("ATTENTION: Starting test with more requests than worker...");

        List<String> commands = new ArrayList();
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
     * This method is testing the server when a line is requested which is outside the documents bounds.
     *
     * @Return void
     */
    public static void automaticReadNonExistentLine() {
        System.out.println("*************************************************************************************");
        System.out.println("ATTENTION: Starting test for reading a non existent line...");

        List<String> commands = new ArrayList();
        commands.add("READ test1,50");
        sendAutomaticCommand(commands);
    }
}
