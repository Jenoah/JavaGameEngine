package nl.framegengine.engine.panels;

import imgui.ImGui;
import nl.framegengine.engine.EditorGameLauncher;
import nl.framegengine.engine.EditorPanel;
import nl.framegengine.engine.EditorWindow;
import nl.jenoah.core.debugging.Debug;

public class GamePanel extends EditorPanel {

    private EditorGameLauncher editorGameLauncher;

    public GamePanel(float posX, float posY, float sizeX, float sizeY) {
        super(posX, posY, sizeX, sizeY);
    }

    @Override
    public void renderFrame() {
        if(editorGameLauncher != null) editorGameLauncher.render();

        if(EditorWindow.getInstance().getGameFBOID() != -1){
            ImGui.image(EditorWindow.getInstance().getGameFBOID(), 1200, 675, 0, 1, 1, 0);
        }else{
            ImGui.text("Game panel");
        }

        ImGui.setCursorPos(600, 579);
        if(ImGui.button("Play", 64f, 64f)){
            Debug.Log("Playing game");
            startGame();
        }
    }

    private void startGame(){
        if(editorGameLauncher == null) {
            editorGameLauncher = new EditorGameLauncher();
            editorGameLauncher.run();
        }
    }
}
