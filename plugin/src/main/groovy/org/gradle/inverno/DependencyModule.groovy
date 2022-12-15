package org.gradle.inverno

import java.lang.module.ModuleReference
import java.nio.file.Path

public class DependencyModule {

    private final Path path;

    private final ModuleReference moduleReference;

    DependencyModule(Path path, ModuleReference moduleReference) {
        this.path = path;
        this.moduleReference = moduleReference;
    }

}

