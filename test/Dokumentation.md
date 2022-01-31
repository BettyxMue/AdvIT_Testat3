# Advanced IT - Testat 3
## Aufgabenstellung
In dieser Aufgabe soll ein File-Server für Textdateien entwickelt werden.
Vereinfachend gehen wir davon aus, dass dem Server ein festes, bereits existierendes Basisverzeichnis
zugeordnet ist, in dem sich alle verwalteten Dateien befinden und dass er die notwendigen Zugriffsrechte
besitzt. Die Textdateien sind dabei zeilenweise organisiert und beginnen mit Zeilennummer 1.
Der Server soll als **Worker-Pool-Server** auf **Port 5999** Aufträge in Form von Strings mit `READ filename,line no` 
entgegennehmen, wobei line no eine positive ganze Zahl sein muss. Daraufhin
wird vom Server die Datei filename geöffnet, die Zeile line no ausgelesen und zurückgeschickt.
Außerdem soll der Server auch das Kommando `WRITE filename,line no,data` verstehen, bei
dem die Zeile line no durch data (kann Kommas und Leerzeichen enthalten) ersetzt werden soll.
Falls sich im Basisverzeichnis des Servers keine solche Datei befindet oder keine entsprechende Zeile
vorhanden ist, soll an den Client eine Fehlermeldung zurückgesendet werden.

Achten Sie darauf, dass nebenläufige Zugriffe konsistente Dateien hinterlassen. Implementieren Sie hierzu
das Zweite Leser-Schreiber-Problem (mit Schreiberpriorität) mit dem Java Monitorkonzept!

Implementieren Sie den Server sowie einen kleinen Test-Client. Verwenden Sie Java und UDP!

Testen Sie die Nebenläufigkeit und das Einhalten der Schreiberpriorität durch geeignete Szenarien und
dokumentieren Sie die Testfälle!

## Umsetzung
Um das Ganze etwas dynamischer zu gestalten, habe ich mir die Freiheit genommen, den Server so zu implementieren, dass
er einen entsprechenden Ordner erstellt, wenn der Benötigte nicht auf dem Desktop vorhanden ist. Sollte diese
Funktionalität nicht erwünscht sein, so lässt sich die **entsprechende Zeile (65 - 74)** aus dem Code der Klasse 
`Server.java` entfernen. Die dann geworfene Fehlermeldung, sollte der Ordner fehlen, wird durch die Exceptions 
abgefangen.

Um den Anforderungen des **Worker-Pools** gerecht zu werden, werden durch den Server (`Server.java`) 5 Worker gestartet, 
welche die eingehenden Aufträge bearbeiten sollen.

**Server.java**
```java
for(int i = 0; i < workers.length; i++) {
    workers[i] = new Worker(i + 1, serverSocket, requestQueue, monitor, PATH);
    workers[i].start();
    System.out.println("SUCCESS: Worker "+ (i + 1) +" was started!");
}
```

In einer Endlosschleife fügt dabei der Server (`Server.java`) die eingehenden Aufträge der Clients zu einer 
Job-Warteschlange hinzu, welche durch eine LinkedList (`Queue.java`) realisiert wurde.

**Server.java**
```java
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
```

Ist ein Worker (`Worker.java`) bereit einen Auftrag zu bearbeiten, so holt er sich diesen aus der Job-Warteschlange, 
bearbeitet diesen und sendet die Antwort zurück an den Auftragsgeber, also den Client (`Client.java`).

**Worker.java**
```java
while (Server.running) {
        DatagramPacket dp = q.remove();
        System.out.println("SUCCESS: Worker " + this.id + " received the job from the queue!");

        try {
            InetAddress clientAddress = dp.getAddress();
            int clientPort = dp.getPort();
            String command = new String(dp.getData(), 0, dp.getLength());
            String s = process(command);
            DatagramPacket sendDp = new DatagramPacket(s.getBytes(), s.getBytes().length, clientAddress, clientPort);
            socket.send(sendDp);
            
        } catch (Exception e) {
            System.err.println("ERROR: " + e);
        }
}
```

