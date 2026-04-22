package com.tangykiwi.kiwiclient.util.seedray;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.mixin.CountPlacementModifierAccessor;
import com.tangykiwi.kiwiclient.mixin.HeightRangePlacementModifierAccessor;
import com.tangykiwi.kiwiclient.mixin.RarityFilterAccessor;
import com.tangykiwi.kiwiclient.module.render.SeedRay;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.data.worldgen.placement.OrePlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.FeatureSorter;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.feature.ScatteredOreFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraft.world.level.levelgen.presets.WorldPresets;

public class Ore {
    public static Map<ResourceKey<Biome>, List<Ore>> getRegistry(Dimension dimension) {
        HolderLookup.Provider registry = VanillaRegistries.createLookup();
        HolderLookup.RegistryLookup<PlacedFeature> features = registry.lookupOrThrow(Registries.PLACED_FEATURE);
        var reg = registry.lookupOrThrow(Registries.WORLD_PRESET).getOrThrow(WorldPresets.NORMAL).value().createWorldDimensions().dimensions();

        var dim = switch (dimension) {
            case Overworld -> reg.get(LevelStem.OVERWORLD);
            case Nether -> reg.get(LevelStem.NETHER);
            case End -> reg.get(LevelStem.END);
        };

        var biomes = dim.generator().getBiomeSource().possibleBiomes();
        var biomes1 = biomes.stream().toList();

        List<FeatureSorter.StepFeatureData> indexer = FeatureSorter.buildFeaturesPerStep(
                biomes1, biomeEntry -> biomeEntry.value().getGenerationSettings().features(), true
        );


        SeedRay seedRay = (SeedRay) KiwiClient.moduleManager.getModule(SeedRay.class);

        Map<PlacedFeature, Ore> featureToOre = new HashMap<>();
        registerOre(featureToOre, indexer, features, OrePlacements.ORE_COAL_LOWER, 6, seedRay.getSetting(0).asToggle().getValue(), new Color(47, 44, 54));
        registerOre(featureToOre, indexer, features, OrePlacements.ORE_COAL_UPPER, 6, seedRay.getSetting(0).asToggle().getValue(), new Color(47, 44, 54));
        registerOre(featureToOre, indexer, features, OrePlacements.ORE_IRON_MIDDLE, 6, seedRay.getSetting(1).asToggle().getValue(), new Color(236, 173, 119));
        registerOre(featureToOre, indexer, features, OrePlacements.ORE_IRON_SMALL, 6, seedRay.getSetting(1).asToggle().getValue(), new Color(236, 173, 119));
        registerOre(featureToOre, indexer, features, OrePlacements.ORE_IRON_UPPER, 6, seedRay.getSetting(1).asToggle().getValue(), new Color(236, 173, 119));
        registerOre(featureToOre, indexer, features, OrePlacements.ORE_GOLD, 6, seedRay.getSetting(2).asToggle().getValue(), new Color(247, 229, 30));
        registerOre(featureToOre, indexer, features, OrePlacements.ORE_GOLD_LOWER, 6, seedRay.getSetting(2).asToggle().getValue(), new Color(247, 229, 30));
        registerOre(featureToOre, indexer, features, OrePlacements.ORE_GOLD_EXTRA, 6, seedRay.getSetting(2).asToggle().getValue(), new Color(247, 229, 30));
        registerOre(featureToOre, indexer, features, OrePlacements.ORE_GOLD_NETHER, 7, seedRay.getSetting(2).asToggle().getValue(), new Color(247, 229, 30));
        registerOre(featureToOre, indexer, features, OrePlacements.ORE_GOLD_DELTAS, 7, seedRay.getSetting(2).asToggle().getValue(), new Color(247, 229, 30));
        registerOre(featureToOre, indexer, features, OrePlacements.ORE_REDSTONE, 6, seedRay.getSetting(3).asToggle().getValue(), new Color(245, 7, 23));
        registerOre(featureToOre, indexer, features, OrePlacements.ORE_REDSTONE_LOWER, 6, seedRay.getSetting(3).asToggle().getValue(), new Color(245, 7, 23));
        registerOre(featureToOre, indexer, features, OrePlacements.ORE_DIAMOND, 6, seedRay.getSetting(4).asToggle().getValue(), new Color(33, 244, 255));
        registerOre(featureToOre, indexer, features, OrePlacements.ORE_DIAMOND_BURIED, 6, seedRay.getSetting(4).asToggle().getValue(), new Color(33, 244, 255));
        registerOre(featureToOre, indexer, features, OrePlacements.ORE_DIAMOND_LARGE, 6, seedRay.getSetting(4).asToggle().getValue(), new Color(33, 244, 255));
        registerOre(featureToOre, indexer, features, OrePlacements.ORE_DIAMOND_MEDIUM, 6, seedRay.getSetting(4).asToggle().getValue(), new Color(33, 244, 255));
        registerOre(featureToOre, indexer, features, OrePlacements.ORE_LAPIS, 6, seedRay.getSetting(5).asToggle().getValue(), new Color(8, 26, 189));
        registerOre(featureToOre, indexer, features, OrePlacements.ORE_LAPIS_BURIED, 6, seedRay.getSetting(5).asToggle().getValue(), new Color(8, 26, 189));
        registerOre(featureToOre, indexer, features, OrePlacements.ORE_EMERALD, 6, seedRay.getSetting(6).asToggle().getValue(), new Color(27, 209, 45));
        registerOre(featureToOre, indexer, features, OrePlacements.ORE_COPPER, 6, seedRay.getSetting(7).asToggle().getValue(), new Color(239, 151, 0));
        registerOre(featureToOre, indexer, features, OrePlacements.ORE_COPPER_LARGE, 6, seedRay.getSetting(7).asToggle().getValue(), new Color(239, 151, 0));
        registerOre(featureToOre, indexer, features, OrePlacements.ORE_QUARTZ_NETHER, 7, seedRay.getSetting(8).asToggle().getValue(), new Color(205, 205, 205));
        registerOre(featureToOre, indexer, features, OrePlacements.ORE_QUARTZ_DELTAS, 7, seedRay.getSetting(8).asToggle().getValue(), new Color(205, 205, 205));
        registerOre(featureToOre, indexer, features, OrePlacements.ORE_ANCIENT_DEBRIS_SMALL, 7, seedRay.getSetting(9).asToggle().getValue(), new Color(209, 27, 245));
        registerOre(featureToOre, indexer, features, OrePlacements.ORE_ANCIENT_DEBRIS_LARGE, 7, seedRay.getSetting(9).asToggle().getValue(), new Color(209, 27, 245));


        Map<ResourceKey<Biome>, List<Ore>> biomeOreMap = new HashMap<>();

        biomes1.forEach(biome -> {
            biomeOreMap.put(biome.unwrapKey().get(), new ArrayList<>());
            biome.value().getGenerationSettings().features().stream()
                    .flatMap(HolderSet::stream)
                    .map(Holder::value)
                    .filter(featureToOre::containsKey)
                    .forEach(feature -> {
                        biomeOreMap.get(biome.unwrapKey().get()).add(featureToOre.get(feature));
                    });
        });
        return biomeOreMap;
    }

