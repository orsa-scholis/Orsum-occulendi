// Load the TCP Library
import * as net from "net";
import Client from './Client';
import { LogController, LogLevel } from './logcontroller';

LogController.setLogLevel(LogLevel.All)

class Server {
    private netserver: any
    private clients: Client[] = new Array()
    private port: number = 4560

    constructor(port?: number) {
      if(port) this.port = port

      this.netserver = net.createServer(this.handleConnection.bind(this))
    }

    start() {
      this.netserver.listen(this.port, () => {
        LogController.log(1, "Server", "Running at port "+this.port);
      })
    }

    handleConnection(clientS: net.Socket) {
        let client = new Client(clientS, LogController.logLevel)
        this.clients.push(client)
    }

    removeClient() {

    }
  }

  export default Server
