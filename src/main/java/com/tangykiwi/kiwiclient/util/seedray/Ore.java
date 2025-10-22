package com.tangykiwi.kiwiclient.util.seedray;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.mixin.CountPlacementModifierAccessor;
import com.tangykiwi.kiwiclient.mixin.HeightRangePlacementModifierAccessor;
import com.tangykiwi.kiwiclient.mixin.RarityFilterPlacementModifierAccessor;
import com.tangykiwi.kiwiclient.module.render.SeedRay;

import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.OrePlacedFeatures;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.ScatteredOreFeature;
import net.minecraft.world.gen.feature.util.PlacedFeatureIndexer;
import net.minecraft.world.gen.heightprovider.HeightProvider;
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.HeightRangePlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.world.gen.placementmodifier.RarityFilterPlacementModifier;

public class Ore {
    public static Map<RegistryKey<Biome>, List<Ore>> getRegistry(Dimension dimension) {
        RegistryWrapper.WrapperLookup registry = BuiltinRegistries.createWrapperLookup();
        RegistryWrapper.Impl<PlacedFeature> features = registry.getOrThrow(RegistryKeys.PLACED_FEATURE);
        var reg = registry.getOrThrow(RegistryKeys.WORLD_PRESET).getOrThrow(WorldPresets.DEFAULT).value().createDimensionsRegistryHolder().dimensions();

        var dim = switch (dimension) {
            case Overworld -> reg.get(DimensionOptions.OVERWORLD);
            case Nether -> reg.get(DimensionOptions.NETHER);
            case End -> reg.get(DimensionOptions.END);
        };

        var biomes = dim.chunkGenerator().getBiomeSource().getBiomes();
        var biomes1 = biomes.stream().toList();

        List<PlacedFeatureIndexer.IndexedFeatures> indexer = PlacedFeatureIndexer.collectIndexedFeatures(
                biomes1, biomeEntry -> biomeEntry.value().getGenerationSettings().getFeatures(), true
        );


        SeedRay seedRay = (SeedRay) KiwiClient.moduleManager.getModule(SeedRay.class);

        Map<PlacedFeature, Ore> featureToOre = new HashMap<>();
        registerOre(featureToOre, indexer, features, OrePlacedFeatures.ORE_COAL_LOWER, 6, seedRay.getSetting(0).asToggle().getValue(), new Color(47, 44, 54));
        registerOre(featureToOre, indexer, features, OrePlacedFeatures.ORE_COAL_UPPER, 6, seedRay.getSetting(0).asToggle().getValue(), new Color(47, 44, 54));
        registerOre(featureToOre, indexer, features, OrePlacedFeatures.ORE_IRON_MIDDLE, 6, seedRay.getSetting(1).asToggle().getValue(), new Color(236, 173, 119));
        registerOre(featureToOre, indexer, features, OrePlacedFeatures.ORE_IRON_SMALL, 6, seedRay.getSetting(1).asToggle().getValue(), new Color(236, 173, 119));
        registerOre(featureToOre, indexer, features, OrePlacedFeatures.ORE_IRON_UPPER, 6, seedRay.getSetting(1).asToggle().getValue(), new Color(236, 173, 119));
        registerOre(featureToOre, indexer, features, OrePlacedFeatures.ORE_GOLD, 6, seedRay.getSetting(2).asToggle().getValue(), new Color(247, 229, 30));
        registerOre(featureToOre, indexer, features, OrePlacedFeatures.ORE_GOLD_LOWER, 6, seedRay.getSetting(2).asToggle().getValue(), new Color(247, 229, 30));
        registerOre(featureToOre, indexer, features, OrePlacedFeatures.ORE_GOLD_EXTRA, 6, seedRay.getSetting(2).asToggle().getValue(), new Color(247, 229, 30));
        registerOre(featureToOre, indexer, features, OrePlacedFeatures.ORE_GOLD_NETHER, 7, seedRay.getSetting(2).asToggle().getValue(), new Color(247, 229, 30));
        registerOre(featureToOre, indexer, features, OrePlacedFeatures.ORE_GOLD_DELTAS, 7, seedRay.getSetting(2).asToggle().getValue(), new Color(247, 229, 30));
        registerOre(featureToOre, indexer, features, OrePlacedFeatures.ORE_REDSTONE, 6, seedRay.getSetting(3).asToggle().getValue(), new Color(245, 7, 23));
        registerOre(featureToOre, indexer, features, OrePlacedFeatures.ORE_REDSTONE_LOWER, 6, seedRay.getSetting(3).asToggle().getValue(), new Color(245, 7, 23));
        registerOre(featureToOre, indexer, features, OrePlacedFeatures.ORE_DIAMOND, 6, seedRay.getSetting(4).asToggle().getValue(), new Color(33, 244, 255));
        registerOre(featureToOre, indexer, features, OrePlacedFeatures.ORE_DIAMOND_BURIED, 6, seedRay.getSetting(4).asToggle().getValue(), new Color(33, 244, 255));
        registerOre(featureToOre, indexer, features, OrePlacedFeatures.ORE_DIAMOND_LARGE, 6, seedRay.getSetting(4).asToggle().getValue(), new Color(33, 244, 255));
        registerOre(featureToOre, indexer, features, OrePlacedFeatures.ORE_DIAMOND_MEDIUM, 6, seedRay.getSetting(4).asToggle().getValue(), new Color(33, 244, 255));
        registerOre(featureToOre, indexer, features, OrePlacedFeatures.ORE_LAPIS, 6, seedRay.getSetting(5).asToggle().getValue(), new Color(8, 26, 189));
        registerOre(featureToOre, indexer, features, OrePlacedFeatures.ORE_LAPIS_BURIED, 6, seedRay.getSetting(5).asToggle().getValue(), new Color(8, 26, 189));
        registerOre(featureToOre, indexer, features, OrePlacedFeatures.ORE_EMERALD, 6, seedRay.getSetting(6).asToggle().getValue(), new Color(27, 209, 45));
        registerOre(featureToOre, indexer, features, OrePlacedFeatures.ORE_COPPER, 6, seedRay.getSetting(7).asToggle().getValue(), new Color(239, 151, 0));
        registerOre(featureToOre, indexer, features, OrePlacedFeatures.ORE_COPPER_LARGE, 6, seedRay.getSetting(7).asToggle().getValue(), new Color(239, 151, 0));
        registerOre(featureToOre, indexer, features, OrePlacedFeatures.ORE_QUARTZ_NETHER, 7, seedRay.getSetting(8).asToggle().getValue(), new Color(205, 205, 205));
        registerOre(featureToOre, indexer, features, OrePlacedFeatures.ORE_QUARTZ_DELTAS, 7, seedRay.getSetting(8).asToggle().getValue(), new Color(205, 205, 205));
        registerOre(featureToOre, indexer, features, OrePlacedFeatures.ORE_DEBRIS_SMALL, 7, seedRay.getSetting(9).asToggle().getValue(), new Color(209, 27, 245));
        registerOre(featureToOre, indexer, features, OrePlacedFeatures.ORE_ANCIENT_DEBRIS_LARGE, 7, seedRay.getSetting(9).asToggle().getValue(), new Color(209, 27, 245));


        Map<RegistryKey<Biome>, List<Ore>> biomeOreMap = new HashMap<>();

        biomes1.forEach(biome -> {
            biomeOreMap.put(biome.getKey().get(), new ArrayList<>());
            biome.value().getGenerationSettings().getFeatures().stream()
                    .flatMap(RegistryEntryList::stream)
                    .map(RegistryEntry::value)
                    .filter(featureToOre::containsKey)
                    .forEach(feature -> {
                        biomeOreMap.get(biome.getKey().get()).add(featureToOre.get(feature));
                    });
        });
        return biomeOreMap;
    }

