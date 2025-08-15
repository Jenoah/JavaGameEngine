package nl.framegengine.core.components;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ComponentLoader {
    private final Path sourceDir;
    private final Path binDir;
    private final URLClassLoader classLoader;

    public ComponentLoader(String scriptSourcePath, String scriptBinPath) throws IOException {
        this.sourceDir = Paths.get(scriptSourcePath);
        this.binDir = Paths.get(scriptBinPath);
        ensureDirectoryExists(binDir);
        compileAllScripts();
        this.classLoader = createClassLoader();
    }

    private void ensureDirectoryExists(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }

    private void compileAllScripts() throws IOException {
        List<File> javaFiles;
        try (Stream<Path> paths = Files.walk(sourceDir)) {
            javaFiles = paths
                    .filter(p -> p.toString().endsWith(".java"))
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        }

        if (javaFiles.isEmpty()) {
            System.out.println("[ComponentLoader] No .java files found.");
            return;
        }

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException("No system Java compiler. Are you running on a JRE instead of a JDK?");
        }

        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null)) {
            Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(javaFiles);

            List<String> options = List.of("-d", binDir.toString());
            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, options, null, compilationUnits);

            boolean success = task.call();
            if (!success) {
                throw new RuntimeException("Compilation of user scripts failed.");
            } else {
                System.out.println("[ComponentLoader] Compilation successful.");
            }
        }
    }

    private URLClassLoader createClassLoader() throws IOException {
        URL[] urls = { binDir.toUri().toURL() };
        return new URLClassLoader(urls, Component.class.getClassLoader());
    }

    public Component loadComponent(String className) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        Class<?> cls = classLoader.loadClass(className);
        if (!Component.class.isAssignableFrom(cls)) {
            throw new IllegalArgumentException("Class " + className + " is not a subclass of Component.");
        }
        return (Component) cls.getDeclaredConstructor().newInstance();
    }
}