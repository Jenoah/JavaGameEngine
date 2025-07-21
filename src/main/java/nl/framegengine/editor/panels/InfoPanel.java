package nl.framegengine.editor.panels;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import nl.framegengine.editor.EditorPanel;
import nl.framegengine.core.entity.GameObject;
import nl.framegengine.core.utils.ClassHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class InfoPanel extends EditorPanel {

    private GameObject currentlySelectedObject = null;
    private List<Field> hierarchyObjects = new ArrayList<>();

    public InfoPanel(int posX, int posY, int sizeX, int sizeY) {
        super(posX, posY, sizeX, sizeY);
    }

    @Override
    public void renderFrame() {
        if(currentlySelectedObject == null) return;

        ImGui.setWindowFontScale(2f);
        ImGui.text(currentlySelectedObject.getName());
        ImGui.newLine();

        for (Field field : hierarchyObjects) {
            try {
                field.setAccessible(true);
                Object value = field.get(currentlySelectedObject);
                if(value == null) continue;
                drawOption(field.getName(), value.toString());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

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
        hierarchyObjects.clear();
        ClassHelper.getAllProperties(hierarchyObjects, currentlySelectedObject.getClass());
    }

    private void drawOption(String title, String content){
        ImGui.setWindowFontScale(1.5f);
        ImGui.text(title);
        ImGui.setWindowFontScale(1.1f);
        ImGui.text(content);
        ImGui.setWindowFontScale(0.4f);
        ImGui.newLine();
        ImGui.setWindowFontScale(1f);
    }
}