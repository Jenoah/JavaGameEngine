package nl.jenoah.core.debugging;

public class Debug {

    public static void Log(String message){
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        // Find the caller's class and line number (index 2 is the caller of Log())
        StackTraceElement caller = stackTrace[2];

        // Extract the class name and line number
        String className = caller.getClassName();
        int lineNumber = caller.getLineNumber();

        System.out.println("\u001B[35m" + "[Log] " + "\u001B[90m" + className + ":" + lineNumber + " \u001B[0m" + message);
    }
}
