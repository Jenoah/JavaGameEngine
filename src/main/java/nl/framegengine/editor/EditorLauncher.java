package nl.framegengine.editor;

public class EditorLauncher{
    public static void main(String[] args){
        EngineSettings.loadEngineConfig();
        EngineSettings.loadSettings();

        EditorWindow editorWindow = new EditorWindow();
        editorWindow.init();
        editorWindow.setEditorLayout(new EditorLayout());
        editorWindow.run();
        editorWindow.cleanUp();
        System.exit(0);
    }
}
