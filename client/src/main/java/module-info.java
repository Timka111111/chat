module kz.knewIt.client {
    requires javafx.controls;
    requires javafx.fxml;


    opens kz.knewIt.client to javafx.fxml;
    exports kz.knewIt.client;
}