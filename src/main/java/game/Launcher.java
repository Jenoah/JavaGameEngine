package game;

import nl.jenoah.core.EngineManager;
import nl.jenoah.core.WindowManager;
import nl.jenoah.core.utils.Constants;
import org.lwjgl.Version;

public class Launcher {
    private static WindowManager window;
    private static TestGame game;

    public static void main(String[] args){
        System.out.println(Version.getVersion());

        window = new WindowManager(Constants.TITLE, 1280, 720, false);
        game = new TestGame();
        EngineManager engine = new EngineManager();

        try{
            engine.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static WindowManager getWindow(){
        return window;
    }

    public static TestGame getGame() {
        return game;
    }
}