Mit Hilfe eines File-Monitors (`FileMonitor.java`) für jedes File (verbunden über eine Map), wird sichergestellt, dass 
kein Worker parallel zu einem anderen Worker eine Datei bearbeiten kann. Paralleles Lesen wird jedoch gewährleistet. 
Solange ein Worker etwas aus einer Datei ausliest, darf jedoch nicht in diese geschrieben werden. Hierbei wurde 
die Schreiber-Priorität implementiert.

**FileMonitor.java**
```java
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
```

## Beispiele
Da das Prinzip des Beschreibens einer Datei sowie des Lesens aus ihr bereits aus der vorherigen Testataufgabe 
sowie durch die Übungen innerhalb der Vorlesung bereits gut etabliert wurde, wird innerhalb dieser Dokumentation
auf das Zeigen der verschiedenen Fälle mittels Groß- und Kleinschreibung von Kommandos verzichtet. Mittels
```java
if (piece[0].equalsIgnoreCase("READ"))
```
und
```java
else if (piece[0].equalsIgnoreCase("WRITE"))
```
und
```java
if (s.equalsIgnoreCase("EXIT"))
```
wird diese Funktionalität jedoch wie auch in der vergangenen Testataufgabe unterstützt. Auch die Eingaben der Dateinamen
sind nicht von Groß- und Kleinschreibung abhängig, sofern eine Datei nicht neu erstellt werden muss, um in diese 
schreiben zu können. Existiert die zu beschreibende Datei noch nicht, so wird eine mit der entsprechenden Groß- und 
Kleinschreibung, wie im Befehl angegeben, angelegt. Der Zugriff erfolgt jedoch "non case sensitive". Auf der anderen 
Seite erfolgt das Schreiben der Daten in eine Datei immer "case sensitive".

Durch die eigene Implementierung der File-Klasse `MyFile.java` ist die Benutzereingabe so konzipiert, dass der Benutzer
nur den Namen der entsprechenden Datei eingeben muss. Innerhalb des Programms wird dann automatisch mit einer Text-Datei
weitergearbeitet. Dies hat zur Folge, dass die `.bak-Datei` auch die entsprechende Endung der Datei `.txt` mitübernimmt,
was jedoch rein technisch erstmal kein Problem darstellt. Dies ist wichtig für den Benutzer, da, wenn er nun eine 
Eingabe mit der Dateiendung vornimmt, diese gedoppelt wird. Es ist vom Benutzer nut gefordert den **DATEINAMEN** anzugeben.

Für das Durchspielen der verschiedenen Beispiele werden folgende Dateien mit dem entsprechenden Inhalt vorausgesetzt:

**Speiseplan.txt**
```java
Montag: Sushi
Dienstag: Burger
Mittwoch: Porridge
Donnerstag: Brot und Aufschnitt
Freitag: Suppe
Samstag: Käsekuchen
Sonntag: Braten
```

**Zahlen.txt**
```java
Null
Eins
Zwei
Drei
Vier
Fünf
Sechs
Sieben
Acht
Neun
Zehn
Doppel Eins
...
```

Diese können im Ordner "_Test Files_" gefunden werden. Für individuelle Testfälle können aber natürlich auch andere 
Textdateien über _das Programm_ oder _manuell_ angelegt werden. In den folgenden Testfällen werden diese Dateien 
geändert. Mit den geänderten Daten wird dann auch in den folgenden Testfall weitergearbeitet.

Um die Parallelität in den eigenen Tests besser nachvollziehen zu können, schläft jeder Worker / Thread, sobald er
ein Befehl ausführt für 5 Sekunden.

Alle folgenden Beispiele finden mittels **Client-Modus 1** statt:
```java
Available mode:
1 - manual user input
2 - prepared automatic input
Please choose one of the modes above:
> 1
*************************************************************************************
ATTENTION: You have chosen mode 1!
Type a command to send:
'READ file, lineNO' OR 'WRITE file, lineNo, data' OR 'EXIT'
> 
```

Dabei wurde der Server entsprechend der folgenden Ausgabe gestartet:
```java
SUCCESS: Server was startet on port 5999!
SUCCESS: Worker 1 was started!
ATTENTION: Worker 1 is running...
SUCCESS: Worker 2 was started!
SUCCESS: Worker 3 was started!
ATTENTION: Worker 2 is running...
ATTENTION: Worker 3 is running...
SUCCESS: Worker 4 was started!
SUCCESS: Worker 5 was started!
ATTENTION: Worker 4 is running...
ATTENTION: Worker 5 is running...
```

