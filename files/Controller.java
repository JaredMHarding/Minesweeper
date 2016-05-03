/**
 * Jared Harding
 * CS 224
 * Project 3 - Minesweeper
 */

package files;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

public class Controller {

    private Minefield mainField;
    @FXML
    private GridPane windowGP;
    private GridPane minefieldGP;
    @FXML
    private ChoiceBox difficultyCB;
    @FXML
    private Label nonminesLabel;
    @FXML
    private Label cellsUnmarked;
    @FXML
    private Label winloseLabel;
    @FXML
    private Label instructionsLabel;
    @FXML
    private Button startButton;
    private String defaultBtStyle;
    private Button[][] btArray;

    @FXML
    private void initialize() {
        defaultBtStyle = startButton.getStyle();
        minefieldGP = new GridPane();
        minefieldGP.setAlignment(Pos.CENTER);
        windowGP.add(minefieldGP,1,1);
//        windowGP.setGridLinesVisible(true);
//        minefieldGP.setGridLinesVisible(true);
        difficultyCB.setItems(FXCollections.observableArrayList("Easy","Medium","Hard"));
        difficultyCB.setValue("Easy");
        nonminesLabel.setText("");
        cellsUnmarked.setText("");
        winloseLabel.setText("");
        instructionsLabel.setText(
                "HOW TO PLAY:\n" +
                "- Find all of the mines to win\n" +
                "either by uncovering all of the safe cells\n" +
                "or marking all of the mines correctly\n" +
                "and then exposing a cell\n" +
                "\n" +
                "CONTROLS:\n" +
                "- Primary click to expose a cell\n" +
                "- Secondary click to mark a cell as a mine\n" +
                "- You can double click an exposed cell\n" +
                "to expose all of it's unmarked neighbors\n" +
                "***Double click at your own risk\n");
        startButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                startGame();
            }
        });
