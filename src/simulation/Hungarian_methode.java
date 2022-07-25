package simulation;

import java.util.Arrays;

public class Hungarian_methode {

    double[][] costMatrix; // initial matrix (cost matrix)
    
    // markers in the matrix
    int[] squareInRow, squareInCol,Assignment;
    double Min, Max;
    int dummy, row, col;
    String methodType;

    //String methodType->(maximiztion or Minmization)
    public Hungarian_methode(double[][] costMatrix, int row, int col, String methodType) {
        this.methodType = methodType;
        if (costMatrix[0].length != col || costMatrix.length != row) {
            try {
                throw new IllegalAccessException("Irregular cost matrix");
            } catch (IllegalAccessException ex) {
                System.err.println(ex);
                System.exit(1);
            }
        }
        this.row = row;
        this.col = col;
        this.dummy = Math.max(costMatrix.length, costMatrix[0].length);
        this.costMatrix = new double[this.dummy][this.dummy];
        for (int i = 0; i <row; i++) {
            for (int j = 0; j < col; j++) {
                this.costMatrix[i][j] = costMatrix[i][j];
            }      
        }
        squareInRow = new int[this.dummy];
        squareInCol = new int[this.dummy];
        Assignment=new int[this.dummy];
        Arrays.fill(Assignment, -1);
         Arrays.fill(squareInCol, -1);
         Arrays.fill(squareInRow, -1);
        Min = Double.POSITIVE_INFINITY;
        Max = 0;
                
    }
    

    public int[] excute() {
        if (methodType == "Maximization") {
            MaximzationStep();
        }

        step1();
        step2();
        if (!step3()) {
            step4();
        }
        
        if(AssignCompleted()){
            return Assignment;
        }
       
        while (coveredLines() <costMatrix.length) {
            step5();
            reset();
            step2();
            if (!step3()) {
                step4();
                step2();
          if(AssignCompleted()){
            return Assignment;
        }
       
            }
             
        }
        
        Assing_Solution();
        return Assignment;
       
    }

    /**
     * Step 1: Reduce the matrix so that in each row and column : 1. subtract
     * each row minimum from each element of the row 2. subtract each column
     * minima from each element of the column
     */
    private void step1() {
        // rows
        for (int i = 0; i < costMatrix.length; i++) {
            // find the min value of the current row
            double currentRowMin = Double.POSITIVE_INFINITY;
            for (int j = 0; j < costMatrix[i].length; j++) {
                if (costMatrix[i][j] < currentRowMin) {
                    currentRowMin = costMatrix[i][j];
                }
            }
            // subtract min value from each element of the current row
            for (int k = 0; k < costMatrix[i].length; k++) {
                costMatrix[i][k] -= currentRowMin;
            }
        }

        // cols
        for (int i = 0; i < costMatrix[0].length; i++) {
            // find the min value of the current column
            double currentColMin = Double.POSITIVE_INFINITY;
            for (int j = 0; j < costMatrix.length; j++) {
                if (costMatrix[j][i] < currentColMin) {
                    currentColMin = costMatrix[j][i];
                }
            }
            // subtract min value from each element of the current column
            for (int k = 0; k < costMatrix.length; k++) {
                costMatrix[k][i] -= currentColMin;
            }
        }
    }

    /**
     * Step 2: mark each 0 with a "square", if there are no other zeroes found
     * in the same row
     *
     */
    private void step2() {
        int c = 0, index = -1;
        for (int i = 0; i < dummy; i++) {
            c = 0;
            index = -1;
            for (int j = 0; j < dummy; j++) {
                // mark if current value == 0 & there are no other marked zeroes in the same row or column
                if (costMatrix[i][j] == 0 && squareInCol[j] == -1) {
                    c++;
                    if (c > 1) {
                        break;
                    }
                    index = j;
                }

            }
            if (c == 1 && index != -1) {
                squareInCol[index] = i;
            }
        }
    }

    /**
     * Step 3: return true if all columns are covered false otherwise"
     */
    private boolean step3() {
        for (int i = 0; i < squareInCol.length; i++) {
            if (squareInCol[i] == -1) {
                return false;
            }
        }
        return true;
    }

    //scanning columns ;column that have only one zero it covers its zero row
    private void step4() {
        int c = 0, index = -1;
        for (int i = 0; i < costMatrix.length; i++) {
            c = 0;
            index = -1;
            for (int j = 0; j < costMatrix.length; j++) {
                if ((costMatrix[j][i] == 0) && squareInCol[i] == -1 && squareInRow[j] == -1) {
                      
        
                    c++;
                    if (c > 1) {
                        break;
                    } else {
                        index = j;
                    }
                }
            }
            if (c == 1 && index != -1) {
                squareInRow[index] = i;
            }
        }
    }
//count coverd rows and columns and return it

