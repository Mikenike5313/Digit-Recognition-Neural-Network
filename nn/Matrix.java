package nn;

public class Matrix {

  //instance variables
  private int rows;
  private int cols;
  private double[][] matrix;

  //constructors
  public Matrix(int rows, int cols) {
    this.rows = rows;
    this.cols = cols;
    this.matrix = new double[rows][cols];
    this.init();
  }

  public Matrix(double[][] matrix) {
    this.setMatrix(matrix);
  }


  //getters & setters
  public int getRows() {
    return this.rows;
  }

  public int getCols() {
    return this.cols;
  }

  public double[][] getMatrix() {
    return this.matrix;
  }

  public void setMatrix(double[][] m) {
    if(m.length == 0) {
      System.out.println("Error: can't create a matrix with no rows!");
      return;
    }

    this.matrix = m;
    this.rows = m.length;
    this.cols = m[0].length;
  }


  //methods
  public void init() {
		for(int row = 0; row < this.rows; row++) {
			for(int col = 0; col < this.cols; col++) {
				this.matrix[row][col] = 0;
			}
		}
	}

	public boolean hasSameDimensionsAs(double[][] m) {
		return this.rows == m.length && this.cols == m[0].length;
	}

	public Matrix add(double[][] m) {
		double[][] result = new double[this.rows][this.cols];
		if(this.hasSameDimensionsAs(m)) {
			for(int row = 0; row < this.rows; row++) {
				for(int col = 0; col < this.cols; col++) {
					result[row][col] = this.matrix[row][col] + m[row][col];
				}
			}
			return new Matrix(result);
		}
		else {
			System.out.println("Error: the dimensions are not equal!");
			return new Matrix(result);
		}
	}

	public Matrix subtract(double[][] m) {
    double[][] result = new double[this.rows][this.cols];
		if(this.hasSameDimensionsAs(m)) {
			for(int row = 0; row < this.rows; row++) {
				for(int col = 0; col < this.cols; col++) {
					result[row][col] = this.matrix[row][col] - m[row][col];
				}
			}
			return new Matrix(result);
		}
		else {
			System.out.println("Error: the dimensions are not equal!");
			return new Matrix(result);
		}
	}

  public Matrix scalar(double s) {
    double[][] result = this.matrix;
		for(int row = 0; row < this.rows; row++) {
			for(int col = 0; col < this.cols; col++) {
				result[row][col] *= s;
			}
		}
		return new Matrix(result);
  }

	public Matrix multiply(double[][] m) {
		double[][] result = new double[this.rows][m[0].length];
		if(this.cols == m.length) {
			for(int row = 0; row < this.rows; row++) {
				for(int col = 0; col < m[0].length; col++) {
					result[row][col] = 0;
					for(int i = 0; i < m.length; i++) {
						result[row][col] += this.matrix[row][i] * m[i][col];
					}
				}
			}
			return new Matrix(result);
		}
		else {
			System.out.println("Error: can't multiply matrices with these dimensions!");
			return new Matrix(result);
		}
	}

	public void print() {
		for(int row = 0; row < this.rows; row++) {
			for(int col = 0; col < this.cols; col++) {
				System.out.print(this.matrix[row][col] + " ");
			}
			System.out.println();
		}
		System.out.println();
	}
}
