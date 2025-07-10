package nl.framegengine.engine.panels;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import nl.framegengine.engine.EditorPanel;
import nl.jenoah.core.entity.GameObject;
import nl.jenoah.core.entity.SceneManager;

public class HierarchyPanel extends EditorPanel {
    private final ImVec2 buttonSize;
    private final ImVec4 activeButtonTextColor = new ImVec4(1f, 1f, 1f, 1f);
    private final ImVec4 inactiveButtonTextColor = new ImVec4(.75f, .75f, .75f, 1f);
    private final ImVec4 selectedButtonTextColor = new ImVec4(1, .5f, .5f, 1f);

    private final ImVec4 standardButtonBackgroundColor = new ImVec4(1f, 1f, 1f, 0f);
    private final ImVec4 hoverButtonBackgroundColor = new ImVec4(0f, 0f, 0f, 1f);
    private InfoPanel infoPanel;
    private GameObject currentlySelectedGameObject = null;

    public HierarchyPanel(int posX, int posY, int sizeX, int sizeY) {
        super(posX, posY, sizeX, sizeY);
        buttonSize = new ImVec2(sizeX, 20);
    }

    @Override
    public void renderFrame() {
        ImGui.setWindowFontScale(1.1f);

        if(SceneManager.getInstance() == null || SceneManager.getInstance().getCurrentScene() == null) return;
        ImGui.pushStyleColor(ImGuiCol.Button, standardButtonBackgroundColor);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, hoverButtonBackgroundColor);
        ImGui.pushStyleVar(ImGuiStyleVar.ButtonTextAlign, 0f, 0.5f);
        SceneManager.getInstance().getCurrentScene().getRootGameObjects().forEach(go -> {
            if(go.getParent() == null) {
                if(currentlySelectedGameObject == go){
                    ImGui.pushStyleColor(ImGuiCol.Text, selectedButtonTextColor);
                    if(ImGui.button(go.getName(), buttonSize)){
                        infoPanel.setCurrentlySelectedObject(go);
                        currentlySelectedGameObject = go;
                    }
                }else if(!go.isEnabled()){
                    ImGui.pushStyleColor(ImGuiCol.Text, inactiveButtonTextColor);
                    if(ImGui.button(go.getName(), buttonSize)){
                        infoPanel.setCurrentlySelectedObject(go);
                        currentlySelectedGameObject = go;
                    }
                }else{
                    ImGui.pushStyleColor(ImGuiCol.Text, activeButtonTextColor);
                    if(ImGui.button(go.getName(), buttonSize)){
                        infoPanel.setCurrentlySelectedObject(go);
                        currentlySelectedGameObject = go;
                    }
                }

                ImGui.popStyleColor();

                go.getChildren().forEach(child -> {
                    if(currentlySelectedGameObject == child){
                        ImGui.pushStyleColor(ImGuiCol.Text, selectedButtonTextColor);
                        if(ImGui.button("- " + child.getName(), buttonSize)){
                            infoPanel.setCurrentlySelectedObject(child);
                            currentlySelectedGameObject = child;
                        }
                    }else if(!child.isEnabled()){
                        ImGui.pushStyleColor(ImGuiCol.Text, inactiveButtonTextColor);
                        if(ImGui.button("- " + child.getName(), buttonSize)){
                            infoPanel.setCurrentlySelectedObject(child);
                            currentlySelectedGameObject = child;
                        }
                    }else{
                        ImGui.pushStyleColor(ImGuiCol.Text, activeButtonTextColor);
                        if(ImGui.button("- " + child.getName(), buttonSize)){
                            infoPanel.setCurrentlySelectedObject(child);
                            currentlySelectedGameObject = child;
                        }
                    }
                    ImGui.popStyleColor();
                });
            }
        });
        ImGui.popStyleColor(2);
        ImGui.popStyleVar();
    }

    public void setInfoPanel(InfoPanel infoPanel){
        this.infoPanel = infoPanel;
    }
}
