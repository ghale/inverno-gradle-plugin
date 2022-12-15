package org.gradle.inverno

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

class ModularizeDependenciesTask extends DefaultTask {

    @InputFiles
    FileCollection dependencies;

    @OutputDirectory
    DirectoryProperty explodedDirectory;

    @TaskAction
    void execute() {
        def dependencyModules = new ResolveDependenciesTask(dependencies).execute()
    }

}
