module com.colonygenesis.oop_project {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    opens com.colonygenesis.oop_project to javafx.fxml;
    exports com.colonygenesis.oop_project;
}