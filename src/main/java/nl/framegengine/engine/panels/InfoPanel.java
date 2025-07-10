package nl.framegengine.engine.panels;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import nl.framegengine.engine.EditorPanel;
import nl.jenoah.core.entity.GameObject;

public class InfoPanel extends EditorPanel {

    private GameObject currentlySelectedObject = null;

    public InfoPanel(int posX, int posY, int sizeX, int sizeY) {
        super(posX, posY, sizeX, sizeY);
    }

    @Override
    public void renderFrame() {
        if(currentlySelectedObject == null) return;

        ImGui.setWindowFontScale(2f);
        ImGui.text(currentlySelectedObject.getName());
        ImGui.newLine();

        drawOption("Position", currentlySelectedObject.getPosition().toString());
        drawOption("Rotation", currentlySelectedObject.getEulerAngles().toString());

        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 1f, 1f, 0f, 1f);
        ImGui.pushStyleColor(ImGuiCol.Text, .2f, .2f, .2f, 1f);
        if(currentlySelectedObject.isDrawDebugWireframe()){
            ImGui.pushStyleColor(ImGuiCol.Button, 0f, 1f, 0f, 1f);
            if(ImGui.button("Debugging frame")){
                currentlySelectedObject.setDrawDebugWireframe(false);
            }
        }else{
            ImGui.pushStyleColor(ImGuiCol.Button, 1f, 0f, 0f, 1f);
            if(ImGui.button("Debugging frame")){
                currentlySelectedObject.setDrawDebugWireframe(true);
            }
        }
        ImGui.popStyleColor(3);
    }

    public void setCurrentlySelectedObject(GameObject gameObject){
        currentlySelectedObject = gameObject;
    }

    private void drawOption(String title, String content){
        ImGui.setWindowFontScale(1.5f);
        ImGui.text(title);
        ImGui.setWindowFontScale(1.1f);
        ImGui.text(content);
        ImGui.newLine();
    }
}