package frc.robot.AI;

import frc.robot.utils.Matrix;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class NeuralNetwork {
    private Matrix weights_input_to_hidden, weights_hidden_to_output, bias_hidden, bias_output;

    private double learningRate;

    private final int inputNodes;
    private final int hiddenNodes;
    private final int outputNodes;

    /**
     * Creates a new neural network.
     * @param inputNodes The number of input nodes.
     * @param hiddenNodes The number of hidden nodes.
     * @param outputNodes The number of output nodes.
     * @param learningRate The learning rate of the neural network. Default: 0.01
     * */
    public NeuralNetwork(int inputNodes, int hiddenNodes, int outputNodes, double learningRate) {
        weights_input_to_hidden = new Matrix(hiddenNodes, inputNodes);
        weights_hidden_to_output = new Matrix(outputNodes, hiddenNodes);

        bias_hidden = new Matrix(hiddenNodes, 1);
        bias_output = new Matrix(outputNodes, 1);

        this.inputNodes = inputNodes;
        this.hiddenNodes = hiddenNodes;
        this.outputNodes = outputNodes;
        this.learningRate = learningRate;
    }

    /**
     * Creates a new neural network.
     * @param inputNodes The number of input nodes.
     * @param hiddenNodes The number of hidden nodes.
     * @param outputNodes The number of output nodes.
     * */
    public NeuralNetwork(int inputNodes, int hiddenNodes, int outputNodes) {
        weights_input_to_hidden = new Matrix(hiddenNodes, inputNodes);
        weights_hidden_to_output = new Matrix(outputNodes, hiddenNodes);

        bias_hidden = new Matrix(hiddenNodes, 1);
        bias_output = new Matrix(outputNodes, 1);

        this.inputNodes = inputNodes;
        this.hiddenNodes = hiddenNodes;
        this.outputNodes = outputNodes;
        this.learningRate = 0.01;
    }

    /**
     * Loads the weights and biases from a specified file
     * @return Whether the files exist.
     * */
    public boolean loadNumbersFromFiles() throws FileNotFoundException {
        String user = System.getProperty("user.name");
        //TODO: Adjust this path
        String trainingPath = "/trainingdata";
        //Check if folder "training-directory" exists
        //If not, create it
        if (!new File(trainingPath).exists()) {
            System.out.println("Folder doesn't exist, therefore not loading weights from file.");
            return false;
        }

        //Create file "training-directory/weights.txt"
        File weightsFile = new File(trainingPath + "/input_to_hidden_weights.txt");
        //Create file "training-directory/bias.txt"
        File biasFile = new File(trainingPath + "/hidden_bias.txt");
        //Create file "training-directory/weights.txt"
        File weights2File = new File(trainingPath + "/hidden_to_output_weights.txt");
        //Create file "training-directory/bias.txt"
        File bias2File = new File(trainingPath + "/output_bias.txt");

        Scanner ihWeightScanner = new Scanner(weightsFile);
        Scanner hBiasScanner = new Scanner(biasFile);
        Scanner hoWeightScanner = new Scanner(weights2File);
        Scanner oBiasScanner = new Scanner(bias2File);

        String ihWeightData = "", hBiasData = "", hoWeightData = "", oBiasData = "";

        while (ihWeightScanner.hasNextLine()) {
            String data = ihWeightScanner.nextLine();
            ihWeightData += data;
        }

        while (hBiasScanner.hasNextLine()) {
            String data = hBiasScanner.nextLine();
            hBiasData += data;
        }

        while (hoWeightScanner.hasNextLine()) {
            String data = hoWeightScanner.nextLine();
            hoWeightData += data;
        }

        while (oBiasScanner.hasNextLine()) {
            String data = oBiasScanner.nextLine();
            oBiasData += data;
        }

        ihWeightScanner.close();
        hBiasScanner.close();
        hoWeightScanner.close();
        oBiasScanner.close();

        ihWeightData = ihWeightData.replaceAll("[\\[\\](){}]","");
        hBiasData = hBiasData.replaceAll("[\\[\\](){}]","");
        hoWeightData = hoWeightData.replaceAll("[\\[\\](){}]","");
        oBiasData = oBiasData.replaceAll("[\\[\\](){}]","");

        if (ihWeightData.contains("[") || hBiasData.contains("[") || hoWeightData.contains("[") || oBiasData.contains("[")) {
            System.out.println("Error: File contains invalid characters.");
            return true;
        }

        if (ihWeightData.contains("]") || hBiasData.contains("]") || hoWeightData.contains("]") || oBiasData.contains("]")) {
            System.out.println("Error: File contains invalid characters.");
            return true;
        }

        //Convert strings to List<Double>
        List<Double> ihWeightList = new ArrayList<>(), hBiasList = new ArrayList<>(), hoWeightList = new ArrayList<>(), oBiasList = new ArrayList<>();

        for (String data : ihWeightData.split(",")) {
            ihWeightList.add(Double.parseDouble(data));
        }

        for (String data : hBiasData.split(",")) {
            hBiasList.add(Double.parseDouble(data));
        }

        for (String data : hoWeightData.split(",")) {
            hoWeightList.add(Double.parseDouble(data));
        }

        for (String data : oBiasData.split(",")) {
            oBiasList.add(Double.parseDouble(data));
        }

        for (int i = 0; i < hiddenNodes; i++) {
            for (int j = 0; j < inputNodes; j++) {
                weights_input_to_hidden.set(i, j, ihWeightList.get(i * inputNodes + j));
            }
        }

        for (int i = 0; i < outputNodes; i++) {
            for (int j = 0; j < hiddenNodes; j++) {
                weights_hidden_to_output.set(i, j, hoWeightList.get(i * hiddenNodes + j));
            }
        }

        for (int i = 0; i < hiddenNodes; i++) {
            bias_hidden.set(i, 0, hBiasList.get(i));
        }

        for (int i = 0; i < outputNodes; i++) {
            bias_output.set(i, 0, oBiasList.get(i));
        }

        return true;
    }

    /**
     * Saves the weights and biases to text files.
     * */
    public void storeNumbersInFile() throws IOException {
        String user = System.getProperty("user.name");
        //FOR mac, in testing - todo: change it to user.home and all that stuff
        String trainingPath = "/trainingdata";
        //Check if folder "training-directory" exists
        //If not, create it
        if (!new File(trainingPath).exists()) {
            new File(trainingPath).mkdir();
        }

        //Create file "training-directory/weights.txt"
        File weightsFile = new File(trainingPath + "/input_to_hidden_weights.txt");
        //Create file "training-directory/bias.txt"
        File biasFile = new File(trainingPath + "/hidden_bias.txt");
        //Create file "training-directory/weights.txt"
        File weights2File = new File(trainingPath + "/hidden_to_output_weights.txt");
        //Create file "training-directory/bias.txt"
        File bias2File = new File(trainingPath + "/output_bias.txt");

        if (weightsFile.createNewFile()) {
            System.out.println("File created: " + weightsFile.getName());
        } else {
            System.out.println("File already exists.");
        }

        if (biasFile.createNewFile()) {
            System.out.println("File created: " + weightsFile.getName());
        } else {
            System.out.println("File already exists.");
        }

        if (weights2File.createNewFile()) {
            System.out.println("File created: " + weightsFile.getName());
        } else {
            System.out.println("File already exists.");
        }

        if (bias2File.createNewFile()) {
            System.out.println("File created: " + weightsFile.getName());
        } else {
            System.out.println("File already exists.");
        }

        //Write weights to file
        List<Double> input_to_hidden = weights_input_to_hidden.toArray();
        List<Double> hidden_bias = bias_hidden.toArray();
        List<Double> hidden_to_output = weights_hidden_to_output.toArray();
        List<Double> output_bias = bias_output.toArray();

        FileWriter ihWeightWriter = new FileWriter(weightsFile, false);
        ihWeightWriter.write(input_to_hidden.toString());

        FileWriter hbWeightWriter = new FileWriter(biasFile, false);
        hbWeightWriter.write(hidden_bias.toString());

        FileWriter htoWeightWriter = new FileWriter(weights2File, false);
        htoWeightWriter.write(hidden_to_output.toString());

        FileWriter obWeightWriter = new FileWriter(bias2File, false);
        obWeightWriter.write(output_bias.toString());

        System.out.println("Finished saving weights to file.");

        ihWeightWriter.close();
        hbWeightWriter.close();
        htoWeightWriter.close();
        obWeightWriter.close();
    }

    /**
     * Run an input through the neural network and return the output.
     * @param x The input to the neural network.
     * @return The output of the neural network.
     * */
    public List<Double> predict(double[] x) {
        Matrix input = Matrix.fromArray(x);
        Matrix hidden = Matrix.multiply(weights_input_to_hidden, input);
        hidden.add(bias_hidden);
        hidden.sigmoid();

        Matrix output = Matrix.multiply(weights_hidden_to_output,hidden);
        output.add(bias_output);
        output.sigmoid();

        return output.toArray();
    }

    /**
     * Train the neural network using backpropagation.
     * @param x Training data
     * @param y Training label
     * @see <a href="https://en.wikipedia.org/wiki/Backpropagation">Backpropagation</a>
     * @see <a href="https://towardsdatascience.com/understanding-and-implementing-neural-networks-in-java-from-scratch-61421bb6352c">Help Used</a>
     * */
    public void train(double[] x, double[] y) {
        //Calculate the output of the neural network
        Matrix input = Matrix.fromArray(x);
        Matrix hidden = Matrix.multiply(weights_input_to_hidden, input);
        hidden.add(bias_hidden);
        hidden.sigmoid();

        Matrix output = Matrix.multiply(weights_hidden_to_output,hidden);
        output.add(bias_output);
        output.sigmoid();

        //Get the correct output
        Matrix target = Matrix.fromArray(y);

        //Calculate the error
        Matrix error = Matrix.subtract(target, output);
        //Get the gradient of the output
        Matrix gradient = output.dsigmoid();
        //Multiply the error by the gradient
        gradient.multiply(error);
        //Multiply the gradient by the learning rate
        gradient.multiply(learningRate);

        //Calculate how much the hidden layer weights need to change
        Matrix hidden_T = Matrix.transpose(hidden);
        Matrix weights_hidden_output_delta =  Matrix.multiply(gradient, hidden_T);

        weights_hidden_to_output.add(weights_hidden_output_delta);
        bias_output.add(gradient);

        //Calculate how much the input layer weights need to change
        Matrix who_T = Matrix.transpose(weights_hidden_to_output);
        Matrix hidden_errors = Matrix.multiply(who_T, error);

        Matrix h_gradient = hidden.dsigmoid();
        h_gradient.multiply(hidden_errors);
        h_gradient.multiply(learningRate);

        Matrix i_T = Matrix.transpose(input);
        Matrix weights_input_hidden_delta = Matrix.multiply(h_gradient, i_T);

        weights_input_to_hidden.add(weights_input_hidden_delta);
        bias_hidden.add(h_gradient);
    }

    /**
     * Train the network using the given training data.
     * @param x Training data
     * @param y Training labels
     * @param epochs Number of epochs to train for
     * */
    public void fit(double[][] x, double[][] y, int epochs)
    {
        int currentTime = (int) System.currentTimeMillis();

        for(int i = 0; i < epochs; i++)
        {
            int sampleN = (int)(Math.random() * x.length);
            this.train(x[sampleN], y[sampleN]);

            if (i % 1000 == 0) {
                System.out.println("Current Epoch: " + i);
            }
        }

        int timeTaken= (int) (System.currentTimeMillis() - currentTime);

        //Convert timeTaken to minutes
        int minutes = timeTaken / 60000;

        System.out.println("Time taken: " + minutes + " minutes.");
    }
}
