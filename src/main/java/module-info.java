module com.summer.section1.group4.medical_entrance_exam {

        requires javafx.controls;
        requires javafx.fxml;
        requires java.sql;
    requires javafx.graphics;

    opens com.summer.section1.group4.medical_entrance_exam
        to javafx.graphics, javafx.fxml;

        opens com.summer.section1.group4.medical_entrance_exam.zinat
        to javafx.fxml;

        opens com.summer.section1.group4.medical_entrance_exam.zinat.Applicant
        to javafx.fxml;

        opens com.summer.section1.group4.medical_entrance_exam.zinat.QuestionModerator
        to javafx.fxml;



        }