package AudioServe;
import java.util.*;

/**
 * SegmentVector.java
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
 * Created: Wed Aug 02 15:25:01 2000
 *
 * @author 
 * @version
 */

public class SegmentVector extends Vector {
  
  private int[] parameters;
  private Segment segment;
  private int i, c;
  private int numberOfParameters = 5;

  public void addSegment(int theta1, int theta2, int x, int y, int radius){
    // adds a segment and sets its parameters
    this.addElement(new Segment(theta1, theta2, x, y, radius));    
  }
  
  public boolean testSegment(int segmentIndex, int x, int y){
    // cast a segment then call the testCoords method on it...
    segment = ((Segment)this.elementAt(segmentIndex));
    return segment.testBounds(x,y);
    
  }

  public int[] getParams(int segmentIndex){
    // get the segment object
    segment = ((Segment)this.elementAt(segmentIndex));
    parameters = segment.getParameters();
    return parameters;
  }
  
  public int[][] getAllParameters(){
    // declare a new 2-d array to hold the parameters
    int[][] allParameters = new int[size()][numberOfParameters];
    for (i=0; i<size();i++){
      int[] oneParam = getParams(i);
      for (c=0; c<numberOfParameters;c++){
	allParameters[i][c] = oneParam[c]; 
      }
    }
    return allParameters;
  }// end getAllParameters

} // SegmentVector
