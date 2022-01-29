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

### Beispiel 1: Paralleles Lesen aus der gleichen Datei


```java
*************************************************************************************
ATTENTION: Starting test for reading parallel from a file...
Send: <READ test1,2>
Send: <READ test1,3>
Send: <READ test1,4>
Send: <READ test1,5>
SUCCESS: Answer received: <Nice to>
SUCCESS: Answer received: <New data in line 5>
SUCCESS: Answer received: <New data 1>
SUCCESS: Answer received: <I am>
*************************************************************************************
```


```java

```

### Beispiel 2: Paralleles Lesen aus der gleichen Datei

```java
*************************************************************************************
ATTENTION: Starting test for writing into the same file...
Send: <WRITE test1,3,New data 1>
Send: <WRITE test1,5,New data in line 5>
SUCCESS: Answer received: <New data 1>
SUCCESS: Answer received: <New data in line 5>
*************************************************************************************
```


```java

```


### Beispiel 3: Paralleles Lesen aus der gleichen Datei

```java
*************************************************************************************
ATTENTION: Starting test for writing in different files...
Send: <WRITE test1,3,New data 1>
Send: <WRITE test2,5,New data in line 5>
SUCCESS: Answer received: <New data 1>
SUCCESS: Answer received: <New data in line 5>
*************************************************************************************
```

### Beispiel 4: Paralleles Lesen aus der gleichen Datei

```java
*************************************************************************************
ATTENTION: Starting test for Writer Priority...
Send: <READ test1,2>
Send: <WRITE test1,3,New data 1>
Send: <READ test1,3>
Send: <READ test1,4>
Send: <WRITE test1,5,New data in line 5>
Send: <READ test1,5>
SUCCESS: Answer received: <New data 1>
SUCCESS: Answer received: <I am>
SUCCESS: Answer received: <Nice to>
SUCCESS: Answer received: <New data 1>
SUCCESS: Answer received: <New data in line 5>
SUCCESS: Answer received: <New data in line 5>
*************************************************************************************
```

### Beispiel 5: Paralleles Lesen aus der gleichen Datei

```java
*************************************************************************************
ATTENTION: Starting test for reading parallel from a file and writing into another file...
Send: <READ test1,2>
Send: <WRITE test2,3,Trying to access in parallel...>
Send: <READ test1,5>
SUCCESS: Answer received: <New data in line 5>
SUCCESS: Answer received: <I am>
SUCCESS: Answer received: <Trying to access in parallel...>
*************************************************************************************
```

### Beispiel 6: Paralleles Lesen aus der gleichen Datei

```java
*************************************************************************************
ATTENTION: Starting test with more requests than worker...
Send: <READ test1,2>
Send: <READ test1,3>
Send: <READ test1,4>
Send: <READ test1,5>
Send: <READ test1,6>
Send: <READ test1,7>
Send: <READ test1,8>
Send: <READ test1,9>
Send: <READ test1,10>
Send: <READ test1,11>
Send: <READ test1,12>
Send: <READ test1,13>
SUCCESS: Answer received: <New data 1>
SUCCESS: Answer received: <New data in line 5>
SUCCESS: Answer received: <Nice to>
SUCCESS: Answer received: <I am>
SUCCESS: Answer received: <ERROR: READ failed - line 6 could not be found in file>
SUCCESS: Answer received: <ERROR: READ failed - line 9 could not be found in file>
SUCCESS: Answer received: <ERROR: READ failed - line 8 could not be found in file>
SUCCESS: Answer received: <ERROR: READ failed - line 7 could not be found in file>
SUCCESS: Answer received: <ERROR: READ failed - line 10 could not be found in file>
SUCCESS: Answer received: <ERROR: READ failed - line 11 could not be found in file>
SUCCESS: Answer received: <ERROR: READ failed - line 13 could not be found in file>
SUCCESS: Answer received: <ERROR: READ failed - line 12 could not be found in file>
*************************************************************************************
```

### Beispiel 7: Paralleles Lesen aus der gleichen Datei

```java
*************************************************************************************
ATTENTION: Starting test for reading from different files...
Send: <READ test1,2>
Send: <READ test2,3>
Send: <READ test1,5>
SUCCESS: Answer received: <Trying to access in parallel...>
SUCCESS: Answer received: <I am>
SUCCESS: Answer received: <New data in line 5>
*************************************************************************************
```

### Beispiel 8: Paralleles Lesen aus der gleichen Datei

```java
*************************************************************************************
ATTENTION: Starting test for reading a non existent line...
Send: <READ test1,50>
SUCCESS: Answer received: <ERROR: READ failed - line 50 could not be found in file>
*************************************************************************************
```

## Auswertung