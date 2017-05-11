<?php
/**
 * Created by PhpStorm.
 * User: Philipp
 * Date: 07/05/2017
 * Time: 22:43
 */

namespace model;


use communication\Communicator;
use controller\LoggerController;
use controller\ServerController;

class PlayerModel
{
    public $name = "";

    public $playerSocket;

    public $connected = false;
    public $inGame = false;
    public $playing = false;

    public $com;

    public $gameC;
    public $logC;
    public $serverC;


    public function __construct($logL, $name, Communicator $communicator, ServerController $server)
    {
        $this->name = $name;
        $this->com = $communicator;
        $this->logC = new LoggerController($logL);
        $this->serverC = $server;
        $this->connected = true;
    }
}