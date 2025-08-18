package nl.framegengine.editor.panels;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import nl.framegengine.editor.EditorPanel;

public class ControlPanel extends EditorPanel {

    private GamePanel gamePanel;
    private String[] aspectRatios = new String[]{"16 x 9", "16 x 10", "Freeform"};
    private ImInt currentAspectRatio = new ImInt(0);

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

        ImGui.setCursorPos((float) sizeX / 2 +32, 6);
        if(ImGui.button("Stop", 64f, sizeY - 12f)){
            if(gamePanel != null) gamePanel.stopGame();
        }

        ImGui.setCursorPos((float) sizeX / 2 + 128, sizeY / 2 - 9);
        if(ImGui.combo("Aspect ratio", currentAspectRatio, aspectRatios)){
            switch (currentAspectRatio.get()){
                case 0:
                    gamePanel.setAspectRatio(16f/9f);
                    break;
                case 1:
                    gamePanel.setAspectRatio(16f/10f);
                    break;
                default:
                    gamePanel.setAspectRatio(0);
                    break;
            }
        }
    }

    public void setGamePanel(GamePanel gamePanel){
        this.gamePanel = gamePanel;
    }
}
