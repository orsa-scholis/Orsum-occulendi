"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var LogController_1 = require("./LogController");
var CommunicationTask_1 = require("./CommunicationTask");
var CTaskHandler_1 = require("./CTaskHandler");
var Message_1 = require("./Message");
var Client = /** @class */ (function () {
    function Client(socket, loglevel) {
        this.socket = socket;
        this.loglevel = loglevel;
        this.name = "";
        this.connected = false;
        this.aes = false;
        LogController_1.LogController.setLogLevel(this.loglevel);
        this.name = this.socket.remoteAddress + ":" + this.socket.remotePort;
        this.socket.on('data', this.onData.bind(this));
        this.socket.on('error', this.onError.bind(this));
        this.socket.on('close', this.onClose.bind(this));
        this.socket.on('end', this.onEnd.bind(this));
        this.ctasks = new CTaskHandler_1.default();
        var connectMessage = new Message_1.Message(Message_1.MessageType.receive, Message_1.MessageDomain.Connection, Message_1.MessageHead.Connect, "*");
        var connectTask = new CommunicationTask_1.default(connectMessage);
        this.ctasks.addCTask(connectTask);
    }
    Client.prototype.onData = function (data) {
        LogController_1.LogController.log(2, this.name, "received Data: " + data);
        if (!this.connected) {
            this.handleConnectionInit();
        }
        else if (!this.aes) {
        }
        else {
        }
    };
    Client.prototype.handleConnectionInit = function () {
        LogController_1.LogController.log(1, "New Client (" + this.name + ")", "connected");
        this.connected = true;
    };
    Client.prototype.onClose = function () {
        LogController_1.LogController.log(1, this.name, "closed connection");
    };
    Client.prototype.onEnd = function () {
        LogController_1.LogController.log(1, this.name, "ended connection");
    };
    Client.prototype.onError = function () {
        LogController_1.LogController.log(1, this.name, "caused an error");
    };
    return Client;
}());
exports.default = Client;
