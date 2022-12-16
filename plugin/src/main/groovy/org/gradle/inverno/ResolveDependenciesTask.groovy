package org.gradle.inverno

import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.component.ModuleComponentIdentifier

import java.lang.module.ModuleFinder
import java.lang.module.ModuleReference
import java.nio.file.Path

class ResolveDependenciesTask {

    private final Configuration configuration;

    ResolveDependenciesTask(Configuration configuration) {
        this.configuration = configuration;
    }

    Set<DependencyModule> execute() {
        Map<Path, String> modulePathsToGroups = configuration
            .resolvedConfiguration
            .resolvedArtifacts
            .findAll { { it.id.componentIdentifier instanceof ModuleComponentIdentifier } }
            .collectEntries { [(it.file.toPath()): ((ModuleComponentIdentifier) it.id.componentIdentifier).group] }

        def moduleFinder = ModuleFinder.of(modulePathsToGroups.keySet().toArray(Path[]::new))
        Map<Path, ModuleReference> filteredModules = moduleFinder.findAll().collectEntries { [(Path.of(it.location().get())), it] }

        return modulePathsToGroups.collect { new DependencyModule(it.key, filteredModules.get(it.key), it.value) }.toSet()
    }
}
