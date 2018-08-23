//created by Michael Kuhn

package nn;

public class NeuralNet {
  //instance variables
  private int[] structure;
  private Neuron[][] brain;


  //constructor
  public NeuralNet(int[] structure) {
    this.structure = structure;

    int headspace = 1;
    for(int i = 0; i<structure.length; i++) {
      headspace = Math.max(structure[i], headspace);
    }

    this.brain = new Neuron[structure.length][headspace];

    for(int layer = 0; layer < structure.length; layer++) {
      for(int neuron = 0; neuron < structure[layer]; neuron++) {
        this.brain[layer][neuron] = new Neuron(structure, layer);
      }
    }
  }


  //getters & setters
  public Neuron[][] getBrain() {
    return this.brain;
  }

  public Matrix getValues(int layer) {
    double[][] values = new double[this.structure[layer]][1];
    for(int n = 0; n < values.length; n++) {
      values[n][0] = this.brain[layer][n].getActivation();
    }
    return new Matrix(values);
  }

  public void setValues(int layer, Matrix values) {
    for(int n = 0; n < structure[layer]; n++) {
      this.brain[layer][n].setActivation(values.getMatrix()[n][0]);
    }
  }

  public Matrix getWeights(int intoLayer) {
    if(intoLayer == 0) {
      return new Matrix(0,0);
    }

    double[][] weights = new double[this.structure[intoLayer]][this.structure[intoLayer-1]];
    for(int r = 0; r < weights.length; r++) {
      weights[r] = this.brain[intoLayer][r].getSynapses();
    }
    return new Matrix(weights);
  }
  public Matrix[] getWeights() {
    Matrix[] weights = new Matrix[this.structure.length-1];
    for(int l = 1; l < this.structure.length; l++) {
      weights[l-1] = this.getWeights(l);
    }

    return weights;
  }

  public void setWeights(int intoLayer, Matrix weights) {
    for(int r = 0; r < weights.getRows(); r++) {
      this.brain[intoLayer][r].setSynapses(weights.getMatrix()[r]);
    }
  }
  public void setWeights(Matrix[] weights) {
    for(int l = 1; l < this.structure.length; l++) {
      this.setWeights(l, weights[l-1]);
    }
  }

  public Matrix getBiases(int intoLayer) {
    if(intoLayer == 0) {
      return new Matrix(0,0);
    }

    double[][] biases = new double[this.structure[intoLayer]][1];
    for(int n = 0; n < biases.length; n++) {
      biases[n][0] = this.brain[intoLayer][n].getThreshold();
    }
    return new Matrix(biases);
  }
  public Matrix[] getBiases() {
    Matrix[] biases = new Matrix[this.structure.length-1];
    for(int l = 1; l < this.structure.length; l++) {
      biases[l-1] = this.getBiases(l);
    }

    return biases;
  }

  public void setBiases(int intoLayer, Matrix biases) {
    for(int r = 0; r < biases.getRows(); r++) {
      this.brain[intoLayer][r].setThreshold(biases.getMatrix()[r][0]);
    }
  }
  public void setBiases(Matrix[] biases) {
    for(int l = 1; l < this.structure.length; l++) {
      this.setBiases(l, biases[l-1]);
    }
  }

  public double[] getExpected(DigitImg example) {
    double[] e = new double[this.structure[this.structure.length-1]];
    for(int i = 0; i < e.length; i++) {
      e[i] = i == example.getLabel() ? 1 : 0;
    }
    return e;
  }

  public double[] getOutput(boolean print) {
    Matrix result = this.getValues(this.structure.length-1);

    double[] output = new double[this.structure[this.structure.length-1]];
    for(int i = 0; i < output.length; i++) {
      output[i] = result.getMatrix()[i][0];
    }

    if(print) {
      result.print();
    }

    return output;
  }

  public void saveNetwork(String wPath, String bPath) {
    FileOps.writeSSV(this.getWeights(), wPath);
    FileOps.writeSSV(this.getBiases(), bPath);
    System.out.println("Network Saved.");
  }

  public void loadNetwork(String wPath, String bPath) {
    this.setWeights(FileOps.readSSV(wPath, 3));
    this.setBiases(FileOps.readSSV(bPath, 3));
    System.out.println("Network Loaded.");
  }


  //methods
  public double sigmoid(double n) {
    return 1/(1 + Math.exp(-n));
  }

  public double sigmoid_prime(Neuron n) {
    return n.getActivation()*(1-n.getActivation());
  }

  public void input(Matrix data) {
    this.setValues(0, data);
  }

  public void signal(int layer, int intoLayer) {
    Matrix weights = this.getWeights(intoLayer);
    Matrix biases = this.getBiases(intoLayer);
    Matrix values = this.getValues(layer);

    Matrix nextValues = new Matrix(weights.multiply(values.getMatrix()).add(biases.getMatrix()).getMatrix());

    for(int n = 0; n < structure[intoLayer]; n++) {
      this.brain[intoLayer][n].setActivation(sigmoid(nextValues.getMatrix()[n][0]));
    }
  }

