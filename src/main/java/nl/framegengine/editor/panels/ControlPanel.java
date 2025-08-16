package nl.framegengine.editor.panels;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import nl.framegengine.editor.EditorPanel;

public class ControlPanel extends EditorPanel {

    private GamePanel gamePanel;

    public ControlPanel(int posX, int posY, int sizeX, int sizeY) {
        super(posX, posY, sizeX, sizeY);
        addWindowFlag(ImGuiWindowFlags.NoTitleBar);
        addWindowFlag(ImGuiWindowFlags.NoScrollbar);
        addWindowFlag(ImGuiWindowFlags.NoScrollWithMouse);
    }

    @Override
    public void renderFrame() {
        ImGui.setCursorPos((float) sizeX / 2 - 32 - 64, 6);
        if(ImGui.button("Play", 64f, sizeY - 12f)){
            if(gamePanel != null) gamePanel.startGame();
        }

        ImGui.setCursorPos((float) sizeX / 2 - 32 + 64, 6);
        if(ImGui.button("Stop", 64f, sizeY - 12f)){
            if(gamePanel != null) gamePanel.stopGame();
        }
    }

    public void setGamePanel(GamePanel gamePanel){
        this.gamePanel = gamePanel;
    }
}
