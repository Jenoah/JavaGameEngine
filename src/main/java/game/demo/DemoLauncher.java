package game.demo;

import nl.jenoah.core.EngineManager;
import nl.jenoah.core.Settings;
import nl.jenoah.core.WindowManager;
import nl.jenoah.core.utils.Constants;
import org.lwjgl.Version;

public class DemoLauncher {
    private static DemoGame game;

    public static void main(String[] args){
        System.out.println(Version.getVersion());

        WindowManager.createInstance(Constants.TITLE, 1280, 720, Settings.isUseVSync(), true);
        game = new DemoGame();
        EngineManager engine = new EngineManager();

        try{
            engine.start(game, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DemoGame getGame() {
        return game;
    }
}
