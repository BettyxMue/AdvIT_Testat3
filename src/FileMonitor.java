import java.net.*;
import java.io.*;

public class FileMonitor {

    private int anzWaitingW = 0;
    private int anzActiveW = 0;
    private int anzActiveR = 0;
    private boolean activeW = false;

    public synchronized void startRead() {

        while ((activeW) || (anzWaitingW > 0)) {

            try {
                this.wait();

            } catch (Exception e){
                System.err.println("ERROR: " + e);
            }
        }
        anzActiveR++;
    }

    public synchronized void endRead() {

        anzActiveR--;

        if (anzActiveR <= 0) {

            this.notifyAll();
        }
    }

    public synchronized void startWrite() {

        anzWaitingW++;

        while ((anzActiveR > 0) || (activeW)) {

            try {
                this.wait();
            } catch (Exception e) {
                System.err.println("ERROR: " + e);
            }
        }

        anzWaitingW--;

        if (anzActiveW <= 0) {
            activeW = true;
        }
        anzActiveW++;
    }

    public synchronized void endWrite() {

        anzActiveW--;
        activeW = false;
        this.notifyAll();
    }
}
