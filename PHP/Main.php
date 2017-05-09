<?php
/**
 * Created by PhpStorm.
 * User: Philipp
 * Date: 05/05/2017
 * Time: 16:23
 */

error_reporting(E_ALL);
set_time_limit(0);
ob_implicit_flush();

spl_autoload_register(function($class) {
    $filename = __DIR__ . '/' . str_replace("\\", "/", $class) . '.php';

    if(!file_exists($filename)) {
        return false; // End autoloader function and skip to the next if available.
    }

    include $filename;
    return true; // End autoloader successfully.

});

$serverC = new controller\ServerController();
$serverC->start();