    private static void registerOre(
            Map<PlacedFeature, Ore> map,
            List<PlacedFeatureIndexer.IndexedFeatures> indexer,
            RegistryWrapper.Impl<PlacedFeature> oreRegistry,
            RegistryKey<PlacedFeature> oreKey,
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
    public IntProvider count = ConstantIntProvider.create(1);
    public HeightProvider heightProvider;
    public HeightContext heightContext;
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
        int bottom = MinecraftClient.getInstance().world.getBottomY();
        int height = MinecraftClient.getInstance().world.getDimension().logicalHeight();
        this.heightContext = new HeightContext(null, HeightLimitView.create(bottom, height));

        for (PlacementModifier modifier : feature.placementModifiers()) {
            if (modifier instanceof CountPlacementModifier) {
                this.count = ((CountPlacementModifierAccessor) modifier).getCount();

            } else if (modifier instanceof HeightRangePlacementModifier) {
                this.heightProvider = ((HeightRangePlacementModifierAccessor) modifier).getHeight();

            } else if (modifier instanceof RarityFilterPlacementModifier) {
                this.rarity = ((RarityFilterPlacementModifierAccessor) modifier).getChance();
            }
        }

        FeatureConfig featureConfig = feature.feature().value().config();

        if (featureConfig instanceof OreFeatureConfig oreFeatureConfig) {
            this.discardOnAirChance = oreFeatureConfig.discardOnAirChance;
            this.size = oreFeatureConfig.size;
        } else {
            throw new IllegalStateException("config for " + feature + "is not OreFeatureConfig.class");
        }

        if (feature.feature().value().feature() instanceof ScatteredOreFeature) {
            this.scattered = true;
        }
    }
}
