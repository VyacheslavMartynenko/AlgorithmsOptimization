package sample;

import algorithm.Genetic;
import algorithm.HillClimbing;
import algorithm.SimulatedAnnealing;
import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.DataSet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class Controller {
    public TextField attemptsText;
    public TextField transpositionText;
    public TextField bugText;
    public TextField alphaText;
    public TextField temperatureText;

    public RadioButton simulatedRadio;
    public RadioButton hillRadio;
    public RadioButton geneticRadio;
    public Label answerLabel;

    private FileChooser fileChooser;
    private Gson gson;
    private ArrayList<Integer> data;
    private int optimalSolution;

    @FXML
    public void initialize() {
        final ToggleGroup group = new ToggleGroup();
        simulatedRadio.setToggleGroup(group);
        hillRadio.setToggleGroup(group);
        geneticRadio.setToggleGroup(group);

        fileChooser = new FileChooser();
        gson = new Gson();
    }

    public void calculate(ActionEvent actionEvent) {
        if (hillRadio.isSelected()) {
            HillClimbing hillClimbing = new HillClimbing(
                    Integer.valueOf(attemptsText.getText()),
                    Integer.valueOf(bugText.getText()),
                    Integer.valueOf(transpositionText.getText()),
                    data);

            int thisSolution = hillClimbing.getBestLength();
            answerLabel.setText("this: " + thisSolution + " optimal: " + optimalSolution);
        } else if (simulatedRadio.isSelected()) {
            SimulatedAnnealing simulatedAnnealing = new SimulatedAnnealing(
                    Integer.valueOf(attemptsText.getText()),
                    Integer.valueOf(bugText.getText()),
                    Integer.valueOf(transpositionText.getText()),
                    data,
                    Double.valueOf(alphaText.getText()),
                    Double.valueOf(temperatureText.getText())
            );

            int thisSolution = simulatedAnnealing.getBestLength();
            answerLabel.setText("this: " + thisSolution + " optimal: " + optimalSolution);
        } else {
            Genetic genetic = new Genetic(
                    Integer.valueOf(attemptsText.getText()),
                    Integer.valueOf(bugText.getText()),
                    data
            );

            int thisSolution = genetic.getBestLength();
            answerLabel.setText("this: " + thisSolution + " optimal: " + optimalSolution);
        }
    }

    public void open(ActionEvent actionEvent) {
        Stage stage = (Stage) answerLabel.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                StringBuilder builder = new StringBuilder();
                Files.lines(file.toPath()).forEach(builder::append);
                String jsonData = builder.toString();

                DataSet dataSet = gson.fromJson(jsonData, DataSet.class);
                bugText.setText(String.valueOf(dataSet.container));
                optimalSolution = dataSet.solution;
                data = dataSet.data;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
