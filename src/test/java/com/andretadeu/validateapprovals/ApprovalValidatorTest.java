package com.andretadeu.validateapprovals;

import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class ApprovalValidatorTest {

    @Test
    public void testValidateRootFolderNotFound() {
        Set<String> approvers = new HashSet<>(Arrays.asList("ghopper", "alovelace"));
        Set<Path> changedFilesPath = new HashSet<>(
                Arrays.asList(
                        Paths.get("src/com/client/follow/Follow.java")
                )
        );
        assertThrows(
                FileNotFoundException.class,
                () -> new ApprovalValidator(approvers, changedFilesPath, Paths.get("/a/b/c")).validate());
    }

    @Test
    public void testValidateChangedFileNotFound() {
        Set<String> approvers = new HashSet<>(Arrays.asList("ghopper", "alovelace"));
        Set<Path> changedFilesPath = new HashSet<>(
                Arrays.asList(
                        Paths.get("src/com/client/unfollow/Unfollow.java")
                )
        );
        Path root = Paths.get("src/test/resources/data/repo_root");
        assertThrows(
                FileNotFoundException.class,
                () -> new ApprovalValidator(approvers, changedFilesPath, root).validate());
    }

    @Test
    public void testValidateRootFolderIsFile() {
        Set<String> approvers = new HashSet<>(Arrays.asList("ghopper", "alovelace"));
        Set<Path> changedFilesPath = new HashSet<>(
                Arrays.asList(
                        Paths.get("src/com/client/follow/Follow.java")
                )
        );
        assertThrows(
                NotDirectoryException.class,
                () -> new ApprovalValidator(approvers, changedFilesPath, Paths.get("build.gradle")).validate());
    }

    @Test
    public void testValidateOKApprovedBaseCase() throws FileNotFoundException, NotDirectoryException {
        Set<String> approvers = new HashSet<>(Arrays.asList("kantonelli"));
        Set<Path> changedFilesPath = new HashSet<>(
                Arrays.asList(
                        Paths.get("src/com/client/message/Message.java")
                )
        );
        Path root = Paths.get("src/test/resources/data/repo_root");
        assertTrue(new ApprovalValidator(approvers, changedFilesPath, root).validate());
    }

    @Test
    public void testValidateOKApproved01() throws FileNotFoundException, NotDirectoryException {
        Set<String> approvers = new HashSet<>(Arrays.asList("ghopper", "alovelace"));
        Set<Path> changedFilesPath = new HashSet<>(
                Arrays.asList(
                        Paths.get("src/com/client/follow/Follow.java"),
                        Paths.get("src/com/client/user/User.java")
                )
        );
        Path root = Paths.get("src/test/resources/data/repo_root");
        assertTrue(new ApprovalValidator(approvers, changedFilesPath, root).validate());
    }

    @Test
    public void testValidateOKApproved02() throws FileNotFoundException, NotDirectoryException {
        Set<String> approvers = new HashSet<>(Arrays.asList("alovelace", "eclarke"));
        Set<Path> changedFilesPath = new HashSet<>(
                Arrays.asList(
                        Paths.get("src/com/client/follow/Follow.java")
                )
        );
        Path root = Paths.get("src/test/resources/data/repo_root");
        assertTrue(new ApprovalValidator(approvers, changedFilesPath, root).validate());
    }

    @Test
    public void testValidateOKApproved03() throws FileNotFoundException, NotDirectoryException {
        Set<String> approvers = new HashSet<>(Arrays.asList("mfox"));
        Set<Path> changedFilesPath = new HashSet<>(
                Arrays.asList(
                        Paths.get("src/com/client/tweet/Tweet.java")
                )
        );
        Path root = Paths.get("src/test/resources/data/repo_root");
        assertTrue(new ApprovalValidator(approvers, changedFilesPath, root).validate());
    }

    @Test
    public void testValidateOKApprovedRootOwner() throws FileNotFoundException, NotDirectoryException {
        Set<String> approvers = new HashSet<>(Arrays.asList("ghopper", "eclarke"));
        Set<Path> changedFilesPath = new HashSet<>(
                Arrays.asList(
                        Paths.get("src/com/client/user/User.java")
                )
        );
        Path root = Paths.get("src/test/resources/data/repo_root");
        assertTrue(new ApprovalValidator(approvers, changedFilesPath, root).validate());
    }

    @Test
    public void testValidateOKInsufficientApprovals01() throws FileNotFoundException, NotDirectoryException {
        Set<String> approvers = new HashSet<>(Arrays.asList("alovelace"));
        Set<Path> changedFilesPath = new HashSet<>(
                Arrays.asList(
                        Paths.get("src/com/client/follow/Follow.java")
                )
        );
        Path root = Paths.get("src/test/resources/data/repo_root");
        assertFalse(new ApprovalValidator(approvers, changedFilesPath, root).validate());
    }

    @Test
    public void testValidateOKInsufficientApprovals02() throws FileNotFoundException, NotDirectoryException {
        Set<String> approvers = new HashSet<>(Arrays.asList("eclarke"));
        Set<Path> changedFilesPath = new HashSet<>(
                Arrays.asList(
                        Paths.get("src/com/client/follow/Follow.java")
                )
        );
        Path root = Paths.get("src/test/resources/data/repo_root");
        assertFalse(new ApprovalValidator(approvers, changedFilesPath, root).validate());
    }

    @Test
    public void testResolveDependenciesOKApproved01() throws FileNotFoundException, NotDirectoryException {
        Set<String> approvers = new HashSet<>(Arrays.asList("ghopper", "alovelace"));
        Set<Path> changedFilesPath = new HashSet<>(
                Arrays.asList(
                        Paths.get("src/com/client/follow/Follow.java"),
                        Paths.get("src/com/client/user/User.java")
                )
        );
        Path root = Paths.get("src/test/resources/data/repo_root");

        Set<Path> rp = getRemainingPaths(root);

        assertEquals(8, new ApprovalValidator(approvers, changedFilesPath, root).resolveDependencies(rp).size());
    }

    @Test
    public void testResolveDependenciesBaseCase() throws FileNotFoundException, NotDirectoryException {
        Set<String> approvers = new HashSet<>(Arrays.asList("kantonelli"));
        Set<Path> changedFilesPath = new HashSet<>(
                Arrays.asList(
                        Paths.get("src/com/client/message/Message.java")
                )
        );
        Path root = Paths.get("src/test/resources/data/repo_root");

        Set<Path> rp = getRemainingPaths(root);

        assertEquals(2, new ApprovalValidator(approvers, changedFilesPath, root).resolveDependencies(rp).size());
    }

    private Set<Path> getRemainingPaths(Path root) {
        Stream<Path> paths = null;
        try {
            paths = Files.walk(root, FileVisitOption.FOLLOW_LINKS);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return paths.filter(p -> p.toFile().isFile() &&
                p.getFileName().toString().equals(Package.DEPENDENCIES_FILE_NAME))
                .map(p -> p.getParent())
                .collect(Collectors.toSet());
    }
}