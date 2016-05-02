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
    private Label winloseLabel;
    @FXML
    private Button startButton;
    private Button[][] btArray;

    @FXML
    private void initialize() {
        minefieldGP = new GridPane();
        minefieldGP.setAlignment(Pos.CENTER);
        windowGP.add(minefieldGP,1,1);
        windowGP.setGridLinesVisible(true);
        minefieldGP.setGridLinesVisible(true);
        difficultyCB.setItems(FXCollections.observableArrayList("Easy","Medium","Hard"));
        nonminesLabel.setText("");
        winloseLabel.setText("");
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
        int width, height, numBombs;
        String difficulty = (String) difficultyCB.getValue();
        if (difficulty.equals("Easy")) {
            width = 5;
            height = 5;
            numBombs = 5;
        }
        else if (difficulty.equals("Medium")) {
            width = 10;
            height = 10;
            numBombs = 20;
        }
        else {
            width = 15;
            height = 15;
            numBombs = 30;
        }
        // make the new minefield and button array based on the size
        mainField = new Minefield(width,height,numBombs);
        btArray = new Button[width][height];
        // update the label
        Integer nonmines = mainField.numUnexposed();
        nonminesLabel.setText(nonmines.toString());
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
                        if (event.getButton() == MouseButton.PRIMARY) {
                            System.out.println("Primary was clicked at " + btColumn + "," + btRow);
                            // if it's an unexposed button
                            int cellState = mainField.getCellState(btColumn,btRow);
                            if (cellState == mainField.UNEXPOSED) {
                                // call expose on the minefield
                                int exposeResult = mainField.expose(btColumn,btRow);
                                refresh();
                                if (exposeResult == -1) {
                                    endGame(false);
                                }
                                if (nonminesLabel.getText().equals("0")) {
                                    endGame(true);
                                }
                            }
                        }
                        if (event.getButton() == MouseButton.SECONDARY) {
                            System.out.println("Secondary was clicked at " + btColumn + "," + btRow);
                            int toggleResult = mainField.toggleMarked(btColumn,btRow);
                            if (toggleResult == mainField.MARKED) {
                                sourceBt.setText("X");
                            }
                            if (toggleResult == mainField.UNEXPOSED) {
                                sourceBt.setText("");
                            }
                        }
                    }
                });
                minefieldGP.add(button, i, j);
                btArray[i][j] = button;
            }
        }
    }

    /**
     * This helper function refreshes the button text of all of the exposed buttons
     * It also updates the nonmines label
     */
    private void refresh() {
        Integer numUnexposed = mainField.numUnexposed();
        nonminesLabel.setText(numUnexposed.toString());
        // iterate through all the buttons and update the text if it's an exposed cell
        for (int i = 0;i < btArray.length;i++) {
            for (int j = 0;j < btArray[0].length;j++) {
                int cellState = mainField.getCellState(i,j);
                if (cellState == mainField.EXPOSED) {
                    Integer numValue = mainField.getValue(i,j);
                    btArray[i][j].setText(numValue.toString());
                }
            }
        }
    }

    /**
     * This method will end the game if an end condition was met
     * @param gameWon
     */
    private void endGame(boolean gameWon) {
        if (gameWon) {
            winloseLabel.setText("YOU WIN");
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
