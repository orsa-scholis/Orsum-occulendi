# Orsum Inflandi “Das aufgeblasene Vorhaben” (Vier Gewinnt und Orsum-ludi-terroris mit PHP und Java über AES)

Das Vier-Gewinnt Spiel auf Server-Client basis. Der Server hält das Spiel und einzelne Clients können über einen Server zusammen spielen. Dabei wird die ganze Kommunikation mit AES verschlüsselt.

## Setup
Der Client ist ein eclipse-Projekt. Nach dem Klonen kann man einfach unter 'Import' -> 'Eclipse Projekt in Workspace importieren' das Projekt in die IDE laden. 
Der Server ist ein PHPStorm-Projekt. Nach dem Klonen kann man das Projekt mit PHPStorm öffnen.

## Aufbau
Das Client-Projekt hat ein default-package mit einer "Main" Klasse. Diese Klasse ist eine ausführbare Klasse, mit der man dann über die Konsole auswählen kann, ob man den alten Java-Server, einen Client oder die Testing-CLI starten möchte. 

## Der Client
Der Client ist in dem Package 'client' zu finden. Er besitzt eine eigene Main-Klasse, die man separat starten kann. 

## Der Server
Der alte Java-Server ist in dem Package server zu finden. Auch er hat eine eigene Main-Klasse, die jedoch nur eine Kommandozeilenapplikation startet. 
Der neue PHP-Server hat die Main.php, die weiterhin nur eine Kommandozeilenapplikation startet.

## Die Verschlüsselung
Alle für die Verschlüsselung wesentliche Pakete sind in dem Package 'crypto' zu finden. Dieses ist in ein Subpackage 'aes' und 'rsa' sowie 'cli' unterteilt
Bei PHP verwenden wird die OpenSSL Library.

#### AES
Im AES Paket befindet sich die eigene Implementation des AES Algorithmus. AES wird für die symmetrische Verschlüsselung zwischen Client und Server verwendet.

#### RSA
In dem RSA Paket befindet sich ein Wrapper, welcher die für den Handshake zwischen Server und Client zentralen Methoden bereitstellt.

#### CLI
In dem CLI Paket sind einige Klassen für das Testen des AES Algorithmus vorhanden.
