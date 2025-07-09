package nl.framegengine.engine.panels;

import imgui.ImGui;
import nl.framegengine.engine.EditorPanel;

public class InspectorPanel extends EditorPanel {

    public InspectorPanel(int posX, int posY, int sizeX, int sizeY) {
        super(posX, posY, sizeX, sizeY);
    }

    @Override
    public void renderFrame() {
        ImGui.text("Inspector panel");
    }
}