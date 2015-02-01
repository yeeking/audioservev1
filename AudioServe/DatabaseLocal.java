package AudioServe;

import java.util.StringTokenizer;
import java.util.Properties;
//import java.io.StringBufferInputStream;
//import java.io.IOException;
//import java.io.File;
import java.io.*;

/**
 * DatabaseLocal.java
 *
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
 *
 * Created: Fri May 23 11:01:53 2003
 *
 * @author <a href="mailto:matthew@deaf-pc15">Matthew Yee-King</a>
 * @version
 */

public class DatabaseLocal extends Database {

  
  /**
   *  constant <code>chunkSize</code> - the size that gets read from files when doing random access to find a circuit
   *
   */
  public int chunkSize = 2000;

  protected boolean pushGenome(Individual individual){
    System.out.println("pushing a genome onto the server!");
    // open the file for writing
    int[] chr;
    try {
      FileWriter writer = new FileWriter(getUrl(), true);
      // write a genome to the end of the file.
      writer.write("\n<!--SpLiTmArKeR--!>");
      writer.write("\nname="+individual.name);
      writer.write("\nmaxAngle="+individual.maxAngle);
      writer.write("\nmaxGridSize="+individual.maxGridSize);
      writer.write("\nmaxRadius="+individual.maxSegmentRadius);
      chr = individual.getChromosome();
      writer.write("\nchromosome=");
      for (int i=0;i<chr.length;i++ ) {
	writer.write(""+chr[i]+" ");
      } 
      
      writer.write("\n\n");
      // close the file.
      writer.close();
    } catch (IOException oops) {
      return false;
    } // end of try-catch
    
    return true;

  }

  protected Individual pullGenome(){
    System.out.println("pulling a genome from the local db!");
    // the perl script essentially gets a single genome's data
    // and puts each parameter on a new line (removing paramname=)
    // then this is read in line by line to get an array of data.

    // the local text file reader should just be able to load a fragment
    // of the text file (representing a single genome's data) into 
    // a Properties hash which can then be used to create an individual
        
    // get the data for a single  genome from the data file
    String circuitData = getRandomGenomeDataFromFile();
    // use this fragment to create a Property hash
    Properties circuitProps = new Properties();
    
    try {
      circuitProps.load(new StringBufferInputStream(circuitData) );
    } catch (IOException e) {
      // oops!
      System.out.println("an error occurred prcessing the local database");
      return null;
    }
    // use the property hash to create an individual

    Individual indi = new Individual(true, 
				     stringToIntArray(circuitProps.getProperty("chromosome")), 
				     new Integer( circuitProps.getProperty("maxGridSize")).intValue(), 
				     new Integer( circuitProps.getProperty("maxRadius")).intValue(), 
				     new Integer( circuitProps.getProperty("maxAngle")).intValue(), 
				     circuitProps.getProperty("name"));
    
    // 
    return indi;
  }

  /**
   *  <code>getRandomGenomeDataFromFile</code> - loads all the genomes into memory from the datafile then returns one of them at random. 
   *
   * @return a <code>String</code> value
   */
  private String getRandomGenomeDataFromFile(){
    System.out.println("pulling a circuit from the file named "+getUrl());
    
    // get the file length
    long length;
    File file = new File(getUrl());
    length = file.length();
    System.out.println("file length="+length);
    // pick a random point to start searching
    long start = randomLong(length);
    //    System.out.println("file length:"+length+" starting to search at: "+start);
    // grab a decent chunk of the file (enough to def have the next circuit or two in)
    if (start+chunkSize>length ) {
	// overshot
	start-=chunkSize;
    } 
    
    // find the next circuit
    String fileChunk = "";
    try {
      RandomAccessFile reader = new RandomAccessFile(getUrl(), "r");
      reader.seek(start);
      byte[] chunk = new byte[chunkSize];
      reader.read(chunk);
      fileChunk = new String(chunk);
    } catch (Exception e) {
      e.printStackTrace();
    } // end of try-catch
    
    //    System.out.println("file chunk: "+fileChunk);
    String[] genomes = explodeString(fileChunk, "<!--SpLiTmArKeR--!>");
    System.out.println("got a genome:"+genomes[0]);
    return genomes[0];
    
  } 

    /**
   *  <code>explode</code> - equivalent to PHP explode function. splits the sent data on the sent separetor and returns an array of strings consisting of the bits in between the seperator.  Basically a wrapper for StringToeknizer methods. 
   *
   * @param str a <code>String</code> value
   * @param delim a <code>String</code> value
   * @return a <code>String[]</code> value
   */
  private  String[] explodeString(String str, String delim){
    // count occurrences of the delimiter

    int count=0,start=0, end=0;
    start = str.indexOf(delim);
    while (start!=-1 ) {
      count++;
      start = str.indexOf(delim, start+1);
    } 
    //    System.out.println("found "+count+" genomes in the file");
    String[] tokens = new String[count];
    count =0;
    
    // wrap around the first delim
    start = str.indexOf(delim);
    //    end = start+delim.length();
    
    while (start!=-1 ) {
      // move start to the start of the next delim
      end = start+delim.length();
      start = str.indexOf(delim, end);
      
      // check if we've overshot ths string
      if (start==-1 ) {
	// we've hit the last one and theres no final delimiter
	start = str.length();
      }
      
      //      System.out.println("adding a genome at count+"+count+" start at :"+end+" end at"+start);
      
      tokens[count] = str.substring(end, start);
      // move end on
      end = start+delim.length();
      if (end>str.length() ) {
	break;
      } 
      
      count++;
    } 
    
    
    return tokens;
  }

  private long randomLong(long max){
    double random = Math.random();
    random = random*max;
    //System.out.println("random = : " +random);

    return((long)random);
  }


  private int randomInt(int max){
    double random = Math.random();
    random = random*max;
    //System.out.println("random = : " +random);
    return((int)random);
  }
  

  public DatabaseLocal (int numberOfCircuitParameters, String url){
    super(numberOfCircuitParameters, url);
  }
  
}// DatabaseLocal
