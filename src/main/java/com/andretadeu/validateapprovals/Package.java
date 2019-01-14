package com.andretadeu.validateapprovals;

import java.io.*;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Class to represent a Java 'package' to inspect
 */
class Package {
    public static final String DEPENDENCIES_FILE_NAME = "DEPENDENCIES";
    public static final String OWNERS_FILE_NAME = "OWNERS";

    private final Path root;
    private final Path location;
    private final Set<String> owners;
    private final Set<Path> dependencies;

    public Package(final Path root, final Path location) throws FileNotFoundException, NotDirectoryException {
        this.root = Optional
                .ofNullable(root)
                .orElseThrow(new IllegalArgumentExceptionSupplier("root"));
        this.location = Optional
                .ofNullable(location)
                .orElseThrow(new IllegalArgumentExceptionSupplier("location"));
        owners = new HashSet<>();
        dependencies = new HashSet<>();
        init();
    }

    private void init() throws FileNotFoundException, NotDirectoryException {
        validatePath(root, "Root");
        validatePath(Paths.get(root.toString(), location.toString()), "Package");
        File dependenciesFile = Paths.get(root.toString(), location.toString(), DEPENDENCIES_FILE_NAME).toFile();
        if (dependenciesFile.exists() && dependenciesFile.isFile()) {
            try (BufferedReader br = new BufferedReader(new FileReader(dependenciesFile))) {
                String dependencyLocation;
                while ((dependencyLocation = br.readLine()) != null) {
                    dependencies.add(Paths.get(dependencyLocation));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File ownersFile = Paths.get(root.toString(), location.toString(), OWNERS_FILE_NAME).toFile();
        if (ownersFile.exists() && ownersFile.isFile()) {
            try (BufferedReader br = new BufferedReader(new FileReader(ownersFile))) {
                String owner;
                while ((owner = br.readLine()) != null) {
                    owners.add(owner);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void validatePath(Path root, String folderName)
            throws FileNotFoundException, NotDirectoryException {
        if (!root.toFile().exists()) {
            throw new FileNotFoundException(folderName + " folder must exist.");
        }
        if (!root.toFile().isDirectory()) {
            throw new NotDirectoryException(folderName + " folder must be a directory.");
        }
    }

    Path getLocation() {
        return location;
    }

    Set<String> getOwners() {
        return Collections.unmodifiableSet(owners);
    }

    Set<Path> getDependencies() {
        return Collections.unmodifiableSet(dependencies);
    }
}
