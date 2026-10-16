package com.summer.section1.group4.medical_entrance_exam.zinat.Applicant;

import com.summer.section1.group4.medical_entrance_exam.zinat.Applicant.Applicant;
import com.summer.section1.group4.medical_entrance_exam.zinat.Applicant.ApplicationSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ApplicantFormController {
    @FXML
    private TextField FullNameTF;
    @FXML
    private DatePicker DobTF;
    @FXML
    private ComboBox<String> SscBoardComB;
    @FXML
    private ComboBox<String> HscBoardComB;
    @FXML
    private TextField SscGpaTF;
    @FXML
    private TextField HscGpaTF;
    @FXML
    private Label FormStatusLabel;

    @FXML
    public void initialize() {
        String[] boards = {"Dhaka", "Chattogram", "Rajshahi", "Sylhet", "Barishal", "Khulna",
                "Jessore", "Dinajpur", "Cumilla", "Mymensingh"};
        SscBoardComB.getItems().addAll(boards);
        HscBoardComB.getItems().addAll(boards);


        Applicant existing = ApplicationSession.getInstance().getApplicant();
        if (existing != null) {
            FullNameTF.setText(existing.getApplicantName());
            DobTF.setValue(existing.getApplicantDob());
            SscBoardComB.setValue(existing.getSscBoard());
            HscBoardComB.setValue(existing.getHscBoard());
            SscGpaTF.setText(String.valueOf(existing.getSscGpa()));
            HscGpaTF.setText(String.valueOf(existing.getHscGpa()));
        }
    }

    @FXML
    public void SubmitApplicationOA(ActionEvent actionEvent) {
        if (FullNameTF.getText().trim().isEmpty() || SscGpaTF.getText().trim().isEmpty() || HscGpaTF.getText().trim().isEmpty()) {
            FormStatusLabel.setText("VALIDATION ERROR: Please fill out all text boxes!");
            return;
        }
        if (DobTF.getValue() == null || SscBoardComB.getValue() == null || HscBoardComB.getValue() == null) {
            FormStatusLabel.setText("VALIDATION ERROR: Missing date selection or board parameters!");
            return;
        }
        if (DobTF.getValue().plusYears(18).isAfter(java.time.LocalDate.now())) {
            FormStatusLabel.setText("VALIDATION ERROR: Applicant must be at least 18 years old.");
            return;
        }



        double sscGpa, hscGpa;
        try {
            sscGpa = Double.parseDouble(SscGpaTF.getText().trim());
            hscGpa = Double.parseDouble(HscGpaTF.getText().trim());
        } catch (NumberFormatException e) {
            FormStatusLabel.setText("VALIDATION ERROR: GPA fields must be numeric (e.g. 4.83).");
            return;
        }

        if (sscGpa > 5.00 || hscGpa > 5.00) {
            FormStatusLabel.setText("VALIDATION ERROR: GPA can not be greater than 5.00");
            return;
        }

        final double MINIMUM_ELIGIBLE_GPA = 3.50;
        if (sscGpa < MINIMUM_ELIGIBLE_GPA || hscGpa < MINIMUM_ELIGIBLE_GPA) {
            FormStatusLabel.setText("VALIDATION ERROR: Minimum GPA of " + MINIMUM_ELIGIBLE_GPA
                    + " in both SSC & HSC is required for medical admission eligibility.");
            return;
        }

        Applicant a = new Applicant(
                FullNameTF.getText().trim(),
                SscBoardComB.getValue(),
                HscBoardComB.getValue(),
                sscGpa,
                hscGpa,
                DobTF.getValue());
        ApplicationSession.getInstance().setApplicant(a);

        FormStatusLabel.setText("SUCCESS: Saved! Move to Goal 2: Document Uploads next.");
    }
}

