package nl.framegengine.editor;

import nl.framegengine.core.debugging.Debug;
import nl.framegengine.core.entity.SceneManager;
import nl.framegengine.core.utils.FileHelper;
import nl.framegengine.core.utils.JsonHelper;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import javax.json.*;
import javax.json.stream.JsonGenerator;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class EngineSettings {
    public static String currentProjectDirectory = "";
    public static String currentLevelPath = "";
    public static String currentProjectName = "Unknown project";

    private static final String manifestFileName = "/.fgmanifest";
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
        updateManifest();
        registerManifestListener();

        Debug.Log("Project settings successfully loaded in");
    }

    private static void registerManifestListener(){
        try {
            String filteredManifestFileName = FileHelper.getFileName(manifestFileName) + "." + FileHelper.getExtension(manifestFileName);
            IOFileFilter excludeManifestFilter = FileFilterUtils.notFileFilter(FileFilterUtils.nameFileFilter(filteredManifestFileName));
            FileAlterationObserver observer = FileAlterationObserver.builder()
                    .setFile(new File(currentProjectDirectory))
                    .setFileFilter(excludeManifestFilter)
                    .get();

            FileAlterationMonitor monitor = new FileAlterationMonitor(1000);
            FileAlterationListener listener = new ManifestFileListener();

            observer.addListener(listener);
            monitor.addObserver(observer);
            monitor.start();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    public static void updateManifest(){
        File manifestFile = new File(getManifestPath());
        List<HashMap<String, String>> existingTextures = new ArrayList<>();
        List<HashMap<String, String>> existingScripts = new ArrayList<>();
        List<HashMap<String, String>> existingLevels = new ArrayList<>();

        if(manifestFile.exists()){
            String manifestFileContent = FileHelper.readFile(manifestFile.getAbsolutePath());
            if(manifestFileContent != null && !manifestFileContent.isBlank()) {
                JsonObject manifestInfo = Json.createReader(new StringReader(manifestFileContent)).readObject();
                manifestInfo.forEach((s, jsonValue) -> {
                    if(jsonValue.getValueType() == JsonValue.ValueType.ARRAY){
                        switch (s) {
                            case "textures" -> jsonValue.asJsonArray().forEach(jsonArrayValue -> {
                                if (jsonArrayValue.getValueType() == JsonValue.ValueType.OBJECT) {
                                    existingTextures.add(manifestJsonToHashmapItem(jsonArrayValue.asJsonObject()));
                                }
                            });
                            case "scripts" -> jsonValue.asJsonArray().forEach(jsonArrayValue -> {
                                if (jsonArrayValue.getValueType() == JsonValue.ValueType.OBJECT) {
                                    existingScripts.add(manifestJsonToHashmapItem(jsonArrayValue.asJsonObject()));
                                }
                            });
                            case "levels" -> jsonValue.asJsonArray().forEach(jsonArrayValue -> {
                                if (jsonArrayValue.getValueType() == JsonValue.ValueType.OBJECT) {
                                    existingLevels.add(manifestJsonToHashmapItem(jsonArrayValue.asJsonObject()));
                                }
                            });
                        }
                    }
                });
            }
        }

        JsonObjectBuilder jsonManifestContent = Json.createObjectBuilder();
        JsonArrayBuilder textureArray = Json.createArrayBuilder();
        JsonArrayBuilder scriptArray = Json.createArrayBuilder();
        JsonArrayBuilder levelArray = Json.createArrayBuilder();

        List<HashMap<String, String>> manifestTextures = new ArrayList<>();
        List<HashMap<String, String>> manifestScripts = new ArrayList<>();
        List<HashMap<String, String>> manifestLevels = new ArrayList<>();

        File[] filesInProject = FileHelper.findFilesInDirectory(new File(currentProjectDirectory)).toArray(File[]::new);

        for (File file : filesInProject) {
            manifestFileType fileType = fileToManifestFileType(file);
            if(!file.exists()) continue;
            AtomicBoolean hasAddedFile = new AtomicBoolean(false);
            String fileGUID = FileHelper.getChecksum(file.getAbsolutePath());
            String relativePath = Paths.get(currentProjectDirectory).relativize(file.toPath()).toString();

            if(fileType == manifestFileType.TEXTURE){
                existingTextures.forEach(textureHashmap -> {
                    if(textureHashmap.get("guid").equals(fileGUID) && !textureHashmap.get("path").equals(relativePath)){
                        textureHashmap.replace("path", relativePath);
                        textureHashmap.replace("filename", FileHelper.getFileName(file.getPath()));
                        manifestTextures.add(textureHashmap);
                        hasAddedFile.set(true);
                    }
                });
                if(!hasAddedFile.get()){
                    HashMap<String, String> fileHashmap = new HashMap<>();
                    fileHashmap.put("guid", fileGUID);
                    fileHashmap.put("path", relativePath);
                    fileHashmap.put("filename", FileHelper.getFileName(file.getPath()));
                    manifestTextures.add(fileHashmap);
                }
            } else if (fileType == manifestFileType.SCRIPT) {
                existingScripts.forEach(scriptHashmap -> {
                    if(scriptHashmap.get("guid").equals(fileGUID) && !scriptHashmap.get("path").equals(relativePath)){
                        scriptHashmap.replace("path", relativePath);
                        manifestScripts.add(scriptHashmap);
                        hasAddedFile.set(true);
                    }
                });
                if(!hasAddedFile.get()){
                    HashMap<String, String> fileHashmap = new HashMap<>();
                    fileHashmap.put("guid", fileGUID);
                    fileHashmap.put("path", relativePath);
                    fileHashmap.put("filename", FileHelper.getFileName(file.getPath()));
                    manifestScripts.add(fileHashmap);
                }
            } else if (fileType == manifestFileType.LEVEL) {
                existingLevels.forEach(levelHashmap -> {
                    if(levelHashmap.get("guid").equals(fileGUID) && !levelHashmap.get("path").equals(relativePath)){
                        levelHashmap.replace("path", relativePath);
                        manifestLevels.add(levelHashmap);
                        hasAddedFile.set(true);
                    }
                });
                if(!hasAddedFile.get()){
                    HashMap<String, String> fileHashmap = new HashMap<>();
                    fileHashmap.put("guid", fileGUID);
                    fileHashmap.put("path", relativePath);
                    fileHashmap.put("filename", FileHelper.getFileName(file.getPath()));
                    manifestLevels.add(fileHashmap);
                }
            }
        }

        manifestTextures.forEach(manifestTexture -> {
            JsonObjectBuilder fileInfo = Json.createObjectBuilder();
            fileInfo.add("guid", manifestTexture.get("guid"));
            fileInfo.add("path", manifestTexture.get("path"));
            fileInfo.add("filename", manifestTexture.get("filename"));
            textureArray.add(fileInfo.build());
        });
        manifestScripts.forEach(manifestScript -> {
            JsonObjectBuilder fileInfo = Json.createObjectBuilder();
            fileInfo.add("guid", manifestScript.get("guid"));
            fileInfo.add("path", manifestScript.get("path"));
            fileInfo.add("filename", manifestScript.get("filename"));
            scriptArray.add(fileInfo.build());
        });
        manifestLevels.forEach(manifestLevel -> {
            JsonObjectBuilder fileInfo = Json.createObjectBuilder();
            fileInfo.add("guid", manifestLevel.get("guid"));
            fileInfo.add("path", manifestLevel.get("path"));
            fileInfo.add("filename", manifestLevel.get("filename"));
            levelArray.add(fileInfo.build());
        });

        jsonManifestContent.add("textures", textureArray.build());
        jsonManifestContent.add("scripts", scriptArray.build());
        jsonManifestContent.add("levels", levelArray.build());

        Map<String, Boolean> config = new HashMap<>();
        config.put(JsonGenerator.PRETTY_PRINTING, true);
        JsonWriterFactory jsonWriterFactory = Json.createWriterFactory(config);

        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = jsonWriterFactory.createWriter(stringWriter);
        jsonWriter.write(jsonManifestContent.build());

        FileHelper.writeToFile(stringWriter.toString(), getManifestPath());
    }

    private static HashMap<String, String> manifestJsonToHashmapItem(JsonObject jsonObject){
        HashMap<String, String> itemInfo = new HashMap<>();
        if(JsonHelper.hasJsonKey(jsonObject, "guid")) itemInfo.put("guid", jsonObject.getString("guid"));
        if(JsonHelper.hasJsonKey(jsonObject, "path")) itemInfo.put("path", jsonObject.getString("path"));
        if(JsonHelper.hasJsonKey(jsonObject, "filename")) itemInfo.put("filename", jsonObject.getString("filename"));

        return itemInfo;
    }

    public static String getManifestPath(){
        return Paths.get(currentProjectDirectory, manifestFileName).toString();
    }

    enum manifestFileType{
        TEXTURE,
        SCRIPT,
        LEVEL,
        NULL
    }

    private static manifestFileType fileToManifestFileType(File file){
        String extension = FileHelper.getExtension(file.getPath());

        switch (extension){
            case "jpg", "png", "gif", "tiff":
                return manifestFileType.TEXTURE;
            case "lvl":
                return manifestFileType.LEVEL;
            case "java":
                return manifestFileType.SCRIPT;
            case null, default:
                return manifestFileType.NULL;
        }
    }

    private static class ManifestFileListener implements FileAlterationListener {
        @Override
        public void onFileCreate(File file) {
            updateManifest();
        }

        @Override
        public void onDirectoryChange(File file) {

        }

        @Override
        public void onDirectoryCreate(File file) {

        }

        @Override
        public void onDirectoryDelete(File file) {

        }

        @Override
        public void onFileChange(File file) {
            updateManifest();
        }

        @Override
        public void onFileDelete(File file) {
            updateManifest();
        }

        @Override
        public void onStart(FileAlterationObserver fileAlterationObserver) {}

        @Override
        public void onStop(FileAlterationObserver observer) {}
    }

}