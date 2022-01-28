import java.net.*;
import java.io.*;

public class Queue {

    private DatagramPacket[] puffer;
    private int ctr = 0;
    private int nextFree = 0;
    private int nextFull = 0;

    public Queue (int size) {
        puffer = new DatagramPacket[size];
    }

    public synchronized void add (DatagramPacket dp) {

        try {

            while (ctr == puffer.length) {

                System.err.println("ATTENTION: The puffer of the dispatcher is full!");
                wait();
            }

            puffer[nextFree] = dp;
            nextFree = (nextFree + 1) % puffer.length;
            ctr++;
            notifyAll();

        } catch (Exception e) {
            System.err.println("ERROR: " + e);
        }
    }

    public synchronized DatagramPacket remove () {

        DatagramPacket dp = null;

        try {

            while (ctr == 0) {

                System.err.println("ATTENTION: The puffer of the dispatcher is empty!");
                wait();
            }

            dp = puffer[nextFull];
            nextFull = (nextFree + 1) % puffer.length;
            ctr--;
            notifyAll();

        } catch (Exception e) {
            System.err.println("ERROR: " + e);
        }

        return dp;
    }
}
