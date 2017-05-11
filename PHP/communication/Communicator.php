<?php
/**
 * Created by PhpStorm.
 * User: Philipp
 * Date: 07/05/2017
 * Time: 23:23
 */

namespace communication;

use controller\LoggerController;
use controller\PlayerController;

class Communicator
{
    private $logC;
    private $player;
    private $sendTasks = array();
    private $receiveTasks = array();

    function __construct($logL, PlayerController $player)
    {
        $this->logC = new LoggerController($logL);
        $this->player = $player;
    }

    public function start()
    {
        $this->inputListener();
        $this->outputHandler();
    }

    private function log($what, $infos){
        $this->logC->log(2, "Communicator from ".$this->player->getModel()->name,$what, $infos);
    }

    public function addTask(CommunicationTask $task)
    {
        if($task->type)
        {
            array_push($this->sendTasks, $task);
            $this->log("Received new Send Task", "Message: ".$task->getFullMessage()." | Encrypted Message: "."I AM THE ENCRYPTED MESSAGE");
        }
        else
        {
            array_push($this->receiveTasks, $task);
            $this->log("Received new Receive Task", "Message: ".$task->getFullMessage()." | Encrypted Message: "."I AM THE ENCRYPTED MESSAGE");
        }
    }

    private function getCurrentTask($type)
    {
        $list = array();
        if($type){
            $list = $this->sendTasks;
        } else
        {
            $list = $this->receiveTasks;
        }
        foreach ($list as $task)
        {
            if(!$task->completed){
                return $task;
            }
        }
        return false;
    }

    private function inputListener()
    {
        while($this->player->getModel()->connected)
        {
            echo socket_read($this->player->getModel()->playerSocket, 18);
        }
    }

    private function outputHandler()
    {
        while($this->player->getModel()->connected)
        {
            if($task = $this->getCurrentTask() !== false)
            {
                $this->log("Current task", $task->getFullMessage());
            }
        }
    }
}