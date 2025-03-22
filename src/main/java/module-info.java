module com.colonygenesis {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens com.colonygenesis.core to javafx.fxml;
    exports com.colonygenesis.core;
    exports com.colonygenesis.ui;
    opens com.colonygenesis.ui to javafx.fxml;
}