//        // makes all of the buttons
//        int mfHeight = minefieldGP.getRowConstraints().size();
//        int mfWidth = minefieldGP.getColumnConstraints().size();
//        for (int i = 0;i < mfWidth;i++) {
//            for (int j = 0; j < mfHeight; j++) {
//                minefieldGP.add(new Button(""), j, i);
//            }
//        }
    }

    private void startGame() {
        winloseLabel.setText("");
        int width, height, numMines;
        String difficulty = (String) difficultyCB.getValue();
        switch (difficulty) {
            default:
                width = 15;
                height = 15;
                numMines = 40;
                break;
            case "Easy":
                width = 5;
                height = 5;
                numMines = 4;
                break;
            case "Medium":
                width = 10;
                height = 10;
                numMines = 15;
                break;
            case "Hard":
                width = 15;
                height = 15;
                numMines = 40;
                break;
        }
        // make the new minefield and button array based on the size
        mainField = new Minefield(width,height,numMines);
        btArray = new Button[width][height];
        // update the labels
        Integer nonmines = mainField.numUnexposed();
        nonminesLabel.setText(nonmines.toString());
        Integer numMarked = mainField.numMarked();
        cellsUnmarked.setText(numMarked.toString());
        minefieldGP.getChildren().clear();
        for (int i = 0;i < width;i++) {
            for (int j = 0; j < height; j++) {
                Button button = new Button("");
                button.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        // get the position of the button in the gridpane
                        Button sourceBt = (Button) event.getSource();
                        int btColumn = minefieldGP.getColumnIndex(sourceBt);
                        int btRow = minefieldGP.getRowIndex(sourceBt);
                        MouseButton clickType = event.getButton();
                        int clicks = event.getClickCount();
                        if ((clicks == 1) && (clickType == MouseButton.PRIMARY)) {
                            System.out.println("Primary was clicked at " + btColumn + "," + btRow);
                            // if it's an unexposed button
                            int cellState = mainField.getCellState(btColumn,btRow);
                            if (cellState == mainField.UNEXPOSED) {
                                // call expose on the minefield
                                int exposeResult = mainField.expose(btColumn,btRow);
                                if (exposeResult == -1) {
                                    endGame(false);
                                }
                                if ((nonminesLabel.getText().equals("0")) || (allCellsCorrect())) {
                                    endGame(true);
                                }
                            }
                        }
                        else if ((clicks == 2) && (clickType == MouseButton.PRIMARY)) {
                            System.out.println("Primary was double clicked at " + btColumn + "," + btRow);
                            int cellState = mainField.getCellState(btColumn,btRow);
                            // if the cell is already exposed
                            if (cellState == mainField.EXPOSED) {
                                boolean mineExposed = mainField.exposeNeighbors(btColumn,btRow);
                                if (mineExposed) {
                                    endGame(false);
                                }
                                if ((nonminesLabel.getText().equals("0")) || (allCellsCorrect())) {
                                    endGame(true);
                                }
                            }
                        }
                        else if (clickType == MouseButton.SECONDARY) {
                            System.out.println("Secondary was clicked at " + btColumn + "," + btRow);
                            int toggleResult = mainField.toggleMarked(btColumn,btRow);
                            if (toggleResult == mainField.MARKED) {
                                sourceBt.setText("X");
                                sourceBt.setStyle("-fx-background-color: #FFFF00;");
                            }
                            if (toggleResult == mainField.UNEXPOSED) {
                                sourceBt.setText("");
                                sourceBt.setStyle(defaultBtStyle);
                            }
                        }
                        refresh(width,height);
                        if (nonminesLabel.getText().equals("0")) {
                            endGame(true);
                        }
                    }
                });
                minefieldGP.add(button, i, j);
                button.setMinSize(30,30);
                btArray[i][j] = button;
            }
        }
    }

    /**
     * This helper function refreshes all of the exposed buttons to reflect the states of the main minefield
     */
    private void refresh(int width, int height) {
        Integer numUnexposed = mainField.numUnexposed();
        nonminesLabel.setText(numUnexposed.toString());
        Integer numMarked = mainField.numMarked();
        if (numMarked < 0) {
            cellsUnmarked.setText("Too Many Marked!");
        }
        else {
            cellsUnmarked.setText(numMarked.toString());
        }
        // iterate through all the buttons and update the text if it's an exposed cell
        for (int i = 0;i < width;i++) {
            for (int j = 0;j < height;j++) {
                int cellState = mainField.getCellState(i,j);
                if (cellState == mainField.EXPOSED) {
                    Integer numValue = mainField.getValue(i,j);
                    if (numValue == -1) {
                        btArray[i][j].setText("B");
                        btArray[i][j].setStyle("-fx-background-color: #FF0000;");
                    }
                    else if (numValue == 0) {
                        btArray[i][j].setStyle("-fx-background-color: #CCCCCC;");
                    }
                    else {
                        btArray[i][j].setText(numValue.toString());
                        btArray[i][j].setStyle("-fx-background-color: #CCCCCC;");
                    }
                }
            }
        }
    }

    /**
     * This method checks all of the cells with mines and sees if all of them are marked
     * It also checks to see if all of the nonmine cells are unmarked
     *
     * This adds another win condition (marking all of the correct mine cells)
     * instead of just exposing all of the unexposed nonmine cells
     *
     * NOTE: This method will not be called when you just mark a cell by right-clicking, this is to prevent cheating
     * so you must still expose at least one cell to win, it just doesn't have to be all the cells
     *
     * @return
     */
    private boolean allCellsCorrect() {
        for (int i = 0;i < btArray.length;i++) {
            for (int j = 0;j < btArray[0].length;j++) {
                int cellState = mainField.getCellState(i,j);
                // if the cell has a mine
                if (mainField.getValue(i,j) == -1) {
                    // tests to see if it isn't marked
                    if (cellState != mainField.MARKED) {
                        // returns false if it isn't, because it should be marked if the game is complete
                        return false;
                    }
                }
                // if it isn't a mine
                else {
                    // tests to see if the cell is marked
                    if (cellState == mainField.MARKED) {
                        // returns false, because this nonmine cell should not be marked if the game is complete
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * This method will end the game if an end condition was met
     * @param gameWon
     */
    private void endGame(boolean gameWon) {
        if (gameWon) {
            winloseLabel.setText("YOU WIN");
            for (int i = 0;i < btArray.length;i++) {
                for (int j = 0; j < btArray[0].length; j++) {
                    // if it's a mine
                    if (mainField.getValue(i,j) == -1) {
                        // turn the button green
                        btArray[i][j].setStyle("-fx-background-color: #00FF00;");
                    }
                }
            }
        }
        else {
            winloseLabel.setText("YOU LOSE");
        }
        // disables all of the buttons
        for (Button[] subArray : btArray) {
            for (Button button : subArray) {
                button.setDisable(true);
            }
        }
    }
}
