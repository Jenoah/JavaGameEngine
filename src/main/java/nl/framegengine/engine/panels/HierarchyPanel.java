package nl.framegengine.engine.panels;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.flag.ImGuiCol;
import nl.framegengine.engine.EditorPanel;
import nl.jenoah.core.debugging.Debug;
import nl.jenoah.core.entity.SceneManager;

public class HierarchyPanel extends EditorPanel {
    private final ImVec2 buttonSize;
    private final ImVec4 activeButtonTextColor = new ImVec4(1f, 1f, 1f, 1f);
    private final ImVec4 inactiveButtonTextColor = new ImVec4(.75f, .75f, .75f, 1f);
    private final ImVec4 debuggingButtonTextColor = new ImVec4(1, .5f, .5f, 1f);

    private final ImVec4 standardButtonBackgroundColor = new ImVec4(1f, 1f, 1f, 0f);
    private final ImVec4 hoverButtonBackgroundColor = new ImVec4(0f, 0f, 0f, 1f);

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
        SceneManager.getInstance().getCurrentScene().getRootGameObjects().forEach(go -> {
            if(go.getParent() == null) {
                if(!go.isEnabled()){
                    ImGui.pushStyleColor(ImGuiCol.Text, inactiveButtonTextColor);
                    ImGui.button(go.getName(), buttonSize);
                    ImGui.popStyleColor();
                }else{
                    if(go.isDrawDebugWireframe()){
                        ImGui.pushStyleColor(ImGuiCol.Text, debuggingButtonTextColor);
                        if(ImGui.button(go.getName(), buttonSize)){
                            go.setDrawDebugWireframe(false);
                            Debug.Log("Debugging disabled for " + go.getName());
                        }
                    }else{
                        ImGui.pushStyleColor(ImGuiCol.Text, activeButtonTextColor);
                        if(ImGui.button(go.getName(), buttonSize)){
                            go.setDrawDebugWireframe(true);
                            Debug.Log("Debugging enabled for " + go.getName());
                        }
                    }
                    ImGui.popStyleColor();
                }
                go.getChildren().forEach(child -> {
                    if(!child.isEnabled()){
                        ImGui.pushStyleColor(ImGuiCol.Text, inactiveButtonTextColor);
                        ImGui.button("- " + child.getName(), buttonSize);
                        ImGui.popStyleColor();
                    }else{
                        ImGui.pushStyleColor(ImGuiCol.Text, activeButtonTextColor);
                        ImGui.button("- " + child.getName(), buttonSize);
                        ImGui.popStyleColor();
                    }
                });
            }
        });
        ImGui.popStyleColor(2);
    }
}
