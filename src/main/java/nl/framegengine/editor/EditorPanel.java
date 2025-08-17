package nl.framegengine.editor;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;

public abstract class EditorPanel {

    protected int posX;
    protected int posY;
    protected int sizeX;
    protected int sizeY;
    protected String windowName;
    protected int windowFlags;
    protected boolean inFocus;


    public EditorPanel(int posX, int posY, int sizeX, int sizeY){
        this.posX = posX;
        this.posY = posY;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.windowName = getClass().getSimpleName();
        this.windowFlags = ImGuiWindowFlags.NoMove |
                ImGuiWindowFlags.NoResize |
                ImGuiWindowFlags.NoCollapse;
    }

    public void prepareFrame(){
        ImGui.setNextWindowPos(posX, posY);
        ImGui.setNextWindowSize(sizeX, sizeY);
        ImGui.begin(windowName, windowFlags);
    }

    public abstract void renderFrame();

    public void endFrame(){
        ImGui.end();
    }

    public void addWindowFlag(int windowFlags){
        this.windowFlags = this.windowFlags | windowFlags;
    }

    public void setSizeAndPosition(int posX, int posY, int sizeX, int sizeY){
        this.posX = posX;
        this.posY = posY;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }
}
