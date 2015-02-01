package AudioServe;
import java.net.*; 
import java.io.*; 


/**
 * DatabasePerl.java
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
 *
 * Created: Fri May 23 11:02:34 2003
 *
 * @author <a href="mailto:matthew@deaf-pc15">Matthew Yee-King</a>
 * @version
 */

public class DatabasePerl extends Database {

  protected boolean pushGenome(Individual individual){
    String[] POSTHash =formatIndividualForSending(individual); 
    try {
      URL url = new URL(getUrl());
      URLConnection connection = url.openConnection();
      connection.setDoOutput(true);
	
      PrintWriter out = new PrintWriter(
					connection.getOutputStream());
      submitGenome(POSTHash, out);
      // open an instream to complete the POST request
      BufferedReader in = new BufferedReader(
					     new InputStreamReader(
								   connection.getInputStream()));
	
      in.close();
      return true;

    }catch (IOException e) {return false;}
  }

 
  protected Individual pullGenome(){
    Individual returnedIndividual;  
    URLConnection connection = getConnection(getUrl()+"?single"); 
    //    counter++;
    
    if(connection!=null){
      // pull some info from the server and turn it into an individual object
      returnedIndividual = receiveGenome(returnURLReader(connection));
      // add this inidividual to the stock of received individuals
      return returnedIndividual;
    }//end getNewCircuit
     // this allows this method to be recalled by the run method
    return null;
  }

  private BufferedReader returnURLReader (URLConnection connection)
  {
    // this method returns a BufferedReader for the URL connection set up
    // by getConnection

    try
      {
	//String inputLine;
	BufferedReader urlInBuffer = new BufferedReader(new InputStreamReader(connection.getInputStream()));	
	return urlInBuffer; 
      } catch (IOException e) {return null; }
  }  // end returnURLReader

  private Individual receiveGenome(BufferedReader reader){
    // takes a buffered reader and pulls genome information from it
    // then converts this info to an individual object
    // can be used to read from a file or a URL
    Individual immigrant;
    // 10 lines should way enough
    String[] inputLine = new String[10];
    Individual tempIndividual2;
    if (reader!=null){
      try{
	
	int lineCounter = 0;
	while (((inputLine[lineCounter] = reader.readLine()) != null)&&
	       (lineCounter<inputLine.length)){
	  lineCounter++;
	}
	reader.close();
      } catch (IOException e) {return null; }
    }
    
    // now generate an Individual from the pulled information
    // sticking with the checks as input from server can be unpredictable...
    if(inputLine[5]!=null){// at least 5 lines read from server
      String name = inputLine[1];
      int maxGridSize = stringToInt(inputLine[2]);
      int  maxAngle = stringToInt(inputLine[3]);
      int maxRadius = stringToInt(inputLine[4]);
      int[] chromosome = stringToIntArray(inputLine[5]);
      //make the individual
      tempIndividual2 = new Individual(true, chromosome, maxGridSize, maxRadius, maxAngle, name);
      
      return tempIndividual2;
    }
    return null;
  }// end receiveGenome()
  


  private void submitGenome(String[] genomeInfo, PrintWriter outStream){
    // takes some formatted genome info and an outstream and sends
    // the genome there.
    // can send to a url or a file
    
    outStream.println(genomeInfo[0]+"&"+genomeInfo[1]+"&"+genomeInfo[2]+"&"+genomeInfo[3]+"&"+genomeInfo[4]);
    outStream.close();
      
    return;
  }// emd pushgenome


  private URLConnection getConnection(String URLString){
     //this.URLString = URLString;
     // code to avoid repeated calls to the same URL...    
    try 
      {	
	//readOut.replace("trying to connnect to:\n  " + URLString, 0);
	URL urlObject = new URL(URLString);
	URLConnection urlConn = urlObject.openConnection();
	//System.out.println("connected to "+URLString);
	return urlConn;
      } 
    catch (MalformedURLException e) {return null;} 
    catch (IOException e) {return null; }    
    //return null;
  }// end getConnection
  
  private String[] formatIndividualForSending(Individual immigrant){
      // reads the fields from the individual and turns them into a POST - able form
    // i.e an array of  HashTable style key=value pairs
    // this could sit in the individual object but it 
    // is handy having POST related stuff in the 
    // network object(Immigration) 
    String[] postHash = new String[getNumberOfCircuitParameters()];
    
    postHash[0] = "maxAngle="+immigrant.maxAngle;
    postHash[1] = "maxRadius="+immigrant.maxSegmentRadius;
    postHash[2] = "maxGridSize="+immigrant.maxGridSize;
    postHash[3] = "name="+immigrant.name;
    postHash[4] = "chromosome="+immigrant.getChromosomeString();
    
    return postHash;
  
  }

  public DatabasePerl(int numberOfCircuitParameters, String url){
    super(numberOfCircuitParameters, url);
  }
  
  
}// DatabasePerl
