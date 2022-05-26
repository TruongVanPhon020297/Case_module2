module com.example.case_study_module2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires zip4j;


    opens com.example.case_study_module2 to javafx.fxml;
    exports com.example.case_study_module2;
}