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

This command will create the artifact **validate-approvals-fatjar**.

## How to test

The build command already runs all the tests for your and show the results.
If you want to run all the tests of the application only and get the results, you must use the
following command:

```{bash}
gradle cleanTest test -i
```

## How to use

You can run **validate-approvals** fat jar with the following command:

```{bash}
java -jar build/libs/validate-approvals-fatjar-<version>.jar --approvers <List of approvers> \
--changed-files <List of files> [--root-folder <Root folder location>]?
```

where:

 -a (--approvers) VAL     : List of approvers

 -c (--changed-files) VAL : Changed files for approval
 
 -r (--root-folder) VAL   : Repository's root folder (Optional. If not set, the command use the current
 folder in shell)

## Source code repository configuration

To this command line application be effective, you must have a source code repository with two types of
files: DEPENDENCIES and OWNERS. Each directory in the repository may contain none or both of them. They
contain the information used to identify who must approve a change. They are DEPENDENCIES and OWNERS.

### DEPENDENCIES files

These files contain a list of paths, one per line. These paths are the directories containing sources
that the current directory's sources depend on. Paths must be relative to the root directory of the
source code repository. If a directory does not contain a DEPENDENCIES file, it is equivalent to
containing an empty DEPENDENCIES file.

### OWNERS files

These files contain a list of usernames of engineers, one per line. The usernames refer to the engineers
who can approve changes affecting the containing directory and its subdirectories. If there is no OWNERS
file or it is empty, then the parent directory's OWNERS file should be used. You may have to set an
OWNER to the repository root folder, otherwise any folder that does not have an OWNER inside and neither
its parents have nobody to approve them.

### Approval rules

A change is approved if all of the affected directories are approved.

A directory is considered to be affected by a change if either: (1) a file in that directory was changed,
or (2) a file in a (transitive) dependency directory was changed.

In case (1), we only consider changes to files directly contained within a directory, not files in
subdirectories, etc.

Case (2) includes transitive changes, so a directory is also affected if a dependency of one of its
dependencies changes, etc.

A directory has approval if at least one engineer listed in an OWNERS file in it or any of its parent
directories has approved it.

For example, consider the following directory tree:

- x/
  - DEPENDENCIES = "y\n"
  - OWNERS = "A\nB\n"
- y/
  - OWNERS = "B\nC\n"
  - file
 
If a change modifies y/file, it affects both directories y (contains file) and x (depends on y).
This change must at a minimum be approved by either B alone (owner of x and y) or both A (owner of x)
and C (owner of y). 