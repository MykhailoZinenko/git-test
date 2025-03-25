/**
 * Module definition for the Colony Genesis application.
 * Specifies required dependencies and exported packages.
 */
module com.colonygenesis {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.logging;

    opens com.colonygenesis.core to javafx.fxml;
    exports com.colonygenesis.core;
    exports com.colonygenesis.ui;
    opens com.colonygenesis.ui to javafx.fxml;
    exports com.colonygenesis.ui.components;
    opens com.colonygenesis.ui.components to javafx.fxml;
}