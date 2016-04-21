package files;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class Controller {

    @FXML
    private GridPane windowGP;
    @FXML
    private GridPane minefieldGP;
    @FXML
    private ChoiceBox difficultyCB;
    @FXML
    private Label nonminesLabel;

    @FXML
    private void initialize() {
        windowGP.setGridLinesVisible(true);
        minefieldGP.setGridLinesVisible(true);
        difficultyCB.setItems(FXCollections.observableArrayList("Easy","Medium","Hard"));
        nonminesLabel.setText("");
        int mfHeight = minefieldGP.getRowConstraints().size();
        int mfWidth = minefieldGP.getColumnConstraints().size();
        for (int i = 0;i < mfWidth;i++) {
            for (int j = 0; j < mfHeight; j++) {
                minefieldGP.add(new Button(""), j, i);
            }
        }
    }
}
