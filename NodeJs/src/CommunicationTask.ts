import { Message } from './Message'

export default class CommunicationTask {
    completed: boolean = false

    constructor(private message:Message) {}

}
