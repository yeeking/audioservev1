package AudioServe;
import java.util.*;
import com.softsynth.jsyn.*;
import java.awt.*;
import java.applet.Applet;

/**
 * Genome.java
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
 * Created: Sun Jul 23 15:44:42 2000
 *
 * @author 
 * @version
 */

public class Genome extends Applet {
  
  // to test the jsyn bit...
  public Circuit circuit;
  LineOut myOut;
  int appletSize = 500;
  
  // proper fields...
  private int genomeLength = 10; // --> 10 units
  private int maxAngle = 360; // max and min for the connection range
  private int minAngle = 0;  //
  public int maxGridSize = 200; // xy limits for grid
  private int maxSegmentRadius = 50; // max radius for connection range
  private double maxLocusValue = 360; // the genome is made up of values in the range 0-maxLocusValue
  private int[] chromosome;       // int array version of the chromsome
  public String chromosomeString; // string verison of the chromosome
  public CircuitSpec spec;

  // convenience fields to give names to the parameters so the chromosome indexes can be
  // called by name ... chromosome[beginning of gene + moduleID] = moduleID for the first gene
  int moduleID = 0;
  int moduleX = 1;
  int moduleY = 2;
  int theta1 = 3; // the bearing from north of the first side of the cone
  int theta2 = 4; // the bearing of the second line of the cone
  int radius = 5; // the radius of the cone
  int inPort = 6; // the port into which the module will wire itself on a module found in the 
  // connection range. this is normalised so it yields an appropriate number
                  // depnedent on how many inputs the targer module has
  int outPort= 7; // the port from which the module produces connections. most oscillator
                  // modules have one output but filter modules have several. again it is 
                  // to yield an appropriate number for that module ID. 
  int bias = 8;   // the bias that will be placed on wires. (try varying it with distance?)
  int numberOfParameters=9; // equates to gene length.
  
  double mutationRate;
  
  private int locus, i, modType, ports;
  
  private double range;          // used in the normalisation method
  private double segmentSize;    // ditto
  private int segmentNumber;
  
  int[] tempParameters;
  
  double mutationFactor = 1.5; // the higher this is the smaller the pointMutation leaps will be

  // mutation modes

  public int point = 0;
  public int duplicate = 1;
  public int all = 2;
  
  public static void main(String argv[]){
    // some test code
    Genome genome = new Genome(20);
    genome.printGenome();
    genome.pointMutate(0.9);
    genome.printGenome();
  }

  public int[] getMutantChromosome(double mutationRate, int mode){
    // returns a mutated version of the chromosome
    // the mode dictates the type of mutation to use
    
    // the mutation methods are non-destructive as they make copies of the
    // chromosome and work on/ return these.
    
    if (mode == point){ // just do point mutations
      
      int[] newChromosome = pointMutate(mutationRate);
      return newChromosome;
    }
    return chromosome;
    
    // other mutation mode code (mode code mode code...)to go here...
  }
  
  public int[] copyChromosome(){
    // returns a copy of the chromosome
    int[] tempChromosome = new int[chromosome.length];
    for(i=0; i< chromosome.length; i++){
      tempChromosome[i] = chromosome[i];
    }
    return tempChromosome;
  }
  
  public String copyChromosomeString(){
    // returns a copy of the chromsome String
    return chromosomeString;
  }

