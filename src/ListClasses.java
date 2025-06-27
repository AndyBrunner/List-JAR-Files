import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import ch.k43.util.K;
import ch.k43.util.KLog;

/**
 * List class names of JAR file(s).
 */
public class ListClasses {

	// Constants
	static final String PROGRAM_NAME		= "ListClasses";
	static final String PROGRAM_VERSION		= "2025.06.27";
	static final int	MAX_CLASS_NAME_SIZE	= 50;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

        ArrayList<String>	argJARFiles			= new ArrayList<>();
		boolean				argSearchClassPath	= false;
		boolean 			argFindDuplicates 	= false;
		boolean 			argSortResult	 	= false;
		boolean				argVersion			= false;
		boolean				argHelp				= false;
		
		KLog.info("{} {} started", PROGRAM_NAME, PROGRAM_VERSION);
		
		// Process command line arguments
		if (args.length == 0) {
			System.out.println("Usage: " + PROGRAM_NAME + " [-cp] [-d] [-s] [-h] [-v] jarfile ...");
			return;
		}
		
		for (String arg : args) {
			
			switch (arg) {
				case "-h": {
					if (argHelp) {
						System.err.println("Error: Multiple -h arguments specified");
						System.exit(1);
					}
					argHelp = true;
					break;
				}
				case "-v": {
					if (argVersion) {
						System.err.println("Error: Multiple -v arguments specified");
						System.exit(1);
					}
					argVersion = true;
					break;
				}
				case "-cp": {
					if (argSearchClassPath) {
						System.err.println("Error: Multiple -cp arguments specified");
						System.exit(1);
					}
					
					argJARFiles.addAll(getClassPathJARFiles());
					
					argSearchClassPath = true;
					break;
				}
				case "-d": {
					if (argFindDuplicates) {
						System.err.println("Error: Multiple -d arguments specified");
						System.exit(1);
					}
					argFindDuplicates = true;
					break;
				}
				case "-s": {
					if (argSortResult) {
						System.err.println("Error: Multiple -s arguments specified");
						System.exit(1);
					}
					argSortResult = true;
					break;
				}
				default: {
					argJARFiles.add(arg);
					break;
				}
			}
		}

		// Process -h command
		if (argHelp) {
			System.out.println("Usage:\n " + PROGRAM_NAME + " [-cp] [-d] [-s] [-v] [-h] jarfile ...");
			System.out.println("");
			System.out.println("Options:\n -cp Include all JAR files found in the current Java class path");
			System.out.println(" -d  List only duplicate class names");
			System.out.println(" -s  Sort output by the Java class name");
			System.out.println(" -v  Show program version information");
			System.out.println(" -h  Show this help page");
			return;
		}
		
		// Process -v command
		if (argVersion) {
			System.out.println(PROGRAM_NAME + " Version " + PROGRAM_VERSION);
			return;
		}
		
		// Check if any JAR file to be processed
		if (argJARFiles.isEmpty()) {
			System.err.println("Error: No JAR file given");
			System.exit(1);	
		}
		
		// Get all class files from all specified JAR files
		ArrayList<String> classFiles = new ArrayList<>();
		
		for (String fileName : argJARFiles) {
			
			KLog.debug("Reading classes from {}", fileName);
			
	        try (JarFile jarFile = new JarFile(fileName)) {
	            Enumeration<JarEntry> entries = jarFile.entries();

	            while (entries.hasMoreElements()) {

	            	JarEntry entry = entries.nextElement();

	                if (entry.getName().toLowerCase().endsWith(".class") && !entry.isDirectory()) {
	                	
	                	StringBuilder outputLine = new StringBuilder();
	                	
	                	// Java class name: Replace package name delimiter, remove ".class" extension and limit length
	                	String className = entry.getName().replace('/', '.');
	                	className = K.truncateMiddle(className.substring(0, className.length() - 6), MAX_CLASS_NAME_SIZE);
	                	outputLine.append(String.format("%-" + MAX_CLASS_NAME_SIZE + 's', className));

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
	                    outputLine
	                    	.append(' ')
	                    	.append(fileName);
	                    
	                    // Add it to array
	                    classFiles.add(outputLine.toString());
	                }
	            }
	        } catch (Exception e) {
	            System.err.println("Error: Unable to read JAR file " + fileName + ": " + e.toString());
				System.exit(1);
	        }
			
		}
		
		// Find duplicates
		if (argFindDuplicates) {
			
			classFiles = getDuplicates(classFiles);

			if (classFiles.isEmpty()) {
	            System.out.println("No duplicate class name found");
				System.exit(0);
			}
		}

		// Sort the class names
		if (argSortResult) {
			Collections.sort(classFiles);
		}
		
		// List class names
		for (String entry : classFiles) {
			System.out.println(entry);
		}
		
		KLog.info("{} ended", PROGRAM_NAME);
		
	}
	
	/**
	 * Return list of jar files found in the Java class path.
	 * 
	 * @return	List of jar files
	 */
	static ArrayList<String> getClassPathJARFiles() {
		
        String classpath = System.getProperty("java.class.path");

        KLog.debug("Searching class path {}", classpath);
        
        // Split the class-path using the appropriate path separator
        String[] paths = classpath.split(K.PATH_SEPARATOR);

        ArrayList<String> fileList = new ArrayList<>();
        
        for (String path : paths) {
            if (path.toLowerCase().endsWith(".jar")) {
            	fileList.add(path);
            }
        }
        
        KLog.debug("Found {} JAR files in classpath", fileList.size());
        
        return fileList;
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
        
        KLog.debug("Found {} duplicate class names", duplicateSet.size());
        
        return duplicates;
	}
}
