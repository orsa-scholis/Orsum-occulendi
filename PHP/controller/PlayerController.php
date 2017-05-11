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

    public function __construct($logL, $name, $server)
    {
        $this->model = new PlayerModel($logL, $name, new Communicator($logL, $this), $server);
    }

    private function connection($socket)
    {
        $this->log("Waiting for connection...");
        socket_listen($socket, 5);
        $client = socket_accept($socket);
        socket_set_blocking($client, false);
        $this->log("Connection successful");
        $this->model->connected = true;
        $this->model->playerSocket = $client;
        $this->model->com->start();

    }

    public function log($what, $infos="")
    {
        $this->model->logC->log(3, $this->model->name, $what, $infos);
    }

    public function start()
    {
        $this->connection($this->model->serverC->model->socket);
        $this->model->com->addTask(new CommunicationTask(CommunicationDomain::connection, "connect", "", CommunicationType::receive));


        $this->model->serverC->newPlayer();
    }

    public function getModel()
    {
        return $this->model;
    }

}