  public int[] pointMutate(double mutationRate){
    // parses the genome generating point mutations.
    
    // non-destructive - it works a copy of the chromosome and returns this.
    // at each locus, a random number is generated. if it is lower than the mutation
    // rate then the int at that locus will be mutated.
    
    // first decide whether to mutate up or down:
    // get another random number 
    // if new rand>0.5, go up. if <0.5, go down.
    
    // then decide on the size of the change:
    // the difference between the mutation rate and the original random number is calced.
    // then this is worked out as a percentage of the diff tween 0 and mutation rate. 
    // this percentage is taken of the smallest
    // 1. maxlocus size - locus. 
    // 2. locus
    // this causes a conservational dynamic, where:
    // 1. high locus values decrease gradually under mutation
    // 2. low locus values increase gradually. 
    // 3. medium locus values can go either way. 
    // e.g mutn.rate = 0.5, number = 0.25. --> mutation at this locus
    // new random number = 0.1 --> locus value will decrease
    // 0.25/0.5 = 0.5 --> locus value will decrease by 50% of current locus value
    // (or maxLocus-locusValue if its smaller) 
     
    double randomOne, randomTwo;
    int locus;
    int  locusChange = 0;
    int upOrDown = 0;
    int[] newChromosome;
    
    newChromosome = copyChromosome();
    this.mutationRate = mutationRate;
    
    // for loop to parse the genetic sequence
    for (locus=0; locus< chromosome.length; locus++){
      // at each locus, generate a random number;
      randomOne = Math.random();
      if (randomOne < mutationRate){// mutate!
	// up or down?
	randomTwo = Math.random();
	if (randomTwo >= 0.5){upOrDown=1;}
	if (randomTwo < 0.5){upOrDown=-1;}
	// size of change?
	randomOne = (mutationRate - randomOne)/(mutationFactor*mutationRate);
	//System.out.println("\nchromosome position: "+locus+" mutationRAte: " +mutationRate+" %change: "+randomOne);
	// random one is now the percentage distance from mutation rate to randomOne.
        if(newChromosome[locus] > maxLocusValue-newChromosome[locus]){
	  // the change should be a percenteage of the distance from locus value to maxLocusValue
	  locusChange = (int)(randomOne*(maxLocusValue-newChromosome[locus]));
	}
	if(newChromosome[locus] < maxLocusValue-newChromosome[locus]){
	  // the change should be a percentage of the distance from locus value to 0
	  locusChange = (int)(randomOne*newChromosome[locus]);
	}
	// make it change the right way
	locusChange *= upOrDown;
	// change the value
	//System.out.println("Going from: "+newChromosome[locus]+" to: "+(newChromosome[locus]+locusChange));
	newChromosome[locus] += locusChange;
      }
    }
    return newChromosome;
    
  }// end pointMutate
  
