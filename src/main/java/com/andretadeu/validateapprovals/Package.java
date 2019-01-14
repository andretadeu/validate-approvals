package com.andretadeu.validateapprovals;

import java.io.*;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Class to represent a Java 'package' to inspect
 */
final class Package {
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
        validatePath(root, "Root");
        validatePath(getAbsolutePath(root, location), "Package");
        owners = extractOwners(root, location);
        dependencies = extractDependencies(root, location);

    }

    static Set<Path> extractDependencies(final Path root, final Path location) {
        return extractDependencies(getAbsolutePath(root, location));
    }

    static Set<Path> extractDependencies(final Path fullPath) {

        File dependenciesFile = Paths.get(fullPath.toString(), DEPENDENCIES_FILE_NAME).toFile();
        Set<Path> result = new HashSet<>();
        if (dependenciesFile.isFile()) {
            try (BufferedReader br = new BufferedReader(new FileReader(dependenciesFile))) {
                String dependencyLocation;
                while ((dependencyLocation = br.readLine()) != null) {
                    result.add(Paths.get(dependencyLocation));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    static Set<String> extractOwners(final Path root, final Path location) {
        final Set<String> result = new HashSet<>();
        File ownersFile = Paths.get(root.toString(), location.toString(), OWNERS_FILE_NAME).toFile();
        result.addAll(readOwners(ownersFile));
        while (!root.equals(ownersFile.getParentFile().toPath())) {
            ownersFile = new File(ownersFile.getParentFile().getParentFile(), OWNERS_FILE_NAME);
            if (ownersFile.isFile()) {
                result.addAll(readOwners(ownersFile));
            }
        }
        return result;
    }

    private static Set<String> readOwners(File ownersFile) {
        final Set<String> result = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ownersFile))) {
            String owner;
            while ((owner = br.readLine()) != null) {
                result.add(owner);
            }
        } catch (IOException e) {
            System.err.println("WARNING: You either have none OWNERS file in the dependency hierarchy or " +
                    " the file cannot be accessed");
        }
        return result;
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

    Path getRoot() {
        return root;
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

    Path getAbsoluteLocation() {
        return getAbsolutePath(root, location);
    }

    static Path getAbsolutePath(Path root, Path location) {
        return Paths.get(root.toString(), location.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Package aPackage = (Package) o;
        return getRoot().equals(aPackage.getRoot()) &&
                getLocation().equals(aPackage.getLocation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRoot(), getLocation());
    }
}
