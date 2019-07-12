# CDIO-Gruppe2

Vi har som projekt at skulle lave en LEGO robot som kan indsamle bordtennisbolde på en bane uden af ramme forhindringer og kanten af banen. 
Boldene skal ind i et af to mål med forskellige størrelser, det lille mål giver flere point.
Projektet går ud på at lave få styr på projektstyring med en gruppe på 8 personer, og samtidig løse problemstillingen.

The robots final lap can be seen here: https://www.youtube.com/watch?v=WGuEuml-Kc4&

#Trouble shooting
Hvis det ikke virker efter kloning prøv dette:

 - Højre klik på projekt-mappen og og vælg "add Framework support" og herefter vælg "Maven"
 - Hvis klasserne ikke er genkendt som java class, højre klik på mappen de ligger i og vælg "Mark Directory as -> Sources Root"
 
 #Sæt op banen
 For at sætte banen op skal du gøre følgende:
 - Sæt den globale værdi "RUN_INFINITLY" til TRUE i ComputerVision
 - Kør programmet
 - Din cursors placering på computerskærmen vil blive noteret i consollen. Placer din mus på midten af de sorte punkter på hjørnerne og noter placeringen
 - Indsæt disse noter i de globale punkter "topLeft", "topRight", "botLeft" og "botRight"
 - Stop programmets kørsel
 - Sæt den globale værdi "RUN_INFINITLY" til FALSE i ComputerVision.
 
