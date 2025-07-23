package nl.framegengine.editor;

import nl.framegengine.core.debugging.Debug;
import nl.framegengine.core.entity.SceneManager;
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
        if(SceneManager.getInstance() != null && SceneManager.getInstance().getCurrentScene() != null)
            FileHelper.writeToFile(SceneManager.sceneToJson(SceneManager.getInstance().getCurrentScene()), currentProjectDirectory + File.separator + currentLevelPath);
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
        saveEngineConfig();

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

    private static void saveEngineConfig(){
        String userHome = System.getProperty("user.home");
        File configDir = new File(userHome, ".framegengine");
        if (!configDir.exists()) {
            configDir.mkdirs();
        }
        File settingsFile = new File(configDir, "editorconfig.json");

        JsonObjectBuilder jsonSaveContent = Json.createObjectBuilder();
        jsonSaveContent.add("currentProjectDirectory", currentProjectDirectory);

        JsonObject jsonSaveContentObject = jsonSaveContent.build();
        FileHelper.writeToFile(jsonSaveContentObject.toString(), settingsFile.getAbsolutePath());
    }

    public static void loadEngineConfig(){
        String userHome = System.getProperty("user.home");
        File configDir = new File(userHome, ".framegengine");
        File settingsFile = new File(configDir, "editorconfig.json");

        String saveFileContent = FileHelper.readFile(settingsFile.getAbsolutePath());
        if(saveFileContent == null) {
            Debug.LogError("No editor config found. Creating...");
            saveEngineConfig();
            return;
        }

        JsonObject projectInfo = Json.createReader(new StringReader(saveFileContent)).readObject();

        if (!JsonHelper.hasJsonKey(projectInfo, "currentProjectDirectory")) return;

        currentProjectDirectory = projectInfo.getString("currentProjectDirectory");
        currentProjectName = FileHelper.getDirectoryName(currentProjectDirectory);
    }
}