    private int coveredLines() {
        int lines_covered = 0;
        for (int i = 0; i < squareInRow.length; i++) {
            if (squareInRow[i] != -1) {
                lines_covered++;
            }
            if (squareInCol[i] != -1) {
                lines_covered++;
            }

        }
        return lines_covered;
    }
//this function find minimum uncovered number

    private void MinimumUncoveredNumber() {
        for (int i = 0; i < costMatrix.length; i++) {
            for (int j = 0; j < costMatrix[0].length; j++) {
                if (squareInRow[i] == -1 && squareInCol[j] == -1) {
                    if (Min > costMatrix[i][j]) {
                        Min = costMatrix[i][j];
                    }
                }
            }
        }
    }

    /*
*This function is executed when the number of Coveredlines is less than the number of rows and columns.
* step5
*subtract Minimum uncovered number from the other uncovered numbers
*add Minimum uncovered number to the the intersected numbers
     */
    private void step5() {
        MinimumUncoveredNumber();
        for (int i = 0; i < costMatrix.length; i++) {
            for (int j = 0; j < costMatrix[0].length; j++) {
                if (squareInRow[i] == -1 && squareInCol[j] == -1) {
                    costMatrix[i][j] -= Min;
                } else if (squareInRow[i] != -1 && squareInCol[j] != -1) {
                    costMatrix[i][j] += Min;
                }
            }
        }
    }
//we need this when we try to solve a Maximzation problem
    private void MaxValue() {
        for (int i = 0; i < costMatrix.length; i++) {
            for (int j = 0; j < costMatrix.length; j++) {
                if (costMatrix[i][j] > Max) {
                    Max = costMatrix[i][j];
                }
            }
        }
    }

    /*this Maximzation step used to solve maximximiz problem 
*this turn maximization problems to minimization to be eazy to solve
     */
    private void MaximzationStep() {
        MaxValue();
        for (int i = 0; i < costMatrix.length; i++) {
            for (int j = 0; j < costMatrix.length; j++) {
                costMatrix[i][j] = Max - costMatrix[i][j];
            }
        }
    }

    private void reset() {
        Arrays.fill(squareInRow, -1);
        Arrays.fill(squareInCol, -1);
        Min = Double.POSITIVE_INFINITY;
    }

    public void print() {
        for (int i = 0; i < this.dummy; i++) {
            for (int j = 0; j < this.dummy; j++) {
                System.out.print(this.costMatrix[i][j] + " ");
            }
            System.out.println();
        }

    }
   
    public void print1() {
        step1();
        step2();
        /*step3();
        step4();
        for (int i = 0; i < squareInCol.length; i++) {
            System.out.print(squareInCol[i] + " ");
        }
        System.out.println();
        for (int i = 0; i < squareInRow.length; i++) {
            System.out.print(squareInRow[i] + " ");
        }
        System.out.println();
        System.out.println();
        print();
        System.out.println();
        System.out.println(coveredLines());
        MinimumUncoveredNumber();
        step5();
        print();
        System.out.println();
        reset();
        step2();
        step4();*/
         for (int i = 0; i < squareInCol.length; i++) {
            System.out.print(squareInCol[i] + " ");
        }
        System.out.println();
        for (int i = 0; i < squareInRow.length; i++) {
            System.out.print(squareInRow[i] + " ");
        }
        /*
        step2();
        step4();
        System.out.println(coveredLines());
        */
        System.out.println();
        System.out.println();
        print();
    }

    public void print2() {
        for (int i = 0; i < squareInCol.length; i++) {
            if (squareInCol[i] != -1) {
                System.out.println("col" + i + "-> Row" + squareInCol[i]);
            }
        }
        for (int i = 0; i < squareInRow.length; i++) {
            if (squareInRow[i] != -1) {
                System.out.println("col" + squareInRow[i] + "-> Row" + i);
            }
        }
    }
     public void print3(){
          for (int i = 0; i < Assignment.length; i++) {
           
                System.out.println("col" + i + "-> Row" + Assignment[i]);
        }
     }
     //Stored zeros, which can be considered a final solution to the problem
     private void DirectSoultion(){
        for(int i=0;i<costMatrix.length;i++){
            for(int j=0;j<costMatrix[0].length;j++){
                if(costMatrix[i][j]==0){
                    if(squareInCol[j]==-1&&squareInRow[i]==-1){
                        squareInCol[j]=i;
                        squareInRow[i]=j;
                        Assignment[j]=i;
                        
                    }
                }
            }}}
     //set final solution in Assignment array
  private void Assing_Solution(){
      for(int i=0;i<costMatrix.length;i++){
          if(squareInCol[i]!=-1){
          Assignment[i]=squareInCol[i];
          }
          if(squareInRow[i]!=-1){
          Assignment[squareInRow[i]]=i;
          }
      }
   }
  //Check if there is at least one zero in each column or row suitable for this solution
  private boolean AssignCompleted() {
        DirectSoultion();
        for (int i = 0; i < Assignment.length; i++) {
            if (Assignment[i] == -1) {
                return false;
            }
        }
        return true;
    }}