  public CircuitSpec develop(){
    // the genome parses its sequence and generates a circuitSpec object
    int coordCounter,geneCounter;
    int[] wireParams = new int[spec.wireDescriptors];// could put this line in constructor
    // but here code is clearer!
    boolean wireTest;
    boolean  continueFlag = true;
    geneCounter = 0;
    for(locus=0; locus<chromosome.length;locus+=numberOfParameters){
      // read a gene
      int[] parameters = getParameters(locus);
	
      // get the coords of all the modules spawned so far
      int[][] allCoords = spec.getAllCoords();
      
      // see if there is a module at that point already.  
      for (coordCounter=0; coordCounter<allCoords.length-1; coordCounter++){
	if((allCoords[coordCounter][0] == parameters[moduleX])&&
	   (allCoords[coordCounter][1] == parameters[moduleY])){
	  // repeat detected!
	  //System.out.println("spec builder: Module detected at target position...");
	  continueFlag = false;
	  continue;
	}
      }
      // break the main for loop so nothing is done with that gene if the above code finds
      // there is a module at the target point already. 
      if(continueFlag == false){continue;}
      // add the module to the circuitSpec.
      // this method in CircuitSpec will also add a connection range(segment) for that module
      spec.addModule(parameters[moduleID],parameters[moduleX],parameters[moduleY],
		     parameters[theta1],parameters[theta2],parameters[radius]);
      
      // next, need to sort out the wires
      
      // go through all these coordinates to see if they are in the connection range.
      int segmentID = spec.numberOfModules-1;
      //System.out.print("segmentId = " + segmentID+ " allcoords length= "+allCoords.length);
      for (coordCounter=0; coordCounter<allCoords.length-1; coordCounter++){
	/*System.out.println("source: "+parameters[moduleX]+","+parameters[moduleY]+
	  "\npotential target: "+allCoords[coordCounter][0]+","+allCoords[coordCounter][1]);*/
	
	// each iteration accesses the modules in turn
	// send each modules coords to spec.testSegment
	wireTest = spec.testSegment(segmentID, allCoords[coordCounter][0], allCoords[coordCounter][1]);
	if (wireTest==true){
	  // build up the wire parameter array
	  // fromX, toX, etc...
	  wireParams[spec.fromX] = parameters[moduleX];
	  wireParams[spec.fromY] = parameters[moduleY];
	  wireParams[spec.toX] = allCoords[coordCounter][0];
	  wireParams[spec.toY] = allCoords[coordCounter][1];
	  wireParams[spec.fromPort] = parameters[outPort];
	  wireParams[spec.bias] = parameters[bias];
	  // the toPort value is left un-normalised as its dependent on the 
	  // number of ports on the target module
	  modType = identifyModule(coordCounter);
	  ports = spec.portNumberQuery(modType, 1);
	  wireParams[spec.toPort] = normalise(0, ports, parameters[inPort]);
	  
	  // have to put the values from wireParams in a separate array for some reason...
	  int[] params2 = {wireParams[spec.fromX], wireParams[spec.fromY],
			   wireParams[spec.fromPort], wireParams[spec.toX], wireParams[spec.toY], 
			   wireParams[spec.toPort], wireParams[spec.bias]};
	  //addWire(wireParams);
	  spec.addWire(params2);
	  int [] wire = spec.wires.getParameters(spec.wires.size()-1);
	  /*System.out.println("Added wire from " + wire[spec.fromX]+","+wire[spec.fromY]+
	    " to "+wire[spec.toX]+","+wire[spec.toY]);*/
	  //spec.printSpec();
	}
	
      }
      
      geneCounter++;
    }  // end main for loop
    
    return spec;
  }
  
  
  public int normalise(int min, int max, int value){
    // generic normalise method
    // all done in doubles
    range = max - min;// range for segments
    segmentSize = maxLocusValue/range;
    segmentNumber = 0;
    if (value<segmentSize){return 0;} // its in the first segment!
    while(value>(segmentNumber*segmentSize)){
      // value is in the next segment!
      segmentNumber++;
    }

    /*System.out.println("There are "+range+" segments. Segment size= "+segmentSize+"\n"+
      value+" --> "+segmentNumber+" as it ends at "+segmentNumber*segmentSize);*/
    return segmentNumber -1 ;
  }
  
  public void initialise(){
    // initialises a genome with random genes
    for (i=0; i<chromosome.length; i++){
      chromosome[i] = randomInt((int)maxLocusValue);
    }
    return;
  }
  
  private int randomInt(int max){
    double random = Math.random();
    random = random*max;
    //System.out.println("random = : " +random);
    return((int)random);
  }
  
  private int identifyModule(int position){
    // position is the position in a concatenated list of all module coords
    // (like that return by CircuitSpec.getAllCoords() ..)
    
    // loop through all module types
    int modCounter;
    int modID = 0;
    int totalLength = 0;
    for (modCounter=0;modCounter<spec.varietiesOfModule; modCounter++){
      // get the size of the module vector for ID modCounter
      // and see if position is greater than that i.e the module at that position is not
      // of variety 'modCounter'
      totalLength +=spec.modules[modCounter].size();
      if (position>totalLength){// it doesn't fall in this section of the module array
	modID++;
      }
    }
    return modID;
  }
  
  public int[][] getSegmentParameters(){
    // this returns an int[][] array that contains the parameters for
    // all the segments in the circuitSpecobject
    int[][] tempSegments = spec.getSegmentParameters();
    return tempSegments;
  
  }
  
