import java.util.*;
import java.io.*;
import java.util.Scanner;

// MaxLP will solve a linear program corresponding to a MAX of two oblivious algorithms.
// It will output a Mathematica code into a file. Simply copy and paste it into Mathematica and run it.
// The result will be the value Mathematica stores in the variable 'sol'.

// The text file might be buggy because of how long it is, but it should be fine if it opened in VSCode.

// To complie and run, enter these into a command prompt:
// javac MaxLP.java
// java MaxLP

public class MaxLP {
    public static void main(String[] args) throws IOException  {
        Scanner scanner = new Scanner(System.in);

        // numMidClasses is the number of divisions of the interval between the boundaries into bias classes.
        // This value must be input when the program is run.
        final int numMidClasses = scanner.nextInt();

        // Edit these values before running the program.
        // Accepts two different boundary values. If b is a boundary value, there will be a boundary
        // of [b, 1 - b], where this corresponds to the FJ bias (bias between 0 and 1, not -1 and 1).
        // smallerBoundary should be the smaller value, but changing the two is sometimes more optimal if
        // using biggerBoundary alone is much better than using smallerBoundary alone.
        final double smallerBoundary = 1.0 / 4.0;
        final double biggerBoundary = 1.0 / 3.5;

        // Change fileName to put the result in a different file.
        String fileName = "lp.txt";
        BufferedWriter bwriter = new BufferedWriter(new FileWriter(fileName));


        // The rest is code that does not need to be edited.
        int numClasses = numMidClasses + 2;
        double[] boundaries = new double[numMidClasses+3];
        for(int i = 0; i <= numMidClasses; i++)  {
            boundaries[i + 1] = smallerBoundary + i * ((1-2*smallerBoundary) / (double) numMidClasses);
        }

        boundaries[0] = 0;
        boundaries[numMidClasses + 2] = 1;

        String oneSumReq = "";
        String positiveReq = "";
        for(int i = 0; i < numClasses; i++)  {
            for(int j = 0; j < numClasses; j++)  {
                oneSumReq += "c" + i + "n" + j + "+";
                positiveReq += "c" + i + "c" + j + ">=0,";
                positiveReq += "c" + i + "n" + j + ">=0,";
                positiveReq += "n" + i + "c" + j + ">=0,";
                positiveReq += "n" + i + "n" + j + ">=0,";
            }
        }
        oneSumReq = oneSumReq + "0==1,";

        String boundaryReq = "";

        for(int i = 0; i < numClasses; i++)  {
            boundaryReq += boundaries[i] + "(";
            for(int j = 0; j < numClasses; j++)  {
                boundaryReq += "c" + i + "c" + j + "+c" + j + "c" + i + "+";
                boundaryReq += "c" + i + "n" + j + "+n" + j + "c" + i + "+";
            }

            boundaryReq = boundaryReq + "0)<=";

            for(int j = 0; j < numClasses; j++)  {
                boundaryReq += "c" + i + "c" + j + "+";
                boundaryReq += "c" + i + "n" + j + "+";
            }

            boundaryReq = boundaryReq + "0<=" + boundaries[i+1] + "(";

            for(int j = 0; j < numClasses; j++)  {
                boundaryReq += "c" + i + "c" + j + "+c" + j + "c" + i + "+";
                boundaryReq += "c" + i + "n" + j + "+n" + j + "c" + i + "+";
            }

            boundaryReq = boundaryReq + "0),";


            // Now do it with n

            boundaryReq += boundaries[i] + "(";
            for(int j = 0; j < numClasses; j++)  {
                boundaryReq += "n" + i + "c" + j + "+c" + j + "n" + i + "+";
                boundaryReq += "n" + i + "n" + j + "+n" + j + "n" + i + "+";
            }

            boundaryReq = boundaryReq + "0)<=";

            for(int j = 0; j < numClasses; j++)  {
                boundaryReq += "n" + i + "c" + j + "+";
                boundaryReq += "n" + i + "n" + j + "+";
            }

            boundaryReq = boundaryReq + "0<=" + boundaries[i+1] + "(";

            for(int j = 0; j < numClasses; j++)  {
                boundaryReq += "n" + i + "c" + j + "+c" + j + "n" + i + "+";
                boundaryReq += "n" + i + "n" + j + "+n" + j + "n" + i + "+";
            }

            boundaryReq = boundaryReq + "0),";
        }

        String solReq = "sol>=";
        double[] midPoints = new double[numClasses];

        for(int i = 0; i < numClasses; i++)  {
            midPoints[i] = (boundaries[i] + boundaries[i + 1]) / 2.0;
        }

        for(int i = 0; i < numClasses; i++)  {
            for(int j = 0; j < numClasses; j++)  {
                if(midPoints[i] <= smallerBoundary)  {
                    continue;
                } else if(midPoints[i] >= 1.0 - smallerBoundary)  {
                    solReq += "1*";
                } else  {
                    solReq += ((1.0 / (1.0 - 2.0 * smallerBoundary)) * (midPoints[i] - smallerBoundary)) + "*";
                }

                if(midPoints[j] <= smallerBoundary)  {
                    
                } else if(midPoints[j] >= 1.0 - smallerBoundary)  {
                    solReq += "0+";
                    continue;
                } else  {
                    solReq += (1.0 - ((1.0 / (1.0 - 2.0 * smallerBoundary)) * (midPoints[j] - smallerBoundary))) + "*";
                }

                solReq += "(c" + i + "c" + j + "+c" + i + "n" + j + "+n" + i + "c" + j + "+n" + i + "n" + j + ")+";
            }
        }

        solReq = solReq + "0,sol>=";


        // Now for big boundary
        for(int i = 0; i < numClasses; i++)  {
            for(int j = 0; j < numClasses; j++)  {
                if(midPoints[i] <= biggerBoundary)  {
                    continue;
                } else if(midPoints[i] >= 1.0 - biggerBoundary)  {
                    solReq += "1*";
                } else  {
                    solReq += ((1.0 / (1.0 - 2.0 * biggerBoundary)) * (midPoints[i] - biggerBoundary)) + "*";
                }

                if(midPoints[j] <= biggerBoundary)  {
                    
                } else if(midPoints[j] >= 1.0 - biggerBoundary)  {
                    solReq += "0+";
                    continue;
                } else  {
                    solReq += (1.0 - ((1.0 / (1.0 - 2.0 * biggerBoundary)) * (midPoints[j] - biggerBoundary))) + "*";
                }

                solReq += "(c" + i + "c" + j + "+c" + i + "n" + j + "+n" + i + "c" + j + "+n" + i + "n" + j + ")+";
            }
        }

        solReq = solReq + "0";

        String variables = "sol";
        for(int i = 0; i < numClasses; i++)  {
            for(int j = 0; j < numClasses; j++)  {
                variables += "," + "c" + i + "c" + j + "," + "c" + i + "n" + j + "," + "n" + i + "c" + j + "," + "n" + i + "n" + j;
            }
        }

        bwriter.write("LinearOptimization[sol,{" + oneSumReq + positiveReq + boundaryReq + solReq + "},{" + variables + "}]\r\n");
        bwriter.close();
    }
}