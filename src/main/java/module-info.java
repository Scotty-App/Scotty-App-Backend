module org.example.scottyapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;

    opens scottyapp to javafx.fxml;
    exports scottyapp.Main;
    opens scottyapp.Main to javafx.fxml;
    opens scottyapp.Usuario to javafx.fxml;
    opens scottyapp.Item to javafx.fxml;
    opens scottyapp.Order to javafx.fxml;
    opens scottyapp.OrderDetails to javafx.fxml;
}