  public void fProp(Matrix data) {
    this.input(data);
    for(int l = 1; l < this.structure.length; l++) {
      signal(l-1, l);
    }
  }

  public double cost(double[] e, double[] o) {
    double cost = 0;
    for(int i = 0; i < e.length; i++) {
      cost += Math.pow(e[i]-o[i], 2);
    }

    return cost;
  }

  public double cost_prime(double e, double o) {
    return 2*(e-o);
  }

  public double delA(int layer, int k, double[] expected) {
    if(layer == this.structure.length-1) {
      return cost_prime(expected[k], this.brain[layer][k].getActivation());
    }

    double sum = 0;
    for(int j = 0; j < this.structure[layer+1]; j++) {
      sum += this.brain[layer+1][j].getSynapses()[k] * this.sigmoid_prime(this.brain[layer+1][j]) * this.delA(layer+1, j, expected);
    }

    return sum;
  }

  public double delB(int intoLayer, int j, double[] expected) {
    return this.sigmoid_prime(this.brain[intoLayer][j]) * this.delA(intoLayer, j, expected);
  }

  public double delW(int k, int intoLayer, int j, double[] expected) {
    return this.brain[intoLayer-1][k].getActivation() * this.sigmoid_prime(this.brain[intoLayer][j]) * this.delA(intoLayer, j, expected);
  }

  public Matrix gDW(int intoLayer, double[] expected) {
    double[][] dW = new double[this.structure[intoLayer]][this.structure[intoLayer-1]];

    for(int j = 0; j < dW.length; j++) {
      for(int k = 0; k < dW[0].length; k++) {
        dW[j][k] = this.delW(k, intoLayer, j, expected);
      }
    }

    return new Matrix(dW);
  }
  public Matrix[] gDW(double[] expected) {
    Matrix[] dW = new Matrix[this.structure.length-1];

    for(int l = 1; l < this.structure.length; l++) {
      dW[l-1] = gDW(l, expected);
    }

    return dW;
  }

  public Matrix gDB(int intoLayer, double[] expected) {
    double[][] dB = new double[this.structure[intoLayer]][1];

    for(int j = 0; j < dB.length; j++) {
      dB[j][0] = this.delB(intoLayer, j, expected);
    }

    return new Matrix(dB);
  }
  public Matrix[] gDB(double[] expected) {
    Matrix[] dB = new Matrix[this.structure.length-1];

    for(int l = 1; l < this.structure.length; l++) {
      dB[l-1] = gDB(l, expected);
    }

    return dB;
  }

  public Matrix[] gradientDescentW(Matrix[] dW) {
    Matrix[] weights = this.getWeights();

    for(int i = 0; i < weights.length; i++) {
      weights[i] = weights[i].add(dW[i].getMatrix());
    }

    return weights;
  }

  public Matrix[] gradientDescentB(Matrix[] dB) {
    Matrix[] biases = this.getBiases();

    for(int i = 0; i < biases.length; i++) {
      biases[i] = biases[i].add(dB[i].getMatrix());
    }

    return biases;
  }

  public void bProp(Matrix[] gDW, Matrix[] gDB) {
    this.setWeights(this.gradientDescentW(gDW));
    this.setBiases(this.gradientDescentB(gDB));
  }

