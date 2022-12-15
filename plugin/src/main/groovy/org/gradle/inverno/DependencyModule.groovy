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

}

