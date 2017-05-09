<?php
/**
 * Created by PhpStorm.
 * User: Philipp
 * Date: 08/05/2017
 * Time: 00:08
 */

namespace model;


class ServerModel
{
    public $loggingLevel;
    public $port;
    public $socket;

    function __construct($socket, $logL = 0, $port = 4560)
    {
        $this->loggingLevel = $logL;
        $this->port = $port;
        $this->socket = $socket;
    }
}