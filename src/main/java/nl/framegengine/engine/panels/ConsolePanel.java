package nl.framegengine.engine.panels;

import imgui.ImGui;
import nl.framegengine.engine.EditorPanel;
import nl.jenoah.core.debugging.ConsoleColors;
import nl.jenoah.core.debugging.Debug;

public class ConsolePanel extends EditorPanel {

    public ConsolePanel(float posX, float posY, float sizeX, float sizeY) {
        super(posX, posY, sizeX, sizeY);
    }

    @Override
    public void renderFrame() {
        ImGui.text("Console Panel");
        ImGui.setWindowFontScale(1.5f);
        for (Debug.LogEntry entry : Debug.GetLog()) {
            for (ConsoleColors seg : entry.segments) {
                ImGui.textColored(seg.color[0], seg.color[1], seg.color[2], seg.color[3], seg.text);
                ImGui.sameLine(0, 0); // No spacing between segments
            }
            ImGui.newLine();
        }
        ImGui.setScrollHereY(1.0f);
    }
}