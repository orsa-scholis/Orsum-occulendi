<?php
/**
 * Created by PhpStorm.
 * User: Philipp
 * Date: 07/05/2017
 * Time: 23:28
 */

namespace communication;

abstract class CommunicationCommands
{
    const connect = 0x01;
    const server = 0x02;
    const game = 0x03;
    const chat = 0x04;
    const info = 0xfa;

    private static function getConsts()
    {
        $o = new \ReflectionClass(__CLASS__);
        return $o->getConstants();
    }

    public static function verifyCommand($command)
    {
        foreach (self::getConsts() as $value)
        {
            if($value === $command)
            {
                return true;
            }
        }
        return false;
    }
}
