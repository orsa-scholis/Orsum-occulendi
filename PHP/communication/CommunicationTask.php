<?php
/**
 * Created by PhpStorm.
 * User: Philipp
 * Date: 07/05/2017
 * Time: 23:24
 */

namespace communication;


class CommunicationTask
{
    public $completed;
    public $domain;
    public $command;
    public $attr;
    public $type;

    function __construct($domain, $command, $attr, $type)
    {
        $this->domain = $domain;
        $this->command = $command;
        $this->attr = $attr;
        $this->type = $type;
    }

    public function getFullMessage()
    {
        return $this->domain.":".$this->command.":".$this->attr;
    }

}