### Beispiel 1: Paralleles Lesen aus einer Datei


Client 1 & 2: Die Benutzereingabe/Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java
*************************************************************************************
ATTENTION: You have chosen mode 1!
Type a command to send:
'READ file, lineNO' OR 'WRITE file, lineNo, data' OR 'EXIT'
> read Speiseplan, 1
SUCCESS: Answer received: <Montag: Sushi>
*************************************************************************************
```

Die Serverausgabe für dieses Beispiel sieht wie folgt aus:
```java
ATTENTION: Dispatcher added an element to queue. Size of queue: 1
ATTENTION: Removing an element from queue. The new size of queue is: 0
SUCCESS: Worker 1 received the job from the queue!
ATTENTION: Dispatcher added an element to queue. Size of queue: 1
ATTENTION: Removing an element from queue. The new size of queue is: 0
SUCCESS: Worker 5 received the job from the queue!
ATTENTION: Worker 1 starts reading...
ATTENTION: Worker 1 stops reading...
ATTENTION: Worker 5 starts reading...
ATTENTION: Worker 5 stops reading...
```

### Beispiel 2: Paralleles Lesen aus mehreren Dateien


Client 1: Die Benutzereingabe/Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java
*************************************************************************************
> read speiseplan,2
SUCCESS: Answer received: <Dienstag: Burger>
*************************************************************************************
```

Client 2: Die Benutzereingabe/Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java
*************************************************************************************
> read Zahlen,1
SUCCESS: Answer received: <Null>
*************************************************************************************
```

Die Serverausgabe für dieses Beispiel sieht wie folgt aus:
```java
ATTENTION: Dispatcher added an element to queue. Size of queue: 1
ATTENTION: Removing an element from queue. The new size of queue is: 0
SUCCESS: Worker 5 received the job from the queue!
ATTENTION: Worker 5 starts reading...
ATTENTION: Worker 5 stops reading...
ATTENTION: Dispatcher added an element to queue. Size of queue: 1
ATTENTION: Removing an element from queue. The new size of queue is: 0
SUCCESS: Worker 2 received the job from the queue!
ATTENTION: Worker 2 starts reading...
ATTENTION: Worker 2 stops reading...
```

### Beispiel 3: Paralleles Schreiben in eine Datei


Client 1: Die Benutzereingabe/Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java
*************************************************************************************
> Write speiseplan,2,Dienstag: Salat
SUCCESS: Answer received: <Overwritten to: Dienstag: Salat>
*************************************************************************************
```

Client 2: Die Benutzereingabe/Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java
*************************************************************************************
> Write speiseplan,3,Mittwoch: ...mag keinen Salat, will lieber Eis!
SUCCESS: Answer received: <Overwritten to: Mittwoch: ...mag keinen Salat, will lieber Eis!>
*************************************************************************************
```

Die Serverausgabe für dieses Beispiel sieht wie folgt aus:
```java
ATTENTION: Dispatcher added an element to queue. Size of queue: 1
ATTENTION: Removing an element from queue. The new size of queue is: 0
SUCCESS: Worker 1 received the job from the queue!
ATTENTION: Dispatcher added an element to queue. Size of queue: 1
ATTENTION: Removing an element from queue. The new size of queue is: 0
SUCCESS: Worker 2 received the job from the queue!
ATTENTION: Worker 1 starts writing...
ATTENTION: Worker 1 stops writing...
ATTENTION: Worker 2 starts writing...
ATTENTION: Worker 2 stops writing...
```

Öffnet man nun die Datei ```Speiseplan.txt```, so erkennt man, dass in sowohl in Zeile 2 als auch in Zeile 3 die 
jeweiligen Eingaben vorhanden sind. Jedoch erfolgte dies zeitlich verzögert, da die Worker beim Schreiben in die gleiche
Datei sich gegenseitig ausschließen und somit der Reihenfolge nach warten mussten.

### Beispiel 4: Paralleles Schreiben in mehrere Dateien


Client 1: Die Benutzereingabe/Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java
*************************************************************************************
> write zahlen,11,10
SUCCESS: Answer received: <Overwritten to: 10>
*************************************************************************************
```

