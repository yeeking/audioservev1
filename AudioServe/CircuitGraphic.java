package AudioServe;
import java.awt.*;


/**
 * CircuitGraphic.java
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
 *
 * Created: Sun Aug 06 18:15:19 2000
 *
 * @author 
 * @version
 */

public class CircuitGraphic  {
  
  // used to store arrays of line and module parameters that allow a circuit to be drawn
  // quickly.

  int[][] lines;   // contains fromX, fromY, toX, toY params for module connection ranges
                   // two lines per range
  int[][] modules; // contains x,y params for all modules
  int[][] coords;
  int[] segmentParams;
  int[] lineParams;
  double x,y,r, coordBoost;
  int xPos, yPos, radius, i, blobSize, blobRadius;
  CircuitSpec spec;
  int frameSize; // the size of the container the circuit graphic will be displayed in 
  int maxGridSize; // the size of the circuit grid, used with frameSize to scale the coordinates
  double blobRad; 
  //Canvas canvas;
  
  


  public void drawCircuit(Canvas canvas){
    // draws the lines and blobs to the canvas object
    //this.canvas = canvas;
    Graphics g = canvas.getGraphics();
    Dimension d = canvas.getSize();
    g.clearRect(0,0, d.width, d.height);
    canvas.setBackground(Color.black);
    g.setColor(Color.green);
    for (i=0; i< lines.length; i++){
      // go through the arrays drawing the lines
      g.fillOval(modules[i][0], modules[i][1], blobSize, blobSize);
      g.drawLine(lines[i][0], lines[i][1], lines[i][2], lines[i][3]);
      g.drawLine(lines[i][4], lines[i][5], lines[i][6], lines[i][7]);
    }
    
    // call repaint once its all been added
    //canvas.repaint();
  }
  
  /* public void paint(Graphics g){
    System.out.println("paint called");
    drawCircuit(canvas);
  }
  */

  // (rather large) constructor...
 
  public CircuitGraphic(CircuitSpec spec, int blobSize, int frameSize) {
    
    this.spec = spec;
    this.frameSize = frameSize;
    this.blobSize = blobSize;
    blobRad = blobSize/2;
    blobRadius = (int)blobRad;
    //System.out.println("spec.maxHeight = "+spec.maxHeight+" max width = " +spec.maxWidth);
    if (spec.maxHeight >= spec.maxWidth){ this.maxGridSize = spec.maxHeight;}
    if (spec.maxHeight < spec.maxWidth){ this.maxGridSize = spec.maxWidth;}
    this.blobSize = blobSize; // the size of the circles representing the modules
    
    coords = spec.getAllCoords();
    modules = new int[coords.length][2];
    lines = new int[coords.length][8];
    coordBoost = frameSize/maxGridSize;
    //System.out.println("coord Boost= "+ coordBoost);
    
    for (i=0; i<coords.length; i++){
      x = coords[i][0]*coordBoost;
      y = coords[i][1]*coordBoost;
      xPos = (int)x;
      yPos = (int)y;
      // make the modules array entry for the module...

      modules[i][0] = xPos;
      modules[i][1] = yPos;
      // add the connection range parameters for each module
      // to the lines array
      segmentParams = spec.getSegmentParameters(i);
      // scale up startx and starty
      x = segmentParams[2]*coordBoost;
      y = segmentParams[3]*coordBoost;
      r = segmentParams[4]*coordBoost;
      xPos = (int)x;
      yPos = (int)y;
      radius = (int)r;
      // correct line origins so they are in the center of the module blob
      xPos += blobRadius;
      yPos += blobRadius;
      
      // send them to the getLineEnd method
      lineParams = getLineEnd(xPos, yPos, segmentParams[0], radius);
      int toX = lineParams[0]+xPos;
      int toY = lineParams[1]+yPos;
      // add a line with these parameters
      lines[i][0] = xPos;
      lines[i][1] = yPos;
      lines[i][2] = xPos+lineParams[0];
      lines[i][3] = yPos+lineParams[1];
      // then the other line..
      lineParams = getLineEnd(xPos, yPos, segmentParams[1], radius);
      lines[i][4] = xPos;
      lines[i][5] = yPos;
      lines[i][6] = xPos+lineParams[0];
      lines[i][7] = yPos+lineParams[1];
    }
    
  }

    public double degreesToRadians(double degrees){
    // divide degrees by 360/2PI 
    double radian = 360/(2*Math.PI);
    return degrees/radian;
    
  }
  
  public int[] getLineEnd(int x, int y, int theta, int hypotenuse){
    
    // takes x,y values of the start of a line and works out where it will end
    // theta is the bearing of the line from north
    // hypotenuse is the length of the line.
    // x and y are the origin of the line
    double dx = x;
    double dy = y;
    double dtheta = theta;
    double temp;
    int [] lineEnd = new int[2];
    //System.out.println("Angle = "+ dtheta+ " hypot = " + hypotenuse);
    // first work out whioch quarter the line is in

    // if the line is in the top right quarter, x is increasing and y is decreasing 
    
    if(dtheta <=90){
      // increase in x is sin theta * hypot
      dx = Math.sin(degreesToRadians(dtheta))*hypotenuse;
      // decrease in y is sin theta * hypot
      dy = Math.cos(degreesToRadians(dtheta))*hypotenuse;
      dy = dy * -1;
      lineEnd[0] = (int)dx;
      lineEnd[1] = (int)dy; 
      return lineEnd;
    }
   
    // if its in the bottom right corner x and y are increasing

    if((dtheta >90)&&(dtheta <=180)){
      // flip it round 90 degs
      dtheta -= 90;
      // increase in x is now the adjacent side
      lineEnd[0] = (int)(Math.cos(degreesToRadians(dtheta))*hypotenuse);
      // increase in y is the opposite side
      lineEnd[1] = (int)(Math.sin(degreesToRadians(dtheta))*hypotenuse);
      return lineEnd;
    }
    
    // if its in the bottom left corner, x is decreasing and y is increasing
    
    if ((dtheta >180)&&(dtheta <=270)){
      // flip it round 180 degs
      dtheta -=180;
      // decrease in x is the opposite side
      lineEnd[0] = -1 * ((int)(Math.sin(degreesToRadians(dtheta))*hypotenuse));
      // increase in y is the adjacent side
      lineEnd[1] = (int)(Math.cos(degreesToRadians(dtheta))*hypotenuse);
      return lineEnd;
    }
       
    // if its in the top right corner, x and y are decreasing
    
    if (dtheta > 270){
      // flip it round 270 degs
      dtheta -= 270;
      // decrease in x is opposite side
      lineEnd[0] = -1 * ((int)(Math.sin(degreesToRadians(dtheta))*hypotenuse));
      // decrease in y is adjacent side
      lineEnd[1] = -1 * ((int)(Math.cos(degreesToRadians(dtheta))*hypotenuse));
      return lineEnd;
    }
    return lineEnd;
    
  }

  
} // CircuitGraphic
