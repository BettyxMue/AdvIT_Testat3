import java.net.*;
import java.io.*;
import java.util.*;

public class MyFile extends File {

    private String fileName = null;
    private FileMonitor fm;

    public MyFile(String parent, String child) {
        super(parent, child);
        fileName = parent + child;
    }

    public MyFile(String fileName) {
        super(fileName);
    }

    public String read (int id, MyFile file, int lineNo, Map<MyFile, FileMonitor> monitor) {

        fm = monitor.get(file);

        if (fm == null) {
            return "ERROR: The corresponding file could not be found!";
        }

        fm.startRead();

        String answer = "ERROR: unable to open file for reading!";
        BufferedReader bf = null;

        try {

            bf = new BufferedReader(new FileReader(fileName + ".txt"));
            String s = "ERROR: READ failed - line + " + lineNo + " could not be found in file!";

            for (int i = 0; (i < lineNo) && (s != null); i++) {
                s = bf.readLine();
            }

            if (s != null) {
                answer = s;

            } else {
                answer = "ERROR: READ failed - line " + lineNo + " could not be found in file";
            }

        } catch (Exception e) {
            System.err.println("ERROR: " + e);
        }

        if (bf != null) {
            try {
                bf.close();
            } catch (Exception e) {
                System.err.println("ERROR: " + e);
            }
        }

        fm.endRead();

        return answer;
    }

    public String write (int id, MyFile file, int lineNo, String data, Map<MyFile, FileMonitor> monitor) {

        fm = monitor.get(file);

        if (fm == null) {
            return "ERROR: The corresponding file could not be found!";
        }

        fm.startWrite();

        String answer = "ERROR: unable to open file for writing!";
        BufferedReader inFile = null;
        PrintWriter outFile = null;
        boolean found = false;

        try {

            inFile = new BufferedReader(new FileReader(fileName));
            outFile = new PrintWriter(new FileWriter(fileName + ".temp"));

            answer = "ERROR; WRITE failed - line " + lineNo + " could not be found in file!";
            String s = "";

            for (int i = 0; s != null; i++) {
                s = inFile.readLine();

                if (i == lineNo - 1) {
                    found = true;
                    outFile.println(data);

                } else if (s != null) {
                    outFile.println(s);
                }
            }

        } catch (Exception e) {
            System.err.println("ERROR: " + e);
        }

        if (inFile != null) {
            try {
                inFile.close();
            } catch (Exception e){
                System.err.println("ERROR: " + e);
            }
        }

        if (outFile != null) {
            try {
                outFile.close();
            } catch (Exception e) {
                System.err.println("ERROR: " + e);
            }
        }

        if (found) {
            answer = data;

            try {

                File f1 = new File(fileName);
                File f2 = new File(fileName + ".temp");
                File f3 = new File(fileName + ".bak");

                f3.delete();
                f1.renameTo(f3);
                f2.renameTo(f1);

            } catch (Exception e) {
                System.err.println("ERROR: " + e);
            }
        }

        fm.endWrite();

        return answer;
    }
}
