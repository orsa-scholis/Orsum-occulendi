export enum LogLevel {
    None = 0,
    Connection = 1,
    Communication = 2,
    All = 3
}

export class LogController
{
	public static setLogLevel(ll: LogLevel): any {
		LogController.logLevel = ll
	}
    static logLevel:number = LogLevel.None;

    /**
     * Logs to console if the LogLevel is high enough
     * @method log
     * @param  logLevel   The LogLevel of the message
     * @param  sender     The log initiater
     * @param  message    The log message
     * @param  additional Additional info
     */
    public static log(logLevel: number, sender: string, message: string, additional?: string)
    {
        if(LogController.logLevel >= logLevel)
            console.log(sender+": "+message+((additional) ? " and "+additional : ""))
            // +"\n"
    }
}
