package com.summer.section1.group4.medical_entrance_exam.zinat.Applicant;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class DocumentUploadController {
    @FXML
    private ComboBox<String> HscDocumentComB;
    @FXML
    private ComboBox<String> SscDocumentComB;
    @FXML
    private ComboBox<String> VerificationDocumentComB;
    @FXML
    private Label DocumentUploadStatusLabel;

//For Documents
    private String identityFileName;
    private String sscFileName;
    private String hscFileName;
    @FXML
    private TextField applicantNameTF;

    @FXML
    public void initialize() {
        VerificationDocumentComB.getItems().addAll("NID", "BirthCertificate", "Passport");
        SscDocumentComB.getItems().addAll("Transcript", "Certificate");
        HscDocumentComB.getItems().addAll("Transcript", "Certificate");

        Applicant applicant = ApplicationSession.getInstance().getApplicant();
        if (applicant != null) {
            applicantNameTF.setText(applicant.getApplicantName());
        }
    }


    @FXML
    public void VerificationDocumentOA(ActionEvent actionEvent) {
        if (VerificationDocumentComB.getValue() == null) {
            DocumentUploadStatusLabel.setText("STATUS: Please select a verification document type first.");
            return;
        }
        File chosen = openFileChooser(actionEvent, "Select Identification Document");
        if (chosen != null) {
            identityFileName = chosen.getName();
            DocumentUploadStatusLabel.setText("Identity file loaded: " + identityFileName);
        }
    }

    @FXML
    public void SscDocumentOA(ActionEvent actionEvent) {
        if (SscDocumentComB.getValue() == null) {
            DocumentUploadStatusLabel.setText("STATUS: Please select the SSC document type first.");
            return;
        }
        File chosen = openFileChooser(actionEvent, "Select SSC Transcript / Certificate");
        if (chosen != null) {
            sscFileName = chosen.getName();
            DocumentUploadStatusLabel.setText("SSC transcript file loaded: " + sscFileName);
        }
    }

    @FXML
    public void HscDocumentOA(ActionEvent actionEvent) {
        if (HscDocumentComB.getValue() == null) {
            DocumentUploadStatusLabel.setText("STATUS: Please select the HSC document type first.");
            return;
        }
        File chosen = openFileChooser(actionEvent, "Select HSC Transcript / Certificate");
        if (chosen != null) {
            hscFileName = chosen.getName();
            DocumentUploadStatusLabel.setText("HSC transcript file loaded: " + hscFileName);
        }
    }

    /** Opens a native file chooser anchored to the current window; returns the picked file, or null if cancelled. */
    private File openFileChooser(ActionEvent actionEvent, String title) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF / JPG files", "*.pdf", "*.jpg", "*.jpeg", "*.png"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        return fileChooser.showOpenDialog(stage);
    }

    @Deprecated
    public void SubmitApplicationOA(ActionEvent actionEvent) {
        // Keeping this button for ensuring the submission
    }

    @FXML
    public void SubmitDocumentsOA(ActionEvent actionEvent) {
        if (identityFileName == null || sscFileName == null || hscFileName == null) {
            DocumentUploadStatusLabel.setText("Portal Upload Status: Pending Attachments");
            return;
        }

        ApplicationSession session = ApplicationSession.getInstance();
        session.setIdentityFileName(identityFileName);
        session.setSscTranscriptFileName(sscFileName);
        session.setHscTranscriptFileName(hscFileName);
        session.setDocumentsUploaded(true);

        DocumentUploadStatusLabel.setText("All Documents Uploaded! Proceed to Goal 3");
    }
}