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
    private int aspectWidth = 0;
    private int aspectHeight = 0;
    private float aspectRatio = 1.7778f;

    public GamePanel(int posX, int posY, int sizeX, int sizeY) {
        super(posX, posY, sizeX, sizeY);
        recalculateResolution();

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

            ImVec2 avail = ImGui.getContentRegionAvail();

// Calculate offset to center image
            float offsetX = (avail.x - aspectWidth) / 2.0f;
            float offsetY = (avail.y - (aspectHeight - 20)) / 2.0f;

// Clamp offset to minimum 0 to avoid negative positions
            offsetX = Math.max(offsetX, 0);
            offsetY = Math.max(offsetY, 0);

// Set cursor position offset for rendering the image centered
            ImGui.setCursorPosX(ImGui.getCursorPosX() + offsetX);
            ImGui.setCursorPosY(ImGui.getCursorPosY() + offsetY);



            ImGui.image(EditorWindow.getInstance().getGameFBOID(), aspectWidth, aspectHeight - 20, 0, 1, 1, 0);
            inFocus = ImGui.isItemHovered();
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

    public void startGame(){
        if(EngineSettings.currentLevelPath.isEmpty()){
            Debug.LogError("Cannot start game. No level selected");
            return;
        }
        if(editorGameLauncher == null) {
            editorGameLauncher = new EditorGameLauncher();
            editorGameLauncher.run(sizeX, sizeY - 20);
        }
    }

    public void stopGame(){
        if(editorGameLauncher != null){
            editorGameLauncher.stop();
            editorGameLauncher = null;
            EditorWindow.getInstance().resetGameFBOID();
        }
    }

    public void setAspectRatio(float aspectRatio){
        this.aspectRatio = aspectRatio;
        recalculateResolution(true);
    }

    public void recalculateResolution(){
        recalculateResolution(false);
    }

    public void recalculateResolution(boolean refreshGameInstance) {
        if (aspectRatio <= 0) {
            aspectWidth = sizeX;
            aspectHeight = sizeY;
        }else {
            aspectWidth = Math.round(sizeY * aspectRatio);
            aspectHeight = sizeY;
            if (aspectWidth > sizeX) {
                aspectWidth = sizeX;
                aspectHeight = Math.round(sizeX / aspectRatio);
            }
        }

        if(refreshGameInstance && WindowManager.getInstance() != null){
            WindowManager.getInstance().setWindowSize(aspectWidth, aspectHeight);
        }
    }
}