Client 2: Die Benutzereingabe/Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java
*************************************************************************************
> write spEiSeplAn,7,Sonntag: Entenbraten
SUCCESS: Answer received: <Overwritten to: Sonntag: Entenbraten>
*************************************************************************************
```

Die Serverausgabe für dieses Beispiel sieht wie folgt aus:
```java
ATTENTION: Dispatcher added an element to queue. Size of queue: 1
ATTENTION: Removing an element from queue. The new size of queue is: 0
SUCCESS: Worker 4 received the job from the queue!
ATTENTION: Dispatcher added an element to queue. Size of queue: 1
ATTENTION: Removing an element from queue. The new size of queue is: 0
SUCCESS: Worker 2 received the job from the queue!
ATTENTION: Worker 4 starts writing...
ATTENTION: Worker 4 stops writing...
ATTENTION: Worker 2 starts writing...
ATTENTION: Worker 2 stops writing...
```

### Beispiel 5: Paralleles Lesen und Schreiben in eine Datei


Client 1: Die Benutzereingabe/Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java
*************************************************************************************
> read zahlen,3
write zahlen,4,3
read zahlen,3
SUCCESS: Answer received: <Zwei>
*************************************************************************************
> SUCCESS: Answer received: <Overwritten to: 3>
*************************************************************************************
> SUCCESS: Answer received: <dos>
*************************************************************************************
```

Client 2: Die Benutzereingabe/Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java
*************************************************************************************
> read zahlen,4
write zahlen,3,dos
read zahlen,4
SUCCESS: Answer received: <Drei>
*************************************************************************************
> SUCCESS: Answer received: <Overwritten to: dos>
*************************************************************************************
> SUCCESS: Answer received: <3>
*************************************************************************************
```

Die Serverausgabe für dieses Beispiel sieht wie folgt aus:
```java
ATTENTION: Dispatcher added an element to queue. Size of queue: 1
ATTENTION: Removing an element from queue. The new size of queue is: 0
SUCCESS: Worker 3 received the job from the queue!
ATTENTION: Worker 5 starts writing...
ATTENTION: Worker 5 stops writing...
ATTENTION: Dispatcher added an element to queue. Size of queue: 1
ATTENTION: Removing an element from queue. The new size of queue is: 0
SUCCESS: Worker 2 received the job from the queue!
ATTENTION: Worker 3 starts writing...
ATTENTION: Worker 3 stops writing...
ATTENTION: Dispatcher added an element to queue. Size of queue: 1
ATTENTION: Removing an element from queue. The new size of queue is: 0
SUCCESS: Worker 5 received the job from the queue!
ATTENTION: Worker 2 starts reading...
ATTENTION: Worker 2 stops reading...
ATTENTION: Worker 5 starts reading...
ATTENTION: Worker 5 stops reading...
```

### Beispiel 6: Paralleles Lesen und Schreiben in mehrere Dateien


Client 1: Die Benutzereingabe/Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java
*************************************************************************************
> read zAhLen,1
write speiseplan,1,Montag: Nix!
SUCCESS: Answer received: <Null>
*************************************************************************************
> SUCCESS: Answer received: <Overwritten to: Montag: Nix!>
*************************************************************************************
```

Client 2: Die Benutzereingabe/Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java
*************************************************************************************
> read speiseplan,1
write zahlen,1,doppel null
SUCCESS: Answer received: <Montag: Nix!>
*************************************************************************************
> SUCCESS: Answer received: <Overwritten to: doppel null>
*************************************************************************************
```

Die Serverausgabe für dieses Beispiel sieht wie folgt aus:
```java
ATTENTION: Dispatcher added an element to queue. Size of queue: 1
ATTENTION: Removing an element from queue. The new size of queue is: 0
SUCCESS: Worker 3 received the job from the queue!
ATTENTION: Worker 3 starts reading...
ATTENTION: Worker 3 stops reading...
ATTENTION: Dispatcher added an element to queue. Size of queue: 1
ATTENTION: Removing an element from queue. The new size of queue is: 0
SUCCESS: Worker 5 received the job from the queue!
ATTENTION: Dispatcher added an element to queue. Size of queue: 1
ATTENTION: Removing an element from queue. The new size of queue is: 0
SUCCESS: Worker 3 received the job from the queue!
ATTENTION: Worker 5 starts writing...
ATTENTION: Worker 5 stops writing...
ATTENTION: Worker 3 starts reading...
ATTENTION: Worker 3 stops reading...
ATTENTION: Dispatcher added an element to queue. Size of queue: 1
ATTENTION: Removing an element from queue. The new size of queue is: 0
SUCCESS: Worker 2 received the job from the queue!
ATTENTION: Worker 2 starts writing...
ATTENTION: Worker 2 stops writing...
```

### Beispiel 7: Lesen aus einer nicht vorhandenen Datei


Die Benutzereingabe/Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java
*************************************************************************************
> read Fahrplan,1
SUCCESS: Answer received: <ERROR: The corresponding file does not exists!>
*************************************************************************************
```

