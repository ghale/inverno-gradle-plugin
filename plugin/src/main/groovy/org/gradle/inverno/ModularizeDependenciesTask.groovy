package org.gradle.inverno

import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration
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

    Configuration configuration

    @Inject
    @InputFiles
    abstract FileCollection getDependencies()

    @Inject
    @OutputDirectory
    abstract DirectoryProperty getExplodedDirectory()

    @Inject
    abstract FileSystemOperations getFilesystemOperations()

    @TaskAction
    void execute() {
        def dependencyModules = new ResolveDependenciesTask(configuration).execute()

        if (explodedDirectory.getAsFile().get().exists()) {
            getFilesystemOperations().delete {
                delete(explodedDirectory)
            }
        }

        dependencyModules.each { dependencyModule ->
            try (FileSystem jarFs = FileSystems.newFileSystem(URI.create("jar:" + dependencyModule.path.toUri()), Map.of())) {
                Path manifestPath = jarFs.getPath("META-INF", "MANIFEST.MF")
                Path moduleInfo = jarFs.getPath("module-info.class")

                if (!Files.exists(moduleInfo)) {
                    if (Files.exists(manifestPath)) {
                        try (InputStream is = Files.newInputStream(manifestPath)) {
                            Manifest manifest = new Manifest(is)

                            if (!manifest.getMainAttributes().containsKey(AUTOMATIC_MODULE_NAME)) {
                                setAutomaticModuleName(manifest, dependencyModule);
                                try (OutputStream jarOutput = Files.newOutputStream(manifestPath)) {
                                    manifest.write(jarOutput);
                                }
                            }
                        }
                    } else {
                        Manifest manifest = new Manifest();
                        setAutomaticModuleName(manifest, dependencyModule)
                        try (OutputStream jarOutput = Files.newOutputStream(manifestPath)) {
                            manifest.write(jarOutput);
                        }
                    }
                }
            }

            // the original maven plugin has the webjar logic here

            getFilesystemOperations().copy { copySpec -> {
                copySpec.from(project.zipTree(dependencyModule.path.toFile()))
                copySpec.into(explodedDirectory.dir(generateModuleName(dependencyModule)))
            }}
        }
    }

    def setAutomaticModuleName(Manifest manifest, DependencyModule dependencyModule) {
        manifest.getMainAttributes().put(new Attributes.Name(AUTOMATIC_MODULE_NAME), generateModuleName(dependencyModule))
    }

    private String generateModuleName(DependencyModule dependencyModule) {
        dependencyModule.group + '.' + dependencyModule.moduleReference.descriptor().name()
    }

}
