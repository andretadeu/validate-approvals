package com.andretadeu.validateapprovals;

import java.io.FileNotFoundException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;

/**
 * Validate approvals fron M approvers to N files
 */
public class ApprovalValidator {
    private final Set<String> approvers;
    private final Set<Path> changedFilePaths;
    private Path root;

    /**
     * Constructor
     * @param approvers Set of approvers
     * @param changedFilePaths Set of all changed file paths
     */
    public ApprovalValidator(final Set<String> approvers, final Set<Path> changedFilePaths) {
        this(approvers, changedFilePaths, getDefaultRoot());
    }

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

        return false;
    }

    private static Path getDefaultRoot() {
        return Paths.get("");
    }

}
