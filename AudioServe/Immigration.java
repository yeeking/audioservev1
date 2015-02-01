package AudioServe;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import java.net.*; 
import java.io.*; 

/**
 * Immigration.java
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
 * Threaded class that receives and submits circuits to the server
 * the run method chugs away sending and receiving circuits.
 *
 *
 *
 * Created: Mon Aug 14 16:42:20 2000
 *
 * @author 
 * @version
 */

public class Immigration extends Thread {

  private Database database;
  

  
  // define the mode flags - this object can be initialised
  // in local or remote mode. in local mode, a database file must be specified. 
  
  public static int MODE_LOCAL = -1;
  public static int MODE_SERVER = -2;
  public static int MODE_DEFAULT = MODE_SERVER;
  
  
  /** remember what mode we are in  */
  private int databaseMode;


 
  private Individual[] individualsToSend; // stores the genomes that the user wants to submit
  private Individual sendMe = null; // sendGenomeToServer sends this
  //private Individual circuitToReplace; // a pointer to a member of the population 
                                       // that can be replaced
  private Individual[] individualsReceived;  // stores the incoming genomes
  private int counter=0;
  private int heapCounter;
  private boolean keepGoing = true;
  private Individual lastSentGenome;
  private boolean flush = false;
  private int maxRadius; 
  private int maxAngle; 
  private int maxGridSize; 
  private String name; 
  private int[] chromosome; 
  private int numberOfParameters = 5;
  private int i;
  private int submitCounter=0;
  private int receiveCounter=0;
  private int actuallySentCounter=0;
  private boolean sendComplete = true;
  private boolean receiveComplete = true;
  private boolean inListFull;
  /*************************/
  /* GUI components        */
  /*************************/
  
  int outListCalls = 0; // temp variable
  private Panel mainPanel;  // to show the activity of this object
  private TextDisplay readOut; // to show text messages
  private TextDisplay outListDisplay; // shows the circuits waiting to be sent
  private java.awt.List inList; // shows the circuits that have come in from the server
  

  /**
   * Get the value of database.
   * @return value of database.
   */
  public Database getDatabase() {
    return database;
  }
  
  /**
   * Set the value of database.
   * @param v  Value to assign to database.
   */
  public void setDatabase(Database  v) {
    this.database = v;
  }
  

  /**
   * Get the value of databaseMode.
   * @return value of databaseMode.
   */
  public int getDatabaseMode() {
    return databaseMode;
  }
  
  /**
   * Set the value of databaseMode.
   * @param v  Value to assign to databaseMode.
   */
  public void setDatabaseMode(int  v) {
    this.databaseMode = v;
  }
  
  
  /**
     * Get the value of keepGoing.
     * @return Value of keepGoing.
     */
  public boolean getKeepGoing() {return keepGoing;}
  
  /**
     * Set the value of keepGoing.
     * @param v  Value to assign to keepGoing.
     */
  public void setKeepGoing(boolean  v) {this.keepGoing = v;}

  public void run() {

    // first of all, grab a bunch of circuits from the server
    initialise();

    while (keepGoing){
      // get a new circuit from the server
      if (receiveComplete==true){
	getNewCircuit();
      }
      // send selected circuits to the server
      if(sendComplete==true){
	sendGenometoServer();
      }
      // get messages from the server
      
      
      getMessages();
      
      // send a message to the server
      
      sendMessages();
      
      if ((inListFull==false)&&(inList.getItemCount()==individualsReceived.length)){
	// the inList has not yet been made visible and its full
	inList.setVisible(true);
	inListFull = true;
      }
      
      try {
	// sleep for a few secs
	Thread.sleep(15000);
      } catch (InterruptedException e){
	// the VM doesn't want us to sleep anymore,
	// so get back to work
      }
    }
  }// end run
  
  private void initialise(){
    // grabs an array full of circuits from the server
    int u;
    for (u=0; u<individualsReceived.length;u++){
      getNewCircuit();
    }
  }

  private void flushtoServer(){
    // called when the prog is exiting, this flushes all the circuits 
    // int he individualsToSend Array to the circuit
    int p;
    flush = true;
    for(p=0; p<individualsToSend.length; p++){
      sendMe = individualsToSend[p];
      sendGenometoServer();
      
    }
  }

  private void addGenomeToInList(Individual individual){
    // same as addGenometoOutList except it receives individuals from the 
    // server rather than the current population
    
    receiveCounter++;
      
    for (heapCounter=1; heapCounter<individualsReceived.length; heapCounter++){
      if (individualsReceived[heapCounter] != null){ // theres something there to shift!
	individualsReceived[heapCounter-1] = individualsReceived[heapCounter];
      }
      
    }
    individualsReceived[individualsReceived.length-1] = individual;
    // update the java.awt.list of received individuals
    updateInList();
    return;

  }// end addGenometoInList
  
   
  public Individual getImmigrant(int index){
    // returns a developed individual (--> ciruitSpec and display ready to go)
    return individualsReceived[index];
  }

