import java.io.*;
import java.util.*;

public class MyFile extends File {

    // Initialisierung der benötigten Variablen
    private String fileName = null;
    private FileMonitor fm;

    /**
     * Konstruktor der Klasse "MyFile"
     *
     * @param parent    - entspricht in diesem Fall dem Pfad der Datei
     * @param child     - entspricht in diesem Fall dem Dateinamen (ohne Dateiendung!)
     */
    public MyFile(String parent, String child) {
        super(parent, child);
        fileName = parent + child;
    }

    /**
     * Diese Methode dient dem Lesen einer Zeile aus einer angegebenen Datei.
     *
     * @param file      - Datei, aus dem gelesen werden soll
     * @param lineNo    - Zeile, die gelesen werden soll
     * @param monitor   - Map mit dazugehörigem Monitor zur entsprechenden Datei
     * @return String   - die gelesene Zeile aus der entsprechenden Datei
     */
    public String read (MyFile file, int lineNo, Map<MyFile, FileMonitor> monitor) {

        // hole den zur Datei zugehörigen Monitor
        fm = monitor.get(file);
        // Überprüfe, ob es zu dieser Datei einen Monitor gibt
        if (fm == null) {
            // wenn nicht, wird ein Fehler zurückgegeben
            return "ERROR: The corresponding file for Worker could not be found!";
        }
        // wenn doch, wird über diesen Monitor das Lesen der Datei gestartet
        fm.startRead();

        // Initialisierung der benötigen Variablen
        String answer = "ERROR: unable to open file for reading!";
        BufferedReader bf = null;

        try {
            // Erstellung eines neuen FileReaders
            bf = new BufferedReader(new FileReader(fileName + ".txt"));
            String s = "ERROR: READ failed - line + " + lineNo + " could not be found in file!";

            // Iteration durch die Zeilen der Datei, bis man bei der geforderten Zeile angelangt ist
            for (int i = 0; (i < lineNo) && (s != null); i++) {
                // Einlesen der Zeile, über welche iteriert wird
                s = bf.readLine();
            }

            // Überprüfung, ob Inhalt der Variable s leer ist
            if (s != null) {
                // wenn nein, setze die Rückgabe-Antwort auf den Inhalt der Variablen s
                answer = s;

            } else {
                // wenn ja, gib eine Fehlermeldung zurück
                answer = "ERROR: READ failed - line " + lineNo + " could not be found in file";
            }

        } catch (FileNotFoundException e) {
            // bei Auftreten einer FileNotFoundException: Fehlerausgabe
            return "ERROR: The corresponding file does not exists!";

        } catch (IOException e) {
            // bei Auftreten einer IOException: Fehlerausgabe
            System.err.println("ERROR: " + e);

        } catch (Exception e) {
            // bei Auftreten einer Exception: Fehlerausgabe
            System.err.println("ERROR: " + e);
        }

        // Überprüfung, ob der BufferedReader null ist
        if (bf != null) {
            try {
                // wenn ja, wird dieser geschlossen
                bf.close();
            } catch (Exception e) {
                // bei Auftreten einer Exception: Fehlerausgabe
                System.err.println("ERROR: " + e);
            }
        }

        // Beenden des Lesens auf dem Monitor der Datei
        fm.endRead();
        // Rückgabe der Antwort
        return answer;
    }

