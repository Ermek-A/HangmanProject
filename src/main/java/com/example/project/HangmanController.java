package com.example.project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * HangmanController handles the game logic for the Hangman game.
 * It manages user input, updates the game state, and reveals parts
 * of the hangman drawing as incorrect guesses are made.
 */
public class HangmanController {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private Label wordLabel;

    // FXML references to the hangman drawing parts
    @FXML
    private Circle head;
    @FXML
    private Line body;
    @FXML
    private Line hand1;
    @FXML
    private Line hand2;
    @FXML
    private Line foot1;
    @FXML
    private Line foot2;

    private String secretWord;
    private boolean[] guessed;
    private int mistakes = 0;
    private final int maxMistakes = 6;

    /**
     * initialize() is automatically called after the FXML file has been loaded.
     * It hides all hangman drawing parts, loads a random secret word from the file,
     * initializes the guessed letters array, and updates the displayed word.
     */
    @FXML
    void initialize() {
        // Hide all hangman parts at the start of the game
        head.setVisible(false);
        body.setVisible(false);
        hand1.setVisible(false);
        hand2.setVisible(false);
        foot1.setVisible(false);
        foot2.setVisible(false);

        // Load a random secret word from words.txt
        loadRandomWord();
        // Initialize the guessed letters array based on the length of the secret word
        guessed = new boolean[secretWord.length()];
        // Update the word label to show underscores for unguessed letters
        updateWordLabel();
    }

    /**
     * loadRandomWord() loads a random word from the "words.txt" file located in the resources folder.
     * If the file is not found or is empty, it defaults to using "JAVA" as the secret word.
     */
    private void loadRandomWord() {
        List<String> words = new ArrayList<>();
        // Read words.txt from resources
        try (InputStream is = getClass().getResourceAsStream("/words.txt")) {
            if (is == null) {
                System.err.println("words.txt not found in resources!");
                secretWord = "JAVA"; // Fallback word
                return;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            // Add each non-empty line as a word
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    words.add(line.trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Select a random word from the list if available, otherwise use fallback
        if (!words.isEmpty()) {
            Random random = new Random();
            secretWord = words.get(random.nextInt(words.size())).toUpperCase();
        } else {
            secretWord = "JAVA"; // Fallback word if list is empty
        }
    }

    /**
     * handleLetterButtonAction() is called when a letter button is pressed.
     * It checks whether the selected letter is part of the secret word,
     * updates the guessed letters and button style accordingly, and reveals
     * a part of the hangman drawing if the guess is incorrect.
     *
     * @param event the ActionEvent triggered by the button click.
     */
    @FXML
    private void handleLetterButtonAction(ActionEvent event) {
        Button btn = (Button) event.getSource();
        String letter = btn.getText();
        boolean correct = false;

        // Check if the selected letter is in the secret word
        for (int i = 0; i < secretWord.length(); i++) {
            if (String.valueOf(secretWord.charAt(i)).equalsIgnoreCase(letter)) {
                guessed[i] = true;
                correct = true;
            }
        }
        // Change the button color based on the correctness of the guess
        if (correct) {
            btn.setStyle("-fx-background-color: green;");
        } else {
            btn.setStyle("-fx-background-color: red;");
            mistakes++;
            // Reveal a part of the hangman drawing for an incorrect guess
            updateHangmanDrawing();
        }
        // Disable the button after it's been pressed
        btn.setDisable(true);
        // Update the displayed word with the new guess
        updateWordLabel();
        // Check if the game has been won or lost
        checkGameStatus();
    }

    /**
     * updateHangmanDrawing() reveals the corresponding part of the hangman drawing
     * based on the current number of mistakes.
     */
    private void updateHangmanDrawing() {
        switch (mistakes) {
            case 1:
                head.setVisible(true);
                break;
            case 2:
                body.setVisible(true);
                break;
            case 3:
                hand1.setVisible(true);
                break;
            case 4:
                hand2.setVisible(true);
                break;
            case 5:
                foot1.setVisible(true);
                break;
            case 6:
                foot2.setVisible(true);
                break;
        }
    }

    /**
     * updateWordLabel() updates the label that displays the secret word,
     * showing guessed letters and underscores for letters not yet guessed.
     */
    private void updateWordLabel() {
        StringBuilder display = new StringBuilder();
        for (int i = 0; i < secretWord.length(); i++) {
            if (guessed[i]) {
                display.append(secretWord.charAt(i)).append(" ");
            } else {
                display.append("_ ");
            }
        }
        wordLabel.setText(display.toString());
    }

    /**
     * checkGameStatus() checks if the player has either guessed the entire word (win)
     * or used up all allowed mistakes (loss). It then updates the game state accordingly
     * by displaying a win/loss message and disabling all letter buttons.
     */
    private void checkGameStatus() {
        boolean win = true;
        // Verify if all letters have been correctly guessed
        for (boolean b : guessed) {
            if (!b) {
                win = false;
                break;
            }
        }
        if (win) {
            wordLabel.setText("Congratulations! You've guessed the word: " + secretWord);
            disableAllButtons();
        } else if (mistakes >= maxMistakes) {
            wordLabel.setText("Game over! The secret word was: " + secretWord);
            disableAllButtons();
        }
    }

    /**
     * disableAllButtons() disables all letter buttons on the root pane to prevent further input.
     */
    private void disableAllButtons() {
        for (Node node : rootPane.getChildren()) {
            if (node instanceof Button) {
                ((Button) node).setDisable(true);
            }
        }
    }
}
