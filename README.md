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
- Optionally show serialVersionUID for each class.
- Filter classes by RexEx expression.
- Sort the output by class name.

## Syntax

```
ListClasses [-c] [-d] [-s] [-a] [-fxxx] [-u] [-h] [-v] [file...]

file  One or more JAR files or directories
-c    Include JAR files found in the current Java classpath
-d    List only duplicate class names
-s    Sort output by the Java class name
-a    Show absolute path of JAR files
-f    RexEx class name filter, e.g. -f\"ch.k43.util\"
-u    Show serialVersionUID for each class
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
- The ch.k43.util.jar package must be present in the current classpath at runtime (Download from [Java Utility Package](https://java-util.k43.ch/downloads/package-ch-k43-util))

## Logging (see [Java Utility Package](https://https://java-util.k43.ch/examples/logging))

- To enable the built-in logging for informational, debug and error messages, edit the file *Klog.properties.sample* to your needs and place it with the name *KLog.properties* in the current directory.
- The default location and name of the logging configuration file *KLog.properties* can be overwritten with the startup parameter *-DKLogPropertyFile.*

Debug Example:

```
2025-07-03T08:46:31.957 D main[1]:ch.k43.util.KLog:<clinit>:199                        ===== Application started 2025-07-03T08:46:31.923 =====
2025-07-03T08:46:31.957 D main[1]:ch.k43.util.KLog:<clinit>:200                        Java Utility Package (Open Source/Freeware) Version 2025.06.05
2025-07-03T08:46:31.957 D main[1]:ch.k43.util.KLog:<clinit>:201                        Homepage java-util.k43.ch - Please send any feedback to andy.brunner@k43.ch
2025-07-03T08:46:31.958 D main[1]:ch.k43.util.KLog:<clinit>:204                        KLog properties read from file KLog.properties
2025-07-03T08:46:32.004 D main[1]:ch.k43.util.KLog:<clinit>:220                        Network host abmacbookpro (10.0.0.104)
2025-07-03T08:46:32.005 D main[1]:ch.k43.util.KLog:<clinit>:224                        OS platform Mac OS X Version 15.5/aarch64
2025-07-03T08:46:32.005 D main[1]:ch.k43.util.KLog:<clinit>:229                        OS disk space total 3.63 TiB, free 2.32 TiB, usable 2.32 TiB
2025-07-03T08:46:32.005 D main[1]:ch.k43.util.KLog:<clinit>:235                        Java version 23 (Java HotSpot(TM) 64-Bit Server VM - Oracle Corporation)
2025-07-03T08:46:32.006 D main[1]:ch.k43.util.KLog:<clinit>:240                        Java directory /Library/Java/JavaVirtualMachines/graalvm-jdk-23.0.1+11.1/Contents/Home
2025-07-03T08:46:32.006 D main[1]:ch.k43.util.KLog:<clinit>:245                        Java CPUs 10, de/CH, UTF-8, UTC +02:00 (Europe/Zurich)
2025-07-03T08:46:32.006 D main[1]:ch.k43.util.KLog:<clinit>:255                        Java heap maximum 16.00 GiB, current 1.01 GiB, used 8.98 MiB, free 1023.02 MiB
2025-07-03T08:46:32.006 D main[1]:ch.k43.util.KLog:<clinit>:262                        Java classpath ../bin/:../lib/angus-mail-2.0.3.jar:../lib/jakarta.mail-api-2.1.3.jar:../lib/org.json.20230618.jar:../lib/h2-2.2.224.jar:../lib/jakarta.activation-api-2.1.3.jar:../lib/ch.k43.util.jar:../lib/angus-activation-2.0.2.jar
2025-07-03T08:46:32.007 D main[1]:ch.k43.util.KLog:<clinit>:266                        Current user andybrunner, language de, home directory /Users/andybrunner/
2025-07-03T08:46:32.007 D main[1]:ch.k43.util.KLog:<clinit>:272                        Current directory /Users/andybrunner/Documents/Eclipse-Workspace/List-JAR-Classes/src/
2025-07-03T08:46:32.007 D main[1]:ch.k43.util.KLog:<clinit>:276                        Temporary directory /var/folders/9s/tbyqn_vn7bs9rf3f1rc2jpxw0000gn/T/
2025-07-03T08:46:32.007 I main[1]:ListClasses:main:225                                 ListClasses started - Version 2025.07.03
2025-07-03T08:46:32.007 I main[1]:ListClasses:main:233                                 Command line parameters: ../lib/ch.k43.util.jar
2025-07-03T08:46:32.008 D main[1]:ListClasses:addJARFiles:76                           Found JAR file ../lib/ch.k43.util.jar
2025-07-03T08:46:32.008 D main[1]:ListClasses:main:359                                 Reading Java classes from ../lib/ch.k43.util.jar
2025-07-03T08:46:32.008 D main[1]:ListClasses:main:437                                 Skipping JAR entry META-INF/
2025-07-03T08:46:32.008 D main[1]:ListClasses:main:437                                 Skipping JAR entry META-INF/MANIFEST.MF
2025-07-03T08:46:32.009 D main[1]:ListClasses:main:437                                 Skipping JAR entry ch/k43/util/
2025-07-03T08:46:32.018 D main[1]:ListClasses:main:437                                 Skipping JAR entry META-INF/native-image/reachability-metadata.json
2025-07-03T08:46:32.019 D main[1]:ListClasses:main:441                                 28 Java classes read from file ../lib/ch.k43.util.jar
2025-07-03T08:46:32.019 D main[1]:ListClasses:main:448                                 Found 28 Java classes - Filtered 0 classes
...
```

## macOS Native Executable

The release includes a native macOS executable created with GraalVM. There is no need for any additional file to execute.

```bash
% ./listclasses ch.k43.util.jar 
ch.k43.util.K                                                             45795 2025-06-05T16:56:20 ch.k43.util.jar
ch.k43.util.KDB                                                           19731 2025-06-05T16:56:20 ch.k43.util.jar
ch.k43.util.KFile                                                          7334 2025-06-05T14:14:44 ch.k43.util.jar
ch.k43.util.KHTTPClient                                                   10570 2025-06-05T16:56:20 ch.k43.util.jar
ch.k43.util.KHTTPServerThread                                             11506 2025-06-05T16:56:20 ch.k43.util.jar
ch.k43.util.KHTTPServerThreadSample                                         577 2025-06-05T14:14:44 ch.k43.util.jar
ch.k43.util.KHelloWorld                                                    1451 2025-06-05T16:56:20 ch.k43.util.jar
ch.k43.util.KLocalData                                                      601 2025-06-05T14:14:44 ch.k43.util.jar
ch.k43.util.KLog                                                          13270 2025-06-05T16:56:20 ch.k43.util.jar
ch.k43.util.KLogCSVFormatter                                               3453 2025-06-05T14:14:44 ch.k43.util.jar
ch.k43.util.KLogJDBCHandler                                                6539 2025-06-05T14:14:44 ch.k43.util.jar
ch.k43.util.KLogJSONFormatter                                              3276 2025-06-05T14:14:44 ch.k43.util.jar
ch.k43.util.KLogLineFormatter                                              2518 2025-06-05T14:14:44 ch.k43.util.jar
ch.k43.util.KLogSMTPHandler                                                6549 2025-06-05T14:14:44 ch.k43.util.jar
ch.k43.util.KLogSMTPHandlerThread                                          2210 2025-06-05T14:14:44 ch.k43.util.jar
ch.k43.util.KLogXMLFormatter                                               3228 2025-06-05T16:56:20 ch.k43.util.jar
ch.k43.util.KLogYAMLFormatter                                              2607 2025-06-05T14:14:44 ch.k43.util.jar
ch.k43.util.KPasswordVault                                                 6079 2025-06-05T14:16:24 ch.k43.util.jar
ch.k43.util.KSMTPMailer$1                                                   824 2025-06-05T14:14:44 ch.k43.util.jar
ch.k43.util.KSMTPMailer                                                   12464 2025-06-05T16:56:20 ch.k43.util.jar
ch.k43.util.KSocketClient                                                 10492 2025-06-05T14:14:44 ch.k43.util.jar
ch.k43.util.KSocketServer                                                  7569 2025-06-05T14:14:44 ch.k43.util.jar
ch.k43.util.KSocketServerListener                                          5791 2025-06-05T14:14:44 ch.k43.util.jar
ch.k43.util.KSocketServerThread                                            7238 2025-06-05T14:14:44 ch.k43.util.jar
ch.k43.util.KSocketServerThreadSample                                      1501 2025-06-05T14:14:44 ch.k43.util.jar
ch.k43.util.KThread                                                        2096 2025-06-05T14:14:44 ch.k43.util.jar
ch.k43.util.KTimer                                                         1307 2025-06-05T14:14:44 ch.k43.util.jar
ch.k43.util.package-info                                                    117 2025-06-05T15:05:34 ch.k43.util.jar
% 
```


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
