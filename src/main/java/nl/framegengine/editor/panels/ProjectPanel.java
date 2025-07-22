package nl.framegengine.editor.panels;

import imgui.ImGui;
import imgui.flag.ImGuiSelectableFlags;
import nl.framegengine.editor.EditorPanel;
import nl.framegengine.editor.EngineSettings;
import nl.framegengine.core.debugging.Debug;
import nl.framegengine.core.utils.FileHelper;

import java.io.File;
import java.io.IOException;

public class ProjectPanel extends EditorPanel {

    public ProjectPanel(int posX, int posY, int sizeX, int sizeY) {
        super(posX, posY, sizeX, sizeY);
    }

    @Override
    public void renderFrame() {
        ImGui.setWindowFontScale(1.2f);
        if(new File(EngineSettings.currentProjectDirectory).exists()) printFolderStructure(EngineSettings.currentProjectDirectory, EngineSettings.currentProjectName, 0);
    }

    private void printFolderStructure(String dir, String folderName, int level){

        File[] projectFoldersAndFiles = FileHelper.listDirectoryAndFiles(dir);
        if(projectFoldersAndFiles.length == 0) return;

        if(ImGui.collapsingHeader(folderName)) {
            ImGui.indent(10 * level);
            for (File projectFoldersAndFile : projectFoldersAndFiles) {
                if (projectFoldersAndFile.getName().startsWith(".")) continue;
                if (projectFoldersAndFile.isDirectory()) {
                    printFolderStructure(projectFoldersAndFile.getAbsolutePath(), projectFoldersAndFile.getName(), level + 1);
                } else {
                    if(ImGui.selectable(projectFoldersAndFile.getName(), false, ImGuiSelectableFlags.AllowDoubleClick)){
                        if(ImGui.isMouseDoubleClicked(0)) {
                            selectFile(projectFoldersAndFile);
                        }
                    }
                }
            }
            ImGui.unindent(10 * level);
        }
    }

    private void selectFile(File selectedFile){
        Debug.Log("Selecting " + selectedFile.getName());
        try {
            String extension = FileHelper.getExtension(selectedFile.getName());
            switch (extension){
                case "lvl":
                    Debug.Log("Loading level " + selectedFile.getName());
                    EngineSettings.currentLevelPath = new File(EngineSettings.currentProjectDirectory).toURI().relativize(selectedFile.toURI()).getPath();
                    EngineSettings.saveSettings();
                    break;
                default:
                    FileHelper.openFile(selectedFile);
                    break;
            }
        } catch (IOException e) {
            Debug.LogError("Cannot open file: " + e.getMessage());
        }
    }
}
