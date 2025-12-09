Tento projekt predstavuje vzdelávaciu platformu, kde sa študenti môžu učiť spoločne v skupinách, riešiť úlohy a pod.
Projekt je realizáciou REST API a preto je rozdelený na dve samostatné časti – Frontend a Backend (klient a server).
Klient je implementovaný ako desktopová aplikácia JavaFX a server je vytvorený pomocou Spring Boot. Aplikácia využíva:

Docker: spúšťanie aplikácie a testov v kontajneri
Websocket: Real-time notifikacie
JUnit(a ostatne) : Testy
Spring Security: na realizáciu relačnej (session) autorizácie, čo síce porušuje princíp stateless, ale v rámci 
vzdelávacieho projektu a vzhľadom na to, že klientom je „hrubá“ JavaFX aplikácia (a nie napríklad React), je to 
úplne prijateľné a prípustné – a mnohé API sú takto navrhnuté.

