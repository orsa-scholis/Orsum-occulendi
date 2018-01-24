"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var CTaskHandler = /** @class */ (function () {
    function CTaskHandler() {
        this.communicationTasks = new Array();
    }
    CTaskHandler.prototype.addCTask = function (ctask) {
        this.communicationTasks.push(ctask);
    };
    return CTaskHandler;
}());
exports.default = CTaskHandler;
