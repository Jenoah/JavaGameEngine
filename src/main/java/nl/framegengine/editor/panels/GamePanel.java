package nl.framegengine.editor.panels;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import nl.framegengine.editor.EditorGameLauncher;
import nl.framegengine.editor.EditorPanel;
import nl.framegengine.editor.EditorWindow;
import nl.framegengine.editor.EngineSettings;
import nl.framegengine.core.EngineManager;
import nl.framegengine.core.WindowManager;
import nl.framegengine.core.debugging.Debug;

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

        ImGui.setCursorPos((float) sizeX / 2 - 64, sizeY - 64 - 32);
        if(ImGui.button("Play", 64f, 64f)){
            startGame();
        }

        ImGui.setCursorPos((float) sizeX / 2 + 64, sizeY - 64 - 32);
        if(ImGui.button("Stop", 64f, 64f)){
            stopGame();
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

    private void stopGame(){
        if(editorGameLauncher != null){
            editorGameLauncher.stop();
            editorGameLauncher = null;
            EditorWindow.getInstance().resetGameFBOID();
        }
    }
}
