package Morrowind;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;


public class MTEUpdater {

	private static String repo = "https://github.com/Tyler799/Morrowind-2019/";
	private static String versionURL = "https://raw.githubusercontent.com/Tyler799/Morrowind-2019/updater/mte-version.txt";
	private static String docTarget = "src/main/java/Morrowind/VersionDoc/";
	private static String localVersionTxt = docTarget + "mte-version.txt";
	private static String remoteVersion = docTarget + "mte-version.tmp";
	private static String repoArchive = repo + "archive/master.zip";
	private static String repoCompare = repo + "compare/";

	private static void downloadVersionDoc(){
		try {
			downloadUsingStream(versionURL, remoteVersion);
		} catch (IOException e) {
			System.out.println("ERROR: Unable to download guide version file!");
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		// Before we do anything else check if the version file exists
		File versionTxt = new File(localVersionTxt);
		if (!versionTxt.exists()) {
			System.out.println("ERROR: Unable to find mte version file!");
			return;//TODO if not found, create dummy file
		}
		
		System.out.println("Downloading mte version file...");

		 downloadVersionDoc();
		 
		 File versionTmp = new File(remoteVersion);
		 
		 System.out.println("Comparing version numbers...");
		 
		 String curVersion = readFile(versionTmp.getAbsolutePath());
		 String lastVersion = readFile(versionTxt.getAbsolutePath());
		 
		 if (!curVersion.equals(lastVersion)) {
		 	System.out.println("Your version of the guide is out of date");
		 	
		 	Scanner reader = new Scanner(System.in);
		 	System.out.println("Would you like to see a list of recent updates? (y/n)");
		 	
		 	// Continue asking for input until the user says yes or no
			boolean inputFlag = false;
		 	while (!inputFlag) {
		 		String input = reader.next();

			 	if (input.equals("yes") || input.equals("y")) {
			 		
			 		// Construct the URL in string format
			 		String urlString = repoCompare + lastVersion + ".." + curVersion;
			 		
			 		// Wrap the string with an URI 
			 		URI compareURL = null;
			 		try {
						compareURL = new java.net.URI(urlString);
					} catch (URISyntaxException e) {
						System.out.print("ERROR: URL string violates RFC 2396!");
						e.printStackTrace();
					}
					// Open the Github website with the compare arguments in URL
			 		try {
						java.awt.Desktop.getDesktop().browse(compareURL);
					} catch (IOException e) {
						System.out.print("ERROR: Unable to open web browser!");
						e.printStackTrace();
					}
			 		
			 		// Download repository files
			 		try {
						downloadUsingStream(repoArchive, "Morrowind-2019.zip");
					} catch (IOException e1) {
						System.out.println("ERROR: Unable to download repo files!");
						e1.printStackTrace();
					}//TODO Looks like this output is at the project level, dont think users can access it well from there
					//if at all. Might be what unzip method is for below

			 		System.out.println("Updating mte version file...");
			 		PrintWriter writer = null;
					try {
						writer = new PrintWriter(versionTxt);
						writer.print(curVersion);
						System.out.println("You're all set, good luck on your adventures!");
					} catch (FileNotFoundException e) {
						System.out.println("ERROR: Unable to find mte version file!");
					}
			 		writer.close();	
			 		inputFlag = true;
			 	}
			 	else if (input.equals("no") || input.equals("n")) {
			 		System.out.println("Not a wise decision, may the curse of Blight strike you down!");
			 		inputFlag = true;
			 	}
		 	}
		 	// Close the scanner here
		 	reader.close();
		 }
		 else System.out.println("Your version of the guide is up-to-date!");
		 
		 // Delete the temporary version file we created
		 try {
			 versionTmp.delete();
		 } catch (SecurityException e) {
			 System.out.println("ERROR: Unable to delete 'mte-version.tmp'!");
			 e.printStackTrace();
		 }
	}

	/** Here we are using URL openStream method to create the input stream. 
	 * Then we are using a file output stream to read data from the input stream and write to the file.
	 * @param urlStr 
	 * @param file
	 * @throws IOException
	 */
	private static void downloadUsingStream(String urlStr, String file) throws IOException 
	{
        URL url = new URL(urlStr);
        BufferedInputStream bis = new BufferedInputStream(url.openStream());
        FileOutputStream fis = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int count=0;
        while((count = bis.read(buffer,0,1024)) != -1)
        {
            fis.write(buffer, 0, count);
        }
        fis.close();
        bis.close();
    }
	
	/**
	 * Read from a text file and return the compiled string
	 * @param filename Name of the file to read from the root directory
	 * @return Content of the text file
	 */
	private static String readFile(String filename) {
		
		// Using Apache Commons IO here
		try(FileInputStream inputStream = new FileInputStream(filename)) {     
			 return IOUtils.toString(inputStream, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
     * Size of the buffer to read/write data
     */
    private static final int BUFFER_SIZE = 4096;
    /**
     * Extracts a zip file specified by the zipFilePath to a directory specified by
     * destDirectory (will be created if does not exists)
     * @param zipFilePath
     * @param destDirectory
     * @throws IOException
     */
    public void unzip(String zipFilePath, String destDirectory) throws IOException {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = zipIn.getNextEntry();
        // iterates over entries in the zip file
        while (entry != null) {
            String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                // if the entry is a file, extracts it
                extractFile(zipIn, filePath);
            } else {
                // if the entry is a directory, make the directory
                File dir = new File(filePath);
                dir.mkdir();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
    }
    /**
     * Extracts a zip entry (file entry)
     * @param zipIn
     * @param filePath
     * @throws IOException
     */
    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }
}
