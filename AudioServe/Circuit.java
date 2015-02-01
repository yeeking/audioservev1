package AudioServe;
import java.util.*;
import com.softsynth.jsyn.*;


/**
 * Circuit.java
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
 * Created: Sun Jul 23 17:18:34 2000
 *
 * @author 
 * @version
 */

public class Circuit extends SynthCircuit {

  public SineOscillator[] sine;
  public SquareOscillator[] square;
  public StateVariableFilter[] filter;
  public PortMatrix portMatrix;
  private int arraySize, i;
  
  private CircuitSpec spec;
  
  public void build(){
    
    try{
      // instantiate the arrays
      /*System.out.println("Circuit build method: sine size= "+sine.length+ 
	"square size= "+square.length+" filter size: "+filter.length);*/
      if(sine.length !=0){
      for(i=0;i<sine.length;i++){
	add(sine[i] = new SineOscillator());
	sine[i].frequency.setup(0.01, 200, 2000); 
	sine[i].amplitude.setup(0.5, 0.5,0.5);
	portMatrix.addModule(spec.modules[spec.sine].getCoords(i), sine[i]);
      }
      }
      if(square.length !=0){
      for(i=0;i<square.length;i++){
	add(square[i] = new SquareOscillator());
	square[i].frequency.setup(0.01, 100, 2000); 
	square[i].amplitude.setup(0.5, 0.5,0.5);
	portMatrix.addModule(spec.modules[spec.square].getCoords(i), square[i]);
      }
      }
      if(filter.length !=0){
      for(i=0;i<filter.length;i++){
	add(filter[i] = new StateVariableFilter());
	portMatrix.addModule(spec.modules[spec.filter].getCoords(i), filter[i]);
      }
      }     
      // make the wires
      arraySize = spec.wires.size();
      for(i=0;i<arraySize;i++){
	portMatrix.addWire(spec.wires.getParameters(i));
      }
      
      // add the port matrix to the circuit so it's multi units will start when 
      // the circuit is started
      add(portMatrix);
      
      // arbitrary output assignment
      
      if(sine.length>0){addPort(output = sine[0].output); return;}
      if(square.length>0){addPort(output = square[0].output); return;}
      if(filter.length>0){addPort(output = filter[0].output); return;}
    }catch(SynthException e) {
      SynthAlert.showError(e);
    }
    return;
  }// end build
  
  public Circuit(CircuitSpec specification) throws SynthException
  {
    // takes a circuitSpec object and initialises synthObject arrays based on the size
    // of the vectors. then it connects the wires 

    this.spec = specification;
    // go through eech vector, find the size and declare an 
    // appropriate size array to hold the modules
    
    arraySize = spec.modules[spec.sine].size();
    //System.out.println("sine array size= "+arraySize);
    sine = new SineOscillator[arraySize];
    
    arraySize = spec.modules[spec.square].size();
    //System.out.println("sqaure array size= "+arraySize);
    square = new SquareOscillator[arraySize];
    
    arraySize = spec.modules[spec.filter].size();
    //System.out.println("filter array size= "+arraySize);
    filter = new StateVariableFilter[arraySize];

    // instantiate the portMatrix. width is max width of the circuitSpec object
    portMatrix = new PortMatrix(spec.maxWidth, spec.maxHeight);
      
  }// end constructor
} // Circuit
