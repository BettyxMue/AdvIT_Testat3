import java.net.*;
import java.io.*;
import java.util.*;

public class QueueLinkedList {

    private LinkedList<DatagramPacket> queue = new LinkedList<>();

    public synchronized void add (DatagramPacket packet) {

        queue.add(packet);
        System.out.println("ATTENTION: Dispatcher added an element to queue. Size of queue: " + queue.size());
        notifyAll();
    }

    public synchronized DatagramPacket remove () {

        try {
            while (queue.isEmpty()) {
                wait();
            }

        } catch (InterruptedException e) {
            System.err.println("ERROR: " + e);
        }

        System.out.println("ATTENTION: Removing an element from queue. The new size of queue is: " + (queue.size() - 1));

        return queue.pop();
    }
}
