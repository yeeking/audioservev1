package AudioServe;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import com.softsynth.jsyn.*;
import com.softsynth.jsyn.view11x.*;
/**
 * AudioServe.java
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
 * [195.8.71.92] is ash server ip...
 *
 * bug - when mutating with ciruits with diff settings to those defined here
 * first gen fine, then the mutants take on the properties of the 
 * default defined in this class
 *
 * Created: Sat Aug 05 21:29:22 2000
 *
 * @author 
 * @version
 */

public class AudioServe extends Applet  {

  //private Thread appletThread = null;
  private String perlScript = "http://www.ash.org.uk/cgi-bin/post/post.pl";
  private String textDatabase = "/home/matthew/Java/classes/AudioServe/server/genomes.txt";

  public Individual selectedIndividual; // to act as 'references' (since there's no such 
                                        // thing as a pointer, right?)
                                        // to chosen individuals and circuits 
  public Circuit selectedCircuit;       // the circuit pointer is used to deal with audio calls
                                      // the individual deals with non-audio calls, like
                                      // inidivual.displayCircuit()

  
  private int appletSize = 500;
  public Frame circuitFrame;
  private boolean audio = true;
  private boolean startCalled = false;
  private boolean buildProblem;
  private Population population;
  private int populationSize = 25;

  private int genomeSize = 20;
  private int maxGridSize = 25;
  private int maxSegmentRadius = 100;
  private int maxSegmentAngle = 360;
  public Canvas canvas;
  public int maxHeight;
  public int maxWidth;
  private double mutationRate;
  private int mutationMode;
  
  /****************************/
  /*    immigration fields    */
  /****************************/

  Immigration immigration;  // deals with all network requests.
  

  /******************************************************/
  /* variables used to store the history of populations */
  /******************************************************/

  private PopulationHistory history;   // history of populations
                                        
  
  private int historyLength = 10;         // the length of the population history (funnily enough)
  private int populationCounter=0;       // keeps track of the currently selected population 
  
  /****************************/
  /*    jsyn fields           */
  /****************************/

  public LineOut myOut;

  /*****************************/
  /*    GUI FIELDS             */
  /*****************************/


  // the candidate list
  Panel leftPanel; // leftPanel sits in the west part of the main GUI
  java.awt.List candidateList;
  ItemListener listChoice;
  int selectedCircuitIndex = 0;
  java.awt.List historyList;
  ItemListener popChoice;
  int historyCounter = 0;
  boolean restarted = true; // controls drawing options on candidate list (parent/not)
  boolean mutated = false;
  // the text readout panel sits in the south part of leftPanel 
  Panel textDisplayPanel; 
  TextDisplay textDisplay;
  boolean textChanged = false;
  
  // the population control buttons
  Panel leftRightPanel; 
  Panel rightPanel;
  boolean mutateOnce = false;
  Panel populationControlPanel;
  Button mutateButton;
  String mutateButtonHelpString = "generate a new population of mutants of the currently selected circuit";
  Panel circuitScrollbarPanel;
  Scrollbar mRateScrollbar;
  AdjustmentListener mRateListener;
  Label mRateLabel;
  Button restartButton;
  String restartButtonHelpString = "generate a fresh population of random circuits"; 
  Button submitButton;
  Button nameButton;
  TextField nameText;
  ButtonAdapter buttonClick;
  
  
  // the Panel for the immigration object
  Panel immigrationPanel;
  ItemListener immigrantChoice;// event listener for the list
  java.awt.List inList; // shows the circuits that have come in from the server
  int i;
  
  // vars for arguments passed to the app
  private int databaseMode = 0;

