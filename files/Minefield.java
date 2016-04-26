package files;

import java.lang.Math;

/**
 * Created by jharding on 4/25/16.
 */
public class Minefield {

    private int[][] numValues;
    private int[][] cellState;
    private int unexposedCount; // this represents the number of unexposed cells that do not have a bomb

    // these final variables represent the different states of the cells
    public final int UNEXPOSED = 0;
    public final int EXPOSED = 1;
    public final int MARKED = 2;

    /**
     * This is the constructor
     * @param width
     * @param height
     * @param numBombs
     */
    public Minefield(int width, int height, int numBombs) {
        numValues = new int[width][height];
        cellState = new int[width][height];
        unexposedCount = width*height - numBombs;
        // places the bombs in the cells
        while (numBombs > 0) {
            // computes a random row and column
            int rndColumn = (int) (Math.random() * width);
            int rndRow = (int) (Math.random() * height);
            // tests if it doesn't already have a bomb
            if (numValues[rndColumn][rndRow] == 0) {
                numValues[rndColumn][rndRow] = -1;
                numBombs--;
            }
        }
        // sets the cells with the correct numbers based on the bombs
        for (int i = 0;i < width;i++) {
            for (int j = 0; j < height; j++) {
                if (numValues[i][j] == -1) { // if it's a bomb, add 1 to each surrounding cell
                    // iterates through all the surrounding cells
                    for (int k = i-1;k <= i+1;k++) {
                        for (int l = j-1;l <= j+1;l++) {
                            // if k and l are valid indices and if the cell doesn't have a bomb...
                            if ((k >= 0) && (k < width) && (l >= 0) && (l < height) && (numValues[k][l] != -1)) {
                                numValues[k][l]++; // increment the number of the cell
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * This method toggles a cell to be marked if it's unexposed, or back to unexposed if it was marked
     * @param column
     * @param row
     * @return
     */
    public void toggleMarked(int column, int row) {
        // if it's already marked, change it to unmarked
        if (cellState[column][row] == MARKED) {
            cellState[column][row] = UNEXPOSED;
        }
        // if it isn't marked yet, mark it
        if (cellState[column][row] == UNEXPOSED) {
            cellState[column][row] = MARKED;
        }
    }

    /**
     * This method will change the state of a cell to exposed
     * Also, if the cell is a 0, it exposes all of it's neighbors
     * @param column
     * @param row
     * @return
     */
    public int expose(int column, int row) {
        // this function assumes you are calling expose on an unexposed cell!
        // this will be tested for in the controller!
        assert(cellState[column][row] == UNEXPOSED);

        // if there are no bombs around it, it will call expose on it's neighbors
        if (numValues[column][row] == 0) {
            // iterate through the neighbors
            for (int i = column - 1; i <= column + 1; i++) {
                for (int j = row - 1; j <= row + 1; j++) {
                    // if i and j are valid indices and it is not the same cell as the function caller's cell
                    if ((i >= 0) && (i < numValues.length) && (j >= 0) && (j < numValues[0].length)
                            && !((i == column) && (j == row))) {
                        // call expose on the neighboring cell
                        expose(i,j);
                    }
                }
            }
        }
        // expose the cell and decrement the unexposedCount if it wasn't a bomb
        cellState[column][row] = EXPOSED;
        if (numValues[column][row] != -1) {
            unexposedCount--;
        }
        return numValues[column][row];
    }

    public boolean isExposed(int column, int row) {
        if (cellState[column][row] == EXPOSED) {
            return true;
        }
        return false;
    }

    public int numUnexposed() {
        return unexposedCount;
    }
}