  public Matrix[][] train(DigitImg example) {
    this.fProp(example.getImg());

    double[] expected = this.getExpected(example);
    double[] output = this.getOutput(false);

    Matrix[] gDW = this.gDW(expected);
    Matrix[] gDB = this.gDB(expected);

    Matrix[][] dGExample = {gDW, gDB};
    return dGExample;
  }
  public Matrix[][] train(DataSet data) {
    Matrix[] avgGDW = new Matrix[this.structure.length-1];
    Matrix[] avgGDB = new Matrix[this.structure.length-1];

    for(int l = 1; l < this.structure.length; l++) {
      avgGDW[l-1] = new Matrix(this.structure[l], this.structure[l-1]);
      avgGDB[l-1] = new Matrix(this.structure[l], 1);
    }

    //Stochastic Backpropagation:
    //sum gradient steps
    for(int i = 0; i < data.getData().length; i++) {
      DigitImg ex = data.getData()[i];
      Matrix[][] gd = this.train(ex);
      for(int l = 1; l < this.structure.length; l++) {
        avgGDW[l-1] = avgGDW[l-1].add(gd[0][l-1].getMatrix());
        avgGDB[l-1] = avgGDB[l-1].add(gd[1][l-1].getMatrix());
      }
    }

    //avg gradient steps => 1 step (multi-dimensional vector)
    for(int l = 1; l < this.structure.length; l++) {
      avgGDW[l-1] = avgGDW[l-1].scalar((double) 1/data.getData().length);
      avgGDB[l-1] = avgGDB[l-1].scalar((double) 1/data.getData().length);
    }

    Matrix[][] gDSet = {avgGDW, avgGDB};
    return gDSet;
  }
  public Matrix[][] train(String path, int[] subset, int group) {
    Matrix[] avgGDW = new Matrix[this.structure.length-1];
    Matrix[] avgGDB = new Matrix[this.structure.length-1];

    for(int l = 1; l < this.structure.length; l++) {
      avgGDW[l-1] = new Matrix(this.structure[l], this.structure[l-1]);
      avgGDB[l-1] = new Matrix(this.structure[l], 1);
    }

    int[] sub_subset = {subset[0], (subset[1] - subset[0])/group + subset[0]};

    while(sub_subset[1] <= subset[1]) {
      DataSet train = new DataSet(path, sub_subset);

      //get descent steps
      Matrix[][] gDSet = this.train(train);
      for(int l = 1; l < this.structure.length; l++) {
        avgGDW[l-1] = avgGDW[l-1].add(gDSet[0][l-1].getMatrix());
        avgGDB[l-1] = avgGDB[l-1].add(gDSet[1][l-1].getMatrix());
      }

      sub_subset[0] = sub_subset[1];
      sub_subset[1] += (subset[1] - subset[0])/group;
    }

    for(int l = 1; l < this.structure.length; l++) {
      avgGDW[l-1] = avgGDW[l-1].scalar((double) 1/group);
      avgGDB[l-1] = avgGDB[l-1].scalar((double) 1/group);
    }

    Matrix[][] gDRange = {avgGDW, avgGDB};
    return gDRange;
  }
  public void train(String path, int totalElements, int step, int repititions) {
    final int maxStep = 10000;
    int group = (int) Math.ceil((double) step/maxStep);
    if(step > maxStep) {
      while(step%group != 0) {
        group++;
      }
    }

    int[] subset = {0, step};

    for(int i = 0; i < repititions; i++) {
      subset[0] = 0;
      subset[1] = step;

      System.out.println();
      System.out.println("****************************************************************");
      System.out.println("                Now on repitition " + (i+1) + " of training.                ");
      System.out.println("****************************************************************");
      System.out.println();

      while(subset[1] <= totalElements) {
        System.out.println("Training on data subset [" + subset[0] + ", " + subset[1] + ")...");
        System.out.println();

        //get descent steps
        Matrix[][] gDSet = this.train(path, subset, group);

        //step along multi-dimensional vector
        this.bProp(gDSet[0], gDSet[1]);

        System.out.println();
        System.out.println("Gradient descent for subset [" + subset[0] + ", " + subset[1] + ") complete.");
        System.out.println("----------------------------------------");

        subset[0] = subset[1];
        subset[1] += step;
      }
    }
  }

  public double[] test(DigitImg example, boolean print) {
    this.fProp(example.getImg());

    if(print) {
      System.out.println(example.getLabel() + ":");
    }

    double[] output = this.getOutput(print);
    double[] expected = this.getExpected(example);

    double[] data = new double[3];

    int max = 0;
    for(int i = 0; i < output.length; i++) {
      max = output[i] > output[max] ? i : max;
    }

    data[0] = this.cost(expected, output);
    data[1] = max;
    data[2] = example.getLabel();

    return data;
  }
  public double[][] test(DataSet data, boolean print) {
    double[][] outputs = new double[data.getData().length][3];
    for(int i = 0; i < outputs.length; i++) {
      outputs[i] = this.test(data.getData()[i], print);
    }

    return outputs;
  }
  public void test(String path, int totalElements, int range, boolean print) {
    int[] subset = {0, range};

    int missed = 0;
    double cost = 0;

    while(subset[1] <= totalElements) {
      DataSet test = new DataSet(path, subset);

      System.out.println("Testing on data in range [" + subset[0] + ", " + subset[1] + ")");
      Matrix result = new Matrix(this.test(test, print));

      for(int i = 0; i < result.getRows(); i++) {
        cost += result.getMatrix()[i][0]/totalElements;
        if(result.getMatrix()[i][1] != result.getMatrix()[i][2]) {
          missed++;
          System.out.println("vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv");
          System.out.println("Failed to correctly recognize element " + (subset[0]+i) + " in '" + path + "':");
          System.out.println("Identified " + result.getMatrix()[i][2] + " as " + result.getMatrix()[i][1] + ".");
          System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        }
      }

      subset[0] += range;
      subset[1] += range;

      System.out.println("----------------------------------------");
    }

    System.out.println("Total missed = " + missed);
    System.out.println();
    System.out.println();
    System.out.println("Avg. Cost = " + cost);
    System.out.println();
    System.out.println("Accuracy = " + ((double) (totalElements-missed)/totalElements));
    System.out.println();
  }

  public void printNetwork() {
    int weights = 0;
    int biases = 0;

    System.out.println("Weights:");
    for(Matrix w : this.getWeights()) {
      w.print();
      weights += w.getRows()*w.getCols();
    }

    System.out.println("Biases:");
    for(Matrix b : this.getBiases()) {
      b.print();
      biases += b.getRows()*b.getCols();
    }

    System.out.println(weights + " weights & " + biases + " biases");
  }
}
