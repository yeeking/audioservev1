package AudioServe;
import java.util.*;

/**
 * CircuitSpec.class
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
 * this object is produced by the developmental process
 * each candidate Object in the population has a CircuitSpec field. 
 * it is taken as the main argument by the Circuit object constructor
 * when a candidate is selected for auditioning by the user, the candidate's
 * CircuitSpec object is used to make a Jsyn Circuit object that is wired to the audio out. 
 *
 * matt yee-king code started: 29.06.2000
 *
 */

public class CircuitSpec {

  // ModuleVector contains all module, information
  ModuleVector[] modules;
  SegmentVector connectionRange;
  // these ints hold the highest x and y values recorded from calls to addModule
  int[] params;
  public int maxWidth=0;
  public int maxHeight=0;
  int numberOfModules = 0; // starts at 0. each time a module is added it gets
                           // incremented. provides a way of indexing the connection
                           // ranges - each module has an index number that links it to 
                           // a connection range. 
  
  // convenience fields - ints that refer to the module type
  
  int sine = 0;
  int square = 1;
  int filter = 2;
  int varietiesOfModule = 3;// in terms of array indexes...
  // and more...
  
  // the wire descriptor array is a vector of arrays of integers. 
  // one array per wire, with these elements: 
  // 0 = from x
  // 1 = from y
  // 2 = from port
  // 3 = to x
  // 4 = to y
  // 5 = to port
  // 6 = bias

  WireVector wires;

  // convenience variables  - refer to array members with these variables rather
  // than with straight ints to code is clearer. 
  int fromX = 0;
  int fromY = 1;
  int fromPort = 2;
  int toX = 3;
  int toY = 4;
  int toPort = 5;
  int bias = 6;
  int wireDescriptors = 7; // number of parameters used to describe a wire 

  // for use within this object
  private int[] coords;
  private int i, d, c;
  private int module;

  public void addModule(int modID, int x, int y, int theta1, int theta2, int radius){
    // add a module to the appropriate moduleVector from the modules array
    // according to what type of module it is
    
    modules[modID].addModule(x,y,numberOfModules);
    numberOfModules++;

    // update the maxHeight and maxWidth fields if appropriate
    // i.e if the module falls outside the present grid size, increase it
    if(x > maxWidth){maxWidth=x;//System.out.println("maxWidth= "+maxWidth);
    }
    if(y > maxHeight){maxHeight=y;//System.out.println("maxHeight= "+maxHeight);
    }

    // add a segment for that module - the index to access this segment will 
    // be the same as the value returned by the getIndex() method in moduleVector
    connectionRange.addSegment(theta1, theta2, x, y, radius);
  }

  public void addWire(int[] wireParams){
    // calls addWire on the wireVector
    
    wires.addWire(wireParams);
  }
  
  public void addWireList(WireVector wireList){
  
    wires = wireList;
  }

  public void printSpec(){
    // prints the spec to standard out

    // the modules
    // where d=module ID
    for (d=0; d < varietiesOfModule; d++){ 
      System.out.println("Module type: " +d);
      for (i=0; i < modules[d].size(); i ++){
	System.out.println("module "+i+" coords: ("+modules[d].getCoords(i)[0]+", "+modules[d].getCoords(i)[1]+")");
      }    
    }
    
    // then the wires
    for (i=0; i<wires.size();i++){
      params= wires.getParameters(i);
      System.out.println("Wire " +i+" from("+params[fromX]+","+params[fromY]+")"+
			 " to ("+params[toX]+","+params[toY]+") from port "+
			 params[fromPort]+" toPort "+params[toPort]);
    }
    
    return;
  }// end printSpec 
   
  public int[] getCoords(int moduleType, int moduleIndex){
    
    // getw the coordinates of a particular module
    coords = new int[2];
    coords = modules[moduleType].getCoords(moduleIndex);
    return coords;
    
  }// end getCoords
  
  public boolean testSegment(int segmentID, int x, int y){
    // tests to see if the point at x,y falls in the segment with ID segmentID
    // feeds right through to a method in the vector, but makes the calling code simpler
    return connectionRange.testSegment(segmentID, x, y);
  }
  
  public int[][] getSegmentParameters(){
    // returns all segment parameters
    int [][] tempParams = connectionRange.getAllParameters();
    return tempParams;
  }
  
  public int[] getSegmentParameters(int segmentID){
    // returns a single segments parameters
    int [] tempParams = connectionRange.getParams(segmentID);
    return tempParams;
  }
  
  public int[][] getAllCoords(){
    // returns a 2d array containing the x,y coords of all the modules in the circuitSpec
    int[][] allCoords = new int[numberOfModules][2];
    int marker=0;
    for(i=0; i<varietiesOfModule; i++){
      // one iteration for each type of module...
      for(c=0;c<modules[i].size(); c++){
	// one iteration for each module of type number 'i'
	allCoords[marker+c][0] = getCoords(i, c)[0];
	allCoords[marker+c][1] = getCoords(i, c)[1];
	//System.out.println("marker +c = " + marker+c);
	//System.out.println("coords are "+allCoords[marker+c][0]+", "+allCoords[marker+c][1]);
      }
      marker+=c;
    }// end main for loop
    return allCoords;
  }// end getAllCoords


  public int portNumberQuery(int moduleID, int inOut){
    // returns the number of inPorts or outPorts on a module with that ID
    // inOut = 1 --> inPort
    // inOut = 0 --> outPort
    if ((moduleID==sine)||(moduleID==square)){
      if(inOut == 1){// its the inPort
	return 2;// there are 2 
      }
      if(inOut == 0){// its the outPort
	return 1;// there are 2
      }
    }
    if(moduleID==filter){
      return 4; // it has 4 ins and 4 outs
    }
    return 0;
  }
  
  // constructor
  public CircuitSpec(){
    // set up the array of module Vectors so theres one vector for each type of module
    modules = new ModuleVector[varietiesOfModule];
    for(i=0; i < varietiesOfModule; i++){
      modules[i] = new ModuleVector();
    }
    
    wires = new WireVector();
    connectionRange = new SegmentVector();
    
  }// end constructor


}// end CircuitSpec
