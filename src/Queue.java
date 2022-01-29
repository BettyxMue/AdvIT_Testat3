import java.net.*;
import java.util.*;

public class Queue {

    // Initialisierung der Job-Warteschlange für die Worker
    private LinkedList<DatagramPacket> queue = new LinkedList<>();

    /**
     * Diese Methode dient dem Hinzufügen eingehender DatagramPackets (Aufgaben für Worker) in die Job-Warteschlange.
     *
     * @param packet
     * @Return void
     */
    public synchronized void add (DatagramPacket packet) {

        // Hinzufügen des eingehenden Packets in die Warteschlange (hintendran)
        queue.add(packet);
        System.out.println("ATTENTION: Dispatcher added an element to queue. Size of queue: " + queue.size());
        // Wecken aller nicht arbeitenden Worker, um sich den eingegangenen Job zu holen
        notifyAll();
    }

    /**
     * Diese Methode dient dem Entnehmen von DatagramPackets (Aufgaben für Worker) aus der Job-Warteschlange.
     *
     * @return DatagramPacket
     */
    public synchronized DatagramPacket remove () {

        try {
            // Überprüfung, ob Warteschlange leer ist
            while (queue.isEmpty()) {
                // wenn ja, dann muss gewartet werden bis ein Element in die Warteschlange eingefügt wird
                wait();
            }

        } catch(IllegalMonitorStateException e) {
            // bei Auftreten einer IllegalMonitorStateException: Fehlerausgabe
            System.err.println("ERROR: " + e);
        } catch (InterruptedException e) {
            // bei Auftreten einer InterruptedException: Fehlerausgabe
            System.err.println("ERROR: " + e);
        }

        System.out.println("ATTENTION: Removing an element from queue. The new size of queue is: " + (queue.size() - 1));

        // Rückgabe des letzten Elements der Warteschlange
        return queue.pop();
    }
}