    /**
     * Diese Methode dient dem Schreiben von Daten in eine Zeile einer angegebenen Datei.
     *
     * @param file      - Datei, in die geschrieben werden soll
     * @param lineNo    - Zeile, in die geschrieben werden soll
     * @param data      - Inhalt, der geschrieben werden soll
     * @param monitor   - Map mit dazugehörigem Monitor zur entsprechenden Datei
     * @return String   - der neue Inhalt der neu beschriebenen Zeile
     */
    public String write (MyFile file, int lineNo, String data, Map<MyFile, FileMonitor> monitor) {

        // hole den zur Datei zugehörigen Monitor
        fm = monitor.get(file);
        // Überprüfe, ob es zu dieser Datei einen Monitor gibt
        if (fm == null) {
            // wenn nicht, wird ein Fehler zurückgegeben
            return "ERROR: The corresponding file could not be found!";
        }
        // wenn doch, wird über diesen Monitor das Beschreiben der Datei gestartet
        fm.startWrite();

        // Initialisierung der benötigten Variablen
        String answer = "ERROR: unable to open file for writing!";
        BufferedReader inFile = null;
        PrintWriter outFile = null;
        boolean found = false;

        try {
            // Erstellung des FileReaders und FileWriters für die entsprechende Datei
            inFile = new BufferedReader(new FileReader(fileName));
            outFile = new PrintWriter(new FileWriter(fileName + ".temp"));

            // Rückgabe einer Fehlermeldung wird die Rückgabe-Antwort nicht auf den Inhalt der eingegebenen Daten
            // gesetzt --> setzt voraus, dass die gesuchte Zeile gefunden wurde
            answer = "ERROR; WRITE failed - line " + lineNo + " could not be found in file!";
            String s = "";
            int i = 1;

            // Lese alle Zeilen, solange deren Inhalt nicht null wird
            while (i <= lineNo || s != null) {
                // Setzen die Variable s auf den Inhalt der Zeile
                s = inFile.readLine();

                // Überprüfung, ob diese Iteration der Zeilennummer entspricht
                if (i == lineNo) {
                    // setze die Variable "found" auf true, da die gesuchte Zeile gefunden wurde
                    found = true;
                    // schreibe die eingegebenen Daten des Benutzers in diese Zeile
                    outFile.println(data);

                } else {
                    //Überprüfung, ob die eingelesene Zeile null ist
                    if (s == null) {
                        // wenn ja, leere Zeilen mit leerem String füllen (nicht mit s, dann wird "null" reingeschrieben)
                        outFile.println("");

                    } else {
                        // wenn nicht, den Inhalt der alten Zeile wieder übertragen
                        outFile.println(s);
                    }
                }
                // Inkrementieren der Zählervariable
                i++;
            }

        } catch (FileNotFoundException e) {
            // bei Auftreten einer FileNotFoundException: Fehlerausgabe
            return "ERROR: The corresponding file does not exists!";

        } catch (IOException e) {
            // bei Auftreten einer IOException: Fehlerausgabe
            System.err.println("ERROR: " + e);

        } catch (Exception e) {
            // bei Auftreten einer Exception: Fehlerausgabe
            System.err.println("ERROR: " + e);
        }

        // Überprüfung, ob der BufferedReader leer ist
        if (inFile != null) {
            try {
                // wenn ja, wird dieser geschlossen
                inFile.close();
            } catch (Exception e){
                // bei Auftreten einer Exception: Fehlerausgabe
                System.err.println("ERROR: " + e);
            }
        }

        // Überprüfung, ob der PrintWriter leer ist
        if (outFile != null) {
            try {
                // wenn ja, wird dieser geschlossen
                outFile.close();
            } catch (Exception e) {
                // bei Auftreten einer Exception: Fehlerausgabe
                System.err.println("ERROR: " + e);
            }
        }

        // Überprüfung, ob die "gefunden"-Varaible auf wahr gesetzt wurde --> d.h., die gesuchte Zeile gefunden wurde
        if (found) {
            // wenn ja, dann setze die Antwort auf den Inhalt der eingegebenen Daten
            answer = data;

            try {
                // Erzeugung neuer Datei-Objekte, um die alte Datei überschreiben zu können und eine "Back-Up"-Datei zu
                // haben, falls beim Überschreiben etwas schiefläuft
                File f1 = new File(fileName);
                File f2 = new File(fileName + ".temp");
                File f3 = new File(fileName + ".bak");

                // Löschen und Umbenennung der entsprechenden Dateien, um die Änderungen durchzuführen
                f3.delete();
                f1.renameTo(f3);
                f2.renameTo(f1);

            } catch (Exception e) {
                // bei Auftreten einer Exception: Fehlerausgabe
                System.err.println("ERROR: " + e);
            }
        }
        // Beende den Schreibprozess auf dem entsprechenden Datei-Monitor
        fm.endWrite();
        // Rückgabe der Antwort an den Benutzer
        return ("Overwritten to: " + answer);
    }
}
