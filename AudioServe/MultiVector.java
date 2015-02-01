package AudioServe;
import java.util.*;
import com.softsynth.jsyn.*;


/**
 * MultiVector.java
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
 * Created: Tue Jul 25 19:42:16 2000
 *
 * @author 
 * @version
 */

public class MultiVector extends Vector {
  
  public MultiplyUnit getModule(int moduleIndex){
    // convenience method returns the requested multiply unit
    return ((MultiplyUnit)this.elementAt(moduleIndex));
  }

  public MultiplyUnit getLastModule(){
    // returns the most recently added multiUnit
    return ((MultiplyUnit)this.elementAt(size()-1));
  }
  
  public SynthInput getInputA(int moduleIndex){
    // returns input a of the required
    return ((MultiplyUnit)this.elementAt(moduleIndex)).inputA;
  }
  
  public SynthInput getInputB(int moduleIndex){
    // returns input b of the required
    return ((MultiplyUnit)this.elementAt(moduleIndex)).inputB;
  }
  
  public SynthOutput getOutput(int moduleIndex){
    // returns the output of the requested multiply unit 
    return ((MultiplyUnit)this.elementAt(moduleIndex)).output;
    
  }
  
  public SynthInput getLastInputA(){
    // returns input a of the most recently added unit
    return ((MultiplyUnit)this.lastElement()).inputA;
  }
  
  public SynthInput getLastInputB(){
    // returns input b of the most recently added unit
    return ((MultiplyUnit)this.lastElement()).inputB;
  }
  
  public SynthOutput getLastOutput(){
    // returns the output of the most recently added unit
    return ((MultiplyUnit)this.lastElement()).output;
    
  }
  
  
  
  
} // MultiVector
