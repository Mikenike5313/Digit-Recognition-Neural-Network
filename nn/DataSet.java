package nn;

public class DataSet {
  //instance variables
  private DigitImg[] data;


  //constructors
  public DataSet(String path) {

    String[] values = FileOps.readSSV(path);

    this.data = new DigitImg[values.length/785];

    for(int i = 0; i < this.data.length; i++) {
      int label = Integer.parseInt(values[i*785]);
      double[][] img = new double[28][28];
      for(int j = 0; j < 784; j++) {
        img[j/28][j%28] = (double) Integer.parseInt(values[j+(i*785)+1])/255;
      }
      this.data[i] = new DigitImg(label, img);
    }

    System.out.println("Data in: '" + path + "' loaded in.");
  }
  public DataSet(String path, int[] subset) {
    this.data = FileOps.readMNIST_CSV(path, subset);

    System.out.println("Data in: '" + path + "'\nin range: [" + subset[0] + " , " + subset[1] + ") loaded in.");
  }


  //getters & setters
  public DigitImg[] getData() {
    return this.data;
  }


  //methods
  public void print() {
    for(int i = 0; i < this.data.length; i++) {
      this.data[i].print();
      System.out.println();
    }
  }
}
