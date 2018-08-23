//Created by Michael Kuhn

import nn.*;
import java.util.Scanner;

public class DigitRecognition {
  //main
  public static void main(String[] args) {
    //constants
    final String wPath = "../src/settings/weights.txt";
    final String bPath = "../src/settings/biases.txt";
    final String dPath = "../src/data/";

    //layers.length = # of layers, layers[i] = neurons/(layer i)
    int[] structure = {784, 16, 16, 10};

    //init network
    NeuralNet numRecog = new NeuralNet(structure);


    //start scanner
    Scanner sc = new Scanner(System.in);

    //train or test?
    System.out.println();
    System.out.print("Train or test?\n(enter train/test): ");

    String action = sc.next().toLowerCase();

    System.out.println();

    //ensure correct input
    while(!action.equals("train") && !action.equals("test")) {
      System.out.print("Enter either 'train' or 'test': ");

      action = sc.next().toLowerCase();

      System.out.println();
    }

    System.out.println("Initializing " + action + "ing...");


    //load network
    numRecog.loadNetwork(wPath, bPath);

    System.out.println();


    //choose data file
    System.out.println("Which data set would you like to " + action + " on?");
    System.out.println();

    String[] files = FileOps.getFilesInDir(dPath);

    for(int f = 1; f < files.length; f++) {
      System.out.println((f) + ") " + files[f]);
    }

    System.out.println();
    System.out.print("Enter your number choice: ");

    int choice = sc.nextInt();
    //ensure correct input
    while(choice < 0 || choice >= files.length) {
      System.out.print("Enter a valid choice: ");

      choice = sc.nextInt();

      System.out.println();
    }

    String path = dPath + files[choice];


    //get # examples
    int totalElements = FileOps.getNumLines(path);


    //stochastic step size
    System.out.println();
    System.out.print("Enter the stochastic step size -\n(100 < step <= " + totalElements + "): ");

    int stochasticStepSize = sc.nextInt();
    //ensure correct input
    while(stochasticStepSize < 100 || stochasticStepSize > totalElements || (double)(totalElements/stochasticStepSize) != ((double)totalElements/stochasticStepSize)) {
      System.out.print("Enter an integer within the range (100 < step <= " + totalElements + ")\nand that is a factor of " + totalElements + ": ");

      stochasticStepSize = sc.nextInt();

      System.out.println();
    }
    System.out.println();


    //training branch
    if(action.equals("train")) {
      System.out.print("Enter the number of repititions you would like to perform: ");

      int repititions = sc.nextInt();
      //ensure correct input
      while(!(repititions >= 0)) {
        System.out.print("Must be a natural number: ");

        repititions = sc.nextInt();

        System.out.println();
      }
      System.out.println();

      //validate training time
      System.out.println("This will take over " + ((int) (0.020*repititions*totalElements)/3600) + " hours, " + ((int) ((0.020*repititions*totalElements)%3600)/60) + " minutes, and " + ((0.020*repititions*totalElements)%60) + " seconds.");
      System.out.print("Is that ok?\n(y/n): ");

      if(!sc.next().equals("y")) {
        return;
      }
      System.out.println("\nStarting training...");
      System.out.println();


      //train
      final long start = System.currentTimeMillis();
      numRecog.train(path, totalElements, stochasticStepSize, repititions);
      final long end = System.currentTimeMillis();

      System.out.println();
      System.out.println("Total training time = " + ((end-start)/3600000) + " hours, " + (((end-start)%3600000)/60000) + " minutes, and " + ((double) ((end-start)%60000)/1000) + " seconds.");
      System.out.println("Time per example = " + ((double) (end-start)/(totalElements*repititions)) + " milliseconds.");

      //save network
      System.out.println();
      numRecog.saveNetwork(wPath, bPath);
      System.out.println();
    }

    //testing branch
    else if(action.equals("test")) {
      //validate test time
      System.out.println("This will take about " + ((int) (0.001*totalElements)/3600) + " hours, " + ((int) ((0.001*totalElements)%3600)/60) + " minutes, and " + ((0.001*totalElements)%60) + " seconds.");
      System.out.print("Is that ok?\n(y/n): ");

      if(!sc.next().equals("y")) {
        return;
      }
      System.out.println();

      //print?
      System.out.print("Do you want to print each test?\n(y/n): ");
      boolean print = sc.next().equals("y");

      System.out.println("\nStarting testing...");


      //test
      final long start = System.currentTimeMillis();
      numRecog.test(path, totalElements, stochasticStepSize, print);
      final long end = System.currentTimeMillis();

      System.out.println();
      System.out.println("Total training time = " + ((end-start)/3600000) + " hours, " + (((end-start)%3600000)/60000) + " minutes, and " + ((double) ((end-start)%60000)/1000) + " seconds.");
      System.out.println("Time per example = " + ((double) (end-start)/totalElements) + " milliseconds.");
      System.out.println();
    }
  }
}
