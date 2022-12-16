package org.gradle.inverno

import org.gradle.api.file.FileCollection

import java.lang.module.ModuleFinder
import java.lang.module.ModuleReference
import java.nio.file.Path

class ResolveDependenciesTask {

    private final FileCollection fileCollection;

    ResolveDependenciesTask(FileCollection fileCollection) {
        this.fileCollection = fileCollection;
    }

    Set<DependencyModule> execute() {
        def filePaths = fileCollection.collect { it.toPath() }
        def moduleFinder = ModuleFinder.of(filePaths.toArray(Path[]::new))
        Map<Path, ModuleReference> filteredModules = moduleFinder.findAll().collectEntries { [(Path.of(it.location().get())), it] }

        return filePaths.collect { new DependencyModule(it, filteredModules.get(it)) }.toSet()
    }

}
