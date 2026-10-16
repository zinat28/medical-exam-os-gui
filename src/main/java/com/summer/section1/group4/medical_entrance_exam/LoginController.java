package com.summer.section1.group4.medical_entrance_exam;

import com.summer.section1.group4.medical_entrance_exam.zinat.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController {
    @javafx.fxml.FXML
    private PasswordField passwordTextField;
    @javafx.fxml.FXML
    private ComboBox<String> userTypeComboBox;
    @javafx.fxml.FXML
    private TextField usernameTextField;
    @javafx.fxml.FXML
    private Label messegeLabel;

    public void initialize(){
        userTypeComboBox.getItems().addAll( "Applicant", "Question Moderator" );

    }

    @javafx.fxml.FXML
    public void loginButtonOnAction(ActionEvent actionEvent) throws IOException {
        String type = userTypeComboBox.getValue();
        if (type == null) {
            messegeLabel.setText("Please select a user type");
            return;
        }

        for (Login x : LoginManager.getLoginArrayList()) {
            if (usernameTextField.getText().equals(x.getUsername())
                    && passwordTextField.getText().equals(x.getPassword())
                    && type.equals(x.getUserType())) {
                messegeLabel.setText("Login Successful");
                String base = "/com/summer/section1/group4/medical_entrance_exam/zinat/";
                SceneSwitcher.switchTo(type.equals("Applicant")
                        ? base + "Applicant/Applicant.fxml"
                        : base + "QuestionModerator/QuestionModerator.fxml");
                return;                      // stop here: do not fall through to "Login Failed"
            }
        }

        messegeLabel.setText("Login Failed");
    }
}
