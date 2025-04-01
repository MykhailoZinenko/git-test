package com.colonygenesis.ui.events;

import com.colonygenesis.map.Tile;

/**
 * Event classes related to tile interaction.
 */
public class TileEvents {

    /**
     * Event fired when a tile is selected on the map.
     */
    public static class TileSelectedEvent implements GameEvent {
        private final Tile tile;

        /**
         * Creates a new tile selected event.
         *
         * @param tile The selected tile
         */
        public TileSelectedEvent(Tile tile) {
            this.tile = tile;
        }

        /**
         * Gets the selected tile.
         *
         * @return The tile
         */
        public Tile getTile() {
            return tile;
        }

        @Override
        public String getName() {
            return "TileSelected";
        }
    }

    /**
     * Event fired when a colonization action is requested.
     */
    public static class ColonizeTileEvent implements GameEvent {
        private final Tile tile;

        /**
         * Creates a new colonize tile event.
         *
         * @param tile The tile to colonize
         */
        public ColonizeTileEvent(Tile tile) {
            this.tile = tile;
        }

        /**
         * Gets the tile to colonize.
         *
         * @return The tile
         */
        public Tile getTile() {
            return tile;
        }

        @Override
        public String getName() {
            return "ColonizeTile";
        }
    }

    /**
     * Event fired when the tile view needs to be updated (after colonization, etc.)
     */
    public static class TileUpdatedEvent implements GameEvent {
        private final Tile tile;

        /**
         * Creates a new tile updated event.
         *
         * @param tile The updated tile
         */
        public TileUpdatedEvent(Tile tile) {
            this.tile = tile;
        }

        /**
         * Gets the updated tile.
         *
         * @return The tile
         */
        public Tile getTile() {
            return tile;
        }

        @Override
        public String getName() {
            return "TileUpdated";
        }
    }

    /**
     * Event fired when the map view needs to be refreshed.
     */
    public static class RefreshMapEvent implements GameEvent {
        @Override
        public String getName() {
            return "RefreshMap";
        }
    }
}