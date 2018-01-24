import CommunicationTask from './CommunicationTask'

export default class CTaskHandler {

    private communicationTasks: CommunicationTask[] = new Array();

    addCTask(ctask: CommunicationTask) {
        this.communicationTasks.push(ctask)
    }

}