  public static void main(String argv[]){
    AudioServe  prog = new AudioServe();
    // process the arguments
    for (int i=0;i<argv.length;i++ ) {
      // check for server/ local
      if (argv[i].equals("server") ) {
	prog.setDatabaseMode(Immigration.MODE_SERVER);
	System.out.println("running in remote database mode");
      } 
      else {
	if (argv[i].equals("local") ) {
	  System.out.println("running in local database mode");
	  prog.setDatabaseMode(Immigration.MODE_LOCAL);
	  prog.textDatabase = argv[i+1];
	} 
	
      }
    }
    if (prog.getDatabaseMode()==0 ) {
      // no options passed
      System.out.println("Usage: java AudioServe.AudioServe remote|local [local database file] ");
      System.exit(1);
      prog.setDatabaseMode(Immigration.MODE_SERVER);
    }
    
    AppletFrame frame = new AppletFrame("AudioServe", prog);
    frame.setSize(prog.appletSize, prog.appletSize);
    frame.show();
    frame.test();
    
    return;
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
  

  public void init(){
    
    // get everything ready that is nothing to do with audio (start() sorts out audio init)
    // instantiate the gui

    selectedIndividual = null;
    selectedCircuit = null;
    populationCounter = 0;
    population = new Population(populationSize);
   
    population.initialiseRandom(genomeSize, maxGridSize, maxSegmentRadius, maxSegmentAngle);
    history = new PopulationHistory(historyLength, population);
    
    circuitFrame = new Frame("Circuit display");
    circuitFrame.setSize(400,400);
    circuitFrame.setResizable(false);
    circuitFrame.setLocation(500,10);
    circuitFrame.show();
    circuitFrame.add(canvas = new Canvas());
    canvas.setSize(400,400);
    buildGUI();

  }

  public void start(){
   
    runApplet();

    /*    if (appletThread == null) {
            appletThread = new Thread(this, "Applet");
            appletThread.start();
	    }*/
  }// end start

  
  public void run() {
    //    Thread myThread = Thread.currentThread();
    runApplet();
  }// end run
    
  public void runApplet(){
    population.circuits[0].drawCircuit(canvas);
    if (getDatabaseMode()==Immigration.MODE_SERVER ) {
      immigration = new Immigration("networker", immigrationPanel, inList, getDatabaseMode(), perlScript);
    } 
    if (getDatabaseMode()==Immigration.MODE_LOCAL ) {
      immigration = new Immigration("networker", immigrationPanel, inList, getDatabaseMode(), textDatabase);
    } 
    

    immigration.start();
    
    if (audio == true){
       try
	 
	 {
	   Synth.startEngine(0);
	   
	   // build all the circuits so they are ready to be connected...
	   boolean built;
	   /*for (i=0; i<populationSize; i++){
	     
	     built = population.circuits[i].build();
	     if(built==false){System.out.println("prob with cicuit "+i);}
	     System.out.println("audio server start()Instantiated circuit "+i);
	     }*/
	   myOut = new LineOut();
	   myOut.start();
	   getParent().validate();
	   getToolkit().sync();
	   
	   // initialise the buttons...
	   //mutateClicked();
	   restartClicked();
	   
	 } catch(SynthException e) {
	   SynthAlert.showError(this,e);
	 }
    }
    startCalled = true;
    return;
  }
  
  
  public void stop(){
    submitClicked();
    removeAll();
    // flush unsent circuits...
    //immigration.flushtoServer();
    immigration.setKeepGoing(false);
    
    if (audio == true){
      try
	{
	  removeAll();
	  circuitFrame.dispose();
	  if (selectedCircuit != null){selectedCircuit.stop();}
	  myOut.stop();
	  Synth.stopEngine();
	  
	} catch(SynthException e) {
	  SynthAlert.showError(this,e);
	}
    }
    //    appletThread=null;
  }
  
  public void buildGUI(){
    
    // main gui has borderlayout
    // on west side is left panel...

    // leftPanel contains the main population controls
    // it has two columns
    // the left column contains the candidate list
    // the right column has two rows-->two further panels, 
    // historyPanel holds the generation history
    // populationControlPanel contains the restart, mutate and mutate rate
    // (slider and label)
    this.setBackground(Color.black);
    this.setLayout (new BorderLayout ());
    this.add("West", leftPanel = new Panel());
    leftPanel.setLayout(new GridLayout(1,2));
    this.add("South", textDisplayPanel = new Panel());
    this.add("East", rightPanel = new Panel());
    rightPanel.setLayout(new GridLayout(2,1));
    rightPanel.add(immigrationPanel = new Panel());
    rightPanel.add(inList = new java.awt.List(10, false));
    inList.setVisible(false); // hide the list. it appears when some immigrants have come in
    // code for the immigration inList
    inList.setBackground(Color.black);
    inList.setForeground(Color.green);
    // the event handler for the inList:
    immigrantChoice = new ItemListener(){
	public void itemStateChanged(ItemEvent listChoice)
	{ immigrantSelected();}};
    
    inList.addItemListener(immigrantChoice);
    //immigration.redrawInList();
    
    
    //textDisplayPanel.setSize(appletSize, 100);
    // make the list of candidates and stick it on leftPanel
    
    candidateList = new java.awt.List(10, false);      //candidate list
    candidateList.setBackground(Color.black);
    candidateList.setForeground(Color.green);
    restarted=true; // its a fresh population
    redrawCandidateList();
    candidateList.setEnabled(true);
    
    leftPanel.add(candidateList);

    // make a listener for the list 
    listChoice = new ItemListener(){
	public void itemStateChanged(ItemEvent listChoice)
	{ candidateSelected();}};
    
    candidateList.addItemListener(listChoice);
    
    getParent().validate();
    getToolkit().sync();
    
    // make the population control panel
    leftPanel.add(leftRightPanel = new Panel());
    // leftRight sits on the right of leftPanel and has two rows.
    // first row contains pop.controlpanel
    leftRightPanel.setLayout(new GridLayout(2,1));
    leftRightPanel.add(populationControlPanel = new Panel());
    populationControlPanel.setLayout(new GridLayout(7,1));
    populationControlPanel.add(restartButton = new Button("Restart"));
    populationControlPanel.add(mutateButton = new Button("Mutate"));
    populationControlPanel.add(nameButton = new Button("Name "));
    populationControlPanel.add(nameText = new TextField("Type name here!", 16));
    nameText.setForeground(Color.green);

    buttonClick = new ButtonAdapter();
    mutateButton.addMouseListener(buttonClick);
    restartButton.addMouseListener(buttonClick);
    nameButton.addMouseListener(buttonClick);
      
    populationControlPanel.add(mRateScrollbar = new Scrollbar (Scrollbar.HORIZONTAL, 1, 1, 1, 100));
    populationControlPanel.add(mRateLabel = new Label("Mutation rate = " +String.valueOf(mRateScrollbar.getValue())));
    populationControlPanel.add(submitButton = new Button("Submit"));
    submitButton.addMouseListener(buttonClick);
	
    mRateLabel.setBackground(Color.black);
    mRateLabel.setForeground(Color.green);

    mRateListener = new AdjustmentListener(){
	public void adjustmentValueChanged(AdjustmentEvent mRateListener) {
	  mRateLabel.setText("Mutation rate = " +String.valueOf(mRateScrollbar.getValue()));
	}
      };
    mRateScrollbar.addAdjustmentListener(mRateListener);
    
    
    historyList = new java.awt.List(1, false);
    historyList.setBackground(Color.black);
    historyList.setForeground(Color.green);
    leftRightPanel.add(historyList);
    redrawHistoryList();
    historyList.setEnabled(true);
    
    // make a listener for the list 
    popChoice = new ItemListener(){
	public void itemStateChanged(ItemEvent listChoice)
	{ populationSelected();}};
    
    historyList.addItemListener(popChoice);

    // make the textDisplayPanel
    textDisplayPanel.add(textDisplay = new TextDisplay(3, 60));
    textDisplayPanel.setLocation(0, 0);
    //textDisplay.setSize(appletSize, 100);
    textDisplay.add("AUDIOSERVE ACTIVATED", textDisplay.errorInfo);
    
    
    
  
  }// end buildGUI
 

  public void updateHistory(){
    // adds the current pop to the history and 
    history.addPopulation(this.population);
  }

  public void syncHistory(){
    // sync the history with the latest changes to the current population
    // first work out which population has been changed.
    // this is the currently selected generation from the history list
    int popToSync = historyList.getSelectedIndex();
    if(popToSync==-1){// no pop selected
      textDisplay.replace("NO POPULATION SELECTED!", 2);
      return;
    }
    history.updateCurrent(this.population, popToSync);
    
  }
  
  public void submitClicked(){
    // called when the user chooses to submit a circuit to the server
    selectedCircuitIndex = candidateList.getSelectedIndex();
    if(selectedCircuitIndex==-1){// no circuit is selected
      //redrawCandidateList();
      textDisplay.replace("NO CIRCUIT SELECTED!", 2);
      return;
    }
    immigration.addGenometoOutList(population.circuits[selectedCircuitIndex]);
    return;
  }// end submit clicked

  
  public void mutateClicked(){

    // get the individual index
    //submitClicked();
    selectedCircuitIndex = candidateList.getSelectedIndex();
    if(selectedCircuit==null){// no circuit is selected
      //redrawCandidateList();
      textDisplay.replace("NO CIRCUIT SELECTED!", 2);
      return;
    }
    //candidateList.deselect(selectedCircuitIndex);
    candidateList.setEnabled(false);
    textDisplay.replace("Generating mutant population from circuit "+selectedCircuitIndex, 2);
    if (selectedCircuit != null){              // kill the current circuit
      selectedCircuit.stop();
    }
    // get the mutation rate
    
    mutationRate =mRateScrollbar.getValue();
    mutationRate /= 100;
    
    // get the mutation mode
    mutationMode = 0;

    // add current pop to the history - this saves the circuits before they are mutated
    updateHistory();
    
    // call mutateAndReplace on the population
    population.mutateAndReplace(selectedIndividual, mutationRate, mutationMode);
    // recall this method the first time its called
    /*if (mutateOnce==false){
      mutated=false;
      mutateClicked();
      } */
    
    mutated = true;
    restarted=false; // its a parented population
    redrawCandidateList();
    candidateList.setEnabled(true);
    historyList.select(historyLength-1);
    // now stick the new population at the top of the history
    updateHistory();
    textDisplay.replace("New population ready", 2);
    
  }// end mutate clicked
  

  public void restartClicked(){
  
    //System.out.println("restart clicked");
    textDisplay.replace("Population re-initialising from random", 2);
    candidateList.setEnabled(false);
    
    if (selectedCircuit != null){              // kill the current circuit
      selectedCircuit.stop();
    }
    population.initialiseRandom(genomeSize, maxGridSize, maxSegmentRadius, maxSegmentAngle);
    
    
    // add current pop to the history
    updateHistory();
    restarted = true;
    redrawCandidateList();
    candidateList.setEnabled(true);
    historyList.select(historyLength-1);
    textDisplay.replace("New population ready", 2);
  }// end restartClicked
  
  public void nameClicked(){
       // sets the name field for the selected candidate
    selectedCircuitIndex = candidateList.getSelectedIndex();
    if(selectedCircuitIndex==-1){// no circuit is selected
      //redrawCandidateList();
      textDisplay.replace("NO CIRCUIT SELECTED!", 2);
      return;
    }
    
    population.renameIndividual(selectedCircuitIndex, nameText.getText());
    textDisplay.replace("Named Circuit "+selectedCircuitIndex+" "+population.getName(selectedCircuitIndex), 2);
    mutated=false;
    // sync the history with changes to the latest population
    syncHistory();
    redrawCandidateList();
  }// emd nameClicked
  
  public void immigrantSelected(){
    // like candidate selected but it starts one of the immigrant circuits instead.
    if (startCalled==true){
      try 
	{
	  if (inList.getSelectedItem() == null)
	    {
	      //readOut.append("no selection");
	      return;
	    }
	  selectedCircuitIndex = inList.getSelectedIndex();
	  textDisplay.replace("building the circuit", 2);
	  
	  System.gc(); 
	  if (selectedCircuit != null)             //kill current circuit
	    {  
	      selectedCircuit.output.disconnect();
	      selectedCircuit.stop();
	      selectedCircuit.delete();
	    }
	  
	  selectedIndividual = immigration.getImmigrant(selectedCircuitIndex);
	  selectedIndividual.build();
	  selectedCircuit = selectedIndividual.circuit;
	  selectedCircuit.output.connect(0, myOut.input, 0);
	  selectedCircuit.output.connect(0, myOut.input, 1);
	  selectedCircuit.start();
	  
	} catch(SynthException e) {
	  SynthAlert.showError(this,e);
	}  
    }// emd immigrantSelected
    // draw the graphic
    selectedIndividual.drawCircuit(canvas);
    textDisplay.replace(selectedIndividual.name+" running!",  1);
    maxHeight = selectedIndividual.spec.maxHeight;
    maxWidth = selectedIndividual.spec.maxWidth;
    textDisplay.add("\ngrid width: "+maxWidth+" height: "+maxHeight, 0);
  }// end immigrantSelected()
  
  
  public void replaceCircuit(){
    // when one of the circuits on the immigration inlist is selected
    // and the select button is clicked, it replaces the last circuit in the population
    
  }

  public void candidateSelected()
  {
    if (startCalled==true){
      try 
	{
	  if (candidateList.getSelectedItem() == null)
	    {
	      //readOut.append("no selection");
	      return;
	    }
	  selectedCircuitIndex = candidateList.getSelectedIndex();
	  textDisplay.replace("building the circuit", 2);

	  System.gc(); 
	  if (selectedCircuit != null)             //kill current circuit
	    {  
	      selectedCircuit.output.disconnect();
	      selectedCircuit.stop();
	      selectedCircuit.delete();
	    }
	  
	  selectedIndividual = population.circuits[selectedCircuitIndex];
	  selectedIndividual.build();
	  selectedCircuit = population.circuits[selectedCircuitIndex].circuit;
	  selectedCircuit.output.connect(0, myOut.input, 0);
	  selectedCircuit.output.connect(0, myOut.input, 1);
	  selectedCircuit.start();
	  //population.circuits[selectedCircuitIndex].circuit.start();
	} catch(SynthException e) {
	  SynthAlert.showError(this,e);
	}  
    }
    // draw the graphic
    population.circuits[selectedCircuitIndex].drawCircuit(canvas);
    textDisplay.replace("circuit "+selectedCircuitIndex+" running. name: "+population.getName(selectedCircuitIndex), 1);
    maxHeight = population.circuits[selectedCircuitIndex].spec.maxHeight;
    maxWidth = population.circuits[selectedCircuitIndex].spec.maxWidth;
    textDisplay.add("\ngrid width: "+maxWidth+" height: "+maxHeight, 0);
  }// end candidateSelected()
  


  public void populationSelected(){
    // finds out which of the previous pops was selected and calls
    // history.retrievePopulation with that index.
    // if the user goes back to an old population, the current population
    // must be stored so that it gets retrieved if they click on it again
    // therefore the first thing to do is to 
    
    
    
    int popIndex = historyList.getSelectedIndex();
    this.population = history.retrievePopulation(popIndex);
    
    if (population.getName(0).equals("default")){// a 1st gen pop has been retrieved
      restarted = true;
    }
    redrawCandidateList();
  }// populationselected
  
  public void redrawCandidateList(){
    candidateList.removeAll();
    if ((population.parentName!=null)&&(restarted==false)){// the population is not a new one
      candidateList.add("<Parent> "+population.parentName);
    }
    
    if (restarted==true){// this must be a new pop!
      candidateList.add(population.getName(0)+" 0");
    }
    
    for (i = 1; i< populationSize; i++){
      candidateList.add(population.getName(i)+" "+i);
    }
  }

  public void redrawHistoryList(){
    historyList.removeAll();
    for (i=0; i<historyLength-1; i++){
      historyList.add("Generation " + i);
    }
    historyList.add("Latest");
  }

  
  // inner class that deals with button events
  public class ButtonAdapter extends MouseAdapter{
    public void mouseClicked(MouseEvent e){
      if(e.getComponent()==mutateButton){
	mutateClicked(); 
      }
      
      if(e.getComponent()==restartButton){
	restartClicked();
      }
      
      if(e.getComponent()==submitButton){
	submitClicked();
      }
      if(e.getComponent()==nameButton){
	nameClicked();
      }
      
    }
    
    
  }

  
} // AudioServe

