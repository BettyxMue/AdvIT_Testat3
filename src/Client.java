import java.net.*;
import java.io.*;

public class Client {

    public final static int MAXSIZE = 65507;
    public final static int DEFAULT_PORT = 5999;
    public final static String HOSTNAME = "localhost";

    public static int getMode () {

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
            DatagramSocket socket = new DatagramSocket();
            InetAddress host = InetAddress.getByName(hostname);

            switch (getMode()) {

                case 1:

                    try {

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
                        }

                        socket.close();

                    } catch (Exception e) {
                        System.err.println("ERROR: " + e);
                    }
                    break;

                case 2:



                    break;

                default:



                    break;
            }
        } catch (Exception e) {
            System.err.println("ERROR: " + e);
        }
    }
}
