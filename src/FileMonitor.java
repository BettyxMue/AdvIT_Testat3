public class FileMonitor {

    // Vorinitialisierung der benötigten Variablen
    private int anzWaitingW = 0;
    private int anzActiveW = 0;
    private int anzActiveR = 0;
    private boolean activeW = false;

    /**
     * Einstiegsmethode eines "Lesers"
     *
     * @Return void
     */
    public synchronized void startRead() {

        // Überprüfung, ob aktive oder wartende Schreiber vorhanden sind --> Schreiber-Priorität
        while ((activeW) || (anzWaitingW > 0)) {

            try {
                // wenn ja, dass muss der Leser warten
                this.wait();

            } catch (Exception e){
                // bei Auftreten einer Exception: Fehlerausgabe
                System.err.println("ERROR: " + e);
            }
        }
        // sonst Inkrementieren der aktiven Leser
        anzActiveR++;
    }

    /**
     * Austrittsmethode eines "Lesers"
     *
     * @Return void
     */
    public synchronized void endRead() {

        // Dekrementieren der Anzahl aktiver Leser
        anzActiveR--;

        // Überprüfung, ob es weitere aktive Leser gibt
        if (anzActiveR <= 0) {

            // wenn nicht, werden alle wartenden Threads geweckt, sodass evtl. wartende Schreiber loslaufen können
            this.notifyAll();
        }
    }

    /**
     * Einstiegsmethode eines "Schreibers"
     *
     * @Return void
     */
    public synchronized void startWrite() {

        // Inkrementieren der Anzahl von wartenden Schreibern
        anzWaitingW++;

        while ((anzActiveR > 0) || (activeW)) {

            try {

                this.wait();

            } catch (Exception e) {
                // bei Auftreten einer Exception: Fehlerausgabe
                System.err.println("ERROR: " + e);
            }
        }
        // muss Schreiber nicht warten, ist kein weiterer Schreiber aktiv, also können wir die Schreibtätigkeit auf
        // "true" setzen, da nun ein Schreiber aktiv sein wird
        activeW = true;
        // muss Schreiber nicht warten, dann Dekrementieren der Anzahl von wartenden Schreibern
        anzWaitingW--;
        // Inkrementieren der Anzahl an aktiven Schreibern --> wird max. 1
        anzActiveW++;
    }

    /**
     * Austrittsmethode eines "Schreibers"
     *
     * @Return void
     */
    public synchronized void endWrite() {

        // Dekrementieren der Anzahl aktiver Schreiber
        anzActiveW--;
        // Setzen des aktiven Schreibens auf "false" --> hier keine Überprüfung auf weitere Schreiber nötig, da sowieso
        // immer nur 1 Schreiber aktiv sein darf
        activeW = false;
        // Wecken aller wartenden Threads, sodass evtl. wartende Schreiber oder Leser loslaufen können
        this.notifyAll();
    }
}
