package nl.framegengine.editor;

import nl.framegengine.core.debugging.Debug;
import nl.framegengine.core.utils.FileHelper;
import nl.framegengine.core.utils.JsonHelper;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.File;
import java.io.StringReader;

public class EngineSettings {
    public static String currentProjectDirectory = "";
    public static String currentLevelPath = "";
    public static String currentProjectName = "Unknown project";

    private static final String settingsFileName = "/.fgsettings";

    public static void saveSettings(){
        JsonObjectBuilder jsonSaveContent = Json.createObjectBuilder();
        jsonSaveContent.add("currentLevelPath", currentLevelPath);

        JsonObject jsonSaveContentObject = jsonSaveContent.build();
        FileHelper.writeToFile(jsonSaveContentObject.toString(), currentProjectDirectory + settingsFileName);
    }

    public static void loadSettings() {
        if(currentProjectDirectory.isEmpty() || !new File(currentProjectDirectory).exists()) return;
        String saveFileContent = FileHelper.readFile(currentProjectDirectory + settingsFileName);
        if(saveFileContent == null) {
            Debug.LogError("No settings file has been found. Creating...");
            saveSettings();
            return;
        }

        JsonObject projectInfo = Json.createReader(new StringReader(saveFileContent)).readObject();

        if (JsonHelper.hasJsonKey(projectInfo, "currentLevelPath")) currentLevelPath = projectInfo.getString("currentLevelPath");
        currentProjectName = FileHelper.getDirectoryName(currentProjectDirectory);

        Debug.Log("Project settings successfully loaded in");
    }

    public static void createNewProject(){
        String projectDirectory = FileHelper.selectDirectory();
        if(projectDirectory == null){
            Debug.LogError("Project directory is not a valid path");
            return;
        }
        EngineSettings.currentProjectDirectory = projectDirectory;
        EngineSettings.saveSettings();

        try {
            FileHelper.copyResourceToDirectory("default project/", projectDirectory);
        } catch (Exception e) {
            Debug.Log("Something went wrong trying to create the project: " + e.getMessage());
        }
        Debug.Log("Creating new project at " + projectDirectory);
    }

    public static void loadProject(){
        String projectDirectory = FileHelper.selectDirectory();
        if(projectDirectory == null){
            Debug.LogError("Project directory is not a valid path");
            return;
        }
        EngineSettings.currentProjectDirectory = projectDirectory;
        EngineSettings.loadSettings();
    }
}
