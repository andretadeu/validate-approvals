# validate-approvals

Project to validate approvals, given OWNER and DEPENDENCIES files in the folders.

## How to build

To build this project, you must have installed:

- Gradle, preferably the latest version
- At least Java 8 SDK, preferably the latest version

Once you have all the softwares installed, please execute the following command:

```{bash}
gradle clean build fatJar
```

This command will create the artifact **validate-approvals-fatjar**, which you can run with the following command:

```{bash}
java -jar build/libs/validate-approvals-fatjar-1.0-SNAPSHOT.jar --approvers ghopper \
--changed-files src/com/client/user/User.java --root-folder src/test/resources/data/repo_root/
```