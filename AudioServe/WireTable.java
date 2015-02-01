package AudioServe;
import java.util.*;
import com.softsynth.jsyn.*;


/**
 * WireTable.java
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
 * Created: Tue Jul 25 15:57:31 2000
 *
 * @author 
 * @version
 */

public class WireTable  {
  
  // maintains a set of flags that indicate the presence or absence
  //of connections in the matrix.
  // there is one flag fro each inport in the matrix. when a wire is connected to a port
  // the flag for that port is set to true and a pointer to the source outport of the wire is 
  // added to the 3-d outport array. 
  // if a connection to inport is requested, 
  // wireTable can return a pointer to the source of a wire that is already connected so
  // the calling method can recall connect on that output and connect the wire into a multi
  // unit.  

  private boolean[][][] flags;
  private SynthOutput[][][] outputs;

  
  public SynthOutput wiringQuery(int targetX, int targetY, int targetPort){
    // checks the flag for the specified port
    // if there is a wire connected to this port, it returns a pointer to the port
    // from which the wire eminates so it can be wired into the conflict resolution unit

    // no wire connected
    if (flags[targetX][targetY][targetPort] == false){return null;}
    
    // there is a wire connected if we get here
    return outputs[targetX][targetY][targetPort];
    
  }
  
  public void addWire(int targetX, int targetY, int targetPort, SynthOutput sourcePort){
    // set the flag for this unit to true, i.e. this port is connected
    flags[targetX][targetY][targetPort] = true;

    // store a pointer to the source of this wire so it can be returned later when 
    // a conflict needs to be sorted...
    outputs[targetX][targetY][targetPort] = sourcePort;
  }

  
  public WireTable(int width, int height, int maxPorts) {
    
    flags = new boolean[width][height][maxPorts];
    outputs = new SynthOutput[width][height][maxPorts];

    // initialise the flags
    int x = 0;
    int y = 0;
    int z = 0;
    
    for (x=0;x<width;x++){
      for(y=0;y<height;y++){
	for(z=0;z<maxPorts;z++){
	  flags[x][y][z] = false;
	}
      }
    }
  }
  
} // WireTable
