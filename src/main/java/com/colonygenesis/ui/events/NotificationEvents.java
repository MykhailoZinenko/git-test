package com.colonygenesis.ui.events;

/**
 * Event classes related to game notifications and messages.
 * Provides a central mechanism for displaying information to the user.
 */
public class NotificationEvents {

    /**
     * Event fired when a game notification should be shown to the user.
     */
    public static class GameNotificationEvent implements GameEvent {
        private final String title;
        private final String message;
        private final NotificationType type;
        private final int durationMs;

        /**
         * Creates a new game notification event.
         *
         * @param title The notification title
         * @param message The notification message
         * @param type The notification type (determines styling and behavior)
         * @param durationMs Duration in milliseconds (0 for persistent notifications)
         */
        public GameNotificationEvent(String title, String message, NotificationType type, int durationMs) {
            this.title = title;
            this.message = message;
            this.type = type;
            this.durationMs = durationMs;
        }

        /**
         * Creates a new game notification with default duration.
         *
         * @param title The notification title
         * @param message The notification message
         * @param type The notification type
         */
        public GameNotificationEvent(String title, String message, NotificationType type) {
            this(title, message, type, type == NotificationType.ERROR ? 0 : 5000);
        }

        /**
         * Gets the notification title.
         *
         * @return The title
         */
        public String getTitle() {
            return title;
        }

        /**
         * Gets the notification message.
         *
         * @return The message
         */
        public String getMessage() {
            return message;
        }

        /**
         * Gets the notification type.
         *
         * @return The type
         */
        public NotificationType getType() {
            return type;
        }

        /**
         * Gets the notification duration in milliseconds.
         *
         * @return The duration (0 for persistent notifications)
         */
        public int getDurationMs() {
            return durationMs;
        }

        /**
         * Checks if this is a persistent notification.
         *
         * @return true if persistent, false otherwise
         */
        public boolean isPersistent() {
            return durationMs <= 0;
        }

        @Override
        public String getName() {
            return "GameNotification";
        }
    }

    /**
     * Event fired when a resource-related notification should be shown.
     */
    public static class ResourceNotificationEvent implements GameEvent {
        private final String message;
        private final NotificationType type;

        /**
         * Creates a new resource notification event.
         *
         * @param message The notification message
         * @param type The notification type
         */
        public ResourceNotificationEvent(String message, NotificationType type) {
            this.message = message;
            this.type = type;
        }

        /**
         * Gets the notification message.
         *
         * @return The message
         */
        public String getMessage() {
            return message;
        }

        /**
         * Gets the notification type.
         *
         * @return The type
         */
        public NotificationType getType() {
            return type;
        }

        @Override
        public String getName() {
            return "ResourceNotification";
        }
    }

    /**
     * Event fired when a building-related notification should be shown.
     */
    public static class BuildingNotificationEvent implements GameEvent {
        private final String buildingName;
        private final String message;
        private final NotificationType type;

        /**
         * Creates a new building notification event.
         *
         * @param buildingName The name of the related building
         * @param message The notification message
         * @param type The notification type
         */
        public BuildingNotificationEvent(String buildingName, String message, NotificationType type) {
            this.buildingName = buildingName;
            this.message = message;
            this.type = type;
        }

        /**
         * Gets the building name.
         *
         * @return The building name
         */
        public String getBuildingName() {
            return buildingName;
        }

        /**
         * Gets the notification message.
         *
         * @return The message
         */
        public String getMessage() {
            return message;
        }

        /**
         * Gets the notification type.
         *
         * @return The type
         */
        public NotificationType getType() {
            return type;
        }

        /**
         * Gets the full formatted message.
         *
         * @return The formatted message
         */
        public String getFormattedMessage() {
            return buildingName + ": " + message;
        }

        @Override
        public String getName() {
            return "BuildingNotification";
        }
    }

    /**
     * Enumeration of notification types.
     * Determines styling and behavior.
     */
    public enum NotificationType {
        INFO("Information", "info-notification"),
        SUCCESS("Success", "success-notification"),
        WARNING("Warning", "warning-notification"),
        ERROR("Error", "error-notification");

        private final String title;
        private final String styleClass;

        NotificationType(String title, String styleClass) {
            this.title = title;
            this.styleClass = styleClass;
        }

        /**
         * Gets the default title for this notification type.
         *
         * @return The title
         */
        public String getTitle() {
            return title;
        }

        /**
         * Gets the CSS style class for this notification type.
         *
         * @return The style class
         */
        public String getStyleClass() {
            return styleClass;
        }
    }

    /**
     * Convenience methods for creating common notifications.
     */
    public static class Factory {
        public static GameNotificationEvent info(String message) {
            return new GameNotificationEvent(NotificationType.INFO.getTitle(), message, NotificationType.INFO);
        }

        public static GameNotificationEvent info(String title, String message) {
            return new GameNotificationEvent(title, message, NotificationType.INFO);
        }

        public static GameNotificationEvent success(String message) {
            return new GameNotificationEvent(NotificationType.SUCCESS.getTitle(), message, NotificationType.SUCCESS);
        }

        public static GameNotificationEvent success(String title, String message) {
            return new GameNotificationEvent(title, message, NotificationType.SUCCESS);
        }

        public static GameNotificationEvent warning(String message) {
            return new GameNotificationEvent(NotificationType.WARNING.getTitle(), message, NotificationType.WARNING);
        }

        public static GameNotificationEvent warning(String title, String message) {
            return new GameNotificationEvent(title, message, NotificationType.WARNING);
        }

        public static GameNotificationEvent error(String message) {
            return new GameNotificationEvent(NotificationType.ERROR.getTitle(), message, NotificationType.ERROR);
        }

        public static GameNotificationEvent error(String title, String message) {
            return new GameNotificationEvent(title, message, NotificationType.ERROR);
        }

        public static BuildingNotificationEvent buildingCompleted(String buildingName) {
            return new BuildingNotificationEvent(
                    buildingName,
                    "Construction completed",
                    NotificationType.SUCCESS);
        }
    }
}