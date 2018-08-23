package nn;

public class DigitImg {
  //instance variables
  private int label;
  private Matrix img;


  //constructor
  public DigitImg(int label, double[][] data) {
    this.label = label;
    this.img = new Matrix(data);
  }


  //getters & setters
  public int getLabel() {
    return this.label;
  }

  public Matrix getImg() {
    double[][] imgVector = new double[this.img.getRows()*this.img.getCols()][1];
    for(int i = 0; i < imgVector.length; i++) {
      imgVector[i][0] = this.img.getMatrix()[i/this.img.getCols()][i%this.img.getCols()];
    }
    return new Matrix(imgVector);
  }


  //methods
  public void print() {
    System.out.println(this.label);
    this.img.print();
  }
}