Die Serverausgabe für dieses Beispiel sieht wie folgt aus:
```java
ATTENTION: Dispatcher added an element to queue. Size of queue: 1
ATTENTION: Removing an element from queue. The new size of queue is: 0
SUCCESS: Worker 3 received the job from the queue!
ATTENTION: Worker 3 starts reading...
ATTENTION: Worker 3 stops reading...
```

### Beispiel 8: Schreiben in eine nicht vorhandene Datei


Die Benutzereingabe/Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java
*************************************************************************************
> wrITE Notizen,1,Trink mehr!
SUCCESS: Answer received: <Overwritten to: Trink mehr!>
*************************************************************************************
```

Die Serverausgabe für dieses Beispiel sieht wie folgt aus:
```java
ATTENTION: Dispatcher added an element to queue. Size of queue: 1
ATTENTION: Removing an element from queue. The new size of queue is: 0
SUCCESS: Worker 2 received the job from the queue!
ATTENTION: No corresponding file could be found! Creating one...
ATTENTION: Worker 2 starts writing...
ATTENTION: Worker 2 stops writing...
```

### Beispiel 9: Lesen einer nicht vorhandenen Zeile


Die Benutzereingabe/Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java
*************************************************************************************
> REad speiseplan,10
SUCCESS: Answer received: <ERROR: READ failed - line 10 could not be found in file>
*************************************************************************************
```

Die Serverausgabe für dieses Beispiel sieht wie folgt aus:
```java
ATTENTION: Dispatcher added an element to queue. Size of queue: 1
ATTENTION: Removing an element from queue. The new size of queue is: 0
SUCCESS: Worker 3 received the job from the queue!
ATTENTION: Worker 3 starts reading...
ATTENTION: Worker 3 stops reading...
```

### Beispiel 10: Beschreiben einer nicht vorhandene Zeile


Die Benutzereingabe/Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java
*************************************************************************************
> WRITE zahlen,20,Twenty
SUCCESS: Answer received: <Overwritten to: Twenty>
*************************************************************************************
```

Die Serverausgabe für dieses Beispiel sieht wie folgt aus:
```java
ATTENTION: Dispatcher added an element to queue. Size of queue: 1
ATTENTION: Removing an element from queue. The new size of queue is: 0
SUCCESS: Worker 1 received the job from the queue!
ATTENTION: Worker 1 starts writing...
ATTENTION: Worker 1 stops writing...
```

### Beispiel 11: Überschreiben einer bereits vorhandenen Zeile


Die Benutzereingabe/Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java
*************************************************************************************
> write zahlen,1,  
SUCCESS: Answer received: <Overwritten to: >
*************************************************************************************
```

Die Serverausgabe für dieses Beispiel sieht wie folgt aus:
```java
ATTENTION: Dispatcher added an element to queue. Size of queue: 1
ATTENTION: Removing an element from queue. The new size of queue is: 0
SUCCESS: Worker 5 received the job from the queue!
ATTENTION: Worker 5 starts writing...
ATTENTION: Worker 5 stops writing...
```

### Beispiel 12: Unvollständiger Lese-Befehl


Die Benutzereingabe/Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java
*************************************************************************************
> read ,3
SUCCESS: Answer received: <ERROR: Invalid command. Please enter a valid filename.>
*************************************************************************************
```

Die Serverausgabe für dieses Beispiel sieht wie folgt aus:
```java
ATTENTION: Dispatcher added an element to queue. Size of queue: 1
ATTENTION: Removing an element from queue. The new size of queue is: 0
SUCCESS: Worker 1 received the job from the queue!
```

### Beispiel 13: Unvollständiger Schreib-Befehl


Die Benutzereingabe/Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java
*************************************************************************************
> write ,1,nö
SUCCESS: Answer received: <ERROR: Invalid command. Please enter a valid filename.>
*************************************************************************************
```

