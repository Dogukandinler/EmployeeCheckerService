package com.dogukandinler.EmployeeCheckerService;

import com.google.gson.Gson;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import okhttp3.*;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Random;

public class MathQuizApp extends Application {
    public static final String URL = "http://localhost:8080/captcha/response";
    private Label mathOperationLabel;
    private TextField answerField;
    private int responseTime;
    private Label resultLabel;
    private Label timerLabel;  // Declare timerLabel here
    private int timerSeconds = 15; // Set the timer duration in seconds
    private Timeline timeline;
    private String macAddress;  // Add a variable to store the mac address

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Math Quiz");

        setRandomPosition(primaryStage);

        macAddress = getMacAddress();

        mathOperationLabel = new Label();
        answerField = new TextField();
        Button checkButton = new Button("Check Answer");
        resultLabel = new Label();
        timerLabel = new Label(String.valueOf(timerSeconds));

        // Set up a TextFormatter to allow only numeric input
        TextFormatter<String> numericFormatter = new TextFormatter<>(
                (TextFormatter.Change change) -> {
                    if (change.getText().matches("[0-9+-]*")) {
                        return change;
                    }
                    return null;
                });


        // Apply the TextFormatter to the answerField
        answerField.setTextFormatter(numericFormatter);

        checkButton.setOnAction(event -> checkAnswer());

        // Create layout using HBox and VBox
        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setId("mainLayout"); // Apply the CSS ID

        // Timer layout at the top right
        HBox timerBox = new HBox(timerLabel);
        HBox.setHgrow(timerLabel, Priority.ALWAYS);
        timerBox.setAlignment(Pos.TOP_RIGHT);
        timerBox.setId("timerLabel"); // Apply the CSS ID

        // Question and Input layout
        HBox questionInputLayout = new HBox(10);
        questionInputLayout.getChildren().addAll(mathOperationLabel, answerField, checkButton);
        questionInputLayout.setId("questionLabel"); // Apply the CSS ID

        mainLayout.getChildren().addAll(timerBox, questionInputLayout, resultLabel);

        // Set up the scene
        Scene scene = new Scene(mainLayout, 400, 125);

        primaryStage.setScene(scene);


        primaryStage.setResizable(false);

        // Generate the first math operation
        generateMathOperation();

        setupTimer(primaryStage);

        // Restart the timer on each valid answer
        restartTimer();

        // Show the stage
        primaryStage.show();
    }

    private void setupTimer(Stage primaryStage) {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            timerSeconds--;

            // Update the timerLabel
            Platform.runLater(() -> timerLabel.setText(String.valueOf(timerSeconds)));

            if (timerSeconds <= 0) {
                // Close the window after 15 seconds
                primaryStage.close();
                timeline.stop(); // Stop the timeline when the window is closed

                // Schedule a new instance after 15 seconds
                Platform.runLater(() -> {
                    try {
                        Thread.sleep(15000);  // Sleep for 15 seconds
                        start(new Stage());  // Start a new instance
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void restartTimer() {
        timerSeconds = 30;
        timerLabel.setText(String.valueOf(timerSeconds));
    }


    public void sendRequest(boolean isCorrect) {
        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");

            CaptchaResponseDto macDto = new CaptchaResponseDto();
            macDto.setMacAddress(macAddress);
            macDto.setResponseTimeSeconds(isCorrect ? responseTime : -1); // Set response time only for correct answers

            RequestBody body = RequestBody.create(mediaType, new Gson().toJson(macDto));
            Request request = new Request.Builder()
                    .url(URL)
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .build();


            // Send the request and get the response
            try (Response response = client.newCall(request).execute()) {
                // Print success or failure based on the response code
                if (response.isSuccessful()) {
                    System.out.println("Success");
                } else {
                    System.out.println("Failure");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setRandomPosition(Stage stage) {
        Random random = new Random();
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        double stageWidth = 400; // Adjust as needed
        double stageHeight = 100; // Adjust as needed

        double maxX = bounds.getMaxX() - stageWidth;
        double maxY = bounds.getMaxY() - stageHeight;

        double randomX = Math.min(maxX, bounds.getMinX() + random.nextDouble() * bounds.getWidth());
        double randomY = Math.min(maxY, bounds.getMinY() + random.nextDouble() * bounds.getHeight());

        stage.setX(randomX);
        stage.setY(randomY);
    }

    private String getMacAddress() {
        try {
            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (!networkInterface.isLoopback() && networkInterface.isUp()) {
                    byte[] mac = networkInterface.getHardwareAddress();
                    if (mac != null) {
                        StringBuilder macAddress = new StringBuilder();
                        for (int i = 0; i < mac.length; i++) {
                            macAddress.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? ":" : ""));
                        }
                        return macAddress.toString();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void generateMathOperation() {
        Random random = new Random();

        // Generate two random single-digit numbers
        int num1 = random.nextInt(10);
        int num2 = random.nextInt(10);

        // Generate a random operator (+, -, *)
        String[] operators = {"+", "-", "*"};
        String operator = operators[random.nextInt(operators.length)];

        // Display the math operation
        mathOperationLabel.setText(num1 + " " + operator + " " + num2);
    }

    private void checkAnswer() {
        try {
            // Get the entered answer
            String answerText = answerField.getText();
            int enteredAnswer = Integer.parseInt(answerText);

            // Get the operands and operator from the displayed math operation
            String[] operationParts = mathOperationLabel.getText().split(" ");
            int num1 = Integer.parseInt(operationParts[0]);
            String operator = operationParts[1];
            int num2 = Integer.parseInt(operationParts[2]);

            // Calculate the correct answer
            int correctAnswer;
            switch (operator) {
                case "+":
                    correctAnswer = num1 + num2;
                    break;
                case "-":
                    correctAnswer = num1 - num2;
                    break;
                case "*":
                    correctAnswer = num1 * num2;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid operator");
            }


            if (enteredAnswer == correctAnswer) {
                // Calculate the response time
                responseTime = 30 - timerSeconds;

                resultLabel.setText("Correct!");
                resultLabel.setStyle("-fx-text-fill: green;");

                // Send a request for a correct answer with response time
                sendRequest(true);
                // Close the window after a brief delay
                Platform.runLater(() -> {
                    Stage stage = (Stage) resultLabel.getScene().getWindow();
                    stage.close();
                });
                timeline.stop();

                // Schedule a new instance after 15 seconds
                Platform.runLater(() -> {
                    try {
                        Thread.sleep(15000);  // Sleep for 15 seconds
                        start(new Stage());  // Start a new instance

                        restartTimer();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            } else {
                // If the answer is incorrect, provide feedback
                resultLabel.setText("Incorrect. The correct answer is: " + correctAnswer);
                resultLabel.setStyle("-fx-text-fill: red;");
                sendRequest(false);
            }

            // Generate a new math operation
            generateMathOperation();

        } catch (NumberFormatException e) {
            resultLabel.setText("Invalid input. Please enter a number.");
            resultLabel.setStyle("-fx-text-fill: red;");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}