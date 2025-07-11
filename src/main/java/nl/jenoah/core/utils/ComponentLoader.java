package nl.jenoah.core.utils;

import nl.jenoah.core.components.Component;
import nl.jenoah.core.debugging.Debug;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class ComponentLoader {
    /**
     * Compiles and loads a Java class by its source file path and fully qualified class name.
     * @param javaFilePath Path to the .java file (relative or absolute)
     * @return An instance of the loaded class as Component
     */
    public static Component loadComponent(String javaFilePath) throws Exception {
        URL resourceUrl = ComponentLoader.class.getResource(javaFilePath);
        if (resourceUrl == null){
            Debug.Log("File not found " + javaFilePath);
            return null;
        }

        File javaFile = new File(resourceUrl.toURI());
        File parentDir = javaFile.getParentFile();

        // Compile the Java file to the parent directory
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException("Java Compiler not available. Make sure to run with a JDK, not a JRE.");
        }
        int result = compiler.run(null, null, null, "-d", parentDir.getAbsolutePath(), javaFile.getPath());
        if (result != 0) {
            throw new RuntimeException("Compilation failed for: " + javaFile.getPath());
        }

        // Extract class name from file path
        String className = Utils.getFileName(javaFile.getPath());
        String fullyQualifiedClassName = "nl.framegengine.customScripts." + className;

        // Load the compiled class
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{parentDir.toURI().toURL()});
        Class<?> loadedClass = Class.forName(fullyQualifiedClassName, true, classLoader);

        if (!Component.class.isAssignableFrom(loadedClass)) {
            throw new IllegalArgumentException("Class does not extend Component");
        }

        return (Component) loadedClass.getDeclaredConstructor().newInstance();
    }
}