Die Serverausgabe für dieses Beispiel sieht wie folgt aus:
```java
ATTENTION: Dispatcher added an element to queue. Size of queue: 1
ATTENTION: Removing an element from queue. The new size of queue is: 0
SUCCESS: Worker 5 received the job from the queue!
```

### Beispiel 14: Unzulässiger Lese-Befehl


Die Benutzereingabe/Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java
*************************************************************************************
> READ zahlen,eins
SUCCESS: Answer received: <ERROR: Bad line number input. Line number has to be an integer number greater than 0.>
*************************************************************************************
```

Die Serverausgabe für dieses Beispiel sieht wie folgt aus:
```java
ATTENTION: Dispatcher added an element to queue. Size of queue: 1
ATTENTION: Removing an element from queue. The new size of queue is: 0
SUCCESS: Worker 5 received the job from the queue!
```

### Beispiel 15: Unzulässiger Schreib-Befehl


Die Benutzereingabe/Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java
*************************************************************************************
> WRITE zahlen,zwei,ZWEI
SUCCESS: Answer received: <ERROR: Bad line number input. Line number has to be an integer number greater than 0.>
*************************************************************************************
```

Die Serverausgabe für dieses Beispiel sieht wie folgt aus:
```java
ATTENTION: Dispatcher added an element to queue. Size of queue: 1
ATTENTION: Removing an element from queue. The new size of queue is: 0
SUCCESS: Worker 1 received the job from the queue!

```

### Beispiel 16: Lesen einer negativen Zeilennummer


Die Benutzereingabe/Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java
*************************************************************************************
> read zahlen,-10
SUCCESS: Answer received: <ERROR: Bad line number input. Please choose a line number greater than 0.>
*************************************************************************************
```

Die Serverausgabe für dieses Beispiel sieht wie folgt aus:
```java
ATTENTION: Dispatcher added an element to queue. Size of queue: 1
ATTENTION: Removing an element from queue. The new size of queue is: 0
SUCCESS: Worker 5 received the job from the queue!
```

### Beispiel 17: Beschreiben einer negativen Zeilennummer


Die Benutzereingabe/Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java
*************************************************************************************
> wRiTe zahlen,-5,Minus Fünf
SUCCESS: Answer received: <ERROR: Bad line number input. Please choose a line number greater than 0.>
*************************************************************************************
```

Die Serverausgabe für dieses Beispiel sieht wie folgt aus:
```java
ATTENTION: Dispatcher added an element to queue. Size of queue: 1
ATTENTION: Removing an element from queue. The new size of queue is: 0
SUCCESS: Worker 1 received the job from the queue!
```

### Beispiel 18: Unbekannter Befehl


Die Benutzereingabe/Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java
*************************************************************************************
> Delete zahlen,1
SUCCESS: Answer received: <ERROR: The given command is unknown!>
*************************************************************************************
```

Die Serverausgabe für dieses Beispiel sieht wie folgt aus:
```java
ATTENTION: Dispatcher added an element to queue. Size of queue: 1
ATTENTION: Removing an element from queue. The new size of queue is: 0
SUCCESS: Worker 1 received the job from the queue!
```

### Beispiel 19: Mehr Befehle als Worker


Client 1: Die Benutzereingabe/Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java
*************************************************************************************
> READ zahlen,1
READ zahlen,2
READ zahlen,3
READ zahlen,4
READ zahlen,5
SUCCESS: Answer received: <doppel null>
*************************************************************************************
> SUCCESS: Answer received: < >
*************************************************************************************
> SUCCESS: Answer received: <dos>
*************************************************************************************
> SUCCESS: Answer received: <3>
*************************************************************************************
> SUCCESS: Answer received: <Vier>
*************************************************************************************
```

