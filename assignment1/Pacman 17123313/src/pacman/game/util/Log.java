// 
// Decompiled by Procyon v0.5.36
// 

package pacman.game.util;

import java.util.Date;

public class Log
{
    private static String fileName;
    private static Log log;
    private StringBuilder msg;
    private boolean timeStamp;
    private boolean console;
    
    private Log() {
        this.msg = new StringBuilder();
        Log.fileName = "log.txt";
        this.timeStamp = false;
        this.console = false;
    }
    
    public static Log getLog() {
        if (Log.log == null) {
            Log.log = new Log();
        }
        return Log.log;
    }
    
    public void enableConsolePrinting() {
        this.console = true;
    }
    
    public void disableConsolePrinting() {
        this.console = false;
    }
    
    public void setFile(final String fileName) {
        Log.fileName = fileName;
    }
    
    public void enableTimeStamp() {
        this.timeStamp = true;
    }
    
    public void disableTimeStamp() {
        this.timeStamp = false;
    }
    
    public void log(final Object context, final String message) {
        if (this.timeStamp) {
            final String string = "[" + new Date().toString() + "; " + context.getClass().toString() + "]\t" + message;
            this.msg.append(string);
            if (this.console) {
                System.out.println(string);
            }
        }
        else {
            final String string = "[" + context.getClass().toString() + "]\t" + message;
            this.msg.append(string);
            if (this.console) {
                System.out.println(string);
            }
        }
    }
    
    public void clear() {
        this.msg = new StringBuilder();
    }
    
    public void saveLog(final boolean append) {
        IO.saveFile(Log.fileName, this.msg.toString(), append);
    }
    
    static {
        Log.log = null;
    }
}
