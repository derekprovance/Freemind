# Use-Cases
## Inhalt
- [Diagramm](https://github.com/Horstexplorer/Freemind/blob/master/DATA/usecases.md#diagramm)
- [UseCases](https://github.com/Horstexplorer/Freemind/blob/master/DATA/usecases.md#usecases)
- [Beschreibungen](https://github.com/Horstexplorer/Freemind/blob/master/DATA/usecases.md#beschreibungen)

## Diagramm
![Image](https://raw.githubusercontent.com/Horstexplorer/Freemind/master/DATA/UCs.jpg)

## UseCases
### Dateiverwaltung
- erstellen
- öffnen
    - importieren
- speichern
    - exportieren
### Contentverwaltung
- Notizen bearbeiten
    - Design ändern
- erstellen / bearbeiten / entfernen von Nodes
    - parent
    - child
    - sibling
- Design anpassen

## Beschreibungen
### Dateiverwaltung
#### erstellen
```
Kurzbeschreibung:	Das Programm erstellt dem Nutzer eine neue Datei, in der er eine MindMap anlegen kann. Dabei wird auch eine Root-Node automatisch generiert.
```
#### öffnen
```
```
#### speichern
```
Kurzbeschreibung:	Das Programm stellt dem Nutzer diese Funktion zum Speichern seines Arbeitsfortschritts zur Verfügung. 
Vorbedingung:		Es gibt eine geöffnete Datei.
Nachbedingung:	An der Struktur der MindMap wurde nichts verändert.
Primärer Aktor:		Nutzer.
Erfolgsszenario:		
    1.	Der Nutzer wählt die Funktion Datei Speichern des Programms.
    2.	Das Programm öffnet ein Fenster zur Auswahl des Speicherorts und Festlegung eines Namens.
    3.	Das Programm speichert die Datei unter den gewünschten Parametern.
    4.	Das Happy Day Szenario endet erfolgreich.
Erweiterungen:		
    1a.	Der Nutzer wählt die Funktion Datei exportieren und ein anderes Dateiformat als das Arbeitsformat.
        1a.1.	Weiter mit 2.
    2a.	Das Programm stellt fest, dass die MindMap bereits mit gewünschten Parametern gespeichert wurde und nicht die Funktion Speichern unter verwendet.
        2a.1.	Das Programm speichert die Datei unter denselben Parametern wie zuvor.
        2a.2.	Das Happy Day Szenario endet erfolgreich.
```
### Contentverwaltung
#### Notizen bearbeiten
```
Kurzbeschreibung: 	Der Nutzer fügt einem bestehenden Knoten eine Notiz hinzu, auf die er im Nachhinein zugreifen kann.
Vorbedingung:	Es existiert ein Baum mit einem Root-Knoten.
Nachbedingung:	Der Baum ist unbeeinflusst und immer noch intakt; besitzt der Knoten eine Notiz bekommt dieser zur Übersichtlichkeit ein Icon an den Titel.
Primärer Aktor:	Benutzer
Erfolgsszenario:
    1.	Nutzer wählt den Knoten aus, an dem er die Notiz bearbeiten möchte.
    2.	Der Nutzer bearbeitet eine bereits vorhandene Notiz an dem ausgewählten Knoten.
    3.	Das Programm ordnet die eingegebenen Informationen dem Knoten zu.
    4.	Das Happy Day Szenario endet erfolgreich.
Erweiterungen:	
    2a.	Das Programm stellt fest, dass das Notizfeld bei Auswahl des Knotens keine Information enthält.
        2a.1.	Der Nutzer gibt seine Informationen zu dem ausgewählten Knoten in das Notizfeld ein.
        2a.2.	Das Programm fügt vor den Titel des Knotens ein Icon an, welches eine dazugehörige Notiz symbolisiert.
        2a.3.	Weiter mit 3.
    3a.	Das Programm stellt fest, dass das Notizfeld nach der Bearbeitung keine Zeichen mehr enthält.
        3a.1.	Das Programm entfernt das Icon zur Angabe einer Notiz.
        3a.2.	Weiter mit4.. (Das Happy Day Szenario endet erfolgreich)
```
#### erstellen von Nodes (child)
```
Kurzbeschreibung:	Der Nutzer fügt dem Baum bzw. der Mindmap einen neuen Knoten hinzu.
Vorbedingung:		Es existiert ein Baum mit einem Root-Knoten.
Nachbedingung:	Es existiert ein gültiger Baum.
Primärer Aktor:		Nutzer. 
Erfolgsszenario	:	
    1.	Der Nutzer wählt einen Knoten und die Funktion Kind Knoten erstellen des Programms.
    2.	Das Programm erstellt einen neuen Knoten mit dem zuvor gewählten Knoten als Elternknoten.
    3.	Das Programm bietet dem Nutzer die Möglichkeit, einen Titel für den neuen Knoten festzulegen.
    4.	Das Happy Day Szenario endet erfolgreich.
```
#### erstellen von Nodes (parent)
```
Kurzbeschreibung:	Der Nutzer fügt dem Baum bzw. der Mindmap einen neuen Knoten hinzu.
Vorbedingung:		Es existiert ein Baum mit einem Root-Knoten.
Nachbedingung:	Es existiert ein gültiger Baum.
Primärer Aktor:		Nutzer. 
Erfolgsszenario:
    1.	Der Nutzer wählt einen Knoten und die Funktion Elternknoten erstellen des Programms.
    2.	Das Programm erstellt einen Knoten mit demselben Elternknoten wie der gewählte. Der vorher ausgewählte Knoten wird dem neuen Knoten nachgeordnet.
    3.	Das Programm bietet dem Nutzer die Möglichkeit, einen Titel für den neuen Knoten festzulegen.
    4.	Das Happy Day Szenario endet erfolgreich.
Erweiterungen:	
    2||a.	Das Programm stellt fest, dass der gewählte Knoten der Root-Knoten des Programms ist.
        2||a.1.	Das Programm nimmt den neu erstellten Knoten als neuen Root Knoten für die MindMap.
```
#### erstellen von Nodes (sibling)
```
Kurzbeschreibung:	Der Nutzer fügt dem Baum bzw. der Mindmap einen neuen Knoten hinzu, der gleichberechtigt mit einem anderen ist.
Vorbedingung:		Es existiert ein Baum mit einem Root-Knoten.
Nachbedingung:	Es existiert ein gültiger Baum.
Primärer Aktor:		Nutzer. 
Erfolgsszenario:
    1.	Der Nutzer wählt einen Knoten und die Funktion Geschwisterknoten erstellen des Programms.
    2.	Das Programm erstellt einen Knoten mit demselben Elternknoten wie der gewählte. Der neue Knoten wird dabei unterhalb des zuvor ausgewählten angezeigt.
    3.	Das Programm bietet dem Nutzer die Möglichkeit, einen Titel für den neuen Knoten festzulegen.
    4.	Das Happy Day Szenario endet erfolgreich.
Ergänzungen:
    1a.	Der Nutzer wählt einen Startknoten und die Funktion vorherigen Geschwisterknoten erstellen.
        1a.1.	Das Programm erstellt einen neuen Knoten mit demselben Elternknoten wie dem gewählten. Der neue Knoten wird oberhalb des zuvor gewählten angezeigt.
        1a.2.	Weiter mit 3.
    2a. = 1a.1a.    Das Programm stellt fest, dass der gewählte Knoten der Root-Knoten ist. Der neue Knoten wird nun als Kind-Knoten generiert.
```
#### bearbeiten von Nodes
```
Kurzbeschreibung:   Das Programm bietet dem Nutzer die Möglichkeit, den Namen eines ausgewählten Knotens zu ändern. Dafür stellt das Programm ein Textbearbeitungstool zur Verfügung, welches auch verschiedene Formatierungen zulässt.
Vorbedingung:       Es existiert ein Baum
Nachbedingung:      Der gewünschte Knoten wurde bearbeitet
Primärer Aktor: 	Nutzer des Programms.
Erfolgsszenario:
    1. Nutzer wählt den zu bearbeitenden Knoten aus.
    2. Nutzer wählt die gewünschte Attribut aus (Icons, Name, etc)
    3. Nutzer verändert entsprechendes Attribut
    4. Nutzer verlässt den Knoten und schließt somit die Bearbeitung ab
    5.	Das Happy Day Szenario endet erfolgreich.
Erweiterungen:
    4.a Der Nutzer wiederhohlt Schritt 3 und 4 beliebig oft
        4.a.1 weiter mit Schritt 5
```
#### entfernen von Nodes
```
Kurzbeschreibung:	Der Nutzer löscht einen Knoten aus dem Graphen der Mind Map. Dabei werden alle folgenden (Kind-Knoten) ebenfalls gelöscht.
Vorbedingung: 		Es existiert ein Baum mit mindestens zwei Knoten.
Nachbedingung: 	Der gewünschte und alle nachfolgenden Knoten sind aus dem Projekt entfernt, der folgende Zweig ist komplett entfernt und der Root-Knoten ist weiterhin existent.
Primärer Aktor: 	Nutzer des Programms.
Erfolgsszenario:
    1.	Nutzer wählt den zu löschenden Knoten aus.
    2.	Nutzer wählt Funktion zum Löschen des Knotens aus.
    3.	Programm stellt sicher, dass der ausgewählte Knoten nicht dem Root-Knoten entspricht.
    4.	Nutzer validiert die Ausführung der Aktion.
    5.	Das Programm entfernt den ausgewählten und alle nachfolgenden Knoten.
    6.	Das Happy Day Szenario endet erfolgreich.
Erweiterungen:
    3a.	Programm stellt fest, dass der ausgewählte Knoten der Root-Knoten ist.
        3a.1.	Das Programm teilt dem Nutzer mit, dass der Root-Knoten nicht entfernt werden kann.
        3a.2.	Das Happy Day Szenario endet erfolglos.
    4a.	Der Nutzer negiert die Ausführung der Entfernung.
        4a.1.	Das Happy Day Szenario endet erfolglos.
```
#### Design anpassen
```
Kurzbeschreibung:	Das Programm bietet dem Nutzer verschiedene Möglichkeiten, um das Design einer Node anzupassen. Dazu kann sowohl die Schrift der Node als auch dessen Form und Farbe verändert werden. Weiterhin können Tags gesetzt werden, welche durch ein Icon vor dem Namen angezeigt werden. Diese erleichtern die Übersichtlichkeit.
```
