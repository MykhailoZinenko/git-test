package com.colonygenesis.ui.components;

import com.colonygenesis.ui.events.EventBus;
import com.colonygenesis.ui.events.NotificationEvents;
import com.colonygenesis.ui.styling.AppTheme;
import com.colonygenesis.util.LoggerUtil;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

/**
 * Manager for displaying notifications to the user.
 * Handles creation, display, and removal of notification toasts.
 */
public class NotificationManager extends VBox {
    private static final Logger LOGGER = LoggerUtil.getLogger(NotificationManager.class);
    private static final int MAX_VISIBLE_NOTIFICATIONS = 3;
    private static final int DEFAULT_NOTIFICATION_DURATION_MS = 3000;

    private final Queue<NotificationToast> activeNotifications = new LinkedList<>();
    private final Queue<NotificationEvents.GameNotificationEvent> pendingNotifications = new ConcurrentLinkedQueue<>();
    private final EventBus eventBus;
    private boolean processingNotifications = false;

    /**
     * Creates a new notification manager.
     */
    public NotificationManager() {
        setSpacing(10);
        setPadding(new Insets(10));
        setAlignment(Pos.BOTTOM_RIGHT);
        setMaxWidth(350);
        setMouseTransparent(false);

        this.setPickOnBounds(false);

        eventBus = EventBus.getInstance();

        initializeEventSubscriptions();

        LOGGER.fine("NotificationManager initialized");
    }

    /**
     * Initializes subscriptions to notification events.
     */
    private void initializeEventSubscriptions() {
        eventBus.subscribe(NotificationEvents.GameNotificationEvent.class, this::handleGameNotification);
        eventBus.subscribe(NotificationEvents.ResourceNotificationEvent.class, this::handleResourceNotification);
        eventBus.subscribe(NotificationEvents.BuildingNotificationEvent.class, this::handleBuildingNotification);
    }

    /**
     * Handles general game notifications.
     */
    private void handleGameNotification(NotificationEvents.GameNotificationEvent event) {
        Platform.runLater(() -> {
            addNotification(event);
        });
    }

    /**
     * Handles resource-specific notifications.
     */
    private void handleResourceNotification(NotificationEvents.ResourceNotificationEvent event) {
        Platform.runLater(() -> {
            NotificationEvents.GameNotificationEvent gameEvent = new NotificationEvents.GameNotificationEvent(
                    "Resource",
                    event.getMessage(),
                    event.getType()
            );

            addNotification(gameEvent);
        });
    }

    /**
     * Handles building-specific notifications.
     */
    private void handleBuildingNotification(NotificationEvents.BuildingNotificationEvent event) {
        Platform.runLater(() -> {
            NotificationEvents.GameNotificationEvent gameEvent = new NotificationEvents.GameNotificationEvent(
                    event.getBuildingName(),
                    event.getMessage(),
                    event.getType()
            );

            addNotification(gameEvent);
        });
    }

    /**
     * Adds a notification to be displayed.
     */
    private void addNotification(NotificationEvents.GameNotificationEvent event) {
        pendingNotifications.add(event);

        if (!processingNotifications) {
            processingNotifications = true;
            processNextNotification();
        }
    }

    /**
     * Processes the next notification in the queue.
     */
    private void processNextNotification() {
        if (pendingNotifications.isEmpty()) {
            processingNotifications = false;
            return;
        }

        if (activeNotifications.size() >= MAX_VISIBLE_NOTIFICATIONS) {
            return;
        }

        NotificationEvents.GameNotificationEvent event = pendingNotifications.poll();
        if (event == null) {
            processNextNotification();
            return;
        }

        NotificationToast toast = new NotificationToast(
                event.getTitle(),
                event.getMessage(),
                event.getType(),
                event.getDurationMs() > 0 ? event.getDurationMs() : DEFAULT_NOTIFICATION_DURATION_MS
        );

        activeNotifications.add(toast);
        getChildren().add(0, toast);

        toast.show(() -> {
            activeNotifications.remove(toast);
            getChildren().remove(toast);

            processNextNotification();
        });

        Platform.runLater(this::processNextNotification);
    }

    /**
     * A toast notification component.
     */
    private class NotificationToast extends HBox {
        private final String title;
        private final String message;
        private final NotificationEvents.NotificationType type;
        private final int durationMs;

        /**
         * Creates a new notification toast.
         */
        public NotificationToast(String title, String message, NotificationEvents.NotificationType type, int durationMs) {
            this.title = title;
            this.message = message;
            this.type = type;
            this.durationMs = durationMs;

            initializeUI();
        }

        /**
         * Initializes the UI components.
         */
        private void initializeUI() {
            setSpacing(10);
            setPadding(new Insets(10));
            setAlignment(Pos.CENTER_LEFT);
            setMaxWidth(350);
            setMinHeight(80);

            switch (type) {
                case INFO -> setStyle("-fx-background-color: rgba(60, 120, 200, 0.85); -fx-background-radius: 5;");
                case SUCCESS -> setStyle("-fx-background-color: rgba(50, 160, 70, 0.85); -fx-background-radius: 5;");
                case WARNING -> setStyle("-fx-background-color: rgba(230, 160, 30, 0.85); -fx-background-radius: 5;");
                case ERROR -> setStyle("-fx-background-color: rgba(200, 60, 60, 0.85); -fx-background-radius: 5;");
            }

            VBox contentBox = new VBox(5);
            HBox.setHgrow(contentBox, Priority.ALWAYS);

            Label titleLabel = new Label(title);
            titleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white; -fx-font-size: 14px;");

            Label messageLabel = new Label(message);
            messageLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
            messageLabel.setWrapText(true);

            contentBox.getChildren().addAll(titleLabel, messageLabel);

            Button closeButton = new Button("×");
            closeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px;");
            closeButton.setPadding(new Insets(0, 5, 0, 5));
            closeButton.setOnAction(e -> close());

            getChildren().addAll(contentBox, closeButton);

            setOpacity(0.0);
        }

        /**
         * Shows the toast with fade-in animation.
         *
         * @param onFinished Callback when the toast is finished (either timed out or closed)
         */
        public void show(Runnable onFinished) {
            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), this);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);

            PauseTransition pause = new PauseTransition(Duration.millis(durationMs));

            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), this);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(e -> {
                if (onFinished != null) {
                    onFinished.run();
                }
            });

            SequentialTransition sequence = new SequentialTransition(fadeIn, pause, fadeOut);
            sequence.play();
        }

        /**
         * Closes the toast immediately.
         */
        public void close() {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(200), this);
            fadeOut.setFromValue(getOpacity());
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(e -> {
                activeNotifications.remove(this);
                getChildren().remove(this);

                processNextNotification();
            });

            fadeOut.play();
        }
    }

    public void dispose() {
        LOGGER.fine("Disposing NotificationManager resources");

        eventBus.unsubscribeAll(this);

        activeNotifications.clear();
        pendingNotifications.clear();
        getChildren().clear();
        processingNotifications = false;
    }
}