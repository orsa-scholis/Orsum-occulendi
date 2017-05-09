<?php
/**
 * Created by PhpStorm.
 * User: Philipp
 * Date: 05/05/2017
 * Time: 15:56
 */

namespace controller;

use communication\CommunicationDomain;
use model\ServerModel;

class ServerController
{
    public $logC;
    public $model;
    private $players = array();
    private $games = array();

    public function __construct()
    {
        $serverS = socket_create(SOCK_STREAM, AF_INET6, SOL_TCP) or die("Der Server konnte nicht gestartet werden.");
        socket_bind($serverS, "5.189.174.198", "4560");
        $log = readline("Welches Logging-Level hättest du gerne? 0 - 3: ");
        $satisfied = false;
        while(!$satisfied)
        {
            if($log >= 0 && $log < 4)
            {
                $satisfied = true;
            }
        }
        $this->logC = new LoggerController($log);
        $this->model = new ServerModel($serverS, $log);
        $player = new PlayerController($log, "Player ".count($this->players), $this, $serverS);
        array_push($this->players, $player);


    }

    public function log($what, $infos="")
    {
        $this->logC->log(1, "Server", $what, $infos);
    }

    public function start()
    {
        $this->players[0]->start();
    }

    public function newPlayer()
    {
        $player = new PlayerController($this->model->loggingLevel, "Player ".count($this->players), $this, $this->model->socket);
        array_push($this->players, $player);
        $player->start();
    }

    /**
     * @param $gameName The name of the Game
     * @param int $gameTyp 0=4InARow, 1=.. | default = 0
     */
    public function newGame($gameName, $gameTyp=0)
    {
        switch ($gameTyp){
            case (0):
                $this->log("New Game ".$gameName, "GameType: ".(string)$gameTyp);
                break;
        }
    }


}