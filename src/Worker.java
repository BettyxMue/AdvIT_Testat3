import java.net.*;
import java.util.*;

public class Worker extends Thread {

    private DatagramSocket socket;
    private Queue q;
    private int id;
    Map<MyFile, FileMonitor> monitor;
    private String path;

    public Worker (int id, DatagramSocket socket, Queue q, Map<MyFile, FileMonitor> monitor, String path) {
        this.id = id;
        this.socket = socket;
        this.q = q;
        this.monitor = monitor;
        this.path = path;
    }

    public void run () {

        System.out.println("WORKER " + this.id + " arbeitet...");

        while (Server.running) {

            DatagramPacket dp = q.remove();
            System.out.println("Worker " + this.id + " hat Job bekommen!");

            try {

                InetAddress clientAddress =  dp.getAddress();
                int clientPort = dp.getPort();

                String command = new String (dp.getData(), 0, dp.getLength());
                String s = process(command);

                DatagramPacket sendDp = new DatagramPacket(s.getBytes(), s.getBytes().length, clientAddress, clientPort);
                socket.send(sendDp);

            } catch (Exception e) {
                System.err.println("ERROR: " + e);
            }
        }
    }

    public String process (String command){

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
            return "FAILED: Something failed on the client-side. Closing the server...";
        }

        try {

            String[] piece = command.split(" ", 2);

            if (piece.length < 2) {
                // Ist das Array kürzer als 2, wird ein Fehler geworfen.
                return "FAILED: A wrong command was used!";
            }

            if (piece[0].equalsIgnoreCase("READ")){

                try {
                    param = command.split(" ", 2);
                    param2 = param[1].split(",", 2);
                    fileName = param2[0].trim();
                    lineNo = Integer.parseInt(param2[1].trim());
                    f = new MyFile(path, fileName);

                    if(!monitor.containsKey(f)){

                        currentMonitor = new FileMonitor();
                        monitor.put(f, currentMonitor);
                    }

                    sleep(5000);

                    answer = f.read(id, f, lineNo, monitor);

                } catch (Exception e) {
                    answer = "ERROR: bad READ command";
                    throw new Exception(e);
                }

            } else if (piece[0].equalsIgnoreCase("WRITE")) {

                try {

                    param = command.split(" ", 2);
                    param2 = param[1].split(",", 3);
                    fileName = param2[0].trim();
                    lineNo = Integer.parseInt(param2[1].trim());
                    newData = param2[2].trim();
                    f = new MyFile(path, fileName + ".txt");

                    if(!f.exists()){
                        f.createNewFile();
                        System.out.println("ATTENTION: No corresponding file could be found! Creating one...");
                    }

                    if(!monitor.containsKey(f)){

                        currentMonitor = new FileMonitor();
                        monitor.put(f, currentMonitor);
                    }

                    sleep(5000);

                    System.out.println("Worker " + this.id + " fängt an zu schreiben...");

                    answer = f.write(id, f, lineNo, newData, monitor);

                    System.out.println("Worker " + this.id + " hört auf zu schreiben...");

                } catch (Exception e) {
                    answer = "ERROR: bad WRITE command";
                    throw new Exception(e);
                }

            } else {
                answer = "ERROR: unknown command";
                throw new Exception("Unknown Command");
            }

        } catch (Exception e) {
            System.err.println("ERROR: " + e);
        }

        return answer;
    }
}
