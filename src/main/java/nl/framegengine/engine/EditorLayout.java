package nl.framegengine.engine;

import nl.framegengine.engine.panels.ConsolePanel;
import nl.framegengine.engine.panels.GamePanel;
import nl.framegengine.engine.panels.HierarchyPanel;
import nl.framegengine.engine.panels.InfoPanel;

public class EditorLayout {
    private final EditorPanel[] editorPanels = new EditorPanel[4];

    public EditorLayout(){
        editorPanels[0] = new HierarchyPanel(0, 0, 384, 768);
        editorPanels[1] = new GamePanel(384, 0, 1200, 675);
        editorPanels[2] = new ConsolePanel(0, 768, 1584, 312);
        editorPanels[3] = new InfoPanel(1584, 0, 336, 1080);

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
