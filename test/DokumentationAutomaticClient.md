# Advanced IT - Testat 3
## Aufgabenstellung
Die Aufgabenstellung bleibt entsprechend der vorherigen Aufgabe erhalten.

## Umsetzung
In Addition zur bereits erfüllten Aufgabe mittels eines manuellen Clients, würde noch ein automatischerClient erstellt, 
welche nach Auswahl des Modus einige Testfälle automatisch durchspielt. Dies soll zeigen, dass das Programm entsprechend
der Aufgabenstellung funktioniert. Innerhalb dieser Dokumentation soll nun der automatische Client genauer beleuchtet 
werden. Dafür werden die Dateien `test1.txt` und `test2.txt`, welche im Ordner "_Test Files_" gefunden werden können, 
vorausgesetzt. Der automatische Client funktioniert auch ohne diese, wird jedoch hauptsächlich Fehlermeldungen
zurückgeben, da die automatisch erstellten Dateien leer sind, solange sie nicht durch die entsprechenden Kommandos 
beschrieben worden sind.

### Client.java


## Beispiele

**test1.txt**
```java
Hello!
I am a
test
file.
Nice to
meet you!



Lovely weather today,
isn't it? :)

...
```

**test2.txt**
```java
Lorem Ipsum
Lorem Ipsum
Lorem Ipsum
Lorem Ipsum
Lorem Ipsum
```

Auf die Serverausgabe in diesen Beispielfällen wird verzichtet, da sie repetitiv sind und im Zuge des automatischen 
Ablaufs der Befehle eher schlecht nachzuvollziehen sind.

Alle folgenden Beispiele finden mittels **Client-Modus 2** statt:
```java
Available mode:
1 - manual user input
2 - prepared automatic input
Please choose one of the modes above:
> 2
*************************************************************************************
ATTENTION: You have chosen mode 2! The automatic tests will start now...
*************************************************************************************
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


Die Benutzereingabe/Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java
*************************************************************************************
ATTENTION: Starting test for parallel reading from a file...
Send: <READ test1,1>
Send: <READ test1,2>
Send: <READ test1,3>
Send: <READ test1,4>
Send: <READ test1,5>
SUCCESS: Answer received: <BYE!>
SUCCESS: Answer received: <ERROR: READ failed - line 3 could not be found in file>
SUCCESS: Answer received: <ERROR: READ failed - line 2 could not be found in file>
SUCCESS: Answer received: <ERROR: READ failed - line 4 could not be found in file>
SUCCESS: Answer received: <ERROR: READ failed - line 5 could not be found in file>
*************************************************************************************
```

### Beispiel 2: Paralleles Lesen aus mehreren Dateien


Die Benutzereingabe/Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java
*************************************************************************************
ATTENTION: Starting test for parallel reading from different files...
Send: <READ test1,1>
Send: <READ test2,3>
Send: <READ test1,5>
SUCCESS: Answer received: <BYE!>
SUCCESS: Answer received: <ERROR: READ failed - line 5 could not be found in file>
SUCCESS: Answer received: <ERROR: READ failed - line 3 could not be found in file>
*************************************************************************************
```

### Beispiel 3: Paralleles Schreiben in eine Datei


Die Benutzereingabe/Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java
*************************************************************************************
ATTENTION: Starting test for parallel writing in different files...
Send: <WRITE test1,3,Random Data>
Send: <WRITE test2,5,Random Data in line 5>
SUCCESS: Answer received: <Overwritten to: Random Data in line 5>
SUCCESS: Answer received: <Overwritten to: Random Data>
*************************************************************************************
```

### Beispiel 4: Paralleles Schreiben in mehrere Dateien


Die Benutzereingabe/Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

### Beispiel 5: Paralleles Lesen und Schreiben in eine Datei


Die Benutzereingabe/Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

### Beispiel 6: Paralleles Lesen und Schreiben in mehrere Dateien


Client 1: Die Benutzereingabe/Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

Client 2: Die Benutzereingabe/Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

### Beispiel 7: Lesen aus einer nicht vorhandenen Datei


Die Benutzereingabe/Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

### Beispiel 8: Schreiben in eine nicht vorhandene Datei


Die Benutzereingabe/Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

### Beispiel 9: Lesen einer nicht vorhandenen Zeile


Die Benutzereingabe/Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

### Beispiel 10: Beschreiben einer nicht vorhandene Zeile


Die Benutzereingabe/Clientausgabe für dieses Beispiel sieht wie folgt aus:

```

### Beispiel 11: Überschreiben einer bereits vorhandenen Zeile


Die Benutzereingabe/Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

### Beispiel 12: Unvollständiger Lese-Befehl


Die Benutzereingabe/Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

### Beispiel 13: Unvollständiger Schreib-Befehl


Die Benutzereingabe/Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

### Beispiel 14: Unzulässiger Lese-Befehl


Die Benutzereingabe/Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

### Beispiel 15: Unzulässiger Schreib-Befehl


Die Benutzereingabe/Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

### Beispiel 16: Lesen einer negativen Zeilennummer


Die Benutzereingabe/Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

### Beispiel 17: Beschreiben einer negativen Zeilennummer


Die Benutzereingabe/Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

### Beispiel 18: Unbekannter Befehl


Die Benutzereingabe/Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

### Beispiel 19: Mehr Befehle als Worker


Die Benutzereingabe/Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

### Beispiel 20: Befehl ohne gestarteten Server


Die Benutzereingabe/Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java
Available mode:
1 - manual user input
2 - prepared automatic input
Please choose one of the modes above:
> 2
*************************************************************************************
ATTENTION: You have chosen mode 2! The automatic tests will start now...
*************************************************************************************
ATTENTION: Starting test for reading parallel from a file...
Send: <READ test1,1>
Send: <READ test1,2>
Send: <READ test1,3>
Send: <READ test1,4>
Send: <READ test1,5>
ERROR: java.net.SocketTimeoutException: Receive timed out
No connection to server available. The client will be closed now...
```


## Auswertung