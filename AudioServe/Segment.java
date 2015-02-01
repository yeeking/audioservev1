package AudioServe;
import java.lang.*;
import java.util.*;

/**
 * Segment.java
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
 * this object represents a segment of a circle. it acts as the connection range for a 
 * module in the circuit. as such it provides methods to test whether a passed
 * x,y point falls within its bounds. 
 *
 *
 *
 * Created: Wed Aug 02 12:22:54 2000
 *
 * @author 
 * @version
 */

public class Segment  {
  
  double theta1, theta2, theta3;
  double x,y,radius, x2,y2, temp;
  private int[] parameters;

  double R; // to hold the vector length from origin to the test point. 
  
  public boolean testBounds(int x2, int y2){
    // takes the coordinates of a point and tests if it falls in the bounds of the
    // segment.
    this.x2 = x2; 
    this.y2 = y2;
    
    // the theme is to work on a right angled triagle where 
    // R is the hypotenuse, connecting segment origin to the test point
    // opposite is a line from origin.x to testpoint.x 
    // adjacent is a line from origin.y to testpoint.y
    
    // 1. do pythagoras to find the length of the vector from segment origin to the test point
    R = Math.sqrt(((x-x2)*(x-x2))+((y-y2)*(y-y2)));
    
    // 2. do atan on opposite over adjacent to get the angle

    // first mod the opp/adj so its positive.
    temp = (y-y2)/(x-x2);
    temp = temp*temp;
    temp = Math.sqrt(temp);
    // then get the angle
    theta3 = Math.atan(temp);
    
    // 3. work out which quadrant this vector is in relative to segment origin
    // so the bearing can be calculated.
    //System.out.println("uncorrected bearing= "+theta3);
    if((x2>=x)&&(y2>=y)){theta3 = 90-theta3;//System.out.println("top right");
    }// top right quadrant
    if((x2>=x)&&(y2<y)){theta3 = 180-theta3;//System.out.println("bott right");
    }// bottom right 
    if((x2<x)&&(y2<y)){theta3 = 270-theta3;//System.out.println("bott left");
    }// bottom left 
    if((x2<x)&&(y2>=y)){theta3 = 360-theta3;//System.out.println("top left");
    }// top left
    
    // 4. now theta3 is the bearing and R is the vector length
    // if theta3 lies between theta1 and theta2 and R is smaller than radius
    // the point is in the cone
    /*System.out.println("theta1 = " + theta1 + " theta2= " + theta2+"theta3 = "+theta3+"\n"+
		       "radius of seg= " + radius+"vactor scalar= " + R);*/
    if((theta3>=theta1)&&(theta3<=theta2)&&(R<=radius)){
      //System.out.println("Point is in the cone!");
      return true;}
    //System.out.println("point isnt't in the connection range!");
    return false;
  }
  
  public int[] getParameters(){
    return parameters;
  }
  
  public Segment(int theta1, int theta2, int x, int y, int radius) {
    // get the two bearings. They are bearings from due north. theta1 is the first side
    // of the cone. theta2 is the second side. 
     
    this.theta1 = theta1;
    this.theta2 = theta2;
    
    // get the coordinates of the origin.
    this.x = x;
    this.y = y;
    this.radius = radius;
    // now make the parameters array
    parameters = new int[5];
    parameters[0] = theta1;
    parameters[1] = theta2;
    parameters[2] = x;
    parameters[3] = y;
    parameters[4] = radius;
  }
  
} // Segment
