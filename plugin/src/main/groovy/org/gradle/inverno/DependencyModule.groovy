package org.gradle.inverno

import java.lang.module.ModuleReference
import java.nio.file.Path

class DependencyModule {

    final Path path;

    final ModuleReference moduleReference;

    DependencyModule(Path path, ModuleReference moduleReference) {
        this.path = path;
        this.moduleReference = moduleReference;
    }

    String getModuleName() {
        return moduleReference.descriptor().name(); // TODO: 1. Can moduleReference be null? 2. There's additional logic in the maven plugin
    }

}

