package nl.framegengine.engine.panels;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import nl.framegengine.engine.EditorGameLauncher;
import nl.framegengine.engine.EditorPanel;
import nl.framegengine.engine.EditorWindow;
import nl.framegengine.engine.EngineSettings;
import nl.jenoah.core.EngineManager;
import nl.jenoah.core.WindowManager;
import nl.jenoah.core.debugging.Debug;

public class GamePanel extends EditorPanel {

    private EditorGameLauncher editorGameLauncher;

    public GamePanel(int posX, int posY, int sizeX, int sizeY) {
        super(posX, posY, sizeX, sizeY);
        addWindowFlag(ImGuiWindowFlags.NoNavFocus);
    }

    @Override
    public void prepareFrame(){
        ImGui.setNextWindowPos(posX, posY);
        ImGui.setNextWindowSize(sizeX, sizeY);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, new ImVec2(0, 0));
        ImGui.begin(windowName, windowFlags);
        ImGui.popStyleVar();
    }

    @Override
    public void renderFrame() {
        if(editorGameLauncher != null) editorGameLauncher.render();

        if(EditorWindow.getInstance().getGameFBOID() != -1){
            ImGui.image(EditorWindow.getInstance().getGameFBOID(), sizeX, sizeY - 20, 0, 1, 1, 0);
            inFocus = ImGui.isItemHovered();
        }

        ImGui.setCursorPos((float) sizeX / 2 - 32, sizeY - 64 - 32);
        if(ImGui.button("Play", 64f, 64f)){
            startGame();
        }

        ImGui.setCursorPos(8, 24);
        ImGui.text("FPS: " + EngineManager.getFps());
    }

    @Override
    public void endFrame() {
        super.endFrame();
        if (WindowManager.getInstance() != null) {
            WindowManager.getInstance().setFocus(inFocus);
        }
    }

    private void startGame(){
        if(EngineSettings.currentLevelPath.isEmpty()){
            Debug.LogError("Cannot start game. No level selected");
            return;
        }
        if(editorGameLauncher == null) {
            editorGameLauncher = new EditorGameLauncher();
            editorGameLauncher.run(sizeX, sizeY - 20);
        }
    }
}
