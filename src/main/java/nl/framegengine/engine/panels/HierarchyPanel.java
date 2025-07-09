package nl.framegengine.engine.panels;

import imgui.ImGui;
import nl.framegengine.engine.EditorPanel;

public class HierarchyPanel extends EditorPanel {
    public HierarchyPanel(float posX, float posY, float sizeX, float sizeY) {
        super(posX, posY, sizeX, sizeY);
    }

    @Override
    public void renderFrame() {
        ImGui.text("Hierarchy panel");
    }
}
