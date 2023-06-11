import java.util.*;
import java.io.*;
import java.util.*;

// SingleLP will solve a linear program corresponding to a single oblivious algorithm.
// The instructions for using the program is identical to MaxLP; please look at that file
// for how to use this program.

public class SingleLP {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        final int numMidClasses = scanner.nextInt();

        // Put in boundary here
        final double smallerBoundary = 1.0 / 3.8625;

        // Change file name here
        String fileName = "singlelp.txt";
        BufferedWriter bwriter = new BufferedWriter(new FileWriter(fileName));

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