    private static void registerOre(
            Map<PlacedFeature, Ore> map,
            List<FeatureSorter.StepFeatureData> indexer,
            HolderLookup.RegistryLookup<PlacedFeature> oreRegistry,
            ResourceKey<PlacedFeature> oreKey,
            int genStep,
            boolean enabled,
            Color color
    ) {
        var orePlacement = oreRegistry.getOrThrow(oreKey).value();

        int index = indexer.get(genStep).indexMapping().applyAsInt(orePlacement);

        Ore ore = new Ore(orePlacement, genStep, index, enabled, color);

        map.put(orePlacement, ore);
    }

    public int step;
    public int index;
    public boolean enabled;
    public IntProvider count = ConstantInt.of(1);
    public HeightProvider heightProvider;
    public WorldGenerationContext heightContext;
    public float rarity = 1;
    public float discardOnAirChance;
    public int size;
    public Color color;
    public boolean scattered;

    private Ore(PlacedFeature feature, int step, int index, boolean enabled, Color color) {
        this.step = step;
        this.index = index;
        this.enabled = enabled;
        this.color = color;
        int bottom = Minecraft.getInstance().level.getMinY();
        int height = Minecraft.getInstance().level.dimensionType().logicalHeight();
        this.heightContext = new WorldGenerationContext(null, LevelHeightAccessor.create(bottom, height));

        for (PlacementModifier modifier : feature.placement()) {
            if (modifier instanceof CountPlacement) {
                this.count = ((CountPlacementModifierAccessor) modifier).getCount();

            } else if (modifier instanceof HeightRangePlacement) {
                this.heightProvider = ((HeightRangePlacementModifierAccessor) modifier).getHeight();

            } else if (modifier instanceof RarityFilter) {
                this.rarity = ((RarityFilterAccessor) modifier).getChance();
            }
        }

        FeatureConfiguration featureConfig = feature.feature().value().config();

        if (featureConfig instanceof OreConfiguration oreFeatureConfig) {
            this.discardOnAirChance = oreFeatureConfig.discardChanceOnAirExposure;
            this.size = oreFeatureConfig.size;
        } else {
            throw new IllegalStateException("config for " + feature + "is not OreFeatureConfig.class");
        }

        if (feature.feature().value().feature() instanceof ScatteredOreFeature) {
            this.scattered = true;
        }
    }
}
