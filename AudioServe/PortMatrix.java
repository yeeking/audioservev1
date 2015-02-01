package AudioServe;
import java.util.*;
import com.softsynth.jsyn.*;


/**
 * portMatrix.java
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
 * Created: Tue Jul 25 10:16:17 2000
 *
 * @author 
 * @version
 */



public class PortMatrix extends SynthCircuit {
  
  // this object represents a virtual patchbay. the Circuit object generates this object
  // to make building the circuit easier. it consists of a load of pointers to the in and out
  // ports of the modules in a circuit. it provides convenience methods to add modules and 
  // wire the together by representing the objects in a discrete 2d space 
  
  // the WireTable holds info about which port is connected to which port
  // the portmatrix object deals with this so the circuit object code can be kept simple 
  // e.g circuit calls addModule or addWire and the matrix sorts out how to do this.
  // --> the further from an object you get the less you have to deal with its specifice  

  private WireTable wireTable;
  private int maxPorts = 4; // how many in and out ports per module?
  private MultiVector multiUnits;
  private MultiplyUnit tempMulti;
  public MultiplyUnit dummyMulti;
  // two arrays for the synth ports. 
  // first a 3-d array to contain (pointers to) the SynthInput ports 
  // (several ports for each module in the matrix)
  
  private SynthInput[][][] inputs;
  
  // then a 3d array for the outputs
  // as some modules have more than one output (e.g.state variable filter)
  
  private SynthOutput[][][] outputs;
  
  private int i, x, y, fromX, fromY, fromPort, toX, toY, toPort, bias;
  private int[] wireParams;
  //
  // methods
  //
  //

  public void addWire(int[] wireParams){
    
    SynthOutput sourceOutput;
    this.wireParams = wireParams;
    fromX = wireParams[0];
    fromY = wireParams[1];
    fromPort = wireParams[2];
    toX = wireParams[3];
    toY = wireParams[4];
    toPort = wireParams[5];
    bias = wireParams[6];
    double biasDouble = (double)bias;
    biasDouble /= 10;
    
    /*System.out.println("portmatrix addwire.\nfromX " + fromX+" fromY " + fromY+" fromPort " 
      + fromPort +" \ntoX " + toX+" toY " + toY+" toPort " + toPort+" bias " + biasDouble);*/
    
    // make a multiply unit
    multiUnits.addElement(new MultiplyUnit());
    
    // set multi inputB to the bias
    // to get it working in v14, bias must be in hertz - max is 10000
    // use setSignalType so it works with v13 as well...
    multiUnits.getLastInputB().setSignalType( Synth.SIGNAL_TYPE_OSC_FREQ );
    multiUnits.getLastInputB().set(biasDouble*10000);
    
    // yeektest
    //multiUnits.getLastInputB().set(0.5);
    // connect the source port to multi inputA 
    outputs[fromX][fromY][fromPort].connect(multiUnits.getLastInputA());
    // get a pointer to the output of this multi unit
    tempMulti = multiUnits.getLastModule();
    // add the multi to the circuit
    add(tempMulti);
    
    // query the wireTable
    // if it returns null, connect multi.output to the target port;
    if((sourceOutput = wireTable.wiringQuery(toX, toY, toPort)) == null ){
      tempMulti.output.connect(inputs[toX][toY][toPort]);
      
      //update the wireTable
      wireTable.addWire(toX, toY, toPort, tempMulti.output);
    }
    
    // if the wiring query returned a synth port, 
    if(sourceOutput != null){
      // make another multi unit
      //System.out.println("Theres already a wire there - spawning a multi unit");
      multiUnits.addElement(new MultiplyUnit());
      
      // disconnect everything from the target port
      inputs[toX][toY][toPort].disconnect();
      
      // connect the old source port to multi.inputA
      sourceOutput.connect(multiUnits.getLastInputA());
      
      // connect the new source port (now a multi unit) to multi.inputB
      tempMulti.output.connect(multiUnits.getLastInputB());
      
      // connect multi.output to the target port
      multiUnits.getLastOutput().connect(inputs[toX][toY][toPort]);
    
      // update the wireTable
      wireTable.addWire(toX, toY, toPort, multiUnits.getLastOutput());
      // add the mult unit to the circuit
      add(multiUnits.getLastModule());
    }
    
  }// end addWire


  // overloaded addModule methods - one for each type of of synth module...
  
  public void addModule (int[] coords, SineOscillator module){
    
    // adds pointers to the sent ports to the appropriate port arrays...
    x = coords[0];
    y = coords[1];
    //System.out.println("sine adder got ("+x+y+")");
    inputs[x][y][0] = module.amplitude;
    inputs[x][y][1] = module.frequency;
    outputs[x][y][0] = module.output;
        
  }// end addModule(sine)

  public void addModule (int[] coords, SquareOscillator module){
    
    // adds pointers to the sent ports to the appropriate port arrays...
    x = coords[0];
    y = coords[1];
    inputs[x][y][0] = module.amplitude;
    inputs[x][y][1] = module.frequency;
    outputs[x][y][0] = module.output;
        
  }// end addModule(square)

  public void addModule (int[] coords, StateVariableFilter module){
    
    // adds pointers to the sent ports to the appropriate port arrays...
    x = coords[0];
    y = coords[1];
    inputs[x][y][0] = module.input;
    inputs[x][y][1] = module.amplitude;
    inputs[x][y][2] = module.frequency;
    inputs[x][y][3] = module.resonance;
    
    outputs[x][y][0] = module.output;
    outputs[x][y][1] = module.bandPass;
    outputs[x][y][2] = module.highPass;
    outputs[x][y][3] = module.lowPass;
        
  }// end addModule(statevariable )
  

  // constructor
  public PortMatrix(int width, int height){
    //System.out.println("port matrix constructor ...width = " + width + " height = " + height);
    // change height readings to array sizes
    width++;
    height++;
    inputs = new SynthInput[width][height][maxPorts];
    outputs = new SynthOutput[width][height][maxPorts];
    
    wireTable = new WireTable(width, height, maxPorts);
    multiUnits = new MultiVector();
    add(dummyMulti = new MultiplyUnit());
  }
}// end PortMAtrix
