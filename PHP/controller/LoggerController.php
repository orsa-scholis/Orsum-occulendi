<?php
/**
 * Created by PhpStorm.
 * User: Philipp
 * Date: 05/05/2017
 * Time: 15:56
 */

namespace controller;


class LoggerController
{
    private $loggingLevel = 0; # 0 = No Logging, 1 = Only Server Logging, 2 = Server and Communicator Logging, 3 = All Logging (+Player +Game)

    public function __construct($log)
    {
        $this->loggingLevel = $log;
    }

    /**
     * @param $loggingL 0 = No Logging, 1 = Only Server Logging, 2 = Server and Communicator Logging, 3 = All Logging (+Player +Game)
     * @param $who
     * @param $what
     * @param $infos
     */
    public function log($loggingL, $who, $what, $infos)
    {
        if($this->loggingLevel >= $loggingL)
        {
            printf("<--| Who: %s | What: %s | Infos: %s |-->%s", $who, $what, $infos, "\n");
        }
    }


}