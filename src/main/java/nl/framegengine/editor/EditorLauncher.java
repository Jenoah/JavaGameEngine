package nl.framegengine.editor;

public class EditorLauncher{
    public static void main(String[] args){
        EditorWindow editorWindow = new EditorWindow();
        editorWindow.init();
        editorWindow.setEditorLayout(new EditorLayout());
        editorWindow.run();
        editorWindow.cleanUp();
    }
}
