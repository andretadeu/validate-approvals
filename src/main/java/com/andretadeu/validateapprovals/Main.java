package com.andretadeu.validateapprovals;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.FileNotFoundException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;

public class Main {

    @Option(name = "-a", aliases = {"--approvers"}, required = true, usage = "List of approvers")
    private String approvers;

    @Option(name = "-c", aliases = {"--changed-files"}, required = true, usage = "Changed files for approval")
    private String changedFiles;

    @Option(name = "-r", aliases = {"--root-folder"}, usage = "Repository's root folder")
    private String rootFolderName;

    public static void main(String... args) {
        new Main().doMain(args);
    }

    /**
     * Effective main method, as an instance method.
     * @param args arguments passed through console
     */
    private void doMain(String... args) {
        CmdLineParser parser = new CmdLineParser(this);

        try {
            parser.parseArgument(args);
        } catch (CmdLineException cmdLineEx) {
            System.err.println(cmdLineEx.getMessage());
            System.err.println(
                    "java -jar validate-approvals-fatjar-<versão>.jar --approvers <List of approvers> " +
                            "--changed-files <Changed files for approval> " +
                            "--root-folder <Repository's root folder>");
            parser.printUsage(System.err);
            System.err.println();

            return;
        }

        final String result = validateApprovers(approvers, changedFiles, rootFolderName);
        if (result != null) {
            System.out.println(result);
        }
    }

    /**
     *
     * @param approvers
     * @param changedFiles
     * @param rootFolderName
     * @return
     */
    String validateApprovers(final String approvers, final String changedFiles, final String rootFolderName) {
        Path root = rootFolderName == null || rootFolderName.isEmpty() ? null : Paths.get(rootFolderName);
        ApprovalValidator approvalValidator = new ApprovalValidator(new HashSet<>(), new HashSet<>(), root);
        try {
            return approvalValidator.validate() ? "Approved" : "Insufficient approvals";
        } catch (FileNotFoundException | NotDirectoryException | IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }
}
