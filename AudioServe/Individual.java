package AudioServe;
import java.util.*;
import com.softsynth.jsyn.*;
import java.awt.*;
import java.applet.Applet;

/**
 * Individual.java
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
 * Created: Sun Jul 23 15:39:30 2000
 *
 * @author yee-king 
 * @version 0.1
 */



public class Individual {
  
  public CircuitSpec spec;
  public Circuit circuit;
  private Genome genome;
  private CircuitGraphic circuitGraphic;
  private boolean genomeReady = false;
  public int maxGridSize = 350; // x,y size for grid. 
  public int maxLocusValue = 500; // sets the granularity for circuit features like connection
                                  // ranges
  public int maxAngle = 360;
  public int maxSegmentRadius = 200;
  
  String name = "default"; // the name of this individual (user assigned)

  LineOut myOut;
  int numberOfGenes = 100;
  int c, i;
  int blobSize = 10;
  int frameSize = 400; // x,y size of the frame to draw the circuit into.
 
  
  // mutation modes

  public int point = 0;
  public int duplicate = 1;
  public int all = 2;

  public void initialise(){
    // sets up stuff that only needs doing when the object needs to do something?
    // maybe just take care of this in the constructor
    return;
  }

  public void develop(){
    // calls develop on the genome
    this.genome.develop();
    
  }

  public void drawCircuit(Canvas canvas){
    // sends the canvas thru to the circuitGraphics object so it can draw itself.
    circuitGraphic.drawCircuit(canvas);
  }
  
  public void replaceChromosome(int[] newChromosome, int maxGridSize, int maxSegmentRadius, int maxSegmentAngle, String name){
    // makes a new genome object with the passed chromosome
    // points this.genome at the new object
    // calls develop on the new genome so its ready to have its 
    // jsyn Circuit built.
    
    // make a new genome
    // have to send the parameters through so the developmental process
    // normalises the locus values appropriately
    genome = new Genome(newChromosome, maxGridSize, maxSegmentRadius, maxSegmentAngle);
    // develop it to get a circuitSpec object
    this.spec = genome.develop();
    this.name = name;
    // update the circuitGraphic object
    circuitGraphic = new CircuitGraphic(spec, blobSize, frameSize);
    
  }

  
  public int[]  getChromosome(){
    // returns a copy of genome.chromsome 
    return this.genome.copyChromosome();
  }
  
  public String getChromosomeString(){
    // returns the string version  of the chromosome
    return this.genome.copyChromosomeString();
  }
  
  public Genome getGenome(){
    // returns a genomeobject
    return this.genome;
  }
  
  public int[] getMutantChromosome(double mutationRate, int mode){
    // returns a mutated version of the chromosome 
    int[] mutantChromosome;
    mutantChromosome = this.genome.getMutantChromosome(mutationRate, mode);
    return mutantChromosome;
  }
  
  public boolean build(){
    try{
      // instantiate (the Circuit constructor declares arrays etc)
      circuit = new Circuit(spec);
      // build the circuit
      circuit.build();
      return true;
    }catch(SynthException e) {
      SynthAlert.showError(e); return false;
    }
  }

  public void setName(String newName){
    // sets the name of the individual to the sent String
    this.name = newName;
    
  }
  
  public void makeCircuitSpec(){
    // generates a CircuitSpec object from the genome. --> developmental method
    genome.develop();
    return;
  }
  
   
  public void pointMutate(double mutationRate){
    genome.pointMutate(mutationRate);
    return;
  }
  
  
  // constructor

  public Individual(int numberOfGenes, int maxGridSize, int maxSegmentRadius, int maxSegmentAngle) {
    // the initiate from scratch constructor
    // generates a new genome and develops it.
    
    this.numberOfGenes = numberOfGenes;
    this.maxGridSize = maxGridSize;
    this.maxAngle = maxSegmentAngle;
    this.maxSegmentRadius = maxSegmentRadius;
    //this.name = name;
    genome = new Genome(numberOfGenes, maxGridSize, maxSegmentRadius, maxSegmentAngle);
    this.spec = genome.develop();
    circuitGraphic = new CircuitGraphic(spec, blobSize, frameSize); 
  }
   
  
  
  public Individual(boolean develop, int[] chromosome, int maxGridSize, int maxSegmentRadius, int maxSegmentAngle, String name) {
    
    // the 'initiate from passed chromosome' constructor
    // generates a compact individual in that it doesn't call develop on the genome
    // it gererates a genome object with the passed chromosome
    
    this.numberOfGenes = numberOfGenes;
    this.maxGridSize = maxGridSize;
    this.maxAngle = maxSegmentAngle;
    this.maxSegmentRadius = maxSegmentRadius;
    this.name = name;
    //System.out.println("inidividual passed "+name+" named "+this.name);
    genome = new Genome(chromosome, maxGridSize, maxSegmentRadius, maxSegmentAngle);
    if (develop == true){
      this.spec = genome.develop();
      circuitGraphic = new CircuitGraphic(spec, blobSize, frameSize); 
    }
  }
  
  public Individual(boolean develop, int[] chromosome, int maxGridSize, int maxSegmentRadius, int maxSegmentAngle) {
    
    // the 'initiate from passed chromosome' constructor
    // generates a compact individual in that it doesn't call develop on the genome
    // it gererates a genome object with the passed chromosome
    
    this.numberOfGenes = numberOfGenes;
    this.maxGridSize = maxGridSize;
    this.maxAngle = maxSegmentAngle;
    this.maxSegmentRadius = maxSegmentRadius;
   
    //System.out.println("inidividual passed "+name+" named "+this.name);
    genome = new Genome(chromosome, maxGridSize, maxSegmentRadius, maxSegmentAngle);
    if (develop == true){
      this.spec = genome.develop();
      circuitGraphic = new CircuitGraphic(spec, blobSize, frameSize); 
    }
  }
    
      /*public Individual(genome Genome) {
    this.genome = genome;
    genome.develop();
    this.spec = genome.spec;
    }*/
  
} // Individual

