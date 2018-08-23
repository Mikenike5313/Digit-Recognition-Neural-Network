package nn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class FileOps {
  //static methods
  public static String read(String path) {
    BufferedReader br = null;

    String data = "";

    try{
      br = new BufferedReader(new FileReader(path));

      String line;
      while((line = br.readLine()) != null){
        data += line + ",";
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    finally {
			try {
				if (br != null) {
					br.close();
        }
			}
      catch (IOException ex) {
				ex.printStackTrace();
			}
    }

    data = data.substring(0, data.length()-1);
    return data;
  }

  public static void write(String data, String path) {
    FileWriter fw = null;
    BufferedWriter bw = null;

    try {
      fw = new FileWriter(path);
			bw = new BufferedWriter(fw);

			bw.write(data);
		}
    catch (IOException e) {
			e.printStackTrace();
		}
    finally {
			try {
				if (bw != null) {
					bw.close();
        }
				if (fw != null) {
					fw.close();
        }
			}
      catch (IOException ex) {
				ex.printStackTrace();
			}
    }
  }

  public static DigitImg parseMNIST_CSV_Line(String line) {
    String[] data = line.split(",");

    double[][] img = new double[28][28];
    for(int j = 0; j < data.length-1; j++) {
      img[j/28][j%28] = (double) Integer.parseInt(data[j+1])/255;
    }
    return new DigitImg(Integer.parseInt(data[0]), img);
  }

  public static DigitImg[] readMNIST_CSV(String path, int[] subset) {
    BufferedReader br = null;

    DigitImg[] dataSet = new DigitImg[subset[1]-subset[0]];

    try{
      br = new BufferedReader(new FileReader(path));

      for(int l = 0; l < subset[0]; l++) {
        br.readLine();
      }

      String line = "";
      for(int i = 0; (line = br.readLine()) != null && i < dataSet.length; i++) {
        dataSet[i] = parseMNIST_CSV_Line(line);
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    finally {
			try {
				if (br != null) {
					br.close();
        }
			}
      catch (IOException ex) {
				ex.printStackTrace();
			}
    }

    return dataSet;
  }

  public static int getNumLines(String path) {
    BufferedReader br = null;

    int lines = 0;

    try{
      br = new BufferedReader(new FileReader(path));

      while(br.readLine() != null){
        lines++;
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    finally {
			try {
				if (br != null) {
					br.close();
        }
			}
      catch (IOException ex) {
				ex.printStackTrace();
			}
    }

    return lines;
  }

  public static String[] getFilesInDir(String dirPath) {
    File dir = new File(dirPath);
    File[] fileList = dir.listFiles();

    String[] names = new String[fileList.length];
    if(fileList != null) {
      for(int f = 0; f < names.length; f++) {
        names[f] = fileList[f].exists() ? fileList[f].getName() : "Error: file not found.";
      }
    }
    else {
      System.out.println("Error: '" + dirPath + "' is not a directory.");
    }

    return names;
  }

  public static String[] readSSV(String path) {
    String fileData = read(path);
    String[] data = fileData.split(",");

    return data;
  }
  public static Matrix[] readSSV(String path, int dimensions) {
    String fileData = read(path);

    ArrayList<ArrayList<String[]>> arrListData = new ArrayList<ArrayList<String[]>>();
    String[] strMatrices = fileData.split(":");
    for(int d = 0; d < strMatrices.length; d++) {
      ArrayList<String[]> matrix = new ArrayList<String[]>();
      String[] strRow = strMatrices[d].split(";");
      for(int r = 0; r < strRow.length; r++) {
        String[] row = strRow[r].split(",");
        matrix.add(row);
      }
      arrListData.add(matrix);
    }

    Matrix[] data = new Matrix[arrListData.size()];
    for(int d = 0; d < data.length; d++) {
      double[][] matrix = new double[arrListData.get(d).size()][arrListData.get(d).get(0).length];
      for(int r = 0; r < matrix.length; r++) {
        for (int c = 0; c < matrix[0].length; c++) {
          matrix[r][c] = Double.parseDouble(arrListData.get(d).get(r)[c]);
        }
      }
      data[d] = new Matrix(matrix);
    }

    return data;
  }

  public static void writeCSV(String[] arrData, String path) {
    String data = "";
    for(int i = 0; i < arrData.length; i++) {
      data += i == arrData.length-1 ? arrData[i] : arrData[i] + ",";
    }
    write(data, path);
  }
  public static void writeSSV(Matrix[] arrData, String path) {
    String data = "";
    for(int d = 0; d < arrData.length; d++) {
      for(int r = 0; r < arrData[d].getRows(); r++) {
        for(int c = 0; c < arrData[d].getCols(); c++) {
          data += c == arrData[d].getCols()-1 ? arrData[d].getMatrix()[r][c] : arrData[d].getMatrix()[r][c] + ",";
        }
        data += r != arrData[d].getRows()-1 ? ";" : "";
      }
      data += d != arrData.length-1 ? ":" : "";
    }
    write(data, path);
  }
}
