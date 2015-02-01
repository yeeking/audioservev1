package AudioServe;
import java.util.*; 
import java.net.*; 
import java.io.*;
import java.awt.*;

/**
 * Population.java
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
 * Created: Sat Aug 05 21:31:09 2000
 * 
 * 
 * 
 *
 * This object represents a population of Individual objects. 
 * 
 *
 * @author 
 * @version
 */

public class Population  {
 
  public String parentName=null;// if the population has a parent this gets assigned
  public Individual[] circuits; 
  public int populationSize = 20;
  public int genomeSize = 20; // the number of genes in a genome

  String name;
  private int i, indiCounter;
  private Genome chosenGenome;

  private int maxGridSize, maxSegmentRadius, maxSegmentAngle;
  public static void main(String argv[]){
    // the main method.
    
    Population pop = new Population(10);
    pop.initialiseRandom(5, 10, 20, 360);

  } 
  
  public void initialiseRandom(int genomeSize, int maxGridSize, int maxSegmentRadius, int maxSegmentAngle){
    // generates a random population of ciruits
    // first run through the individual array instantiating and 
    // developing them
    this.genomeSize = genomeSize;
    for(i=0; i< circuits.length;i++){ 
      // instantiate an Individual. this will cause it to generate a random 
      // genome, then develop to produce a circuitSpec object and a CircuitGraphic Object
      circuits[i] = new Individual(genomeSize, maxGridSize, maxSegmentRadius, maxSegmentAngle);
    }
  }
  
  public void browseCircuits(Canvas canvas){
    // flicks through the population displaying the circuits
    for (i=0; i< circuits.length; i++){
    
      circuits[i].drawCircuit(canvas);
    }   
  }

  public String getName(int index){
    // returns the name string of the individual with the specified index
    return circuits[index].name;
  }

  public void setName(int index, String newName){
    // sets the name of the specified individual to the sent String
    circuits[index].setName(newName);
  }
  
  public void renameIndividual(int index, String name){
    circuits[index].setName(name);
  }

  public int[] getChromosome(int index){
    // returns the chromosome of the desired member of the population
    return circuits[index].getChromosome();
  }
   
  public void mutateAndReplace(Individual individual, double mutationRate, int mode){
    // replaces the population with mutated versions of the chosen individual
    // and makes a copy of the current population to allow the user to backtrack. 
    int[] mutantChromosome;
    // first call the nextGeneration   method. this stores a comapact 
    // copy of the current population so the current pop can be changed
    // then replace all individuals chromosomes with mutant versions of the chosen one
    // except the first inidividual. this gets an unadulterated version of the chosen
    // individuals chromosome.
    maxGridSize = individual.maxGridSize;
    maxSegmentRadius = individual.maxSegmentRadius;
    maxSegmentAngle = individual.maxAngle;
    name = individual.name;
    circuits[0].replaceChromosome(individual.getChromosome(), maxGridSize,
				  maxSegmentRadius, maxSegmentAngle, name);
    
    for (indiCounter=1; indiCounter<circuits.length;indiCounter++){
      // individual.getMutantChromosome returns a mutant copy of the
      // chosen chromsome, leaving the original chromosome intact. 
      mutantChromosome = circuits[0].getMutantChromosome(mutationRate, mode);
      // individual.replaceChromosome replaces the chromosome and
      // calls develop, so the individual has a new circuitSpec object
      circuits[indiCounter].replaceChromosome(mutantChromosome, maxGridSize, maxSegmentRadius, maxSegmentAngle, name+"v"+indiCounter);
    } 
    parentName = name;
        
  }
  
  public Population(int populationSize) {
    this.populationSize = populationSize;
    circuits = new Individual[populationSize];

  }


} // Population


