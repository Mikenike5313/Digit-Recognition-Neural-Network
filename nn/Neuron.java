package nn;

public class Neuron {

  //instance variables
  private double activation;
  private double threshold;
  private int layer;
  private double[] synapses;


  //constructor
  public Neuron(int[] structure, int layer) {
    this.activation = 0;
    this.layer = layer;

    if(layer == 0) {
      return;
    }

    this.threshold = 0;
    this.synapses = new double[structure[layer-1]];

    double r = (layer == structure.length-1) ? Math.sqrt((double)12/(structure[layer]*structure[layer-1])) : Math.sqrt((double)12/(structure[layer]*structure[layer-1] + structure[layer]*structure[layer+1]));
    for(int w = 0; w < this.synapses.length; w++) {
      this.synapses[w] = (Math.random() < .5) ? Math.random()*-r : Math.random()*r;
    }
  }


  //getters & setters
  public double getActivation() {
    return this.activation;
  }

  public void setActivation(double val) {
    this.activation = val;
  }

  public double getThreshold() {
    return this.threshold;
  }

  public void setThreshold(double b) {
    this.threshold = b;
  }

  public int getLayer() {
    return this.layer;
  }

  public double[] getSynapses() {
    return this.synapses;
  }

  public void setSynapses(double[] synapses) {
    this.synapses = synapses;
  }
}
