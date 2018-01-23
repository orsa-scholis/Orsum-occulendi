"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
// Load the TCP Library
var net = require("net");
var Client_1 = require("./Client");
var logcontroller_1 = require("./logcontroller");
logcontroller_1.LogController.setLogLevel(logcontroller_1.LogLevel.All);
var Server = /** @class */ (function () {
    function Server(port) {
        this.clients = new Array();
        this.port = 4560;
        if (port)
            this.port = port;
        this.netserver = net.createServer(this.handleConnection.bind(this));
    }
    Server.prototype.start = function () {
        var _this = this;
        this.netserver.listen(this.port, function () {
            logcontroller_1.LogController.log(1, "Server", "Running at port " + _this.port);
        });
    };
    Server.prototype.handleConnection = function (clientS) {
        var client = new Client_1.default(clientS, logcontroller_1.LogController.logLevel);
        this.clients.push(client);
    };
    Server.prototype.removeClient = function () {
    };
    return Server;
}());
exports.default = Server;
