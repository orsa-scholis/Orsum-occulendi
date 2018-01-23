"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var MessageType;
(function (MessageType) {
    MessageType[MessageType["send"] = 1] = "send";
    MessageType[MessageType["receive"] = 2] = "receive";
})(MessageType = exports.MessageType || (exports.MessageType = {}));
var MessageDomain;
(function (MessageDomain) {
    MessageDomain["Connection"] = "connection";
    MessageDomain[MessageDomain["receive"] = 2] = "receive";
})(MessageDomain = exports.MessageDomain || (exports.MessageDomain = {}));
var MessageHead;
(function (MessageHead) {
    MessageHead["Connect"] = "connect";
    MessageHead[MessageHead["receive"] = 2] = "receive";
})(MessageHead = exports.MessageHead || (exports.MessageHead = {}));
var Message = /** @class */ (function () {
    function Message(type, domain, head, content) {
        this.type = type;
        this.domain = domain;
        this.head = head;
        this.content = content;
    }
    Message.prototype.isSend = function () {
        return this.type === MessageType.send;
    };
    Message.prototype.isReceive = function () {
        return this.type === MessageType.receive;
    };
    Message.parseRawData = function (data) {
        console.log(data);
    };
    return Message;
}());
exports.Message = Message;
