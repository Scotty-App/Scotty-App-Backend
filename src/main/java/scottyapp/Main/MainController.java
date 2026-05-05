package scottyapp.Main;

import javafx.fxml.FXML;

import java.io.IOException;

public class MainController {

    @FXML
    public void irAPantalla2() throws IOException {
        MainApplication.setRoot("scottyapp/SegundaPantallaController.java");

    }

}
