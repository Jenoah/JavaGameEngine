package nl.jenoah.core.utils;

import nl.jenoah.core.debugging.Debug;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileHelper {

    public static String getFileName(String filePath) {
        String fileName = new File(filePath).getName();
        return fileName.substring(0, fileName.lastIndexOf('.'));
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

    public static File[] listDirectoryAndFiles(String dir) {
        try {
            return new File(dir).listFiles();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void openFile(File file) throws IOException {
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
            if (!targetFile.exists()) targetFile.createNewFile();
            fileOutput = new BufferedWriter(new FileWriter(targetFile));
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
}
