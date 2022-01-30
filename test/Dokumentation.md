# Advanced IT - Testat 3
## Aufgabenstellung


## Umsetzung


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
if (s == null || s.equalsIgnoreCase("EXIT"))
```
wird diese Funktionalität jedoch wie auch in der vergangenen Testataufgabe unterstützt. Auch die Eingaben der Dateinamen
sind nicht von Groß- und Kleinschreibung abhängig, sofern eine Datei nicht neu erstellt werden muss, um in diese 
schreiben zu können. Existiert die zu beschreibende Datei noch nicht, so wird eine mit der entsprechenden Groß- und 
Kleinschreibung, wie im Befehl angegeben, angelegt. Der Zugriff erfolgt jedoch "non case sensitive". Auf der anderen 
Seite erfolgt das Schreiben der Daten in eine Datei immer "case sensitive".



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

Diese können im Ordner "_Test Files_" gefunden werden. Für individuelle Testfälle können aber natürlich auch andere Textdateien über _das Programm_ oder _manuell_ angelegt werden.

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


Client 1: Die Benutzereingabe für dieses Beispiel sieht wie folgt aus:
```java

```

Client 1: Die Ausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

Client 2: Die Benutzereingabe für dieses Beispiel sieht wie folgt aus:
```java

```

Client 2: Die Ausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

Die Serverausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

### Beispiel 2: Paralleles Lesen aus mehreren Dateien


Client 1: Die Benutzereingabe für dieses Beispiel sieht wie folgt aus:
```java

```

Client 1: Die Ausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

Client 2: Die Benutzereingabe für dieses Beispiel sieht wie folgt aus:
```java

```

Client 2: Die Ausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

Die Serverausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

### Beispiel 3: Paralleles Schreiben in eine Datei


Client 1: Die Benutzereingabe für dieses Beispiel sieht wie folgt aus:
```java

```

Client 1: Die Ausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

Client 2: Die Benutzereingabe für dieses Beispiel sieht wie folgt aus:
```java

```

Client 2: Die Ausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

Die Serverausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

### Beispiel 4: Paralleles Schreiben in mehrere Dateien


Client 1: Die Benutzereingabe für dieses Beispiel sieht wie folgt aus:
```java

```

Client 1: Die Ausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

Client 2: Die Benutzereingabe für dieses Beispiel sieht wie folgt aus:
```java

```

Client 2: Die Ausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

Die Serverausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

### Beispiel 5: Paralleles Lesen und Schreiben in eine Datei


Client 1: Die Benutzereingabe für dieses Beispiel sieht wie folgt aus:
```java

```

Client 1: Die Ausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

Client 2: Die Benutzereingabe für dieses Beispiel sieht wie folgt aus:
```java

```

Client 2: Die Ausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

Die Serverausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

### Beispiel 6: Paralleles Lesen und Schreiben in mehrere Dateien


Client 1: Die Benutzereingabe für dieses Beispiel sieht wie folgt aus:
```java

```

Client 1: Die Ausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

Client 2: Die Benutzereingabe für dieses Beispiel sieht wie folgt aus:
```java

```

Client 2: Die Ausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

Die Serverausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

### Beispiel 7: Lesen aus einer nicht vorhandenen Datei


Die Benutzereingabe für dieses Beispiel sieht wie folgt aus:
```java

```

Die Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

Die Serverausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

### Beispiel 8: Schreiben in eine nicht vorhandene Datei


Die Benutzereingabe für dieses Beispiel sieht wie folgt aus:
```java

```

Die Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

Die Serverausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

### Beispiel 9: Lesen einer nicht vorhandenen Zeile


Die Benutzereingabe für dieses Beispiel sieht wie folgt aus:
```java

```

Die Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

Die Serverausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

### Beispiel 10: Beschreiben einer nicht vorhandene Zeile


Die Benutzereingabe für dieses Beispiel sieht wie folgt aus:
```java

```

Die Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

Die Serverausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

### Beispiel 11: Überschreiben einer bereits vorhandenen Zeile


Die Benutzereingabe für dieses Beispiel sieht wie folgt aus:
```java

```

Die Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

Die Serverausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

### Beispiel 12: Unvollständiger Lese-Befehl


Die Benutzereingabe für dieses Beispiel sieht wie folgt aus:
```java

```

Die Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

Die Serverausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

### Beispiel 13: Unvollständiger Schreib-Befehl


Die Benutzereingabe für dieses Beispiel sieht wie folgt aus:
```java

```

Die Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

Die Serverausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

### Beispiel 14: Unzulässiger Lese-Befehl


Die Benutzereingabe für dieses Beispiel sieht wie folgt aus:
```java

```

Die Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

Die Serverausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

### Beispiel 15: Unzulässiger Schreib-Befehl


Die Benutzereingabe für dieses Beispiel sieht wie folgt aus:
```java

```

Die Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

Die Serverausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

### Beispiel 16: Lesen einer negativen Zeilennummer


Die Benutzereingabe für dieses Beispiel sieht wie folgt aus:
```java

```

Die Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

Die Serverausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

### Beispiel 17: Beschreiben einer negativen Zeilennummer


Die Benutzereingabe für dieses Beispiel sieht wie folgt aus:
```java

```

Die Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

Die Serverausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

### Beispiel 18: Unbekannter Befehl


Die Benutzereingabe für dieses Beispiel sieht wie folgt aus:
```java

```

Die Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

Die Serverausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

### Beispiel 19: Befehl ohne gestarteten Server


Die Benutzereingabe für dieses Beispiel sieht wie folgt aus:
```java

```

Die Clientausgabe für dieses Beispiel sieht wie folgt aus:
```java

```

## Auswertung
Da für diese Testataufgabe viele Testfälle gefunden werden können, erlaube ich es mir nur eine Gesamtauswertung zu 
verfassen. Sollte natürlich genauere Erklärungen nötig sein, so sind diese im entsprechenden Beispiel vermerkt oder 
in der Code-Dokumentation findbar.