Client 2: Die Benutzereingabe/Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java
*************************************************************************************
> READ zahlen,6
READ zahlen,7
READ zahlen,8
READ zahlen,9
READ zahlen,10
SUCCESS: Answer received: <Fünf>
*************************************************************************************
> SUCCESS: Answer received: <Sechs>
*************************************************************************************
> SUCCESS: Answer received: <Sieben>
*************************************************************************************
> SUCCESS: Answer received: <Acht>
*************************************************************************************
> SUCCESS: Answer received: <Neun>
*************************************************************************************
```

Die Serverausgabe für dieses Beispiel sieht wie folgt aus:
```java
ATTENTION: Dispatcher added an element to queue. Size of queue: 1
ATTENTION: Removing an element from queue. The new size of queue is: 0
SUCCESS: Worker 1 received the job from the queue!
ATTENTION: Worker 1 starts reading...
ATTENTION: Dispatcher added an element to queue. Size of queue: 1
ATTENTION: Removing an element from queue. The new size of queue is: 0
SUCCESS: Worker 5 received the job from the queue!
ATTENTION: Worker 5 starts reading...
ATTENTION: Worker 1 stops reading...
ATTENTION: Dispatcher added an element to queue. Size of queue: 1
ATTENTION: Removing an element from queue. The new size of queue is: 0
SUCCESS: Worker 2 received the job from the queue!
ATTENTION: Worker 2 starts reading...
ATTENTION: Worker 5 stops reading...
ATTENTION: Dispatcher added an element to queue. Size of queue: 1
ATTENTION: Removing an element from queue. The new size of queue is: 0
SUCCESS: Worker 1 received the job from the queue!
ATTENTION: Worker 1 starts reading...
ATTENTION: Worker 2 stops reading...
ATTENTION: Dispatcher added an element to queue. Size of queue: 1
ATTENTION: Removing an element from queue. The new size of queue is: 0
SUCCESS: Worker 5 received the job from the queue!
ATTENTION: Worker 5 starts reading...
ATTENTION: Worker 1 stops reading...
ATTENTION: Dispatcher added an element to queue. Size of queue: 1
ATTENTION: Removing an element from queue. The new size of queue is: 0
SUCCESS: Worker 2 received the job from the queue!
ATTENTION: Worker 2 starts reading...
ATTENTION: Worker 5 stops reading...
ATTENTION: Dispatcher added an element to queue. Size of queue: 1
ATTENTION: Removing an element from queue. The new size of queue is: 0
SUCCESS: Worker 1 received the job from the queue!
ATTENTION: Worker 1 starts reading...
ATTENTION: Worker 2 stops reading...
ATTENTION: Dispatcher added an element to queue. Size of queue: 1
ATTENTION: Removing an element from queue. The new size of queue is: 0
SUCCESS: Worker 5 received the job from the queue!
ATTENTION: Worker 5 starts reading...
ATTENTION: Worker 1 stops reading...
ATTENTION: Dispatcher added an element to queue. Size of queue: 1
ATTENTION: Removing an element from queue. The new size of queue is: 0
SUCCESS: Worker 2 received the job from the queue!
ATTENTION: Worker 2 starts reading...
ATTENTION: Worker 5 stops reading...
ATTENTION: Dispatcher added an element to queue. Size of queue: 1
ATTENTION: Removing an element from queue. The new size of queue is: 0
SUCCESS: Worker 1 received the job from the queue!
ATTENTION: Worker 1 starts reading...
ATTENTION: Worker 2 stops reading...
ATTENTION: Worker 1 stops reading...
```

### Beispiel 20: Befehl ohne gestarteten Server


Die Benutzereingabe/Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java
*************************************************************************************
> read speiseplan,6
ERROR: java.net.SocketTimeoutException: Receive timed out
No connection to server available. The client timed out after 300000 milliseconds and will be closed now...
```

## Auswertung
Die passenden Ausgaben bzw. Fehlerausgaben zeigen, dass das Programm mit allen Eventualitäten klarkommt und somit die
Aufgabe entsprechend der Anforderungen erfüllt wurde. Die Testdateien sollten am Ende des Durchlaufs wie folgt
aussehen:

**Speiseplan.txt**
```java
Montag: Nix!
Dienstag: Salat
Mittwoch: ...mag keinen Salat, will lieber Eis!
Donnerstag: Brot und Aufschnitt
Freitag: Suppe
Samstag: Käsekuchen
Sonntag: Entenbraten
```

**Zahlen.txt**
```java
doppel null
 
dos
3
Vier
Fünf
Sechs
Sieben
Acht
Neun
10
Doppel Eins
...
```