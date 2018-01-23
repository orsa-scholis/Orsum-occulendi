"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var LogLevel;
(function (LogLevel) {
    LogLevel[LogLevel["None"] = 0] = "None";
    LogLevel[LogLevel["Connection"] = 1] = "Connection";
    LogLevel[LogLevel["Communication"] = 2] = "Communication";
    LogLevel[LogLevel["All"] = 3] = "All";
})(LogLevel = exports.LogLevel || (exports.LogLevel = {}));
var LogController = /** @class */ (function () {
    function LogController() {
    }
    LogController.setLogLevel = function (ll) {
        LogController.logLevel = ll;
    };
    /**
     * Logs to console if the LogLevel is high enough
     * @method log
     * @param  logLevel   The LogLevel of the message
     * @param  sender     The log initiater
     * @param  message    The log message
     * @param  additional Additional info
     */
    LogController.log = function (logLevel, sender, message, additional) {
        if (LogController.logLevel >= logLevel)
            console.log(sender + ": " + message + ((additional) ? " and " + additional : ""));
        // +"\n"
    };
    LogController.logLevel = LogLevel.None;
    return LogController;
}());
exports.LogController = LogController;
