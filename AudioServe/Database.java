package AudioServe;

import java.util.*;

/**
 * Database.java
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
 * Created: Thu May 22 20:01:27 2003
 *
 * @author <a href="mailto:matthew@deaf-pc15">Matthew Yee-King</a>
 * @version
 */

public abstract class Database {
  
  private String url;

  private int numberOfCircuitParameters;

  /**
   *  <code>pushGenome</code> - (interface definition) immigration object uses this to send a circuit out
   *
   * @param individual an <code>Individual</code> value
   * @return a <code>boolean</code> value
   */
  abstract boolean pushGenome(Individual individual);

  

  /**
   *  <code>pullGenome</code> - (interface definition) - immigration object uses this to get a new circuit
   *
   * @return an <code>Individual</code> value
   */
  abstract Individual pullGenome();
  
  

  /**
   * Get the value of numberOfCircuitParameters.
   * @return value of numberOfCircuitParameters.
   */
  public int getNumberOfCircuitParameters() {
    return numberOfCircuitParameters;
  }
  
  /**
   * Set the value of numberOfCircuitParameters.
   * @param v  Value to assign to numberOfCircuitParameters.
   */
  public void setNumberOfCircuitParameters(int  v) {
    this.numberOfCircuitParameters = v;
  }
  


 

  protected int stringToInt(String intString){
    // returns the int value represented by the string
    Integer integer = new Integer(intString);
    return integer.intValue();
  }
  
  protected int[] stringToIntArray(String intArrayString){
    // returns an int array version of a string thats a list of numbers
    StringTokenizer st = new StringTokenizer(intArrayString);
    int[]chromosomeArray = new int[st.countTokens()];
    int tokenCounter = 0;
    while (st.hasMoreTokens()) {
      chromosomeArray[tokenCounter] = stringToInt(st.nextToken());
      tokenCounter++;
    }
    return chromosomeArray;
  }
  


  /**
   * Get the value of url.
   * @return value of url.
   */
  public String getUrl() {
    return url;
  }
  
  /**
   * Set the value of url.
   * @param v  Value to assign to url.
   */
  public void setUrl(String  v) {
    this.url = v;
  }
  
  public Database (int numberOfCircuitParameters, String url){
    setNumberOfCircuitParameters( numberOfCircuitParameters);
    setUrl(url);
  }
  
}// Database
