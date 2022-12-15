package org.gradle.inverno

import org.gradle.api.file.FileCollection

class ResolveDependenciesTask {

    private final FileCollection fileCollection;

    public ResolveDependenciesTask(FileCollection fileCollection) {
        this.fileCollection = fileCollection;
    }

    public Set<DependencyModule> execute() {
        return Collections.emptySet();
    }

}
