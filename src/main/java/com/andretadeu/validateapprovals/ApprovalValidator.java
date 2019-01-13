package com.andretadeu.validateapprovals;

import java.nio.file.Path;
import java.util.Set;

public class ApprovalValidator {
    private final Set<String> approvers;
    private final Set<Path> changedFilePaths;

    public ApprovalValidator(Set<String> approvers, Set<Path> changedFilePaths) {
        this.approvers = approvers;
        this.changedFilePaths = changedFilePaths;
    }

    public void validate() {

    }
}
