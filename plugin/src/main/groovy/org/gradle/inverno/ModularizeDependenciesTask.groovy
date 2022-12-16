package org.gradle.inverno

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

import javax.inject.Inject
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.util.jar.Attributes
import java.util.jar.Manifest

abstract class ModularizeDependenciesTask extends DefaultTask {


    public static final String AUTOMATIC_MODULE_NAME = "Automatic-Module-Name"

    @Inject
    @InputFiles
    abstract FileCollection getDependencies()

    @Inject
    @OutputDirectory
    abstract DirectoryProperty getExplodedDirectory()

    @Inject abstract FileSystemOperations getFilesystemOperations()

    @TaskAction
    void execute() {
        def dependencyModules = new ResolveDependenciesTask(dependencies).execute()

        if (explodedDirectory.getAsFile().get().exists()) {
            getFilesystemOperations().delete {
                delete(explodedDirectory)
            }
        }

        dependencyModules.each {dependencyModule ->
            try (FileSystem jarFs = FileSystems.newFileSystem(URI.create("jar:" + dependencyModule.path.toUri()), Map.of())) {
                Path manifestPath = jarFs.getPath("META-INF", "MANIFEST.MF")
                Path moduleInfo = jarFs.getPath("module-info.class")

                if (!Files.exists(moduleInfo)) {
                    if (Files.exists(manifestPath)) {
                        try (InputStream is = Files.newInputStream(manifestPath)) {
                            Manifest manifest = new Manifest(is)
                            if (!manifest.getMainAttributes().containsKey(AUTOMATIC_MODULE_NAME)) {
                                manifest.getMainAttributes().put(new Attributes.Name(AUTOMATIC_MODULE_NAME), dependencyModule.moduleReference.descriptor().name());
                                try (OutputStream jarOutput = Files.newOutputStream(manifestPath)) {
                                    manifest.write(jarOutput);
                                }
                            }
                        }
                    } else {
                        Manifest manifest = new Manifest();
                        manifest.getMainAttributes().put(new Attributes.Name(AUTOMATIC_MODULE_NAME), dependencyModule.moduleName);
                        try (OutputStream jarOutput = Files.newOutputStream(manifestPath)) {
                            manifest.write(jarOutput);
                        }
                    }
                }
            }
        }
    }

}
