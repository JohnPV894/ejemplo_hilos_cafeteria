module com.example.javafx_cafetera {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.javafx_cafetera to javafx.fxml;
    exports com.example.javafx_cafetera;
}