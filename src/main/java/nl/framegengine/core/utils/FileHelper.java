package nl.framegengine.core.utils;

import nl.framegengine.core.debugging.Debug;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static org.lwjgl.util.nfd.NativeFileDialog.*;

public class FileHelper {

    public static String getFileName(String filePath) {
        String fileName = new File(filePath).getName();
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }

    public static String getDirectoryName(String directoryPath) {
        File file = new File(directoryPath);
        if (file.isDirectory()) {
            return file.getName();
        } else {
            File parent = file.getAbsoluteFile().getParentFile();
            return parent != null ? parent.getName() : null;
        }
    }

    public static String getDirectoryPath(String directoryPath) {
        File file = new File(directoryPath).getAbsoluteFile();
        if (file.isDirectory()) {
            return file.getAbsolutePath();
        } else {
            File parent = file.getParentFile();
            return parent != null ? parent.getAbsolutePath() : null;
        }
    }

    public static List<File> findAllJavaFiles(File rootDir) {
        List<File> javaFiles = new ArrayList<>();
        File[] files = rootDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    javaFiles.addAll(findAllJavaFiles(file));
                } else if (file.getName().endsWith(".java")) {
                    javaFiles.add(file);
                }
            }
        }
        return javaFiles;
    }

    public static List<File> findFilesInDirectory(File rootDir) {
        List<File> files = new ArrayList<>();
        File[] directoryFiles = rootDir.listFiles();
        if (directoryFiles != null) {
            for (File file : directoryFiles) {
                if (file.isDirectory()) {
                    files.addAll(findFilesInDirectory(file));
                } else {
                    files.add(file);
                }
            }
        }
        return files;
    }

    public static File[] listDirectoryAndFiles(String dir) {
        try {
            return new File(dir).listFiles();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void openFile(File file) throws IOException {
        if (!file.exists()) {
            throw new IllegalArgumentException("File does not exist: " + file.getAbsolutePath());
        }

        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("win")) {
            // Windows
            new ProcessBuilder("explorer", file.getAbsolutePath()).start();
        } else if (osName.contains("mac")) {
            // macOS
            new ProcessBuilder("open", file.getAbsolutePath()).start();
        } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
            // Linux/Unix
            new ProcessBuilder("xdg-open", file.getAbsolutePath()).start();
        } else {
            throw new UnsupportedOperationException("Unsupported operating system: " + osName);
        }
    }

    public static void openDirectory(File file) throws IOException {
        if (!file.exists()) {
            throw new IllegalArgumentException("File does not exist: " + file.getAbsolutePath());
        }

        String osName = System.getProperty("os.name").toLowerCase();
        File parentDir = file.getParentFile();
        if (parentDir == null) {
            throw new IOException("File does not have a parent directory: " + file.getAbsolutePath());
        }

        if (osName.contains("win")) {
            // Windows: open Explorer and select the file
            new ProcessBuilder("explorer.exe", "/select,", file.getAbsolutePath()).start();

        } else if (osName.contains("mac")) {
            // macOS: open Finder and reveal file
            new ProcessBuilder("open", "-R", file.getAbsolutePath()).start();

        } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
            // Linux/Unix: open containing folder (no standard way to select a file)
            new ProcessBuilder("xdg-open", parentDir.getAbsolutePath()).start();

        } else {
            throw new UnsupportedOperationException("Unsupported operating system: " + osName);
        }
    }

    public static String getExtension(String filename){
        String extension = "";

        int i = filename.lastIndexOf('.');
        if (i > 0) {
            extension = filename.substring(i+1);
        }

        return extension;
    }

    public static String loadResource(String fileName) throws Exception{
        String result;
        try(InputStream in = Utils.class.getResourceAsStream(fileName);
            Scanner scanner = new Scanner(in, StandardCharsets.UTF_8)){
            result = scanner.useDelimiter("\\A").next();
        }
        return result;
    }

    public static List<String> readAllLines(String fileName) {
        List<String> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(Class.forName(Utils.class.getName()).getResourceAsStream(fileName)))) {
            String line;
            while ((line = br.readLine()) != null) {
                list.add(line);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static String readFile(String filePath){
        StringBuilder sb = new StringBuilder();
        try {
            File fileToRead = new File(filePath);
            if(!fileToRead.exists()) return null;
            Scanner fileReader = new Scanner(fileToRead);
            while (fileReader.hasNextLine()){
                sb.append(fileReader.nextLine());
            }
        } catch (Exception e) {
            Debug.LogError("Error reading file: " + e.getMessage());
        }

        return sb.toString();
    }

    public static void writeToFile(String content, String targetPath){
        File targetFile  = new File(targetPath);
        BufferedWriter fileOutput = null;
        try {
            if(!targetFile.exists()){
                targetFile.getParentFile().mkdirs();
                targetFile.createNewFile();
            }

            fileOutput = new BufferedWriter(new FileWriter(targetPath));
            fileOutput.write(content);

        } catch (IOException e) {
            Debug.Log("Cannot write contents to file at " + targetPath + ". " + e.getMessage());
        }finally {
            if(fileOutput != null){
                try {
                    fileOutput.close();
                } catch (IOException e) {
                    Debug.Log("Cannot write contents to file at " + targetPath + ". " + e.getMessage());
                }
            }
        }
    }

    public static String selectDirectory(){
        String selectedDirectory = null;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            final PointerBuffer outPath = stack.mallocPointer(1);

            int result = NFD_PickFolder(outPath, (ByteBuffer) null);
            if (result == NFD_OKAY) {
                selectedDirectory = outPath.getStringUTF8(0);
                NFD_FreePath(outPath.get(0));
            } else if (result == NFD_CANCEL) {
                Debug.LogError("User canceled directory selection.");
            } else {
                Debug.LogError("Something went wrong selecting a directory: " + NFD_GetError());
            }
        }

        return selectedDirectory;
    }

    public static void copyResourceToDirectory(String inputDirectory, String outputDirectory) throws IOException, URISyntaxException {
        Path outputPath = new File(outputDirectory).toPath();

        if (!Files.exists(outputPath)) {
            Files.createDirectories(outputPath);
        }

        URL dirURL = FileHelper.class.getClassLoader().getResource(inputDirectory);
        if (dirURL == null) {
            throw new IllegalArgumentException("Resource folder not found: " + inputDirectory);
        }

        if (dirURL.getProtocol().equals("file")) {
            copyResourceFromFilesystem(inputDirectory, outputPath, dirURL);
        }else if(dirURL.getProtocol().equals("jar")) {
            copyResourceFromJar(inputDirectory, outputPath, dirURL);
        }
    }

    private static void copyResourceFromFilesystem(String inputDirectory, Path outputDirectory, URL dirURL) throws URISyntaxException, IOException {
        Path sourcePath = Paths.get(dirURL.toURI());
        Files.walk(sourcePath).forEach(source -> {
            try {
                Path target = outputDirectory.resolve(sourcePath.relativize(source).toString());
                if (Files.isDirectory(source)) {
                    if (!Files.exists(target)) Files.createDirectories(target);
                } else {
                    Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static void copyResourceFromJar(String inputDirectory, Path outputDirectory, URL dirURL) throws UnsupportedEncodingException {
        String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!"));
        try (JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"))) {
            Enumeration<JarEntry> entries = jar.entries(); // all entries in jar
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();

                if (name.startsWith(inputDirectory + "/")) {
                    String relativePath = name.substring(inputDirectory.length() + 1);
                    Path outPath = outputDirectory.resolve(relativePath);

                    if (entry.isDirectory()) {
                        Files.createDirectories(outPath);
                    } else {
                        InputStream is = jar.getInputStream(entry);
                        Files.createDirectories(outPath.getParent());
                        Files.copy(is, outPath, StandardCopyOption.REPLACE_EXISTING);
                        is.close();
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean deleteFile(File inputFile){
        if(!inputFile.exists()) return false;
        File[] allContents = inputFile.listFiles();
        if (allContents != null) {
            for (File fileChild : allContents) {
                deleteFile(fileChild);
            }
        }
        return inputFile.delete();
    }
}
