<?php
/**
 * Created by PhpStorm.
 * User: Philipp
 * Date: 07/05/2017
 * Time: 23:28
 */

namespace communication;


abstract class CommunicationDomain
{
    const connection = "connection";
    const chat = "chat";
    const info = "info";
    const game = "game";
    const server = "server";

    private static function getConsts()
    {
        $o = new \ReflectionClass(__CLASS__);
        return $o->getConstants();
    }

    public static function verifyDomain($domain)
    {
        foreach (self::getConsts() as $value)
        {
            if($value === $domain)
            {
                return true;
            }
        }
        return false;
    }
}
