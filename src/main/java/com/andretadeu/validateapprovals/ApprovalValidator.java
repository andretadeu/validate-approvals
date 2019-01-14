package com.andretadeu.validateapprovals;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Validate approvals fron M approvers to N files
 */
public class ApprovalValidator {
    private final Set<String> approvers;
    private final Set<Path> changedFilePaths;
    private Path root;

    private static final Set<FileVisitOption> OPTIONS = new HashSet(Arrays.asList(FileVisitOption.FOLLOW_LINKS));

    /**
     * Constructor
     * @param approvers Set of approvers
     * @param changedFilePaths Set of all changed file paths
     * @param root Path to the root folder of the changed files
     */
    public ApprovalValidator(final Set<String> approvers, final Set<Path> changedFilePaths, final Path root) {
        this.approvers = Optional
                .ofNullable(approvers)
                .orElseThrow(new IllegalArgumentExceptionSupplier("approvers"));
        this.changedFilePaths = Optional
                .ofNullable(changedFilePaths)
                .orElseThrow(new IllegalArgumentExceptionSupplier("changedFilePaths"));
        this.root = root == null ? getDefaultRoot() : root;
    }

    /**
     * Validate approvals
     * @return true if all the files have enough approvers, false otherwise.
     * @throws FileNotFoundException If either {@link #root} is not found or any of the {@link #changedFilePaths} to be
     *  evaluated are not found.
     * @throws NotDirectoryException If {@link #root} is not a directory
     */
    public boolean validate() throws FileNotFoundException, NotDirectoryException {
        if (!root.toFile().exists()) {
            throw new FileNotFoundException("Root folder not found");
        }
        if (!root.toFile().isDirectory()) {
            throw new NotDirectoryException("Root folder cannot be a file.");
        }

        Stream<Path> paths = null;
        try {
            paths = Files.walk(root, FileVisitOption.FOLLOW_LINKS);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Set<Path> remainingPaths = paths.filter(p -> p.toFile().isFile() &&
                p.getFileName().toString().equals(Package.DEPENDENCIES_FILE_NAME))
                .map(p -> p.getParent())
                .collect(Collectors.toSet());

        Map<Path, Package> packageMap = resolveDependencies(remainingPaths);

        return matchApprovers(packageMap, approvers);
    }

    /**
     * Check if the approvers are in all projects required
     * @param packageMap Map of packages, resulted from the resolved dependencies
     * @param approvers Set of approvers, provided by command line argument
     * @return true if it matches, false if there is at least one project that the approvers cannot approve.
     */
    static boolean matchApprovers(Map<Path, Package> packageMap, Set<String> approvers) {
        next_package:
        for (Package pckg : packageMap.values()) {
            for (String approver : approvers) {
                if (pckg.getOwners().contains(approver)) {
                    continue next_package;
                }
            }
            return false;
        }
        return true;
    }

    Map<Path, Package> resolveDependencies(Set<Path> remainingPaths)
            throws FileNotFoundException, NotDirectoryException {
        Map<Path, Package> result = new HashMap<>();
        for (Path changedFilePath : changedFilePaths) {
            File sourceFile = Package.getAbsolutePath(root, changedFilePath).toFile();
            if (!sourceFile.isFile()) {
                throw new FileNotFoundException("File " + sourceFile.getName() + " does not exist.");
            }
            Package pckg = new Package(root, changedFilePath.getParent());
            if (!result.containsKey(pckg.getAbsoluteLocation())) {
                result.put(pckg.getAbsoluteLocation(), pckg);
                remainingPaths.remove(pckg.getAbsoluteLocation());
                calculateAllDependencies(pckg, result, remainingPaths);
            }
        }
        return result;
    }

    private void calculateAllDependencies(final Package pckg,
                                          final Map<Path, Package> packageMap,
                                          final Set<Path> remainingPaths)
            throws FileNotFoundException, NotDirectoryException {
        if (!remainingPaths.isEmpty()) {
            Set<Package> pathsToRemove = new HashSet<>();
            for (Path path : remainingPaths) {
                Set<Path> locations = Package.extractDependencies(path);
                if (locations.contains(pckg.getLocation()) && !packageMap.containsKey(path)) {
                    Path newLocation = path.subpath(root.getNameCount(), path.getNameCount());
                    pathsToRemove.add(new Package(root, newLocation));
                }
            }
            for (Package p : pathsToRemove) {
                packageMap.put(p.getAbsoluteLocation(), p);
                remainingPaths.remove(p.getAbsoluteLocation());
            }
            for (Package p : pathsToRemove) {
                calculateAllDependencies(p, packageMap, remainingPaths);
            }
        }

    }

    private static Path getDefaultRoot() {
        return Paths.get("");
    }

}
