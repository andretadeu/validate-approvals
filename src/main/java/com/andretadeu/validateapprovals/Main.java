package com.andretadeu.validateapprovals;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class Main {

    @Option(name = "-a", aliases = {"--approvers"}, required = true, usage = "List of approvers")
    private String approvers;

    @Option(name = "-c", aliases = {"--changed-files"}, required = true, usage = "Changed files for approval")
    private String changedFiles;

    public static void main(String... args) {
        new Main().doMain(args);
    }

    private void doMain(String... args) {
        CmdLineParser parser = new CmdLineParser(this);

        try {
            parser.parseArgument(args);
        } catch (CmdLineException cmdLineEx) {
            System.err.println(cmdLineEx.getMessage());
            System.err.println(
                    "java -jar dep-mgmt-<versão>.jar --approvers <List of approvers> " +
                            "--changed-files <Changed files for approval>");
            parser.printUsage(System.err);
            System.err.println();

            return;
        }
    }
}
