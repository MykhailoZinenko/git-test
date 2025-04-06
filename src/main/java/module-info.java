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
    requires java.management;

    // Core exports
    exports com.colonygenesis.core;
    opens com.colonygenesis.core to javafx.fxml;

    // UI exports
    exports com.colonygenesis.ui;
    opens com.colonygenesis.ui to javafx.fxml;
    exports com.colonygenesis.ui.components;
    opens com.colonygenesis.ui.components to javafx.fxml;
    exports com.colonygenesis.ui.events;
    exports com.colonygenesis.ui.styling;
    exports com.colonygenesis.ui.debug;

    // Game components exports
    exports com.colonygenesis.map;
    exports com.colonygenesis.resource;
    exports com.colonygenesis.building;
    exports com.colonygenesis.util;
}