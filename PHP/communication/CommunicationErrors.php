<?php
/**
 * Created by PhpStorm.
 * User: Philipp
 * Date: 07/05/2017
 * Time: 23:27
 */

namespace communication;


abstract class CommunicationErrors
{
    const unknownErr = "error:Unbekannte Anfrage";
    const nameTooShort = "error:Name zu kurz";
    const notYetConnected = "error:Noch nicht mit dem Server verbunden";
    const gameExists = "error:Ein Spiel mit dem selben Namen existier bereits, bitte wähle einene anderen Namen!";
    const gameFull = "error:Das Spiel ist bereits voll!";
    const emptyChatMessage = "error:Die Nachricht war leer";
}