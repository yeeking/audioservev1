package AudioServe;
import java.util.*;

/**
 * moduleVector.java
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
 * Created: Sun Jul 23 20:37:57 2000
 *
 * @author Matt yee-king
 * @version 1.0
 */

public class ModuleVector extends Vector {

  int[] coords;
  
  public int[] getModuleDetails(int moduleIndex){
    // returns an int array containing all the details for the module with that index
    return ((int[])this.elementAt(moduleIndex));
  }

  public int[] getCoords(int moduleIndex){
    // returns the x y coords of module at moduleIndex

    coords = new int[2];
    coords[0] = ((int[])this.elementAt(moduleIndex))[0];
    coords[1] = ((int[])this.elementAt(moduleIndex))[1];
    //System.out.println("module vector returning coords" + coords[0]+ coords[1]);
    return coords;
  }
  
    public int getIndex(int moduleIndex){
    // returns the index  of module at moduleIndex
    int index = ((int[])this.elementAt(moduleIndex))[2];
    // this index links that module to a particular Segment object
    return index;
  }
  
  public void addModule(int x, int y, int index){
    // adds a module and sets its coordinates
    coords = new int[3];
    coords[0] = x;
    coords[1] = y;
    // index associates this module with a segment object
    coords[2] = index;
    this.addElement(coords);
  }
} // moduleVector
