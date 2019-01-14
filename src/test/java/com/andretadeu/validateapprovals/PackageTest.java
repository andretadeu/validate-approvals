package com.andretadeu.validateapprovals;

import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PackageTest {

    @Test
    void testPackageNotFound() {
        assertThrows(FileNotFoundException.class, () -> new Package(Paths.get("/a/b/c")));
    }

    @Test
    void testPackageIsFile() {
        assertThrows(NotDirectoryException.class, () -> new Package(Paths.get("build.gradle")));
    }

    @Test
    void testFollowGetLocation() throws FileNotFoundException, NotDirectoryException {
        Package pckg = new Package(Paths.get("src/test/resources/data/repo_root/src/com/client/follow"));
        assertEquals(
                "src/test/resources/data/repo_root/src/com/client/follow",
                pckg.getLocation().toString());
    }

    @Test
    void testFollowGetOwners() throws FileNotFoundException, NotDirectoryException {
        Package pckg = new Package(Paths.get("src/test/resources/data/repo_root/src/com/client/follow"));
        assertTrue(pckg.getOwners().contains("alovelace") && pckg.getOwners().contains("ghopper"));
    }

    @Test
    void testFollowGetDependencies() throws FileNotFoundException, NotDirectoryException {
        Package pckg = new Package(Paths.get("src/test/resources/data/repo_root/src/com/client/follow"));
        assertTrue(
                pckg.getDependencies().contains(Paths.get("src/test/resources/data/repo_root/src/com/client/user"))
        );
    }

    @Test
    void testMessageGetLocation() throws FileNotFoundException, NotDirectoryException {
        Package pckg = new Package(Paths.get("src/test/resources/data/repo_root/src/com/client/message"));
        assertEquals(
                "src/test/resources/data/repo_root/src/com/client/message",
                pckg.getLocation().toString());
    }

    @Test
    void testMessageGetOwners() throws FileNotFoundException, NotDirectoryException {
        Package pckg = new Package(Paths.get("src/test/resources/data/repo_root/src/com/client/message"));
        assertTrue(pckg.getOwners().contains("eclarke") && pckg.getOwners().contains("kantonelli"));
    }

    @Test
    void testMessageGetDependencies() throws FileNotFoundException, NotDirectoryException {
        Package pckg = new Package(Paths.get("src/test/resources/data/repo_root/src/com/client/message"));
        Set<Path> deps = pckg.getDependencies();
        Path follow = Paths.get("src/test/resources/data/repo_root/src/com/client/follow");
        Path user = Paths.get("src/test/resources/data/repo_root/src/com/client/user");
        assertTrue(deps.contains(follow) && deps.contains(user));
    }

    @Test
    void testTweetGetLocation() throws FileNotFoundException, NotDirectoryException {
        Package pckg = new Package(Paths.get("src/test/resources/data/repo_root/src/com/client/tweet"));
        assertEquals(
                "src/test/resources/data/repo_root/src/com/client/tweet",
                pckg.getLocation().toString());
    }

    @Test
    void testTweetGetOwners() throws FileNotFoundException, NotDirectoryException {
        Package pckg = new Package(Paths.get("src/test/resources/data/repo_root/src/com/client/tweet"));
        Set<String> o = pckg.getOwners();
        assertTrue(o.contains("alovelace") && o.contains("ghopper") && o.contains("mfox"));
    }

    @Test
    void testTweetGetDependencies() throws FileNotFoundException, NotDirectoryException {
        Package pckg = new Package(Paths.get("src/test/resources/data/repo_root/src/com/client/tweet"));
        Set<Path> deps = pckg.getDependencies();
        Path follow = Paths.get("src/test/resources/data/repo_root/src/com/client/follow");
        Path user = Paths.get("src/test/resources/data/repo_root/src/com/client/user");
        assertTrue(deps.contains(follow) && deps.contains(user));
    }

    @Test
    void testUserGetLocation() throws FileNotFoundException, NotDirectoryException {
        Package pckg = new Package(Paths.get("src/test/resources/data/repo_root/src/com/client/user"));
        assertEquals(
                "src/test/resources/data/repo_root/src/com/client/user",
                pckg.getLocation().toString());
    }

    @Test
    void testUserGetOwners() throws FileNotFoundException, NotDirectoryException {
        Package pckg = new Package(Paths.get("src/test/resources/data/repo_root/src/com/client/user"));
        assertTrue(pckg.getOwners().contains("ghopper"));
    }

    @Test
    void testUserGetDependencies() throws FileNotFoundException, NotDirectoryException {
        Package pckg = new Package(Paths.get("src/test/resources/data/repo_root/src/com/client/user"));
        assertTrue(pckg.getDependencies().isEmpty());
    }
}