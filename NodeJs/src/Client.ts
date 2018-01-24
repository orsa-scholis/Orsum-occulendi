// Load the TCP Library
import * as net from 'net';
import { LogController } from './LogController';
import CommunicationTask from './CommunicationTask'
import CTaskHandler from './CTaskHandler'
import { Message, MessageType, MessageDomain, MessageHead } from './Message'

class Client {
    private name: string = ""
    private connected: boolean = false;
    private aes: boolean = false;

    private ctasks: CTaskHandler;

    constructor(private socket: net.Socket, private loglevel:number) {
        LogController.setLogLevel(this.loglevel);

        this.name = this.socket.remoteAddress + ":" + this.socket.remotePort
        this.socket.on('data', this.onData.bind(this));
        this.socket.on('error', this.onError.bind(this));
        this.socket.on('close', this.onClose.bind(this));
        this.socket.on('end', this.onEnd.bind(this));

        this.ctasks = new CTaskHandler();

        let connectMessage = new Message(MessageType.receive, MessageDomain.Connection, MessageHead.Connect, "*")
        let connectTask = new CommunicationTask(connectMessage);
        this.ctasks.addCTask(connectTask)
    }

    onData(data: any) {
        LogController.log(2, this.name, "received Data: "+data)

        if(!this.connected) {
            this.handleConnectionInit()
        } else if(!this.aes) {

        } else {

        }
    }

    private handleConnectionInit() {
        

        LogController.log(1, "New Client ("+this.name+")", "connected")
        this.connected = true;
    }

    onClose() {
        LogController.log(1, this.name, "closed connection")
    }

    onEnd() {
        LogController.log(1, this.name, "ended connection")
    }

    onError() {
        LogController.log(1, this.name, "caused an error")
    }
}

export default Client;