  private int[] getParameters(int locus){
    
    // returns an int array containing the parameters of one gene
    tempParameters = new int[numberOfParameters];
    
    int tempAngle;
    for(i=0;i<numberOfParameters;i++){
      tempParameters[i] = chromosome[locus+i];
    }
    // normalise the parameters
    tempParameters[moduleID] = normalise(0, spec.varietiesOfModule, tempParameters[moduleID]);
    tempParameters[moduleX] = normalise(0, maxGridSize, tempParameters[moduleX]);
    tempParameters[moduleY] = normalise(0, maxGridSize, tempParameters[moduleY]);
    tempParameters[radius] = normalise(1, maxSegmentRadius, tempParameters[radius]);
    tempParameters[theta1] = normalise(minAngle, maxAngle, tempParameters[theta1]);
    tempParameters[theta2] = normalise(minAngle, maxAngle, tempParameters[theta2]);
    // if theta2< theta1 then swap them round to get a reasonable segment
    if(tempParameters[theta2]<tempParameters[theta1]){
      tempAngle = theta2; 
      theta2 = theta1; 
      theta1 = tempAngle;
    }
    // to sort out the outport parameters, first call 
    // portNumberQuery on the spec object with the module ID and 1 for in and 0 for out
    // this gives the number of ports on the module
    // then normalise tempParameter[port] value with the 'number of ports' as max value.
    ports = spec.portNumberQuery(tempParameters[moduleID], 0);
    tempParameters[outPort] = normalise(0, ports, tempParameters[outPort]);
    // the bias parmater must be in the range 0-10. when it is used in the Circuit
    // constructor it is cast toa a double and divided by 10 to get a value 0-1
    // so normalise it with max=10
    tempParameters[bias] = normalise(0,10, tempParameters[bias]);
    
    return tempParameters;
  }
  
  public void printGenome(){
    int geneCounter=0;
    int d;
    /*for (i=0;i<chromosome.length; i++){
      System.out.println(" " + chromosome[i]);
      }*/
    for(i=0;i<chromosome.length; i+=numberOfParameters){
      // this loop makes us move through the genome one gene at a time
      System.out.println("\ngene number: " + geneCounter);
      for(d=0; d<numberOfParameters; d++){
	System.out.print(" "+chromosome[i+d]);
      }
      geneCounter++;
    }
  }
 
  public String intArrayToString(int[] array){
    // well named method...
    // used so the genome object also has a String version of its chromosome
    int intCounter;
    StringBuffer buffer = new StringBuffer();
    for (intCounter=0;intCounter<array.length; intCounter++){
      // generates a StringBuffer of the int array
      buffer.append(array[intCounter]+" ");
    }
    return buffer.toString();
  }
  
  public Genome(int genomeLength){
    spec = new CircuitSpec();
    chromosome = new int [genomeLength*numberOfParameters];
    initialise();
    chromosomeString = intArrayToString(chromosome);
  }

  public Genome(int genomeLength, int gridSize, int maxSegmentRadius, int maxSegmentAngle) {
    // standard constructor
    // generates a new random genome
    
    spec = new CircuitSpec();
    chromosome = new int [genomeLength*numberOfParameters];
    this.maxGridSize = gridSize;
    this.maxLocusValue = maxLocusValue;
    this.maxSegmentRadius = maxSegmentRadius;
    this.maxAngle = maxSegmentAngle;
    initialise();
    chromosomeString = intArrayToString(chromosome);
  }
  
  public Genome(int[] chromosome, int gridSize, int maxSegmentRadius, int maxSegmentAngle) {
    
    // this constructor allows a genome object to be instantiated with a ready made chromosome
    
    spec = new CircuitSpec();
    this.maxGridSize = gridSize;
    this.maxLocusValue = maxLocusValue;
    this.maxSegmentRadius = maxSegmentRadius;
    this.maxAngle = maxSegmentAngle;
    
    // take the passed chromosome
    this.chromosome = chromosome;
    chromosomeString = intArrayToString(chromosome);
  }
  
} // Genome

