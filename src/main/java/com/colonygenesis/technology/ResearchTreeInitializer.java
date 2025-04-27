package com.colonygenesis.technology;

import com.colonygenesis.resource.ResourceType;

/**
 * Initializes the research tree with all technologies.
 */
public class ResearchTreeInitializer {

    public static void populate(TechnologyTree tree) {
        // Survival Branch - Tier 1
        Technology hydroponics = new Technology("hydroponics", "Hydroponics Basics",
                "Unlocks Basic Hydroponics Farm", TechBranch.SURVIVAL, 1, 100, 100);
        hydroponics.addResourceCost(ResourceType.RESEARCH, 100);
        hydroponics.addResourceCost(ResourceType.MATERIALS, 50);
        tree.addTechnology(hydroponics);

        Technology waterRecycling = new Technology("water_recycling", "Water Recycling",
                "-20% water consumption for all buildings", TechBranch.SURVIVAL, 1, 100, 220);
        waterRecycling.addResourceCost(ResourceType.RESEARCH, 150);
        waterRecycling.addResourceCost(ResourceType.MATERIALS, 75);
        tree.addTechnology(waterRecycling);

        Technology basicMedicine = new Technology("basic_medicine", "Basic Medicine",
                "+10% population growth rate", TechBranch.SURVIVAL, 1, 100, 340);
        basicMedicine.addResourceCost(ResourceType.RESEARCH, 120);
        basicMedicine.addResourceCost(ResourceType.MATERIALS, 40);
        basicMedicine.addResourceCost(ResourceType.ENERGY, 20);
        tree.addTechnology(basicMedicine);

        // Survival Branch - Tier 2
        Technology advancedHydroponics = new Technology("advanced_hydroponics", "Advanced Hydroponics",
                "+30% food production from farms", TechBranch.SURVIVAL, 2, 300, 100);
        advancedHydroponics.addResourceCost(ResourceType.RESEARCH, 350);
        advancedHydroponics.addResourceCost(ResourceType.MATERIALS, 100);
        advancedHydroponics.addResourceCost(ResourceType.ENERGY, 50);
        advancedHydroponics.addPrerequisite("hydroponics");
        tree.addTechnology(advancedHydroponics);

        Technology atmosphericProcessing = new Technology("atmospheric_processing", "Atmospheric Processing",
                "Unlocks Atmosphere Processor building", TechBranch.SURVIVAL, 2, 300, 220);
        atmosphericProcessing.addResourceCost(ResourceType.RESEARCH, 400);
        atmosphericProcessing.addResourceCost(ResourceType.MATERIALS, 150);
        atmosphericProcessing.addResourceCost(ResourceType.ENERGY, 75);
        atmosphericProcessing.addPrerequisite("water_recycling");
        tree.addTechnology(atmosphericProcessing);

        Technology colonyHealth = new Technology("colony_health", "Colony Health Systems",
                "+20% population growth, +1 max workers per habitation", TechBranch.SURVIVAL, 2, 300, 340);
        colonyHealth.addResourceCost(ResourceType.RESEARCH, 300);
        colonyHealth.addResourceCost(ResourceType.MATERIALS, 100);
        colonyHealth.addResourceCost(ResourceType.ENERGY, 50);
        colonyHealth.addPrerequisite("basic_medicine");
        tree.addTechnology(colonyHealth);

        // Survival Branch - Tier 3
        Technology verticalFarming = new Technology("vertical_farming", "Vertical Farming",
                "Unlocks Vertical Farm (3x food production)", TechBranch.SURVIVAL, 3, 500, 100);
        verticalFarming.addResourceCost(ResourceType.RESEARCH, 800);
        verticalFarming.addResourceCost(ResourceType.MATERIALS, 200);
        verticalFarming.addResourceCost(ResourceType.ENERGY, 100);
        verticalFarming.addPrerequisite("advanced_hydroponics");
        tree.addTechnology(verticalFarming);

        Technology lifeSupport = new Technology("life_support", "Life Support Optimization",
                "-30% resource consumption for habitation buildings", TechBranch.SURVIVAL, 3, 500, 280);
        lifeSupport.addResourceCost(ResourceType.RESEARCH, 750);
        lifeSupport.addResourceCost(ResourceType.MATERIALS, 250);
        lifeSupport.addResourceCost(ResourceType.ENERGY, 150);
        lifeSupport.addPrerequisite("atmospheric_processing");
        lifeSupport.addPrerequisite("colony_health");
        tree.addTechnology(lifeSupport);

        // Survival Branch - Tier 4
        Technology terraforming = new Technology("terraforming", "Terraforming Basics",
                "Unlocks Terraforming Station, allows converting harsh terrain", TechBranch.SURVIVAL, 4, 700, 190);
        terraforming.addResourceCost(ResourceType.RESEARCH, 2000);
        terraforming.addResourceCost(ResourceType.MATERIALS, 500);
        terraforming.addResourceCost(ResourceType.ENERGY, 300);
        terraforming.addResourceCost(ResourceType.RARE_MINERALS, 100);
        terraforming.addPrerequisite("life_support");
        terraforming.addPrerequisite("vertical_farming");
        tree.addTechnology(terraforming);

        // Industry Branch - Tier 1
        Technology improvedMining = new Technology("improved_mining", "Improved Mining",
                "+25% materials production from mines", TechBranch.INDUSTRY, 1, 100, 460);
        improvedMining.addResourceCost(ResourceType.RESEARCH, 120);
        improvedMining.addResourceCost(ResourceType.MATERIALS, 60);
        tree.addTechnology(improvedMining);

        Technology basicAutomation = new Technology("basic_automation", "Basic Automation",
                "-1 worker requirement for basic production buildings", TechBranch.INDUSTRY, 1, 100, 580);
        basicAutomation.addResourceCost(ResourceType.RESEARCH, 150);
        basicAutomation.addResourceCost(ResourceType.MATERIALS, 80);
        basicAutomation.addResourceCost(ResourceType.ENERGY, 40);
        tree.addTechnology(basicAutomation);

        Technology energyGrid = new Technology("energy_grid", "Energy Grid",
                "+20% energy production from all sources", TechBranch.INDUSTRY, 1, 100, 700);
        energyGrid.addResourceCost(ResourceType.RESEARCH, 180);
        energyGrid.addResourceCost(ResourceType.MATERIALS, 100);
        tree.addTechnology(energyGrid);

        // Industry Branch - Tier 2
        Technology deepCoreMining = new Technology("deep_core_mining", "Deep Core Mining",
                "Unlocks Deep Mine (produces rare minerals)", TechBranch.INDUSTRY, 2, 300, 460);
        deepCoreMining.addResourceCost(ResourceType.RESEARCH, 400);
        deepCoreMining.addResourceCost(ResourceType.MATERIALS, 200);
        deepCoreMining.addResourceCost(ResourceType.ENERGY, 100);
        deepCoreMining.addPrerequisite("improved_mining");
        tree.addTechnology(deepCoreMining);

        Technology industrialRobotics = new Technology("industrial_robotics", "Industrial Robotics",
                "-2 worker requirement for advanced production buildings", TechBranch.INDUSTRY, 2, 300, 580);
        industrialRobotics.addResourceCost(ResourceType.RESEARCH, 450);
        industrialRobotics.addResourceCost(ResourceType.MATERIALS, 250);
        industrialRobotics.addResourceCost(ResourceType.ENERGY, 150);
        industrialRobotics.addPrerequisite("basic_automation");
        tree.addTechnology(industrialRobotics);

        Technology fusionPower = new Technology("fusion_power", "Fusion Power",
                "Unlocks Fusion Reactor (high energy output)", TechBranch.INDUSTRY, 2, 300, 700);
        fusionPower.addResourceCost(ResourceType.RESEARCH, 500);
        fusionPower.addResourceCost(ResourceType.MATERIALS, 300);
        fusionPower.addResourceCost(ResourceType.ENERGY, 100);
        fusionPower.addResourceCost(ResourceType.RARE_MINERALS, 50);
        fusionPower.addPrerequisite("energy_grid");
        tree.addTechnology(fusionPower);

        // Industry Branch - Tier 3
        Technology resourceRefinement = new Technology("resource_refinement", "Resource Refinement",
                "+50% production for all basic resources", TechBranch.INDUSTRY, 3, 500, 520);
        resourceRefinement.addResourceCost(ResourceType.RESEARCH, 900);
        resourceRefinement.addResourceCost(ResourceType.MATERIALS, 400);
        resourceRefinement.addResourceCost(ResourceType.ENERGY, 200);
        resourceRefinement.addPrerequisite("deep_core_mining");
        resourceRefinement.addPrerequisite("industrial_robotics");
        tree.addTechnology(resourceRefinement);

        Technology orbitalManufacturing = new Technology("orbital_manufacturing", "Orbital Manufacturing",
                "Unlocks Orbital Platform (special production facility)", TechBranch.INDUSTRY, 3, 500, 640);
        orbitalManufacturing.addResourceCost(ResourceType.RESEARCH, 950);
        orbitalManufacturing.addResourceCost(ResourceType.MATERIALS, 500);
        orbitalManufacturing.addResourceCost(ResourceType.ENERGY, 300);
        orbitalManufacturing.addResourceCost(ResourceType.RARE_MINERALS, 100);
        orbitalManufacturing.addPrerequisite("industrial_robotics");
        orbitalManufacturing.addPrerequisite("fusion_power");
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
        tree.addTechnology(megastructure);

        // Science Branch - Tier 1
        Technology researchMethodology = new Technology("research_methodology", "Research Methodology",
                "+20% research production", TechBranch.SCIENCE, 1, 100, 820);
        researchMethodology.addResourceCost(ResourceType.RESEARCH, 100);
        researchMethodology.addResourceCost(ResourceType.MATERIALS, 30);
        tree.addTechnology(researchMethodology);

        Technology materialsScience = new Technology("materials_science", "Materials Science",
                "-15% construction costs for all buildings", TechBranch.SCIENCE, 1, 100, 940);
        materialsScience.addResourceCost(ResourceType.RESEARCH, 140);
        materialsScience.addResourceCost(ResourceType.MATERIALS, 70);
        tree.addTechnology(materialsScience);

        Technology sensorArrays = new Technology("sensor_arrays", "Sensor Arrays",
                "Reveals resources in adjacent tiles", TechBranch.SCIENCE, 1, 100, 1060);
        sensorArrays.addResourceCost(ResourceType.RESEARCH, 160);
        sensorArrays.addResourceCost(ResourceType.MATERIALS, 80);
        sensorArrays.addResourceCost(ResourceType.ENERGY, 40);
        tree.addTechnology(sensorArrays);

        // Science Branch - Tier 2
        Technology advancedComputing = new Technology("advanced_computing", "Advanced Computing",
                "Unlocks Quantum Lab (+50% research production)", TechBranch.SCIENCE, 2, 300, 820);
        advancedComputing.addResourceCost(ResourceType.RESEARCH, 400);
        advancedComputing.addResourceCost(ResourceType.MATERIALS, 200);
        advancedComputing.addResourceCost(ResourceType.ENERGY, 100);
        advancedComputing.addPrerequisite("research_methodology");
        tree.addTechnology(advancedComputing);

        Technology nanotechConstruction = new Technology("nanotech_construction", "Nanotech Construction",
                "-25% construction time for all buildings", TechBranch.SCIENCE, 2, 300, 940);
        nanotechConstruction.addResourceCost(ResourceType.RESEARCH, 450);
        nanotechConstruction.addResourceCost(ResourceType.MATERIALS, 250);
        nanotechConstruction.addResourceCost(ResourceType.RARE_MINERALS, 50);
        nanotechConstruction.addPrerequisite("materials_science");
        tree.addTechnology(nanotechConstruction);

        Technology deepSpaceScanning = new Technology("deep_space_scanning", "Deep Space Scanning",
                "Reveals entire map, detects rare resources", TechBranch.SCIENCE, 2, 300, 1060);
        deepSpaceScanning.addResourceCost(ResourceType.RESEARCH, 350);
        deepSpaceScanning.addResourceCost(ResourceType.MATERIALS, 150);
        deepSpaceScanning.addResourceCost(ResourceType.ENERGY, 100);
        deepSpaceScanning.addPrerequisite("sensor_arrays");
        tree.addTechnology(deepSpaceScanning);

        // Science Branch - Tier 3
        Technology artificialIntelligence = new Technology("artificial_intelligence", "Artificial Intelligence",
                "Buildings operate at 50% efficiency without workers", TechBranch.SCIENCE, 3, 500, 820);
        artificialIntelligence.addResourceCost(ResourceType.RESEARCH, 1000);
        artificialIntelligence.addResourceCost(ResourceType.MATERIALS, 300);
        artificialIntelligence.addResourceCost(ResourceType.ENERGY, 200);
        artificialIntelligence.addResourceCost(ResourceType.RARE_MINERALS, 100);
        artificialIntelligence.addPrerequisite("advanced_computing");
        tree.addTechnology(artificialIntelligence);

        Technology alienXenobiology = new Technology("alien_xenobiology", "Alien Xenobiology",
                "Unlocks Xenobiology Lab (produces Alien Compounds)", TechBranch.SCIENCE, 3, 500, 940);
        alienXenobiology.addResourceCost(ResourceType.RESEARCH, 850);
        alienXenobiology.addResourceCost(ResourceType.MATERIALS, 400);
        alienXenobiology.addResourceCost(ResourceType.ENERGY, 200);
        alienXenobiology.addPrerequisite("advanced_computing");
        alienXenobiology.addPrerequisite("deep_space_scanning");
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
        tree.addTechnology(techSingularity);

        // Adaptation Branch - Tier 1
        Technology environmentalSuits = new Technology("environmental_suits", "Environmental Suits",
                "Colonists can work in harsh environments", TechBranch.ADAPTATION, 1, 100, 1180);
        environmentalSuits.addResourceCost(ResourceType.RESEARCH, 130);
        environmentalSuits.addResourceCost(ResourceType.MATERIALS, 60);
        environmentalSuits.addResourceCost(ResourceType.ENERGY, 30);
        tree.addTechnology(environmentalSuits);

        Technology climateMonitoring = new Technology("climate_monitoring", "Climate Monitoring",
                "Weather events cause 50% less damage", TechBranch.ADAPTATION, 1, 100, 1300);
        climateMonitoring.addResourceCost(ResourceType.RESEARCH, 120);
        climateMonitoring.addResourceCost(ResourceType.MATERIALS, 50);
        tree.addTechnology(climateMonitoring);

        Technology adaptedAgriculture = new Technology("adapted_agriculture", "Adapted Agriculture",
                "Farms can operate in desert/tundra tiles", TechBranch.ADAPTATION, 1, 100, 1420);
        adaptedAgriculture.addResourceCost(ResourceType.RESEARCH, 150);
        adaptedAgriculture.addResourceCost(ResourceType.MATERIALS, 80);
        tree.addTechnology(adaptedAgriculture);

        // Adaptation Branch - Tier 2
        Technology radiationShielding = new Technology("radiation_shielding", "Radiation Shielding",
                "Buildings immune to radiation events", TechBranch.ADAPTATION, 2, 300, 1180);
        radiationShielding.addResourceCost(ResourceType.RESEARCH, 380);
        radiationShielding.addResourceCost(ResourceType.MATERIALS, 200);
        radiationShielding.addResourceCost(ResourceType.ENERGY, 100);
        radiationShielding.addPrerequisite("environmental_suits");
        tree.addTechnology(radiationShielding);

        Technology weatherControl = new Technology("weather_control", "Weather Control",
                "Unlocks Weather Control Station", TechBranch.ADAPTATION, 2, 300, 1300);
        weatherControl.addResourceCost(ResourceType.RESEARCH, 420);
        weatherControl.addResourceCost(ResourceType.MATERIALS, 250);
        weatherControl.addResourceCost(ResourceType.ENERGY, 150);
        weatherControl.addPrerequisite("climate_monitoring");
        tree.addTechnology(weatherControl);

        Technology geneticEngineering = new Technology("genetic_engineering", "Genetic Engineering",
                "+25% population adaptation to environment", TechBranch.ADAPTATION, 2, 300, 1420);
        geneticEngineering.addResourceCost(ResourceType.RESEARCH, 450);
        geneticEngineering.addResourceCost(ResourceType.MATERIALS, 200);
        geneticEngineering.addResourceCost(ResourceType.ENERGY, 100);
        geneticEngineering.addResourceCost(ResourceType.ALIEN_COMPOUNDS, 50);
        geneticEngineering.addPrerequisite("adapted_agriculture");
        tree.addTechnology(geneticEngineering);

        // Adaptation Branch - Tier 3
        Technology domeTechnology = new Technology("dome_technology", "Dome Technology",
                "Unlocks Biodome (creates artificial habitable zones)", TechBranch.ADAPTATION, 3, 500, 1240);
        domeTechnology.addResourceCost(ResourceType.RESEARCH, 800);
        domeTechnology.addResourceCost(ResourceType.MATERIALS, 400);
        domeTechnology.addResourceCost(ResourceType.ENERGY, 200);
        domeTechnology.addPrerequisite("radiation_shielding");
        domeTechnology.addPrerequisite("weather_control");
        tree.addTechnology(domeTechnology);

        Technology nativeSpecies = new Technology("native_species", "Native Species Integration",
                "Can harvest unique resources from alien flora/fauna", TechBranch.ADAPTATION, 3, 500, 1360);
        nativeSpecies.addResourceCost(ResourceType.RESEARCH, 900);
        nativeSpecies.addResourceCost(ResourceType.MATERIALS, 300);
        nativeSpecies.addResourceCost(ResourceType.ENERGY, 150);
        nativeSpecies.addResourceCost(ResourceType.ALIEN_COMPOUNDS, 100);
        nativeSpecies.addPrerequisite("genetic_engineering");
        tree.addTechnology(nativeSpecies);

        // Adaptation Branch - Tier 4
        Technology planetaryHarmony = new Technology("planetary_harmony", "Planetary Harmony",
                "Complete environmental control, unlocks victory condition", TechBranch.ADAPTATION, 4, 700, 1300);
        planetaryHarmony.addResourceCost(ResourceType.RESEARCH, 2200);
        planetaryHarmony.addResourceCost(ResourceType.MATERIALS, 800);
        planetaryHarmony.addResourceCost(ResourceType.ENERGY, 400);
        planetaryHarmony.addResourceCost(ResourceType.ALIEN_COMPOUNDS, 200);
        planetaryHarmony.addPrerequisite("dome_technology");
        planetaryHarmony.addPrerequisite("native_species");
        tree.addTechnology(planetaryHarmony);

        // Cross-Branch Technologies
        Technology advancedEnergySystems = new Technology("advanced_energy_systems", "Advanced Energy Systems",
                "+30% energy production and -20% energy consumption", TechBranch.INDUSTRY, 3, 400, 760);
        advancedEnergySystems.addResourceCost(ResourceType.RESEARCH, 600);
        advancedEnergySystems.addResourceCost(ResourceType.MATERIALS, 300);
        advancedEnergySystems.addResourceCost(ResourceType.ENERGY, 150);
        advancedEnergySystems.addPrerequisite("energy_grid");
        advancedEnergySystems.addPrerequisite("research_methodology");
        tree.addTechnology(advancedEnergySystems);

        Technology biotechIntegration = new Technology("biotech_integration", "Biotech Integration",
                "Population is more resilient, +30% growth rate", TechBranch.SURVIVAL, 3, 400, 400);
        biotechIntegration.addResourceCost(ResourceType.RESEARCH, 700);
        biotechIntegration.addResourceCost(ResourceType.MATERIALS, 250);
        biotechIntegration.addResourceCost(ResourceType.ALIEN_COMPOUNDS, 75);
        biotechIntegration.addPrerequisite("genetic_engineering");
        biotechIntegration.addPrerequisite("colony_health");
        tree.addTechnology(biotechIntegration);

        Technology integratedEcosystem = new Technology("integrated_ecosystem", "Integrated Ecosystem",
                "Self-sustaining colonies, reduced resource consumption", TechBranch.ADAPTATION, 4, 600, 1000);
        integratedEcosystem.addResourceCost(ResourceType.RESEARCH, 1200);
        integratedEcosystem.addResourceCost(ResourceType.MATERIALS, 500);
        integratedEcosystem.addResourceCost(ResourceType.ENERGY, 250);
        integratedEcosystem.addPrerequisite("vertical_farming");
        integratedEcosystem.addPrerequisite("dome_technology");
        tree.addTechnology(integratedEcosystem);

        Technology alienTechMastery = new Technology("alien_tech_mastery", "Alien Technology Mastery",
                "Can build alien structures, massive efficiency boost", TechBranch.SCIENCE, 4, 600, 1060);
        alienTechMastery.addResourceCost(ResourceType.RESEARCH, 1800);
        alienTechMastery.addResourceCost(ResourceType.MATERIALS, 600);
        alienTechMastery.addResourceCost(ResourceType.ALIEN_COMPOUNDS, 300);
        alienTechMastery.addPrerequisite("alien_xenobiology");
        alienTechMastery.addPrerequisite("native_species");
        tree.addTechnology(alienTechMastery);
    }
}