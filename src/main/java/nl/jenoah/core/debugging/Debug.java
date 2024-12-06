package nl.jenoah.core.debugging;

public class Debug {

    public static void Log(String message){
        System.out.println("\u001B[35m" + "[Log] " + "\u001B[0m" + message);
    }
}
