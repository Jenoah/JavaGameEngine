package nl.framegengine.engine;

public class EditorLauncher{
    public static void main(String[] args){
        EditorWindow editorWindow = new EditorWindow(new EditorLayout());
        editorWindow.init();
        editorWindow.run();
        editorWindow.cleanUp();
    }
}
