package AudioServe;
import java.awt.*;

/**
 * TextDisplay.java
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
 * Created: Tue Aug 08 13:24:45 2000
 *
 * @author 
 * @version
 */

public class TextDisplay extends TextArea {
  
  // these ints define the codes of writing. the text is displayed in different colours 
  // depending on the code
  public int circuitInfo = 0;
  public int networkInfo = 1;
  public int errorInfo  = 2;
  int columns; 
  int rows;  
  private String lastText = "";
  private int code;
  
  public void clear(){
    // gets rid of all the text in the display.
    this.setText(null);
  }
  
  public void displayLastText(){
    this.replace(lastText, code);
  }
  
  
  
  public void add(String text, int code){
    // appends the sent text to the end of the display.
    // depending on the code, the text is a different colour
    lastText = this.getText(); // to remeber last text displayed and its color
    this.code = code;
    this.setTextColor(code);
    this.append(text);
    return;
    
  }

  public void replace(String text, int code){
    // clears the text and replaces it with the 
    this.setTextColor(code);
    this.clear();
    this.code = code;
    this.append(text);
    return;
  }
  
  private void setTextColor(int code){
    
    if(code == circuitInfo){this.setBackground(Color.black);this.setForeground(Color.green);return;}
    /*    if(code == networkInfo){this.setBackground(Color.blue);this.setForeground(Color.yellow);return;}*/
    if(code == errorInfo){this.setBackground(Color.red);this.setForeground(Color.white);}
  }
  
  public TextDisplay(int rows, int columns){
    this.setColumns(columns);
    this.setRows(rows);
  }
    
} // TextDisplay
