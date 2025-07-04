import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import ch.k43.util.K;
import ch.k43.util.KLog;
import ch.k43.util.KTimer;

/**
 * List class names or find duplicates in JAR files.
 * 
 * @author andy.brunner@k43.ch
 */
public class ListClasses {

	// Constants
	static final String PROGRAM_NAME			= "ListClasses";
	static final String PROGRAM_VERSION			= "2025.07.04";
	static final KTimer	START_TIME				= new KTimer();
	static final int	MAX_CLASS_NAME_SIZE		= 70;

	/**
	 * Return list of jar files found in the Java classpath.
	 *
	 * @param	argJARFiles			Array with class names
	 */
	static void addClassPathJARFiles(ArrayList<String> argJARFiles) {
		
        String	classpath		= System.getProperty("java.class.path", "").trim();
        int		jarFileCount	= argJARFiles.size();

        // Return if empty classpath
        if (K.isEmpty(classpath)) {
        	KLog.debug("No entries in classpath found");
        	return;
        }
        
        KLog.debug("Searching classpath {}", classpath);
        
        // Split the classpath using the system dependent path separator
        String[] paths = classpath.split(K.PATH_SEPARATOR);
        
        for (String path : paths) {
			addJARFiles(path, argJARFiles);
        }
        
        KLog.debug("{} JAR files found in classpath", argJARFiles.size() - jarFileCount);
	}
	
	/**
	 * Add JAR file or all JAR file in directory to array.
	 * 
	 * @param argFileOrDirectory	File or directory to be checked
	 * @param argJARFiles			Array with class names
	 */
	static void addJARFiles(String argFileOrDirectory, ArrayList<String> argJARFiles) {
		
        File fileOrDirectory = new File(argFileOrDirectory);
        
        // Check file or directory exists
        if (!fileOrDirectory.exists()) {
        	logError("File {} does not exist", fileOrDirectory.getName());
        }

        // Check if passed name is a file and has JAR extension
        if (fileOrDirectory.isFile()) {
        	if (fileOrDirectory.getName().toLowerCase().endsWith(".jar")) {
        		argJARFiles.add(fileOrDirectory.getPath());
            	KLog.debug("Found JAR file {}", fileOrDirectory.getPath());
            	return;
        	} else {
            	KLog.debug("Skipping non-JAR file {}", fileOrDirectory.getName());
        		return;
        	}
        }

        // Search directory for JAR files
        File[] files = fileOrDirectory.listFiles();

        if ((files != null) && (files.length > 0)) {
           	for (File file : files) {
           		if (file.isFile() && (file.getName().toLowerCase().endsWith(".jar"))) {
           			argJARFiles.add(file.getPath());
                	KLog.debug("Found JAR file {}", file.getPath());
           		}
           	}
        }
	}
	
	/**
	 * Return list with duplicate class names.
	 *  
	 * @param	argList	List of entries with class names
	 * @return	List of all duplicate items
	 */
	static ArrayList<String> getDuplicates(ArrayList<String> argList) {

        Set<String>	copiedSet		= new HashSet<>();
        Set<String>	duplicateSet	= new HashSet<>();

        // Find all duplicates
        KLog.debug("Analyzing {} Java classes for duplicate names", argList.size());
        
        for (String item : argList) {
        	
        	String className = item.split(" ")[0];
        	
        	// Add name to list of duplicates if previously stored in hash set
            if (!copiedSet.add(className)) {
            	duplicateSet.add(className);
            }
        }
		
        // Create new list with all duplicates
        ArrayList<String> duplicates = new ArrayList<>();
        
        for (String item : argList) {
        	
        	String className = item.split(" ")[0];
        	
        	// Add name to list if found in list of duplicates
        	if (duplicateSet.contains(className)) {
            	duplicates.add(item);
        	}
        }

        return duplicates;
	}

	/**
	 * Return formatted serialVersionUID field from java class in JAR file.
	 * 
	 * @param argJARFile	JAR file name
	 * @param argClassName	Class name
	 * 
	 * @return Formatted field
	 */
	static String getUIDFromClassFile(String argJARFile, String argClassName) {

		// Load the class from the JAR file
		try (URLClassLoader classLoader = new URLClassLoader(new URL[]{ new File(argJARFile).toURI().toURL() },ListClasses.class.getClassLoader());
) {
			Class<?> clazz	= classLoader.loadClass(argClassName.replace("/", ".").replace(".class", ""));
            
			// Get serialVersionUID field
			Field field = clazz.getDeclaredField("serialVersionUID");
			field.setAccessible(true);

			// Check if field is "Static final long"
			if (field.getType() != long.class) {
				return "Not-Long-Type";
			}

			int fieldModifier = field.getModifiers();

			if (!Modifier.isStatic(fieldModifier)) {
				return "Not-Static-Modifier";
			}

			if (!Modifier.isFinal(fieldModifier)) {
				return "Not-Final-Modifier";
			}

			// Return long value converted to String
			return String.format("%d", field.getLong(null));

		} catch (NoSuchFieldException e1) {
			return "Not-Set";
		} catch (IllegalAccessException e2) {
			return "Illegal-Access";
		} catch (NoClassDefFoundError e3) {
			return "Not-Std-Class";
		} catch (Exception e0) {
			KLog.error("Unable to get SerialVersionUID of class {}: {}", argClassName, e0);
			return "Exception";
		}
	}

