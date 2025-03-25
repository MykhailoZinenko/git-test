package com.colonygenesis.util;

import com.colonygenesis.ui.ScreenManager;
import com.colonygenesis.ui.styling.AppTheme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Utility class for creating and displaying dialogs in the game.
 * Ensures dialogs display properly in fullscreen mode.
 */
public class DialogUtil {

    /**
     * Shows a simple message dialog.
     *
     * @param title The dialog title
     * @param message The message to display
     */
    public static void showMessageDialog(String title, String message) {
        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(30));
        content.getStyleClass().add(AppTheme.STYLE_PANEL);
        content.setMaxWidth(500);

        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.getStyleClass().add(AppTheme.STYLE_LABEL);

        Button okButton = new Button("OK");
        okButton.getStyleClass().addAll(AppTheme.STYLE_BUTTON, AppTheme.STYLE_BUTTON_PRIMARY);
        okButton.setPrefWidth(100);

        content.getChildren().addAll(messageLabel, okButton);

        Stage dialogStage = createDialogStage(title, content);

        okButton.setOnAction(e -> dialogStage.close());

        dialogStage.showAndWait();
    }

    /**
     * Shows a confirmation dialog.
     *
     * @param title The dialog title
     * @param message The message to display
     * @param onConfirm Callback when user confirms
     * @param onCancel Callback when user cancels
     */
    public static void showConfirmDialog(String title, String message, Runnable onConfirm, Runnable onCancel) {
        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(30));
        content.getStyleClass().add(AppTheme.STYLE_PANEL);
        content.setMaxWidth(500);

        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.getStyleClass().add(AppTheme.STYLE_LABEL);

        Button confirmButton = new Button("Yes");
        confirmButton.getStyleClass().addAll(AppTheme.STYLE_BUTTON, AppTheme.STYLE_BUTTON_SUCCESS);
        confirmButton.setPrefWidth(100);

        Button cancelButton = new Button("No");
        cancelButton.getStyleClass().addAll(AppTheme.STYLE_BUTTON, AppTheme.STYLE_BUTTON_DANGER);
        cancelButton.setPrefWidth(100);

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(cancelButton, confirmButton);

        content.getChildren().addAll(messageLabel, buttonBox);

        Stage dialogStage = createDialogStage(title, content);

        confirmButton.setOnAction(e -> {
            dialogStage.close();
            if (onConfirm != null) {
                onConfirm.run();
            }
        });

        cancelButton.setOnAction(e -> {
            dialogStage.close();
            if (onCancel != null) {
                onCancel.run();
            }
        });

        dialogStage.showAndWait();
    }

    /**
     * Shows a custom dialog with the specified content.
     *
     * @param title The dialog title
     * @param content The dialog content
     * @return The dialog stage that can be controlled programmatically
     */
    public static Stage showCustomDialog(String title, Node content) {
        Stage dialogStage = createDialogStage(title, content);
        dialogStage.show();
        return dialogStage;
    }

    /**
     * Creates a dialog stage with proper setup for fullscreen applications.
     *
     * @param title The dialog title
     * @param content The dialog content
     * @return The configured stage
     */
    private static Stage createDialogStage(String title, Node content) {
        Stage primaryStage = ScreenManager.getInstance().getPrimaryStage();

        BorderPane root = new BorderPane();
        root.setCenter(content);
        root.getStyleClass().add(AppTheme.STYLE_PANEL);

        Scene dialogScene = new Scene(root);

        String cssPath = AppTheme.MAIN_STYLESHEET;
        if (primaryStage.getScene().getStylesheets().contains(cssPath)) {
            dialogScene.getStylesheets().add(cssPath);
        }
        dialogScene.getStylesheets().add(AppTheme.BOOTSTRAP_STYLESHEET);

        Stage dialogStage = new Stage();
        dialogStage.initOwner(primaryStage);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.DECORATED);
        dialogStage.setTitle(title);
        dialogStage.setScene(dialogScene);

        dialogStage.setMinWidth(400);
        dialogStage.setMinHeight(200);

        dialogStage.setMaxWidth(primaryStage.getWidth() * 0.8);
        dialogStage.setMaxHeight(primaryStage.getHeight() * 0.8);

        dialogStage.centerOnScreen();

        return dialogStage;
    }
}