export enum MessageType {
    send = 1,
    receive = 2
}

export enum MessageDomain {
    Connection = "connection",
    receive = 2
}

export enum MessageHead {
    Connect = "connect",
    receive = 2
}

export class Message {
    constructor(private type: MessageType, private domain: MessageDomain, private head: MessageHead, private content: string) {

    }

    isSend():boolean {
        return this.type === MessageType.send
    }

    isReceive(): boolean {
        return this.type === MessageType.receive
    }

    static parseRawData(data: any)
    {
        console.log(data);
    }
}