  public void addGenometoOutList(Individual individual){
    // this is called by the population object.
    // it takes the essential parts of an Individual object
    // (maxAngle, maxRadius, maxGridSize, int[] chromosome, name)
    // and adds this minimal individual to the backlog stack 
    // that the immigration object  is working through.
    // the 'backlog stack' is an array of circuits that need to be sent to the server
    // as they have been labelled as good by the user
    maxRadius = individual.maxSegmentRadius;
    maxAngle = individual.maxAngle;
    maxGridSize = individual.maxGridSize;
    name = individual.name;
    chromosome = individual.getChromosome();
    
    // make a new individual from these fields
    Individual tempIndi = new Individual(false, chromosome, maxGridSize, maxRadius, maxAngle, name);
    submitCounter++;
    // this code runs a FIFO heap of compact versions of individuals that are waiting
    // to be sent to the server. this array is used by the sendCircuit method
    // each time the sendCircuit method is called it 
    // sends the individual at index 0 in the individualsToSend Array
    // index 0 is null until the array is full. 
   
    // put the individual at index 0 to the sendMe position..
    if (individualsToSend[0] != null){
      sendMe = individualsToSend[0];
    }
      
    for (heapCounter=1; heapCounter<individualsToSend.length; heapCounter++){
      if (individualsToSend[heapCounter] != null){ // theres something there to shift!
	
	individualsToSend[heapCounter-1] = individualsToSend[heapCounter];
	
      }
      
    }
    // assign the newly created individual to the top of the heap. 
    individualsToSend[individualsToSend.length-1] = tempIndi;
    // finally update the list of outgoing circuits on the display panel
    updateOutList();
    
    return;
    
  }// end addgenometoOutList


  private synchronized void getNewCircuit(){
    //System.out.println("getting new circuit. ");
    // connects to the server and pulls a genome from it. 
    // use getConnection to get a connection to the url
    // use getURLReader to get a BufferedReader
    // send the BufferedReader to pullGenome to pull info from the server and
    // translate it into an indvidual object.
    receiveComplete = false;
    addGenomeToInList(getDatabase().pullGenome());
    receiveComplete = true;
  }
  
  private void sendGenometoServer(){
    // System.out.println("sending a circuit. ");
    // if sendMe is different from the genome 
    // (every time it changes, submitCounter changes too)
    // this sends an individuals details to the server
    // 
    // uses formatIndividualForSending to get a hash of the individuals properties
    // gets its own connection to the server
    // calls pushGenome, sending it the hash and a printWriter pointing to the server
    // then gets an in stream from the server to complete the POST transaction
    // sendComplete is set to false while its working to prevent it from being recalled 
    // in the middle of sending a genome
    
    sendComplete = false;
    
    

    if(((sendMe!=null)&&(submitCounter>actuallySentCounter))||
       (flush==true)){
      
      // there is something to send and its not the same as the last thing that got sent...
      lastSentGenome = individualsToSend[0];
      // posting code - to completet a post transaction with the web server
      // must send data and recieve confirmation.
      getDatabase().pushGenome(sendMe);
      readOut.add("\n"+sendMe.name+" submitted to server", 1);
      
    }
    
  
    // sync this methods activity counter
    actuallySentCounter=submitCounter;
    // allow this method to be recalled by the run method
    sendComplete = true;
    return;
  }// emd sendGenoneToServer




  private void updateInList(){
    // updates the names shown on the list of received circuits
    redrawInList();
  }

  private void updateOutList(){
    // updates the display with a list of circuits waiting to be sent.
    outListDisplay.replace(getOutList(), 0);
  }

  private String getOutList(){
    // returns a formatted string of the names of the circuit
    String formattedList = "Circuits to be sent:";
    for(i=0; i<individualsToSend.length;i++){
      if(individualsToSend[i]!=null){
	// append its name to the String
	formattedList = formattedList+"\n"+individualsToSend[i].name;
      }
    }
    

    return formattedList;
  }

  private void getMessages(){
    // used for user to user comms. 
    // gets messages from other users
    return;
  }

  private void sendMessages(){
    // used for user to user comms. 
    // sends messages from local user to the server
    return;
  }
  
  private void buildGUI(){
    // sets up the display panel for the immigration object's activities
    mainPanel.setLayout(new GridLayout(2,1));
    mainPanel.add(readOut = new TextDisplay(4, 25));
    mainPanel.add(outListDisplay=new TextDisplay(4, 25));
    readOut.replace("immigration object running!", 2);
  }
  
  private void redrawInList(){
    int listCounter;
    inList.removeAll();
    for (listCounter=0;listCounter<individualsReceived.length;listCounter++){
      if(individualsReceived[listCounter]!=null){
	inList.add("immigrant "+listCounter+" "+individualsReceived[listCounter].name);
      }
    }
  }

   public Immigration(String str, Panel networkDisplay, java.awt.List inList, int databaseMode, String url) {
    super(str);
    this.mainPanel = networkDisplay;
    //this.circuitToReplace = replaceMe;
    this.inList = inList;
    buildGUI();
    individualsToSend = new Individual[1];
    individualsReceived = new Individual[8];
    // this is for the sendGenome method so it knows if a genome has already been sent
    lastSentGenome = individualsToSend[0];
    this.setDatabaseMode(databaseMode);
    
    if (databaseMode==MODE_LOCAL ) {
      this.setDatabase(new DatabaseLocal(this.numberOfParameters, url));
    } 
    if (databaseMode==MODE_SERVER ) {
      this.setDatabase(new DatabasePerl(this.numberOfParameters, url));
    } 

  }
  
} // Immigration
