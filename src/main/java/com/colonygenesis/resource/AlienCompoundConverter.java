package com.colonygenesis.resource;

import com.colonygenesis.core.Game;
import com.colonygenesis.ui.events.EventBus;
import com.colonygenesis.ui.events.NotificationEvents;
import com.colonygenesis.util.LoggerUtil;
import com.colonygenesis.util.Result;

import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Handles conversion of alien compounds to other resources and special effects.
 */
public class AlienCompoundConverter {
    private static final Logger LOGGER = LoggerUtil.getLogger(AlienCompoundConverter.class);

    private final Game game;
    private final Map<ResourceType, Integer> conversionRates;

    public AlienCompoundConverter(Game game) {
        this.game = game;
        this.conversionRates = new EnumMap<>(ResourceType.class);

        // Initialize conversion rates
        conversionRates.put(ResourceType.ENERGY, 100);  // 1 alien compound -> 100 energy
        conversionRates.put(ResourceType.FOOD, 50);     // 1 alien compound -> 50 food
        conversionRates.put(ResourceType.WATER, 50);    // 1 alien compound -> 50 water
        conversionRates.put(ResourceType.MATERIALS, 75); // 1 alien compound -> 75 materials
        conversionRates.put(ResourceType.RESEARCH, 25);  // 1 alien compound -> 25 research
    }

    /**
     * Converts alien compounds to the specified resource.
     */
    public Result<Integer> convert(int amount, ResourceType targetResource) {
        if (!conversionRates.containsKey(targetResource)) {
            return Result.failure("Cannot convert to " + targetResource.getName());
        }

        ResourceManager resourceManager = game.getResourceManager();
        int available = resourceManager.getResource(ResourceType.ALIEN_COMPOUNDS);

        if (available < amount) {
            return Result.failure("Not enough alien compounds: need " + amount + ", have " + available);
        }

        // Remove alien compounds
        Result<Integer> removeResult = resourceManager.removeResource(ResourceType.ALIEN_COMPOUNDS, amount);
        if (removeResult.isFailure()) {
            return removeResult;
        }

        // Add target resource
        int conversionRate = conversionRates.get(targetResource);
        int output = amount * conversionRate;

        Result<Integer> addResult = resourceManager.addResource(targetResource, output);
        if (addResult.isFailure()) {
            // Try to restore alien compounds if conversion failed
            resourceManager.addResource(ResourceType.ALIEN_COMPOUNDS, amount);
            return addResult;
        }

        // Notify success
        EventBus.getInstance().publish(NotificationEvents.Factory.info(
                "Alien Compound Conversion",
                "Converted " + amount + " alien compounds to " + output + " " + targetResource.getName()
        ));

        LOGGER.info("Converted " + amount + " alien compounds to " + output + " " + targetResource.getName());
        return Result.success(output);
    }

    /**
     * Boosts research production temporarily using alien compounds.
     */
    public Result<Integer> boostResearch(int compoundsToUse) {
        ResourceManager resourceManager = game.getResourceManager();
        int available = resourceManager.getResource(ResourceType.ALIEN_COMPOUNDS);

        if (available < compoundsToUse) {
            return Result.failure("Not enough alien compounds: need " + compoundsToUse + ", have " + available);
        }

        // Remove alien compounds
        Result<Integer> removeResult = resourceManager.removeResource(ResourceType.ALIEN_COMPOUNDS, compoundsToUse);
        if (removeResult.isFailure()) {
            return removeResult;
        }

        // Apply boost (simulated by adding extra research)
        int boost = compoundsToUse * 100; // Each compound gives 100 research points
        Result<Integer> boostResult = resourceManager.addResource(ResourceType.RESEARCH, boost);

        if (boostResult.isFailure()) {
            // Restore alien compounds if boost failed
            resourceManager.addResource(ResourceType.ALIEN_COMPOUNDS, compoundsToUse);
            return boostResult;
        }

        EventBus.getInstance().publish(NotificationEvents.Factory.success(
                "Research Boost",
                "Used " + compoundsToUse + " alien compounds to boost research by " + boost + " points"
        ));

        LOGGER.info("Research boost: Used " + compoundsToUse + " compounds for " + boost + " research");
        return Result.success(boost);
    }

    /**
     * Upgrades a building using alien compounds (placeholder for future implementation).
     */
    public Result<Boolean> upgradeBuilding(String buildingId, int compoundsRequired) {
        // Placeholder for future building upgrade functionality
        return Result.failure("Building upgrades not implemented yet");
    }

    /**
     * Gets the conversion rate for a specific resource.
     */
    public int getConversionRate(ResourceType resourceType) {
        return conversionRates.getOrDefault(resourceType, 0);
    }

    /**
     * Gets all available conversion rates.
     */
    public Map<ResourceType, Integer> getAllConversionRates() {
        return new EnumMap<>(conversionRates);
    }
}