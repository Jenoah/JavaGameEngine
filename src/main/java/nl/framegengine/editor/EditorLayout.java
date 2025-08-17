package nl.framegengine.editor;

import imgui.ImGui;
import nl.framegengine.editor.panels.*;

public class EditorLayout {
    private final EditorPanel[] editorPanels = new EditorPanel[6];

    public EditorLayout(){
        editorPanels[0] = new HierarchyPanel(0, 18, fromPercentageX(15), fromPercentageY(70));
        editorPanels[1] = new GamePanel(fromPercentageX(15), 18, fromPercentageX(70), fromPercentageY(60));
        editorPanels[2] = new ConsolePanel(fromPercentageX(15), fromPercentageY(70), fromPercentageX(85), fromPercentageY(30));
        editorPanels[3] = new InfoPanel(fromPercentageX(85), 18, fromPercentageX(15), fromPercentageY(70));
        editorPanels[4] = new ProjectPanel(0, fromPercentageY(70), fromPercentageX(15), fromPercentageY(30));
        editorPanels[5] = new ControlPanel(fromPercentageX(15), fromPercentageY(60), fromPercentageX(70), fromPercentageY(10));

        ((HierarchyPanel)editorPanels[0]).setInfoPanel(((InfoPanel)editorPanels[3]));
        ((ControlPanel)editorPanels[5]).setGamePanel((GamePanel)editorPanels[1]);
    }

    public void recalculatePanels(){
        editorPanels[0].setSizeAndPosition(0, 18, fromPercentageX(15), fromPercentageY(70));
        editorPanels[1].setSizeAndPosition(fromPercentageX(15), 18, fromPercentageX(70), fromPercentageY(60));
        editorPanels[2].setSizeAndPosition(fromPercentageX(15), fromPercentageY(70), fromPercentageX(85), fromPercentageY(30));
        editorPanels[3].setSizeAndPosition(fromPercentageX(85), 18, fromPercentageX(15), fromPercentageY(70));
        editorPanels[4].setSizeAndPosition(0, fromPercentageY(70), fromPercentageX(15), fromPercentageY(30));
        editorPanels[5].setSizeAndPosition(fromPercentageX(15), fromPercentageY(60), fromPercentageX(70), fromPercentageY(10));
    }

    private int fromPercentageX(int absoluteX){
        return (int)(EditorWindow.windowWidth / 100f * absoluteX / EditorWindow.windowScaleX);
    }

    private int fromPercentageY(int absoluteY){
        return (int)(EditorWindow.windowHeight / 100f * absoluteY / EditorWindow.windowScaleY);
    }

    public void renderLayout(){
        for (EditorPanel editorPanel : editorPanels) {
            editorPanel.prepareFrame();
            editorPanel.renderFrame();
            editorPanel.endFrame();
        }
        renderMenuBar();
    }

    private void renderMenuBar(){
        ImGui.beginMainMenuBar();
        if(ImGui.beginMenu("Project")){
            if(ImGui.menuItem("New")) EngineSettings.createNewProject();
            if(ImGui.menuItem("Open")) EngineSettings.loadProject();
            if(ImGui.menuItem("Save")) EngineSettings.saveSettings();
            ImGui.endMenu();
        }
        ImGui.endMainMenuBar();
    }
}
