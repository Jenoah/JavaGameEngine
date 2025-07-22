package nl.framegengine.editor;

import imgui.ImGui;
import nl.framegengine.editor.panels.*;

public class EditorLayout {
    private final EditorPanel[] editorPanels = new EditorPanel[5];

    public EditorLayout(){
        editorPanels[0] = new HierarchyPanel(0, 18, (int)(384 / EditorWindow.windowScaleX), (int)(768 / EditorWindow.windowScaleY));
        editorPanels[1] = new GamePanel((int)(384 / EditorWindow.windowScaleX), 18, (int)(1200 / EditorWindow.windowScaleX), (int)(675 / EditorWindow.windowScaleY));
        editorPanels[2] = new ConsolePanel((int)(528 / EditorWindow.windowScaleX), (int)(786 / EditorWindow.windowScaleY), (int)(1056 / EditorWindow.windowScaleX), (int)(294 / EditorWindow.windowScaleY));
        editorPanels[3] = new InfoPanel((int)(1584 / EditorWindow.windowScaleX), 18, (int)(336 / EditorWindow.windowScaleX), (int)(1062 / EditorWindow.windowScaleY));
        editorPanels[4] = new ProjectPanel(0, (int)(786 / EditorWindow.windowScaleY), (int)(528 / EditorWindow.windowScaleX), (int)(294 / EditorWindow.windowScaleY));

        ((HierarchyPanel)editorPanels[0]).setInfoPanel(((InfoPanel)editorPanels[3]));
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
