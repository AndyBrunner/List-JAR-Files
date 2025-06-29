# ListClasses - A command-line tool to view content of Java JAR files

A simple and powerful command-line tool to list Java class names or find duplicates in JAR files.


## Features

- List content of JAR files (class name, size, creation time and JAR file name)
- Include multiple JAR files from command line or all files from the current Java class path
- Show duplicate classes for possible conflicts
- Sort output by class name

## Syntax

```bash
ListClasses [-cp] [-d] [-s] [-a] [-fxxx] [-h] [-v] [jarfile...]

jarFile One or more JAR files or directories
-cp Include JAR files found in the current Java class path
-d List only duplicate class names
-s Sort output by the Java class name
-a Show absolute path of JAR files
-f RexEx class name filter, e.g. -f\"ch.k43.util\"
-v Show program version information
-h Show help page
```

## Prerequisites

- Any Java JRE (Java 8+)
- ch.k43.util.jar package

## Notes

- The required ch.k43.util.jar file must be found in the current classpath or specified at runtime.
- To enable debugging, edit and place the KLog.properties file in the current directory, see [Java Utility Package](https://java-util.k43.ch)

## Examples

Show all duplicate classes from all JAR files in the current class path and sort them alphabetically:

```bash
ListClasses -cp -d -s
```

Show all classes found in file1.jar and file2.jar plus all JAR files in directory1:

```bash
ListClasses file1.jar file2.jar dir/
```

Search all JAR files in current class path and show only the classes matching "mail." in the class name:

```bash
ListClasses -cp -f"mail."
```
