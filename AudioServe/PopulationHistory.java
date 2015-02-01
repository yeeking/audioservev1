package AudioServe;
/**
 * PopulationHistory.java
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
 * Created: Mon Aug 14 17:37:32 2000
 *
 * @author 
 * @version
 */

public class PopulationHistory  {
  
  private int historyLength;
  private Population[] history;
  private Population tempPopulation;
  
  private int indiCounter, i;
   
  public Population retrievePopulation(int popIndex){
    // generate a new population object
    // that is a decompacted version of the stored population and return it
    // get a pointer to the desired compact feller
    tempPopulation = history[popIndex];

    // instantiate a new population
    Population bigPop = new Population(tempPopulation.populationSize);
    int[] oldChromo;
    for (indiCounter=0; indiCounter<bigPop.populationSize; indiCounter++){
      // this loop runs through the population that needs expanding
      // get the chromosome and circuit params from the compact pop.
    
      int maxGridSize = tempPopulation.circuits[indiCounter].maxGridSize;
      int maxSegmentRadius = tempPopulation.circuits[indiCounter].maxSegmentRadius;
      int maxSegmentAngle = tempPopulation.circuits[indiCounter].maxAngle;
      String name = tempPopulation.getName(indiCounter);
      bigPop.parentName = tempPopulation.parentName;
      oldChromo = tempPopulation.circuits[indiCounter].getChromosome();
      // instantiate the individual and develop it (boolean argument)
      bigPop.circuits[indiCounter] = new Individual(true, oldChromo, maxGridSize,
						    maxSegmentRadius, maxSegmentAngle, name);
      
    }
    return bigPop;
    
  } 
  
  public void updateCurrent(Population latestPop, int currentPop){
    // makes sure the most evolved population is in sync with the population running in the applet
    // this method is called whrn the user changes a name in the population for example...
    for (indiCounter=0; indiCounter<latestPop.circuits.length; indiCounter++){
      // synchronise the fields that may have changed with those in the passed pop object
      
      history[currentPop].setName(indiCounter, latestPop.getName(indiCounter));

    }
    
  }
  
  public void addPopulation(Population newPopulation){
    // makes a compact version of the passed population and adds it to the
    // top of the history array

    // get a new population object that is a 
    // compact version of the passed pop.
    tempPopulation = getCompactPopulation(newPopulation);
    
    // add tempPopulation to the top of the heap(history[length-1], losing the oldest population
    // the population at index 0 is replaced with index 1 and so on --> FIFO
           
    // do the FIFO on the current population history to lose the oldest population
    // and make a space for the new one.
    int heapCounter;
   
    for (heapCounter=1; heapCounter<history.length; heapCounter++){
      if (history[heapCounter] != null){ // theres something there to shift!
	history[heapCounter-1] = history[heapCounter];
	//System.out.println("\nnext gen moved pop "+heapCounter+" to "+(heapCounter-1));
      }
    }
    history[history.length-1] = tempPopulation;
    
  }
  
  public Population getCompactPopulation(Population targetPop){
    // transforms the current population into a compact form
    // used for storing previous generations for retrieval
    // parse the individual array generating a new array of compact individuals
    // the compact population is equivalent to a population that
    // has not had its individual.genome.develop method called
    // i.e. no circuitSpecs have been instantiated
    // this means a compact population consists of little more than an array of
    // int arrays. but is ready to be turned into a set of running circuits
    
    Population compactPopulation = new Population(targetPop.populationSize);
    
    int[] oldChromosome;
    for (indiCounter=0; indiCounter < targetPop.circuits.length; indiCounter++){
      // this for loop copies the genomes from this population object to 
      // the compact one
      
      // first get all the necessary fields...
      int maxGridSize = targetPop.circuits[indiCounter].maxGridSize;
      int maxSegmentRadius = targetPop.circuits[indiCounter].maxSegmentRadius;
      int maxSegmentAngle = targetPop.circuits[indiCounter].maxAngle;
      String name = targetPop.getName(indiCounter);
      compactPopulation.parentName = targetPop.parentName;
      oldChromosome = targetPop.circuits[indiCounter].getChromosome();
      
      // then instantiate the individual using the compact constructor in the compact population.
      // boolean argument to the constructor tells it wether to develop or not
      compactPopulation.circuits[indiCounter] = new Individual(false, oldChromosome, maxGridSize,
							       maxSegmentRadius, maxSegmentAngle, 
							       name);
      
    }
      
    return compactPopulation;
  }

  public PopulationHistory(int length, Population initialPopulation) {
    this.historyLength = length;
    history = new Population[historyLength];
    int popCounter;
    // initialise the history array so its got <length> copies of the initial population
    // this makes coding the history list in audioserve object simpler
    for (popCounter=0;popCounter<length;popCounter++){
      addPopulation(initialPopulation);
    }
   
  }
  
} // PopulationHistory
