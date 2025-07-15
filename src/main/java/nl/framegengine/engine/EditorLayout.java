package nl.framegengine.engine;

import nl.framegengine.engine.panels.ConsolePanel;
import nl.framegengine.engine.panels.GamePanel;
import nl.framegengine.engine.panels.HierarchyPanel;
import nl.framegengine.engine.panels.InfoPanel;

public class EditorLayout {
    private final EditorPanel[] editorPanels = new EditorPanel[4];

    public EditorLayout(){
        editorPanels[0] = new HierarchyPanel(0, 0, (int)(384 / EditorWindow.windowScaleX), (int)(768 / EditorWindow.windowScaleY));
        editorPanels[1] = new GamePanel((int)(384 / EditorWindow.windowScaleX), 0, (int)(1200 / EditorWindow.windowScaleX), (int)(675 / EditorWindow.windowScaleY));
        editorPanels[2] = new ConsolePanel(0, (int)(768 / EditorWindow.windowScaleY), (int)(1584 / EditorWindow.windowScaleX), (int)(312 / EditorWindow.windowScaleY));
        editorPanels[3] = new InfoPanel((int)(1584 / EditorWindow.windowScaleX), 0, (int)(336 / EditorWindow.windowScaleX), (int)(1080 / EditorWindow.windowScaleY));

        ((HierarchyPanel)editorPanels[0]).setInfoPanel(((InfoPanel)editorPanels[3]));
    }

    public void renderLayout(){
        for (EditorPanel editorPanel : editorPanels) {
            editorPanel.prepareFrame();
            editorPanel.renderFrame();
            editorPanel.endFrame();
        }
    }
}
