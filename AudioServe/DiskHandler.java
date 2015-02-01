package AudioServe;

import java.io.*;

/**
 * DiskHandler.java
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * <br>A class that provides robust methods to access the filesystem
 * <br>Returns error codes as defined in ErrorCodes
 *
 * @author Matthew Yee-King
 * @version 0.1
 * @see wisdom.server.errorCodes
 */

public class DiskHandler {

  /**
   *  <code>doesFileExist</code> - does the sent file or dir exist? returns true or false. 
   *
   * @param path a <code>String</code> value
   * @return a <code>boolean</code> value
   */
  public static boolean doesFileExist(String path){
    File f = new File(path);
    return f.exists();
  }

  public static int copyFile(String fromPath, String toPath){
    //ErrorCodes.print("copying "+fromPath+" to "+toPath);
    try {
      FileInputStream inStream = new FileInputStream(fromPath);
      
      File outputFile = new File(toPath);
      FileOutputStream outStream = new FileOutputStream(outputFile);
      
      BufferedInputStream in = new BufferedInputStream(inStream);
      BufferedOutputStream out = new BufferedOutputStream(outStream);

      int c;
      while ((c = in.read()) != -1)
	out.write(c);
      out.close();
      in.close();
      return ErrorCodes.TASK_SUCCESS;
    } catch (IOException e) {
      return ErrorCodes.TASK_FAILURE;
    }
  }

  /**
   *  <code>moveFile</code> - move the sent file from fromPath to toPath. 
   *
   * @param fromPath a <code>String</code> value - complete filepath where the file currently resides
   * @param toPath a <code>String</code> value - complete file path where the file should be moved to 
   * @return an <code>int</code> value
   */
  public static int moveFile(String fromPath, String toPath){
    
    if (copyFile(fromPath, toPath)==ErrorCodes.TASK_SUCCESS&&
	deleteFile(fromPath)) {
      return ErrorCodes.TASK_SUCCESS;
    }
    else {
      return ErrorCodes.TASK_FAILURE;
    }  
  } 
  

  /**
   * <code>getUniqueFileName</code> - creates a file name that is unique in the semt directory
   *
   * @param directory a <code>String</code> value
   * @return a <code>String</code> value
   */
  public static String getUniqueFileName(String directory){
    try {
      String [] names = getDirectoryContents(directory);
      String filename = "0.file";
      int count = 1;
      for (int i=0;i<names.length;i++ ) {
	if (names[i]!=null&&names[i].equals(filename) ) {
	  // not unique
	  filename = ""+count+".file";
	  count++;
	} 
	
      } 
      return filename;
    } catch (Exception e) {
      return null;
    } 
  }

  /**
   *  <code>getDirectoryContents</code> - get an array of the the contents of the sent directory.
   *
   * @param directory a <code>String</code> value
   * @return a <code>String[]</code> value
   */
  public static String[] getDirectoryContents(String directory){
    File[] all = (new File(directory)).listFiles();
    String[] names = new String [all.length];
    for (int i=0;i<all.length;i++ ) {
      names[i] = all[i].getName();
    } 
    return names;
  }
  
  /**
   * <code>deleteFile</code> - tries to delete the sent file
   *
   * @param fileName a <code>String</code> value
   * @return a <code>boolean</code> value
   */
  public static boolean deleteFile(String fileName){
    ErrorCodes.print("deleting a file; "+fileName);
    File f = new File(fileName);
    return f.delete();
  }


  /**
   *  <code>inputStreamToFile</code> - writes the sent inputstream to the sent file name
   *
   * @param in an <code>InputStream</code> value
   * @param targetFilePath a <code>String</code> value
   * @return an <code>int</code> value
   */
  public static String inputStreamToFile (InputStream inStream, String targetFilePath)throws IOException {
    //try {
      File outputFile = new File(targetFilePath);
      FileOutputStream outStream = new FileOutputStream(outputFile);
      BufferedInputStream in = new BufferedInputStream(inStream);
      BufferedOutputStream out = new BufferedOutputStream(outStream);
      int c;
      
      while ((c = in.read()) != -1)
	out.write(c);
      
      in.close();
      out.close();
      return "file written";
      //} catch (IOException eek){return "File error - problem writing file named: "+targetFilePath;}
  }
  
  /**
   *  <code>stringToFile</code> - writes the sent string to the sent filename
   *
   * @param fileString a <code>String</code> value
   * @param fileName a <code>String</code> value
   * @return a <code>String</code> value
   */
  public static String stringToFile(String fileString, String fileName){
    // run some checks on the file
        
    File file = new File(fileName);
    try{
      String path =  file.getCanonicalPath();
      file = new File(path);
      File parent = file.getParentFile();
      if (!parent.isDirectory() ) {
	return "File error - directory  "+parent.getPath()+" does not exist!";
      }
      if (!parent.canWrite() ) {
	return "File error - don't have premission to write to directory "+parent.getPath();
      }
      FileWriter fileWriter= new FileWriter(file);
      fileWriter.write(fileString, 0, fileString.length());
      fileWriter.close();
      return "file named: "+fileName+" written to disk!";
      //return FILE_WRITE_SUCCESS;
    } catch (IOException eek){return "File error - problem writing file named: "+fileName;}
  }
    
  /**
   *  <code>fileToString</code> - load the sent file and return a string of its contents. 
   *
   * @param fileName a <code>String</code> value
   * @return a <code>String</code> value
   */
  public static String fileToString(String fileName){
    String fileString = "";
    File file = new File(fileName);
    try {
      BufferedReader reader = new BufferedReader(new FileReader(file));
      String inputLine;
      while ((inputLine = reader.readLine()) != null){
	fileString += inputLine+"\n";
      }
      reader.close();
    }catch (Exception e){
      e.printStackTrace();
      return "";
    }
    
    return fileString;
  }

 


  /**
   *  <code>createDirectory</code> - create the sent directory.
   *
   * @param dirName a <code>String</code> value
   * @return an <code>int</code> value
   */
  public static int createDirectory(String dirName){
    System.out.println("creating dir: "+dirName);	
    File file = new File(dirName);
    if (file.mkdir() ) {
      return ErrorCodes.TASK_SUCCESS;
    } 
    else {
      return ErrorCodes.TASK_FAILURE;
    } 
  }


  public DiskHandler(){
  }
  
}
