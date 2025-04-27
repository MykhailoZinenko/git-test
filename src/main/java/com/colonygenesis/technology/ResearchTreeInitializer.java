package com.colonygenesis.technology;

import com.colonygenesis.building.BuildingType;
import com.colonygenesis.resource.ResourceType;
import com.colonygenesis.technology.effects.*;

/**
 * Initializes the research tree with all technologies and their effects.
 */
public class ResearchTreeInitializer {

    public static void populate(TechnologyTree tree) {
        // Survival Branch - Tier 1
        Technology hydroponics = new Technology("hydroponics", "Hydroponics Basics",
                "Unlocks Basic Hydroponics Farm", TechBranch.SURVIVAL, 1, 100, 100);
        hydroponics.addResourceCost(ResourceType.RESEARCH, 100);
        hydroponics.addResourceCost(ResourceType.MATERIALS, 50);
        hydroponics.addEffect(new BuildingUnlockEffect("hydroponics_farm", "Basic Hydroponics Farm", BuildingType.PRODUCTION));
        tree.addTechnology(hydroponics);

        Technology waterRecycling = new Technology("water_recycling", "Water Recycling",
                "-20% water consumption for all buildings", TechBranch.SURVIVAL, 1, 100, 220);
        waterRecycling.addResourceCost(ResourceType.RESEARCH, 150);
        waterRecycling.addResourceCost(ResourceType.MATERIALS, 75);
        waterRecycling.addEffect(new ConsumptionModifierEffect(ResourceType.WATER, 0.8, null));
        tree.addTechnology(waterRecycling);

        Technology basicMedicine = new Technology("basic_medicine", "Basic Medicine",
                "+10% population growth rate", TechBranch.SURVIVAL, 1, 100, 340);
        basicMedicine.addResourceCost(ResourceType.RESEARCH, 120);
        basicMedicine.addResourceCost(ResourceType.MATERIALS, 40);
        basicMedicine.addResourceCost(ResourceType.ENERGY, 20);
        basicMedicine.addEffect(new PopulationGrowthModifierEffect(1.1));
        tree.addTechnology(basicMedicine);

        // Survival Branch - Tier 2
        Technology advancedHydroponics = new Technology("advanced_hydroponics", "Advanced Hydroponics",
                "+30% food production from farms", TechBranch.SURVIVAL, 2, 300, 100);
        advancedHydroponics.addResourceCost(ResourceType.RESEARCH, 350);
        advancedHydroponics.addResourceCost(ResourceType.MATERIALS, 100);
        advancedHydroponics.addResourceCost(ResourceType.ENERGY, 50);
        advancedHydroponics.addPrerequisite("hydroponics");
        advancedHydroponics.addEffect(new ProductionModifierEffect(ResourceType.FOOD, 1.3, BuildingType.PRODUCTION));
        tree.addTechnology(advancedHydroponics);

        Technology atmosphericProcessing = new Technology("atmospheric_processing", "Atmospheric Processing",
                "Unlocks Atmosphere Processor building", TechBranch.SURVIVAL, 2, 300, 220);
        atmosphericProcessing.addResourceCost(ResourceType.RESEARCH, 400);
        atmosphericProcessing.addResourceCost(ResourceType.MATERIALS, 150);
        atmosphericProcessing.addResourceCost(ResourceType.ENERGY, 75);
        atmosphericProcessing.addPrerequisite("water_recycling");
        atmosphericProcessing.addEffect(new BuildingUnlockEffect("atmosphere_processor", "Atmosphere Processor", BuildingType.PRODUCTION));
        tree.addTechnology(atmosphericProcessing);

        Technology colonyHealth = new Technology("colony_health", "Colony Health Systems",
                "+20% population growth", TechBranch.SURVIVAL, 2, 300, 340);
        colonyHealth.addResourceCost(ResourceType.RESEARCH, 300);
        colonyHealth.addResourceCost(ResourceType.MATERIALS, 100);
        colonyHealth.addResourceCost(ResourceType.ENERGY, 50);
        colonyHealth.addPrerequisite("basic_medicine");
        colonyHealth.addEffect(new PopulationGrowthModifierEffect(1.2));
        tree.addTechnology(colonyHealth);

        // Survival Branch - Tier 3
        Technology verticalFarming = new Technology("vertical_farming", "Vertical Farming",
                "Unlocks Vertical Farm (3x food production)", TechBranch.SURVIVAL, 3, 500, 100);
        verticalFarming.addResourceCost(ResourceType.RESEARCH, 800);
        verticalFarming.addResourceCost(ResourceType.MATERIALS, 200);
        verticalFarming.addResourceCost(ResourceType.ENERGY, 100);
        verticalFarming.addPrerequisite("advanced_hydroponics");
        verticalFarming.addEffect(new BuildingUnlockEffect("vertical_farm", "Vertical Farm", BuildingType.PRODUCTION));
        tree.addTechnology(verticalFarming);

        Technology lifeSupport = new Technology("life_support", "Life Support Optimization",
                "-30% resource consumption for habitation buildings", TechBranch.SURVIVAL, 3, 500, 280);
        lifeSupport.addResourceCost(ResourceType.RESEARCH, 750);
        lifeSupport.addResourceCost(ResourceType.MATERIALS, 250);
        lifeSupport.addResourceCost(ResourceType.ENERGY, 150);
        lifeSupport.addPrerequisite("atmospheric_processing");
        lifeSupport.addPrerequisite("colony_health");
        lifeSupport.addEffect(new ConsumptionModifierEffect(ResourceType.WATER, 0.7, BuildingType.HABITATION));
        lifeSupport.addEffect(new ConsumptionModifierEffect(ResourceType.ENERGY, 0.7, BuildingType.HABITATION));
        lifeSupport.addEffect(new ConsumptionModifierEffect(ResourceType.FOOD, 0.7, BuildingType.HABITATION));
        tree.addTechnology(lifeSupport);

        // Survival Branch - Tier 4
        Technology advancedLifeSupport = new Technology("advanced_life_support", "Advanced Life Support",
                "Greatly reduces resource consumption", TechBranch.SURVIVAL, 4, 700, 190);
        advancedLifeSupport.addResourceCost(ResourceType.RESEARCH, 2000);
        advancedLifeSupport.addResourceCost(ResourceType.MATERIALS, 500);
        advancedLifeSupport.addResourceCost(ResourceType.ENERGY, 300);
        advancedLifeSupport.addResourceCost(ResourceType.RARE_MINERALS, 100);
        advancedLifeSupport.addPrerequisite("life_support");
        advancedLifeSupport.addPrerequisite("vertical_farming");
        advancedLifeSupport.addEffect(new ConsumptionModifierEffect(ResourceType.WATER, 0.5, null));
        advancedLifeSupport.addEffect(new ConsumptionModifierEffect(ResourceType.ENERGY, 0.5, null));
        tree.addTechnology(advancedLifeSupport);

        // Industry Branch - Tier 1
        Technology improvedMining = new Technology("improved_mining", "Improved Mining",
                "+25% materials production from mines", TechBranch.INDUSTRY, 1, 100, 460);
        improvedMining.addResourceCost(ResourceType.RESEARCH, 120);
        improvedMining.addResourceCost(ResourceType.MATERIALS, 60);
        improvedMining.addEffect(new ProductionModifierEffect(ResourceType.MATERIALS, 1.25, BuildingType.PRODUCTION));
        tree.addTechnology(improvedMining);

        Technology basicAutomation = new Technology("basic_automation", "Basic Automation",
                "-1 worker requirement for basic production buildings", TechBranch.INDUSTRY, 1, 100, 580);
        basicAutomation.addResourceCost(ResourceType.RESEARCH, 150);
        basicAutomation.addResourceCost(ResourceType.MATERIALS, 80);
        basicAutomation.addResourceCost(ResourceType.ENERGY, 40);
        basicAutomation.addEffect(new WorkerModifierEffect(1, BuildingType.PRODUCTION));
        tree.addTechnology(basicAutomation);

        Technology energyGrid = new Technology("energy_grid", "Energy Grid",
                "+20% energy production from all sources", TechBranch.INDUSTRY, 1, 100, 700);
        energyGrid.addResourceCost(ResourceType.RESEARCH, 180);
        energyGrid.addResourceCost(ResourceType.MATERIALS, 100);
        energyGrid.addEffect(new ProductionModifierEffect(ResourceType.ENERGY, 1.2, null));
        tree.addTechnology(energyGrid);

        // Industry Branch - Tier 2
        Technology deepCoreMining = new Technology("deep_core_mining", "Deep Core Mining",
                "Unlocks Deep Mine (produces rare minerals)", TechBranch.INDUSTRY, 2, 300, 460);
        deepCoreMining.addResourceCost(ResourceType.RESEARCH, 400);
        deepCoreMining.addResourceCost(ResourceType.MATERIALS, 200);
        deepCoreMining.addResourceCost(ResourceType.ENERGY, 100);
        deepCoreMining.addPrerequisite("improved_mining");
        deepCoreMining.addEffect(new BuildingUnlockEffect("deep_mine", "Deep Mine", BuildingType.PRODUCTION));
        tree.addTechnology(deepCoreMining);

        Technology industrialRobotics = new Technology("industrial_robotics", "Industrial Robotics",
                "-2 worker requirement for advanced production buildings", TechBranch.INDUSTRY, 2, 300, 580);
        industrialRobotics.addResourceCost(ResourceType.RESEARCH, 450);
        industrialRobotics.addResourceCost(ResourceType.MATERIALS, 250);
        industrialRobotics.addResourceCost(ResourceType.ENERGY, 150);
        industrialRobotics.addPrerequisite("basic_automation");
        industrialRobotics.addEffect(new WorkerModifierEffect(2, BuildingType.PRODUCTION));
        tree.addTechnology(industrialRobotics);

        Technology fusionPower = new Technology("fusion_power", "Fusion Power",
                "Unlocks Fusion Reactor (high energy output)", TechBranch.INDUSTRY, 2, 300, 700);
        fusionPower.addResourceCost(ResourceType.RESEARCH, 500);
        fusionPower.addResourceCost(ResourceType.MATERIALS, 300);
        fusionPower.addResourceCost(ResourceType.ENERGY, 100);
        fusionPower.addResourceCost(ResourceType.RARE_MINERALS, 50);
        fusionPower.addPrerequisite("energy_grid");
        fusionPower.addEffect(new BuildingUnlockEffect("fusion_reactor", "Fusion Reactor", BuildingType.PRODUCTION));
        tree.addTechnology(fusionPower);

        // Industry Branch - Tier 3
        Technology resourceRefinement = new Technology("resource_refinement", "Resource Refinement",
                "+50% production for all basic resources", TechBranch.INDUSTRY, 3, 500, 520);
        resourceRefinement.addResourceCost(ResourceType.RESEARCH, 900);
        resourceRefinement.addResourceCost(ResourceType.MATERIALS, 400);
        resourceRefinement.addResourceCost(ResourceType.ENERGY, 200);
        resourceRefinement.addPrerequisite("deep_core_mining");
        resourceRefinement.addPrerequisite("industrial_robotics");
        resourceRefinement.addEffect(new ProductionModifierEffect(ResourceType.MATERIALS, 1.5, null));
        resourceRefinement.addEffect(new ProductionModifierEffect(ResourceType.ENERGY, 1.5, null));
        resourceRefinement.addEffect(new ProductionModifierEffect(ResourceType.FOOD, 1.5, null));
        tree.addTechnology(resourceRefinement);

        Technology orbitalManufacturing = new Technology("orbital_manufacturing", "Orbital Manufacturing",
                "Unlocks Orbital Platform (special production facility)", TechBranch.INDUSTRY, 3, 500, 640);
        orbitalManufacturing.addResourceCost(ResourceType.RESEARCH, 950);
        orbitalManufacturing.addResourceCost(ResourceType.MATERIALS, 500);
        orbitalManufacturing.addResourceCost(ResourceType.ENERGY, 300);
        orbitalManufacturing.addResourceCost(ResourceType.RARE_MINERALS, 100);
        orbitalManufacturing.addPrerequisite("industrial_robotics");
        orbitalManufacturing.addPrerequisite("fusion_power");
        orbitalManufacturing.addEffect(new BuildingUnlockEffect("orbital_platform", "Orbital Platform", BuildingType.SPECIAL));
        tree.addTechnology(orbitalManufacturing);

        // Industry Branch - Tier 4
        Technology megastructure = new Technology("megastructure", "Megastructure Engineering",
                "Unlocks Megastructure projects", TechBranch.INDUSTRY, 4, 700, 580);
        megastructure.addResourceCost(ResourceType.RESEARCH, 2500);
        megastructure.addResourceCost(ResourceType.MATERIALS, 1000);
        megastructure.addResourceCost(ResourceType.ENERGY, 500);
        megastructure.addResourceCost(ResourceType.RARE_MINERALS, 200);
        megastructure.addPrerequisite("resource_refinement");
        megastructure.addPrerequisite("orbital_manufacturing");
        megastructure.addEffect(new VictoryConditionEffect("Industrial Victory"));
        tree.addTechnology(megastructure);

        // Science Branch - Tier 1
        Technology researchMethodology = new Technology("research_methodology", "Research Methodology",
                "+20% research production", TechBranch.SCIENCE, 1, 100, 820);
        researchMethodology.addResourceCost(ResourceType.RESEARCH, 100);
        researchMethodology.addResourceCost(ResourceType.MATERIALS, 30);
        researchMethodology.addEffect(new ProductionModifierEffect(ResourceType.RESEARCH, 1.2, null));
        tree.addTechnology(researchMethodology);

        Technology materialsScience = new Technology("materials_science", "Materials Science",
                "-15% construction costs for all buildings", TechBranch.SCIENCE, 1, 100, 940);
        materialsScience.addResourceCost(ResourceType.RESEARCH, 140);
        materialsScience.addResourceCost(ResourceType.MATERIALS, 70);
        materialsScience.addEffect(new ConstructionCostModifierEffect(0.85, null));
        tree.addTechnology(materialsScience);

        Technology advancedSensors = new Technology("advanced_sensors", "Advanced Sensors",
                "+20% production for all research buildings", TechBranch.SCIENCE, 1, 100, 1060);
        advancedSensors.addResourceCost(ResourceType.RESEARCH, 160);
        advancedSensors.addResourceCost(ResourceType.MATERIALS, 80);
        advancedSensors.addResourceCost(ResourceType.ENERGY, 40);
        advancedSensors.addEffect(new ProductionModifierEffect(ResourceType.RESEARCH, 1.2, BuildingType.RESEARCH));
        tree.addTechnology(advancedSensors);

        // Science Branch - Tier 2
        Technology advancedComputing = new Technology("advanced_computing", "Advanced Computing",
                "Unlocks Quantum Lab (+50% research production)", TechBranch.SCIENCE, 2, 300, 820);
        advancedComputing.addResourceCost(ResourceType.RESEARCH, 400);
        advancedComputing.addResourceCost(ResourceType.MATERIALS, 200);
        advancedComputing.addResourceCost(ResourceType.ENERGY, 100);
        advancedComputing.addPrerequisite("research_methodology");
        advancedComputing.addEffect(new BuildingUnlockEffect("quantum_lab", "Quantum Lab", BuildingType.RESEARCH));
        tree.addTechnology(advancedComputing);

        Technology nanotechConstruction = new Technology("nanotech_construction", "Nanotech Construction",
                "-25% construction time for all buildings", TechBranch.SCIENCE, 2, 300, 940);
        nanotechConstruction.addResourceCost(ResourceType.RESEARCH, 450);
        nanotechConstruction.addResourceCost(ResourceType.MATERIALS, 250);
        nanotechConstruction.addResourceCost(ResourceType.RARE_MINERALS, 50);
        nanotechConstruction.addPrerequisite("materials_science");
        nanotechConstruction.addEffect(new ConstructionTimeModifierEffect(0.75, null));
        tree.addTechnology(nanotechConstruction);

        Technology advancedResearch = new Technology("advanced_research", "Advanced Research",
                "+30% research production from all sources", TechBranch.SCIENCE, 2, 300, 1060);
        advancedResearch.addResourceCost(ResourceType.RESEARCH, 350);
        advancedResearch.addResourceCost(ResourceType.MATERIALS, 150);
        advancedResearch.addResourceCost(ResourceType.ENERGY, 100);
        advancedResearch.addPrerequisite("advanced_sensors");
        advancedResearch.addEffect(new ProductionModifierEffect(ResourceType.RESEARCH, 1.3, null));
        tree.addTechnology(advancedResearch);

        // Science Branch - Tier 3
        Technology artificialIntelligence = new Technology("artificial_intelligence", "Artificial Intelligence",
                "Buildings operate at 50% efficiency without workers", TechBranch.SCIENCE, 3, 500, 820);
        artificialIntelligence.addResourceCost(ResourceType.RESEARCH, 1000);
        artificialIntelligence.addResourceCost(ResourceType.MATERIALS, 300);
        artificialIntelligence.addResourceCost(ResourceType.ENERGY, 200);
        artificialIntelligence.addResourceCost(ResourceType.RARE_MINERALS, 100);
        artificialIntelligence.addPrerequisite("advanced_computing");
        artificialIntelligence.addEffect(new EfficiencyModifierEffect(0.5));
        tree.addTechnology(artificialIntelligence);

        Technology alienXenobiology = new Technology("alien_xenobiology", "Alien Xenobiology",
                "Unlocks Xenobiology Lab (produces Alien Compounds)", TechBranch.SCIENCE, 3, 500, 940);
        alienXenobiology.addResourceCost(ResourceType.RESEARCH, 850);
        alienXenobiology.addResourceCost(ResourceType.MATERIALS, 400);
        alienXenobiology.addResourceCost(ResourceType.ENERGY, 200);
        alienXenobiology.addPrerequisite("advanced_computing");
        alienXenobiology.addPrerequisite("advanced_research");
        alienXenobiology.addEffect(new BuildingUnlockEffect("xenobiology_lab", "Xenobiology Lab", BuildingType.PRODUCTION));
        tree.addTechnology(alienXenobiology);

        // Science Branch - Tier 4
        Technology techSingularity = new Technology("tech_singularity", "Technological Singularity",
                "+100% research production, unlocks victory condition", TechBranch.SCIENCE, 4, 700, 880);
        techSingularity.addResourceCost(ResourceType.RESEARCH, 3000);
        techSingularity.addResourceCost(ResourceType.MATERIALS, 1000);
        techSingularity.addResourceCost(ResourceType.ENERGY, 500);
        techSingularity.addResourceCost(ResourceType.RARE_MINERALS, 300);
        techSingularity.addPrerequisite("artificial_intelligence");
        techSingularity.addPrerequisite("alien_xenobiology");
        techSingularity.addEffect(new ProductionModifierEffect(ResourceType.RESEARCH, 2.0, null));
        techSingularity.addEffect(new VictoryConditionEffect("Scientific Victory"));
        tree.addTechnology(techSingularity);

        // Adaptation Branch - Tier 1
        Technology efficientInfrastructure = new Technology("efficient_infrastructure", "Efficient Infrastructure",
                "-10% energy consumption", TechBranch.ADAPTATION, 1, 100, 1180);
        efficientInfrastructure.addResourceCost(ResourceType.RESEARCH, 130);
        efficientInfrastructure.addResourceCost(ResourceType.MATERIALS, 60);
        efficientInfrastructure.addResourceCost(ResourceType.ENERGY, 30);
        efficientInfrastructure.addEffect(new ConsumptionModifierEffect(ResourceType.ENERGY, 0.9, null));
        tree.addTechnology(efficientInfrastructure);

        Technology resourceConservation = new Technology("resource_conservation", "Resource Conservation",
                "-10% resource consumption", TechBranch.ADAPTATION, 1, 100, 1300);
        resourceConservation.addResourceCost(ResourceType.RESEARCH, 120);
        resourceConservation.addResourceCost(ResourceType.MATERIALS, 50);
        resourceConservation.addEffect(new ConsumptionModifierEffect(ResourceType.FOOD, 0.9, null));
        resourceConservation.addEffect(new ConsumptionModifierEffect(ResourceType.WATER, 0.9, null));
        tree.addTechnology(resourceConservation);

        Technology adaptedAgriculture = new Technology("adapted_agriculture", "Adapted Agriculture",
                "Farms produce +25% more food", TechBranch.ADAPTATION, 1, 100, 1420);
        adaptedAgriculture.addResourceCost(ResourceType.RESEARCH, 150);
        adaptedAgriculture.addResourceCost(ResourceType.MATERIALS, 80);
        adaptedAgriculture.addEffect(new ProductionModifierEffect(ResourceType.FOOD, 1.25, BuildingType.PRODUCTION));
        tree.addTechnology(adaptedAgriculture);

        // Adaptation Branch - Tier 2
        Technology advancedRecycling = new Technology("advanced_recycling", "Advanced Recycling",
                "-25% material consumption", TechBranch.ADAPTATION, 2, 300, 1180);
        advancedRecycling.addResourceCost(ResourceType.RESEARCH, 380);
        advancedRecycling.addResourceCost(ResourceType.MATERIALS, 200);
        advancedRecycling.addResourceCost(ResourceType.ENERGY, 100);
        advancedRecycling.addPrerequisite("efficient_infrastructure");
        advancedRecycling.addEffect(new ConsumptionModifierEffect(ResourceType.MATERIALS, 0.75, null));
        tree.addTechnology(advancedRecycling);

        Technology closedLoopSystems = new Technology("closed_loop_systems", "Closed Loop Systems",
                "-20% all resource consumption", TechBranch.ADAPTATION, 2, 300, 1300);
        closedLoopSystems.addResourceCost(ResourceType.RESEARCH, 420);
        closedLoopSystems.addResourceCost(ResourceType.MATERIALS, 250);
        closedLoopSystems.addResourceCost(ResourceType.ENERGY, 150);
        closedLoopSystems.addPrerequisite("resource_conservation");
        closedLoopSystems.addEffect(new ConsumptionModifierEffect(ResourceType.FOOD, 0.8, null));
        closedLoopSystems.addEffect(new ConsumptionModifierEffect(ResourceType.WATER, 0.8, null));
        closedLoopSystems.addEffect(new ConsumptionModifierEffect(ResourceType.ENERGY, 0.8, null));
        tree.addTechnology(closedLoopSystems);

        Technology geneticEngineering = new Technology("genetic_engineering", "Genetic Engineering",
                "+25% population growth", TechBranch.ADAPTATION, 2, 300, 1420);
        geneticEngineering.addResourceCost(ResourceType.RESEARCH, 450);
        geneticEngineering.addResourceCost(ResourceType.MATERIALS, 200);
        geneticEngineering.addResourceCost(ResourceType.ENERGY, 100);
        geneticEngineering.addResourceCost(ResourceType.ALIEN_COMPOUNDS, 50);
        geneticEngineering.addPrerequisite("adapted_agriculture");
        geneticEngineering.addEffect(new PopulationGrowthModifierEffect(1.25));
        tree.addTechnology(geneticEngineering);

        // Adaptation Branch - Tier 3
        Technology advancedBiodomes = new Technology("advanced_biodomes", "Advanced Biodomes",
                "Unlocks Biodome (efficient habitation)", TechBranch.ADAPTATION, 3, 500, 1240);
        advancedBiodomes.addResourceCost(ResourceType.RESEARCH, 800);
        advancedBiodomes.addResourceCost(ResourceType.MATERIALS, 400);
        advancedBiodomes.addResourceCost(ResourceType.ENERGY, 200);
        advancedBiodomes.addPrerequisite("advanced_recycling");
        advancedBiodomes.addPrerequisite("closed_loop_systems");
        advancedBiodomes.addEffect(new BuildingUnlockEffect("biodome", "Biodome", BuildingType.HABITATION));
        tree.addTechnology(advancedBiodomes);

        Technology symbioticSystems = new Technology("symbiotic_systems", "Symbiotic Systems",
                "Buildings produce more with less", TechBranch.ADAPTATION, 3, 500, 1360);
        symbioticSystems.addResourceCost(ResourceType.RESEARCH, 900);
        symbioticSystems.addResourceCost(ResourceType.MATERIALS, 300);
        symbioticSystems.addResourceCost(ResourceType.ENERGY, 150);
        symbioticSystems.addResourceCost(ResourceType.ALIEN_COMPOUNDS, 100);
        symbioticSystems.addPrerequisite("genetic_engineering");
        symbioticSystems.addEffect(new ProductionModifierEffect(ResourceType.FOOD, 1.3, null));
        symbioticSystems.addEffect(new ConsumptionModifierEffect(ResourceType.ENERGY, 0.7, null));
        tree.addTechnology(symbioticSystems);

        // Adaptation Branch - Tier 4
        Technology planetaryHarmony = new Technology("planetary_harmony", "Planetary Harmony",
                "Perfect balance with environment, unlocks victory condition", TechBranch.ADAPTATION, 4, 700, 1300);
        planetaryHarmony.addResourceCost(ResourceType.RESEARCH, 2200);
        planetaryHarmony.addResourceCost(ResourceType.MATERIALS, 800);
        planetaryHarmony.addResourceCost(ResourceType.ENERGY, 400);
        planetaryHarmony.addResourceCost(ResourceType.ALIEN_COMPOUNDS, 200);
        planetaryHarmony.addPrerequisite("advanced_biodomes");
        planetaryHarmony.addPrerequisite("symbiotic_systems");
        planetaryHarmony.addEffect(new VictoryConditionEffect("Harmony Victory"));
        tree.addTechnology(planetaryHarmony);

        // Cross-Branch Technologies
        Technology advancedEnergySystems = new Technology("advanced_energy_systems", "Advanced Energy Systems",
                "+30% energy production and -20% energy consumption", TechBranch.INDUSTRY, 3, 400, 760);
        advancedEnergySystems.addResourceCost(ResourceType.RESEARCH, 600);
        advancedEnergySystems.addResourceCost(ResourceType.MATERIALS, 300);
        advancedEnergySystems.addResourceCost(ResourceType.ENERGY, 150);
        advancedEnergySystems.addPrerequisite("energy_grid");
        advancedEnergySystems.addPrerequisite("research_methodology");
        advancedEnergySystems.addEffect(new ProductionModifierEffect(ResourceType.ENERGY, 1.3, null));
        advancedEnergySystems.addEffect(new ConsumptionModifierEffect(ResourceType.ENERGY, 0.8, null));
        tree.addTechnology(advancedEnergySystems);

        Technology biotechIntegration = new Technology("biotech_integration", "Biotech Integration",
                "Population is more resilient, +30% growth rate", TechBranch.SURVIVAL, 3, 400, 400);
        biotechIntegration.addResourceCost(ResourceType.RESEARCH, 700);
        biotechIntegration.addResourceCost(ResourceType.MATERIALS, 250);
        biotechIntegration.addResourceCost(ResourceType.ALIEN_COMPOUNDS, 75);
        biotechIntegration.addPrerequisite("genetic_engineering");
        biotechIntegration.addPrerequisite("colony_health");
        biotechIntegration.addEffect(new PopulationGrowthModifierEffect(1.3));
        tree.addTechnology(biotechIntegration);

        Technology integratedEcosystem = new Technology("integrated_ecosystem", "Integrated Ecosystem",
                "Self-sustaining colonies, reduced resource consumption", TechBranch.ADAPTATION, 4, 600, 1000);
        integratedEcosystem.addResourceCost(ResourceType.RESEARCH, 1200);
        integratedEcosystem.addResourceCost(ResourceType.MATERIALS, 500);
        integratedEcosystem.addResourceCost(ResourceType.ENERGY, 250);
        integratedEcosystem.addPrerequisite("vertical_farming");
        integratedEcosystem.addPrerequisite("advanced_biodomes");
        integratedEcosystem.addEffect(new ConsumptionModifierEffect(ResourceType.FOOD, 0.5, null));
        integratedEcosystem.addEffect(new ConsumptionModifierEffect(ResourceType.WATER, 0.5, null));
        tree.addTechnology(integratedEcosystem);

        Technology alienTechMastery = new Technology("alien_tech_mastery", "Alien Technology Mastery",
                "Can build alien structures, massive efficiency boost", TechBranch.SCIENCE, 4, 600, 1060);
        alienTechMastery.addResourceCost(ResourceType.RESEARCH, 1800);
        alienTechMastery.addResourceCost(ResourceType.MATERIALS, 600);
        alienTechMastery.addResourceCost(ResourceType.ALIEN_COMPOUNDS, 300);
        alienTechMastery.addPrerequisite("alien_xenobiology");
        alienTechMastery.addPrerequisite("symbiotic_systems");
        alienTechMastery.addEffect(new BuildingUnlockEffect("alien_megastructure", "Alien Megastructure", BuildingType.SPECIAL));
        alienTechMastery.addEffect(new ProductionModifierEffect(ResourceType.ALIEN_COMPOUNDS, 2.0, null));
        tree.addTechnology(alienTechMastery);
    }
}