package AudioServe;
import java.util.*;


/**
 * WireVector.java
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
 * Created: Sun Jul 23 22:23:48 2000
 *
 * @author matt yee-king
 * @version
 */

public class WireVector extends Vector {
 
  int numberOfWireParameters = 5;
  
  public int[] getParameters(int wireIndex){
    // returns an int array containing all the details for the wire with that index
    //System.out.println("wire index" + wireIndex+" requested!");
    return ((int[])this.elementAt(wireIndex));
  }
  
  public void addWire(int[] wireParameters){
    //System.out.println("adding wire at "+size());
    addElement(wireParameters);
  }
				   
} // WireVector





