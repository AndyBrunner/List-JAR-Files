![Java](https://img.shields.io/badge/Language-Java-blue)
![Open Source](https://img.shields.io/badge/Code-Open_Source-green)
![Unlicense](https://img.shields.io/badge/License-Unlicense-green)

![Author](https://img.shields.io/badge/Author-andy.brunner@k43.ch-grey)

---

# ListClasses - A command-line tool to view content of Java JAR files

A simple command-line tool to list (or find duplicate) Java class names in JAR files.

---

## Features

- List the contents of JAR files (class name, size, creation time, and JAR file name).
- Include multiple JAR files from the command line or all files from the current Java classpath.
- Show duplicate classes to identify potential conflicts.
- Filter classes by RexEx expression.
- Sort the output by class name.

## Syntax

```
ListClasses [-c] [-d] [-s] [-a] [-fxxx] [-h] [-v] [file...]

file  One or more JAR files or directories
-c    Include JAR files found in the current Java classpath
-d    List only duplicate class names
-s    Sort output by the Java class name
-a    Show absolute path of JAR files
-f    RexEx class name filter, e.g. -f\"ch.k43.util\"
-v    Show program version information
-h    Show help page
```

## Example Output

```
ch.k43.util.K                                                             45795 2025-06-05T16:56:20 ../lib/ch.k43.util.jar
ch.k43.util.KDB                                                           19731 2025-06-05T16:56:20 ../lib/ch.k43.util.jar
ch.k43.util.KFile                                                          7334 2025-06-05T14:14:44 ../lib/ch.k43.util.jar
ch.k43.util.KHTTPClient                                                   10570 2025-06-05T16:56:20 ../lib/ch.k43.util.jar
ch.k43.util.KHTTPServerThread                                             11506 2025-06-05T16:56:20 ../lib/ch.k43.util.jar
ch.k43.util.KHTTPServerThreadSample                                         577 2025-06-05T14:14:44 ../lib/ch.k43.util.jar
ch.k43.util.KHelloWorld                                                    1451 2025-06-05T16:56:20 ../lib/ch.k43.util.jar
ch.k43.util.KLocalData                                                      601 2025-06-05T14:14:44 ../lib/ch.k43.util.jar
ch.k43.util.KLog                                                          13270 2025-06-05T16:56:20 ../lib/ch.k43.util.jar
ch.k43.util.KLogCSVFormatter                                               3453 2025-06-05T14:14:44 ../lib/ch.k43.util.jar
ch.k43.util.KLogJDBCHandler                                                6539 2025-06-05T14:14:44 ../lib/ch.k43.util.jar
ch.k43.util.KLogJSONFormatter                                              3276 2025-06-05T14:14:44 ../lib/ch.k43.util.jar
ch.k43.util.KLogLineFormatter                                              2518 2025-06-05T14:14:44 ../lib/ch.k43.util.jar
ch.k43.util.KLogSMTPHandler                                                6549 2025-06-05T14:14:44 ../lib/ch.k43.util.jar
ch.k43.util.KLogSMTPHandlerThread                                          2210 2025-06-05T14:14:44 ../lib/ch.k43.util.jar
ch.k43.util.KLogXMLFormatter                                               3228 2025-06-05T16:56:20 ../lib/ch.k43.util.jar
ch.k43.util.KLogYAMLFormatter                                              2607 2025-06-05T14:14:44 ../lib/ch.k43.util.jar
ch.k43.util.KPasswordVault                                                 6079 2025-06-05T14:16:24 ../lib/ch.k43.util.jar
ch.k43.util.KSMTPMailer$1                                                   824 2025-06-05T14:14:44 ../lib/ch.k43.util.jar
ch.k43.util.KSMTPMailer                                                   12464 2025-06-05T16:56:20 ../lib/ch.k43.util.jar
ch.k43.util.KSocketClient                                                 10492 2025-06-05T14:14:44 ../lib/ch.k43.util.jar
ch.k43.util.KSocketServer                                                  7569 2025-06-05T14:14:44 ../lib/ch.k43.util.jar
ch.k43.util.KSocketServerListener                                          5791 2025-06-05T14:14:44 ../lib/ch.k43.util.jar
ch.k43.util.KSocketServerThread                                            7238 2025-06-05T14:14:44 ../lib/ch.k43.util.jar
ch.k43.util.KSocketServerThreadSample                                      1501 2025-06-05T14:14:44 ../lib/ch.k43.util.jar
ch.k43.util.KThread                                                        2096 2025-06-05T14:14:44 ../lib/ch.k43.util.jar
ch.k43.util.KTimer                                                         1307 2025-06-05T14:14:44 ../lib/ch.k43.util.jar
ch.k43.util.package-info                                                    117 2025-06-05T15:05:34 ../lib/ch.k43.util.jar
```

## Prerequisites

- Any Java JRE (Java 8+)
- ch.k43.util.jar package

## Notes

- The required ch.k43.util.jar file must be present in the current classpath at runtime.
- To enable debugging mode, edit and place the KLog.properties file in the current directory. See [Java Utility Package](https://java-util.k43.ch)


## Examples

Show all duplicate classes from all JAR files in the current classpath and sort them alphabetically:

```
java ListClasses -c -d -s
```

Show all classes found in file1.jar and file2.jar plus all JAR files in dir1:

```
java ListClasses file1.jar file2.jar dir1/
```

Search all JAR files in current classpath and show only the classes matching "mail." in the class name:

```
java ListClasses -c -f"mail."
```

## License

This project is released into the public domain under [The Unlicense](LICENSE).
