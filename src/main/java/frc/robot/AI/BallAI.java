package frc.robot.AI;

import java.io.File;

//Deprecated. Use NeuralNetwork or AIManager instead.
public class BallAI {
    private final int numInputs = 1024;
    private final int numHiddenLayers = 1;
    private final int numHiddenNodes = 15;
    //First is for if there is a ball in the image, second is for if there is no ball in the image
    private final int numOutputs = 2;

    private double[][] weightsBetweenInputAndLayer;
    private double[][] weightsBetweenLayerAndOutput;

    private double[] biasesForHiddenLayer;

    private double[] inputs;

    public static void main(String[] args) {
        BallAI ballAi = new BallAI();
    }

    @Deprecated
    public BallAI() {
        biasesForHiddenLayer = new double[numHiddenNodes];
        weightsBetweenInputAndLayer = new double[numInputs][numHiddenNodes];
        weightsBetweenLayerAndOutput = new double[numHiddenNodes][numOutputs];

        inputs = new double[numInputs];

        initializeBiasForHiddenLayers();
        initializeWeights();
    }


    public void doTestFrame() {
        createARandomInput();

        double[] out = calculateValueForOutputLayer();
        System.out.println("IsBall? Yes: " + out[0] + ", No: " + out[1]);
    }

    public void createARandomInput() {
        for (int i = 0; i < numOutputs; i++) {
            inputs[i] = Math.random();
        }
    }

    public void initializeBiasForHiddenLayers() {
        for (int i = 0; i < numHiddenNodes; i++) {
            biasesForHiddenLayer[i] = Math.random() * 2 - 1;
        }
    }

    public void initializeWeights() {
        for (int i = 0; i < weightsBetweenInputAndLayer.length; i++) {
            for (int j = 0; j < weightsBetweenInputAndLayer[i].length; j++) {
                weightsBetweenInputAndLayer[i][j] = Math.random() * 2 - 1;
            }
        }

        for (int i = 0; i < weightsBetweenLayerAndOutput.length; i++) {
            for (int j = 0; j < weightsBetweenLayerAndOutput[i].length; j++) {
                weightsBetweenLayerAndOutput[i][j] = Math.random() * 2 - 1;
            }
        }
    }

    public void readImage() {
        //Take a random image from either Training/Balls or Training/NotBalls
        boolean isBall = Math.random() < 0.5;
        //Get the computer's username
        String username = System.getProperty("user.name");

        //FOR MAC. Kind of made for my computer (jaiden's) but u can adjust it.
        String path = "/Users/" + username + "/IdeaProjects/image-testing/src/Training/" + (isBall ? "Balls" : "NotBalls") + "/";
        //Read the files inside the path.
        File[] files = new File(path).listFiles();
        //Get a random file from the list of files
        assert files != null;
        File file = files[(int) (Math.random() * files.length)];

        //Read the image
    }

    public double calculateValueForNeuron(int neuronIndex) {
        double val = 0;

        for (int i = 0; i < weightsBetweenInputAndLayer[neuronIndex].length; i++) {
            val += weightsBetweenInputAndLayer[neuronIndex][i] * inputs[i];
        }

        return sigmoid(val + biasesForHiddenLayer[neuronIndex]);
    }

    public double[] calculateValueForOutputLayer() {
        double[] outputs = new double[numOutputs];

        for (int i = 0; i < numOutputs; i++) {
            double val = 0;
            for (int j = 0; j < numHiddenNodes; j++) {
                val += weightsBetweenLayerAndOutput[j][i] * calculateValueForNeuron(j);
            }
        }

        return outputs;
    }

    public static double sigmoid(double x) {
        return 1 / (1 + Math.exp(-x));
    }
}
