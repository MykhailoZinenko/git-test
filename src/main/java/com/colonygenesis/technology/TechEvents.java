package com.colonygenesis.technology;

import com.colonygenesis.ui.events.GameEvent;

/**
 * Events related to the technology system.
 */
public class TechEvents {

    public static class TechnologyResearchedEvent implements GameEvent {
        private final Technology technology;

        public TechnologyResearchedEvent(Technology technology) {
            this.technology = technology;
        }

        public Technology getTechnology() {
            return technology;
        }

        @Override
        public String getName() {
            return "TechnologyResearched";
        }
    }

    public static class ResearchTreeUpdatedEvent implements GameEvent {
        @Override
        public String getName() {
            return "ResearchTreeUpdated";
        }
    }
}