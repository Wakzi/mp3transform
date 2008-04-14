package org.mp3transform.build;

import java.io.File;
import java.util.List;

/**
 * The implementation of the pure Java build.
 */
public class Build extends BuildBase {

    public static void main(String[] args) {
        new Build().run(args);
    }

    public void all() {
        clean();
        compile();
        jar();
    }
    
    public void clean() {
        delete("target");
        mkdir("target");
    }
    
    public void compile() {
        clean();
        javac(new String[] {"-d", "target", "-sourcepath", "src/main" }, getFiles("src"));
        
        List files = getFiles("src/main");
        files = filterFiles(files, false, "*.java");
        files = filterFiles(files, false, "*.launch");
        copy("target", files, "src/main");
        
        manifest("org.mp3transform.awt.Player");
    }
    
    private void manifest(String mainClassName) {
        String manifest = new String(readFile(new File("src/main/META-INF/MANIFEST.MF")));
        manifest = replaceAll(manifest, "${buildJdk}", getJavaSpecVersion());
        String createdBy = System.getProperty("java.runtime.version") + " (" + System.getProperty("java.vm.vendor")
                + ")";
        manifest = replaceAll(manifest, "${createdBy}", createdBy);
        String mainClassTag = manifest == null ? "" : "Main-Class: " + mainClassName;
        manifest = replaceAll(manifest, "${mainClassTag}", mainClassTag);
        writeFile(new File("target/META-INF/MANIFEST.MF"), manifest.getBytes());
    }
    
    public void jar() {
        List files = getFiles("target");
        files = filterFiles(files, false, "target/org/mp3transform/build/*");
        jar("bin/mp3transform.jar", "target", files);
        delete("target");
    }

}
