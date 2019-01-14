package com.andretadeu.validateapprovals;

import java.io.FileNotFoundException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Class to represent a Java 'package' to inspect
 */
class Package {
    private final Path location;
    private final Set<String> owners;
    private final Set<Path> dependencies;

    public Package(final Path location) throws FileNotFoundException, NotDirectoryException {
        this.location = Optional
                .ofNullable(location)
                .orElseThrow(new IllegalArgumentExceptionSupplier("location"));
        owners = new HashSet<>();
        dependencies = new HashSet<>();
        init();
    }

    private void init() throws FileNotFoundException, NotDirectoryException {
        if (!location.toFile().exists()) {
            throw new FileNotFoundException("Package folder must exist.");
        }
        if (!location.toFile().isDirectory()) {
            throw new NotDirectoryException("Package folder must be a directory.");
        }
    }

    Path getLocation() {
        return location;
    }

    Set<String> getOwners() {
        return owners;
    }

    Set<Path> getDependencies() {
        return dependencies;
    }
}
