package nl.framegengine.engine;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;

public abstract class EditorPanel {

    private final float posX;
    private final float posY;
    private final float sizeX;
    private final float sizeY;
    private final String windowName;


    public EditorPanel(float posX, float posY, float sizeX, float sizeY){
        this.posX = posX;
        this.posY = posY;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.windowName = getClass().getSimpleName();
    }

    public void prepareFrame(){
        ImGui.setNextWindowPos(posX, posY);
        ImGui.setNextWindowSize(sizeX, sizeY);
        ImGui.begin(windowName, ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoTitleBar);
    }

    public abstract void renderFrame();

    public void endFrame(){
        ImGui.end();
    }
}
