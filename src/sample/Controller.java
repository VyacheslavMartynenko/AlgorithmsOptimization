package sample;

import algorithm.HillClimbing;
import algorithm.SimulatedAnnealing;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import math.Transposition;

public class Controller {
    public TextField attemptsText;
    public TextField transpositionText;
    public TextField bugText;
    public TextField alphaText;
    public TextField temperatureText;

    public RadioButton simulatedRadio;
    public RadioButton hillRadio;
    public Label answerLabel;

    @FXML
    public void initialize() {
        final ToggleGroup group = new ToggleGroup();

        simulatedRadio.setToggleGroup(group);
        hillRadio.setToggleGroup(group);
    }

    public void calculate(ActionEvent actionEvent) {
        if (hillRadio.isSelected()) {
            HillClimbing hillClimbing = new HillClimbing(
                    Integer.valueOf(attemptsText.getText()),
                    Integer.valueOf(bugText.getText()),
                    Integer.valueOf(transpositionText.getText()));

            Transposition answer = hillClimbing.getBestLength();
            answerLabel.setText(answer.getElementsList().toString());
        } else {
            SimulatedAnnealing simulatedAnnealing = new SimulatedAnnealing(
                    Integer.valueOf(attemptsText.getText()),
                    Integer.valueOf(bugText.getText()),
                    Integer.valueOf(transpositionText.getText()),
                    Double.valueOf(alphaText.getText()),
                    Double.valueOf(temperatureText.getText())
            );

            Transposition answer = simulatedAnnealing.getBestLength();
            answerLabel.setText(answer.getElementsList().toString());
        }
    }
}
