package com.summer.section1.group4.medical_entrance_exam.zinat.QuestionModerator;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class QuestionModeratorController {
    @javafx.fxml.FXML
    private AnchorPane paneContentContainerQues;
    @javafx.fxml.FXML
    private Button CreateQuestionSetBTN;
    @javafx.fxml.FXML
    private Button AddMultipleChoiceBTN;
    @javafx.fxml.FXML
    private Button SetPassMarkRulesBTN;
    @javafx.fxml.FXML
    private Button PreviewCompleteTestBTN;
    @javafx.fxml.FXML
    private Button QuestionMarkAnalysisBTN;
    @javafx.fxml.FXML
    private Button ViewStatusSummaryBTN;
    @javafx.fxml.FXML
    private Button SetCreationHistoryTimeLogBTN;
    @javafx.fxml.FXML
    private Button SetMarksAwardDeductBTN;

    @javafx.fxml.FXML
    public void handleMenuSelection(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String buttonId = clickedButton.getId();
        String fxmlFile = "";

        // Determine which target view window needs to load inside the pane container box
        switch (buttonId) {
            case "CreateQuestionSetBTN":
                fxmlFile = "/com/summer/section1/group4/medical_entrance_exam/zinat/QuestionModerator/CreateQuestionSet.fxml";
                break;
            case "AddMultipleChoiceBTN":
                fxmlFile = "/com/summer/section1/group4/medical_entrance_exam/zinat/QuestionModerator/AddQuestionActiveBank.fxml";
                break;
            case "SetPassMarkRulesBTN":
                fxmlFile = "/com/summer/section1/group4/medical_entrance_exam/zinat/QuestionModerator/PassMarkRules.fxml";
                break;
            case "QuestionMarkAnalysisBTN":
                fxmlFile = "/com/summer/section1/group4/medical_entrance_exam/zinat/QuestionModerator/QuestionMarkAnalysis.fxml";
                break;
            case "ViewStatusSummaryBTN":
                fxmlFile = "/com/summer/section1/group4/medical_entrance_exam/zinat/QuestionModerator/QuestionStatusSummary.fxml";
                break;
            case "PreviewCompleteTestBTN":
                fxmlFile = "/com/summer/section1/group4/medical_entrance_exam/zinat/QuestionModerator/PreviewQuestionPaper.fxml";
                break;
            case "SetMarksAwardDeductBTN":
                fxmlFile = "/com/summer/section1/group4/medical_entrance_exam/zinat/QuestionModerator/MarkDistribution.fxml";
                break;
            case "SetCreationHistoryTimeLogBTN":
                fxmlFile = "/com/summer/section1/group4/medical_entrance_exam/zinat/QuestionModerator/TimeLog.fxml";
                break;
        }

        try {
            // Dynamically load the layout file
            Node newContentPane = FXMLLoader.load(getClass().getResource(fxmlFile));

            // Clear out old view elements and display the new goal view window form
            paneContentContainerQues.getChildren().clear();
            paneContentContainerQues.getChildren().add(newContentPane);

            // Lock positioning layout constraints perfectly anchors
            AnchorPane.setTopAnchor(newContentPane, 0.0);
            AnchorPane.setBottomAnchor(newContentPane, 0.0);
            AnchorPane.setLeftAnchor(newContentPane, 0.0);
            AnchorPane.setRightAnchor(newContentPane, 0.0);

        } catch (IOException e) {
            System.out.println("Could not switch forms. Ensure target layout resource file exists: " + fxmlFile);
            e.printStackTrace();
        }

    }

    @javafx.fxml.FXML
    public void ExitOA(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/summer/section1/group4/medical_entrance_exam/Login.fxml"));
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @javafx.fxml.FXML
    public void MainMenuOA(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/summer/section1/group4/medical_entrance_exam/zinat/QuestionModerator/QuestionModerator.fxml"));
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}