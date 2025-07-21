package nl.framegengine.core;

import nl.framegengine.core.utils.Constants;
import org.lwjgl.Version;

public class Launcher {
    private static GameInstance game;

    public static void main(String[] args){
        System.out.println(Version.getVersion());

        WindowManager.createInstance(Constants.TITLE, 1280, 720, Settings.isUseVSync(), true);
        game = new GameInstance();
        EngineManager engine = new EngineManager();

        try{
            engine.start(game, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static GameInstance getGame() {
        return game;
    }
}
