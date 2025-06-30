import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
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
	static final String PROGRAM_NAME		= "ListClasses";
	static final String PROGRAM_VERSION		= "2025.06.30";
	static final KTimer	START_TIME			= new KTimer();
	static final int	MAX_CLASS_NAME_SIZE	= 70;

	/**
	 * Return list of jar files found in the Java classpath.
	 * 
	 * @return	List of jar files
	 */
	static void addClassPathJARFiles(ArrayList<String> argJARFiles) {
		
        String	classpath		= System.getProperty("java.class.path", "");
        int		jarFileCount	= argJARFiles.size();
        
        KLog.debug("Searching classpath {}", classpath);
        
        // Split the classpath using the system dependent path separator
        String[] paths = classpath.split(K.PATH_SEPARATOR);
        
        for (String path : paths) {
			addJARFiles(path, argJARFiles);
        }
        
        KLog.debug("{} JAR files found in classpath", argJARFiles.size() - jarFileCount);
	}
	
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

        // Step 1: Find all duplicates
        KLog.debug("Analyzing {} classes for duplicate class names", argList.size());
        
        for (String item : argList) {
        	
        	String className = item.substring(0, MAX_CLASS_NAME_SIZE);
        	
        	// Add item to the list of duplicates if already processed
            if (!copiedSet.add(className)) {
            	duplicateSet.add(className);
            }
        }
		
        // Step 2: Create new list with all duplicates
        ArrayList<String> duplicates = new ArrayList<>();
        
        for (String item : argList) {
        	
        	String className = item.substring(0, MAX_CLASS_NAME_SIZE);
        	
        	if (duplicateSet.contains(className)) {
            	duplicates.add(item);
        	}
        }

        return duplicates;
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
	 * @param args
	 */
	public static void main(String[] args) {

        ArrayList<String>	argJARFiles				= new ArrayList<>();
    	Pattern				argFilterRegExPattern	= null;
        String				argFilterRegEx			= null;
		boolean				argSearchClassPath		= false;
		boolean 			argFindDuplicates 		= false;
		boolean 			argSortResult	 		= false;
		boolean 			argAbsolutePath		 	= false;
		boolean				argVersion				= false;
		boolean				argHelp					= false;
		boolean				argFilter				= false;
		
		KLog.info("{} {} started", PROGRAM_NAME, PROGRAM_VERSION);
		
		//
		// Process command line arguments
		//
		if (args.length == 0) {
			logError("Usage: {} [-cp] [-d] [-s] [-a] [-fxxx] [-h] [-v] [file...]", PROGRAM_NAME);
		}

		KLog.debug("Program arguments: {}", String.join(" ", args));
		
		for (String arg : args) {
			
			// Pre-process -f parameter
			if (arg.startsWith("-f")) {
				
				if (arg.length() < 3) {
					logError("Error: -f argument must be followed by a RegEx expression");
				}
				
				argFilterRegEx = arg.substring(2);
				arg = "-f";
			}
			
			switch (arg) {
				case "-h": {
					if (argHelp) {
						logError("Error: Multiple -h arguments specified");
					}
					argHelp = true;
					break;
				}
				case "-v": {
					if (argVersion) {
						logError("Error: Multiple -v arguments specified");
					}
					argVersion = true;
					break;
				}
				case "-cp": {
					if (argSearchClassPath) {
						logError("Error: Multiple -cp arguments specified");
					}
					addClassPathJARFiles(argJARFiles);
					argSearchClassPath = true;
					break;
				}
				case "-a": {
					if (argAbsolutePath) {
						logError("Error: Multiple -a arguments specified");
					}
					argAbsolutePath = true;
					break;
				}
				case "-d": {
					if (argFindDuplicates) {
						logError("Error: Multiple -d arguments specified");
					}
					argFindDuplicates = true;
					break;
				}
				case "-f": {
					if (argFilter) {
						logError("Error: Multiple -f arguments specified");
					}
					argFilter = true;
					break;
				}
				case "-s": {
					if (argSortResult) {
						logError("Error: Multiple -s arguments specified");
					}
					argSortResult = true;
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
			logOut(" {} [-cp] [-d] [-s] [-a] [-fxxx] [-h] [-v] [file...]", PROGRAM_NAME);
			logOut("");
			logOut("Parameters:");
			logOut(" file  One or more JAR files or directories");
			logOut(" -cp   Include JAR files found in the current Java classpath");
			logOut(" -d    List only duplicate class names");
			logOut(" -s    Sort output by the Java class name");
			logOut(" -a    Show absolute path of JAR files");
			logOut(" -f    RexEx class name filter, e.g. -f\"ch.k43.util\"");
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
		
		// Check if any JAR file to be processed
		if (argJARFiles.isEmpty()) {
			logError("Error: No JAR file found to be processed");
		}
		
		//
		// Get all class files from all specified JAR files
		//
		ArrayList<String> classFiles = new ArrayList<>();
		
		for (String fileName : argJARFiles) {
			
			KLog.debug("Reading classes from {}", fileName);
			
			int jarFileCount = 0;
			
	        try (JarFile jarFile = new JarFile(fileName)) {
	            Enumeration<JarEntry> entries = jarFile.entries();

	            String formatterString = "%-" + MAX_CLASS_NAME_SIZE + 's';
	            
	            while (entries.hasMoreElements()) {

	            	JarEntry entry = entries.nextElement();

	                if (entry.getName().toLowerCase().endsWith(".class") && !entry.isDirectory()) {
	                	
	                	StringBuilder outputLine = new StringBuilder();
	                	
	                	// Java class name: Replace package name delimiter, remove ".class" extension and limit length
	                	String className = entry.getName().replace('/', '.');
	                	className = K.truncateMiddle(className.substring(0, className.length() - 6), MAX_CLASS_NAME_SIZE);
	                	outputLine.append(String.format(formatterString, className));

	                	// Class size
	                	outputLine.append(String.format(" %8d", entry.getSize()));
	                	
	                	// Date/Time: Convert to ISO 8601 and remove decimals from seconds
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

	                    // File name
	                    if (argAbsolutePath) {
	                    	fileName = Paths.get(fileName).toRealPath().toString();
	                    }
	                    
	                    outputLine
	                    	.append(' ')
	                    	.append(fileName);
	                    
	                    // Add it to array
	                    classFiles.add(outputLine.toString());
	                    jarFileCount++;
	                }
	            }
                
                KLog.debug("{} Java classes read from file {}", jarFileCount, fileName);
                
	        } catch (Exception e) {
	            logError("Error: Unable to read JAR file {}: {}", fileName, e.toString());
	        }
		}
		
		//
		// Apply RegEx filter by class name
		//
		if (argFilter) {
			
			argFilterRegExPattern		= Pattern.compile(argFilterRegEx);
	        Iterator<String> iterator	= classFiles.iterator();
	        int	listCounter				= 0;
	        
	        while (iterator.hasNext()) {
	        	
	            String className = iterator.next().substring(0, MAX_CLASS_NAME_SIZE).trim();
	            
	            if (!argFilterRegExPattern.matcher(className).find()) {
	                iterator.remove();
	                listCounter++;
	            }
	        }
	        
	        KLog.debug("RexEx class name filter removed {} items", listCounter);
		}
		
		//
		// Find duplicates
		//
		if (argFindDuplicates) {
			
			classFiles = getDuplicates(classFiles);

			if (classFiles.isEmpty()) {
	            logError("No duplicate class name found");
			} else {
	            KLog.debug("{} duplicate class names found", classFiles.size());
			}
		}

		//
		// Sort the class names
		//
		if (argSortResult) {
            KLog.debug("Sorting class names");
			Collections.sort(classFiles);
		}
		
		//
		// List class names
		//
        KLog.debug("Formatting {} Java classes", classFiles.size());
        
		if (classFiles.isEmpty()) {
			logOut("No matching classes found");
		} else {
			for (String entry : classFiles) {
				logOut(entry);
			}
		}
		
		KLog.info("{} ended ({} ms)", PROGRAM_NAME, START_TIME.getElapsedMilliseconds());
	}
}