	/**
	 * Checks if application is running as GraalVM native executable.
	 */
	static void checkRunningUnderGraalVM() {

        String vmName		= System.getProperty("java.vm.name", "");
        String runtimeName	= System.getProperty("java.runtime.name", "");

        if (vmName.contains("GraalVM") || runtimeName.contains("GraalVM")) {
        	logError("Option -u not supported for GraalVM native executable due to restrictions in support for the Java Reflection API");
        }
	}
	
	/**
	 * Write error to standard output and terminate.
	 * 
	 * @param argMessage	Message to be written
	 * @param argParameters	Replace {} parameters
	 */
	static void logError(String argMessage, Object... argParameters) {
		System.err.println(K.replaceParams(argMessage, argParameters));
		System.exit(1);
	}
	
	/**
	 * Write message to standard output.
	 * 
	 * @param argMessage	Message to be written
	 * @param argParameters	Replace {} parameters
	 */
	static void logOut(String argMessage, Object... argParameters) {
		System.out.println(K.replaceParams(argMessage, argParameters));
	}
	
	/**
	 * Main entry point.
	 * 
	 * @param args	Command line arguments
	 */
	public static void main(String[] args) {

        ArrayList<String>	argJARFiles				= new ArrayList<>();
    	Pattern				argFilterRegExPattern	= null;
		boolean				argSearchClassPath		= false;
		boolean 			argFindDuplicates 		= false;
		boolean 			argSortResult	 		= false;
		boolean 			argAbsolutePath		 	= false;
		boolean				argVersion				= false;
		boolean				argHelp					= false;
		boolean				argFilter				= false;
		boolean				argSerialVersionUID		= false;
		
		KLog.info("{} started - Version {}", PROGRAM_NAME, PROGRAM_VERSION);
				
		//
		// Process command line arguments
		//
		if (args.length == 0) {
			logError("Usage: {} [-c] [-d] [-s] [-a] [-fxxx] [-u] [-v] [-h] [file...]", PROGRAM_NAME);
		} else {
			KLog.info("Command line arguments: {}", String.join(" ", args));
		}
		
		for (String arg : args) {
			
			// Check filter option
			if (arg.startsWith("-f")) {
				
				if (arg.length() < 3) {
					logError("Error: -f option must be followed by a RegEx expression");
				}
				argFilterRegExPattern = Pattern.compile(arg.substring(2));
				arg = "-f";
			}
			
			switch (arg) {
				case "-h": {
					if (argHelp) {
						logError("Error: Multiple -h options specified");
					}
					argHelp = true;
					break;
				}
				case "-v": {
					if (argVersion) {
						logError("Error: Multiple -v options specified");
					}
					argVersion = true;
					break;
				}
				case "-c": {
					if (argSearchClassPath) {
						logError("Error: Multiple -c options specified");
					}
					addClassPathJARFiles(argJARFiles);
					argSearchClassPath = true;
					break;
				}
				case "-a": {
					if (argAbsolutePath) {
						logError("Error: Multiple -a options specified");
					}
					argAbsolutePath = true;
					break;
				}
				case "-d": {
					if (argFindDuplicates) {
						logError("Error: Multiple -d options specified");
					}
					argFindDuplicates = true;
					break;
				}
				case "-f": {
					if (argFilter) {
						logError("Error: Multiple -f options specified");
					}
					argFilter = true;
					break;
				}
				case "-s": {
					if (argSortResult) {
						logError("Error: Multiple -s options specified");
					}
					argSortResult = true;
					break;
				}
				case "-u": {
					if (argSerialVersionUID) {
						logError("Error: Multiple -u options specified");
					}
					argSerialVersionUID = true;
					break;
				}
				
				default: {
					if (arg.startsWith("-")) {
						logError("Error: Option " + arg + " unrecognized");
					}
					addJARFiles(arg, argJARFiles);
					break;
				}
			}
		}

		//
		// Process -h command
		//
		if (argHelp) {
			logOut("Syntax:");
			logOut(" {} [-c] [-d] [-s] [-a] [-fxxx] [-u] [-v] [-h] [file...]", PROGRAM_NAME);
			logOut("");
			logOut("Options:");
			logOut(" file  One or more JAR files or directories");
			logOut(" -c    Include JAR files found in the current Java classpath");
			logOut(" -d    List only duplicate class names");
			logOut(" -s    Sort output by the Java class name");
			logOut(" -a    Show absolute path of JAR files");
			logOut(" -f    RexEx class name filter, e.g. -f\"ch.k43.util\"");
			logOut(" -u    Show serialVersionUID for each class");
			logOut(" -v    Show program version information");
			logOut(" -h    Show help page");
			return;
		}
		
		//
		// Process -v command
		//
		if (argVersion) {
			logOut("{} Version {}", PROGRAM_NAME, PROGRAM_VERSION);
			return;
		}
		
		//
		// Check if running as GraalVM native executable
		//
		checkRunningUnderGraalVM();
		
		// Check if any JAR file to be processed
		if (argJARFiles.isEmpty()) {
			logError("Error: No JAR file found to be processed");
		}
		
		//
		// Get all class files from all specified JAR files
		//
		ArrayList<String>	classFiles		= new ArrayList<>();
		String				formatterString = "%-" + MAX_CLASS_NAME_SIZE + 's';
        int					skippedEntries	= 0;
		
		for (String fileName : argJARFiles) {
			
			KLog.debug("Reading Java classes from {}", fileName);
			
			int jarFileCount = 0;

	        try (JarFile jarFile = new JarFile(fileName)) {
	        
	            Enumeration<JarEntry> entries = jarFile.entries();

	            while (entries.hasMoreElements()) {

	            	JarEntry entry = entries.nextElement();
	            	
	                if (entry.getName().toLowerCase().endsWith(".class") && !entry.isDirectory()) {

	                	StringBuilder outputLine = new StringBuilder();

	                	//
	                	// Java class name
	                	//
	                	
	                	// Replace package name delimiter and remove ".class" extension
	                    String className = entry.getName().replace('/', '.').replace(".class", "");

		            	// Check if RexEx filter matches
		            	if (argFilter && !argFilterRegExPattern.matcher(entry.getName()).find()) {
		            		skippedEntries++;
		    	           	continue;
		    	        }
	                    
	                    // Truncate it to maximum size
	                	outputLine
	                		.append(String.format(formatterString, K.truncateMiddle(className, MAX_CLASS_NAME_SIZE)));

	                	//
	                	// Class size
	                	//
	                	outputLine
	                		.append(String.format(" %8d", entry.getSize()));
	                	
	                	//
	                	// Date/Time
	                	//
	                	
	                	// Convert to ISO 8601 and remove decimals from seconds
	                    Calendar dateTime = Calendar.getInstance();
	                    
	                    long time = entry.getTime();
	                    if (time <= 0) {
	                    	time = System.currentTimeMillis();
	                    }
	                    dateTime.setTime(new Date(time));

	                    String dateTimeISO = K.getTimeISO8601(dateTime);

	                    outputLine
	                    	.append(' ')
	                    	.append(dateTimeISO.substring(0, dateTimeISO.length() - 4));

	                    //
	                    // Show serialVersionUID
	                    //
	                    if (argSerialVersionUID) {
		                    outputLine
		                    .append(' ')
		                    .append(String.format("%20s", getUIDFromClassFile(Paths.get(fileName).toRealPath().toString(), entry.getName())));
	                    }
	                    
	                    //
	                    // File name
	                    //
	                    outputLine
	                    	.append(' ')
	                    	.append(argAbsolutePath ? Paths.get(fileName).toRealPath().toString() : fileName);
	                    
	                    // Add it to array
	                    classFiles.add(outputLine.toString());
	                    jarFileCount++;
	                } else {
	                	KLog.debug("Skipping JAR entry {}", entry.getName());
	                }
	            }
                
                KLog.debug("{} Java classes read from file {}", jarFileCount, fileName);
                
	        } catch (Exception e) {
	            logError("Error: Unable to read JAR file {}: {}", fileName, e.toString());
	        }
		}
		
        KLog.debug("Found {} Java classes - Filtered {} classes" , classFiles.size(), skippedEntries);
		
		//
		// Find duplicates
		//
		if (argFindDuplicates) {
			
			classFiles = getDuplicates(classFiles);

			if (classFiles.isEmpty()) {
	            logError("No duplicate Java class names found");
			} else {
	            KLog.debug("{} duplicate Java class names found", classFiles.size());
			}
		}

		//
		// Sort the class names
		//
		if (argSortResult) {
            KLog.debug("Sorting Java class names");
			Collections.sort(classFiles);
		}
		
		//
		// List class names
		//
		if (classFiles.isEmpty()) {
			logOut("No matching Java classes found");
		} else {
			for (String entry : classFiles) {
				logOut(entry);
			}
		}
		
		KLog.info("{} ended (Elapsed time {} ms)", PROGRAM_NAME, START_TIME.getElapsedMilliseconds());
	}
}
