<?php
/**
 * Created by PhpStorm.
 * User: Philipp
 * Date: 05/05/2017
 * Time: 15:56
 */

namespace controller;


use communication\CommunicationDomain;
use communication\CommunicationTask;
use communication\CommunicationType;
use communication\Communicator;
use model\PlayerModel;

class PlayerController
{
    private $model;

    public function __construct($logL, $name, $server, $socket)
    {

        $this->model = new PlayerModel($logL, $name, new Communicator($logL, $this), $server);
        $this->log("Waiting for connection...", "");
        socket_listen($socket, 5);
        $client = socket_accept($socket);
        $this->log("Waiting for connection...", "");
        $this->model->playerSocket = $client;
    }

    public function log($what, $infos)
    {
        $this->model->logC->log(3, $this->model->name, $what, $infos);
    }

    public function start()
    {
        $this->log("Started", "ME!");
        $this->model->com->addTask(new CommunicationTask(CommunicationDomain::connection, "connect", "", CommunicationType::receive));
    }

    public function getModel()
    {
        return $this->model;
    }

}