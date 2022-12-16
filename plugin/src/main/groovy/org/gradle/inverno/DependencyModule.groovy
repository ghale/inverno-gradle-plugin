package org.gradle.inverno

import java.lang.module.ModuleReference
import java.nio.file.Path

class DependencyModule {

    final Path path;

    final ModuleReference moduleReference;

    final String group;

    DependencyModule(Path path, ModuleReference moduleReference, String group) {
        this.path = path;
        this.moduleReference = moduleReference;
        this.group = group;
    }

}

