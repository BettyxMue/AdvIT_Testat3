import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Server {

    public static final int MAXSIZE = 65507;
    private static final int DEFAULT_PORT = 5999;
    private static final String PATH = System.getProperty("user.home") + "/Desktop/Messages/";
    public static boolean running = true;

    private static DatagramSocket serverSocket = null;
    private static Worker[] workers;
    private static Queue requestQueue;
    private static Map<MyFile, FileMonitor> monitor = new HashMap<>();

    public static void main(String[] args) {

        int port = DEFAULT_PORT;
        DatagramPacket dp = null;
        workers = new Worker[5];
        requestQueue = new Queue();

        if (args.length > 1) {

            try {
                port = Integer.parseInt(args[1]);

                if (port < 1 || port > 65535) {
                    throw new Exception();
                }
            } catch (Exception e) {
                System.out.println("ATTENTION: usage of Server on port: " + port);
            }
        }

        try {

            serverSocket = new DatagramSocket(DEFAULT_PORT);
            System.out.println("SUCCESS: Server was startet on port " + DEFAULT_PORT + "!");

            File folder = new File(PATH);
            if(!folder.exists()) {
                System.out.println("ATTENTION: Message Folder at the corresponding path does not exist.\nCreating " +
                        "one...");
                folder.mkdirs();
            }

            for(int i = 0; i < workers.length; i++) {
                workers[i] = new Worker(i, serverSocket, requestQueue, monitor, PATH);
                workers[i].start();
                System.out.println("SUCCESS: Worker "+ i +" was started!");
            }

            while (true) {

                try {

                    dp = new DatagramPacket(new byte[MAXSIZE], MAXSIZE);
                    serverSocket.receive(dp);
                    requestQueue.add(dp);

                } catch (Exception e) {
                    System.err.println("ERROR: " + e + "\nATTENTION: Shutting down server!");
                    System.exit(1);
                }
            }

        } catch (Exception e) {
            System.err.println("ERROR: " + e);
        }
    }
}
