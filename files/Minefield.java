/**
 * Jared Harding
 * CS 224
 * Project 3 - Minesweeper
 */

package files;

import java.lang.Math;

public class Minefield {

    private int[][] numValues;
    private int[][] cellState;
    private int unexposedCount; // this represents the number of unexposed cells that do not have a bomb
    private int unmarkedCount;

    // these final variables represent the different states of the cells
    public final int UNEXPOSED = 0;
    public final int EXPOSED = 1;
    public final int MARKED = 2;
    private final int PENDING = 3;

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
        unmarkedCount = numBombs;
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
     * If it is already exposed, it does nothing
     * @param column
     * @param row
     * @return
     */
    public int toggleMarked(int column, int row) {
        // if it's already marked, change it to unmarked
        if (cellState[column][row] == MARKED) {
            cellState[column][row] = UNEXPOSED;
            unmarkedCount++;
        }
        // if it isn't marked yet, mark it
        else if (cellState[column][row] == UNEXPOSED) {
            cellState[column][row] = MARKED;
            unmarkedCount--;
        }
        return cellState[column][row];
    }

    /**
     * This method will change the state of a cell to exposed
     * @param column
     * @param row
     * @return
     */
    public int expose(int column, int row) {
        // this function assumes you are calling expose on an unexposed cell!
        // this will be tested for in the controller
        assert(cellState[column][row] == UNEXPOSED);

        // if there are no bombs around it, it will call expose on it's neighbors
        if (numValues[column][row] == 0) {
            cellState[column][row] = PENDING;
            // iterate through the neighbors
            for (int i = column - 1; i <= column + 1; i++) {
                for (int j = row - 1; j <= row + 1; j++) {
                    // if i and j are valid indices and it's not a cell already on the stack
                    if ((i >= 0) && (i < numValues.length) && (j >= 0) && (j < numValues[0].length)
                            && (cellState[i][j] == UNEXPOSED)) {
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

    /**
     * This method exposes all of the unexposed neighbors of a cell that's already exposed
     * This skips over cells that are marked
     *
     * The purpose of this method is to make exposing cells you know are safe a lot faster
     * @return returns whether you exposed a mine with this method
     */
    public boolean exposeNeighbors(int column, int row) {
        // the cell must already be exposed for this method
        assert(cellState[column][row] == EXPOSED);

        boolean mineExposed = false;
        for (int i = column - 1;i <= column + 1;i++) {
            for (int j = row - 1;j <= row + 1;j++) {
                // if i and j are valid indices and it is an unexposed cell
                if ((i >= 0) && (i < numValues.length) && (j >= 0) && (j < numValues[0].length)
                        && (cellState[i][j] == UNEXPOSED)) {
                    expose(i,j);
                    // if the cell you just exposed was a mine
                    if (numValues[i][j] == -1) {
                        // a mine was exposed
                        mineExposed = true;
                    }
                }
            }
        }
        return mineExposed;
    }

    /**
     * This method returns the state of the specified cell
     * @param column
     * @param row
     * @return
     */
    public int getCellState(int column, int row) {
        return cellState[column][row];
    }

    /**
     * This method returns the number in the specified cell
     * @param column
     * @param row
     * @return
     */
    public int getValue(int column, int row) {
        return numValues[column][row];
    }

    public int numUnexposed() {
        return unexposedCount;
    }

    public int numMarked() {
        return unmarkedCount;
    }
}
