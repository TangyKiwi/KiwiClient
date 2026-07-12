package com.tangykiwi.kiwiclient.module.render;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tangykiwi.kiwiclient.command.Command;
import com.tangykiwi.kiwiclient.mixin.ClientChunkCacheAccessor;
import com.tangykiwi.kiwiclient.mixin.ClientChunkMapAccessor;
import com.tangykiwi.kiwiclient.event.LevelRenderEvent;
import com.tangykiwi.kiwiclient.module.Category;
import com.tangykiwi.kiwiclient.module.Module;
import com.tangykiwi.kiwiclient.module.setting.SliderSetting;
import com.tangykiwi.kiwiclient.module.setting.ToggleSetting;
import com.tangykiwi.kiwiclient.util.render.RenderUtils;
import com.tangykiwi.kiwiclient.util.seedray.Dimension;
import com.tangykiwi.kiwiclient.util.seedray.Ore;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class SeedRay extends Module{
    private final Map<Long, Map<Ore, Set<Vec3>>> chunkRenderers = new ConcurrentHashMap<>();
    public Long worldSeed = null;
    private Map<ResourceKey<Biome>, List<Ore>> oreConfig;

    public SeedRay() {
        super("SeedRay", "Attempts to simulate ore positions given a seed, use ,seedray to set seed", Category.RENDER,
            new ToggleSetting("Coal", "", false), // 0
            new ToggleSetting("Iron", "", false), // 1
            new ToggleSetting("Gold", "", false), // 2
            new ToggleSetting("Redstone", "", false), // 3
            new ToggleSetting("Diamond", "", false), // 4
            new ToggleSetting("Lapis", "", false), // 5
            new ToggleSetting("Emerald", "", false), // 6
            new ToggleSetting("Copper", "", false), // 7
            new ToggleSetting("Quartz", "", false), // 8
            new ToggleSetting("Debris", "", false), // 9
            new SliderSetting("Range", "Chunks to simulate", 1, 10, 5, 0)); // 10
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onWorldRender(LevelRenderEvent event) {
        if (mc.player == null) return;

        if (worldSeed != null) {
            int chunkX = mc.player.chunkPosition().x();
            int chunkZ = mc.player.chunkPosition().z();

            int rangeVal = getSetting(10).asSlider().getValueInt();
            for (int range = 0; range <= rangeVal; range++) {
                for (int x = -range + chunkX; x <= range + chunkX; x++) {
                    renderChunk(event.getSubmitNodeStorage(), x, chunkZ + range - rangeVal);
                }
                for (int x = (-range) + 1 + chunkX; x < range + chunkX; x++) {
                    renderChunk(event.getSubmitNodeStorage(), x, chunkZ - range + rangeVal + 1);
                }
            }
        }
    }

    private void renderChunk(SubmitNodeStorage submitNodeStorage, int x, int z) {
        long chunkKey = ChunkPos.pack(x, z);

        if (chunkRenderers.containsKey(chunkKey)) {
            Map<Ore, Set<Vec3>> chunk = chunkRenderers.get(chunkKey);

            for (Map.Entry<Ore, Set<Vec3>> oreRenders : chunk.entrySet()) {
                if (oreRenders.getKey().enabled) {
                    for (Vec3 pos : oreRenders.getValue()) {
                        AABB box = new AABB(new BlockPos(new Vec3i((int) pos.x, (int) pos.y, (int) pos.z)));
                        RenderUtils.drawBoxOutline(submitNodeStorage, box, oreRenders.getKey().color.getRGB(), 1);
                    }
                }
            }
        }
    }

    @Override
    public void onEnable() {
        if (worldSeed == null) {
            Command.addMessage("Please input a seed using ,seedray <seed>");
            setEnabled(false);
        }

        reload();

        super.onEnable();
    }

    @Override
    public void onDisable() {
        this.chunkRenderers.clear();
        this.oreConfig = null;

        super.onDisable();
    }

    public void reload() {
        oreConfig = Ore.getRegistry(getDimension());
        chunkRenderers.clear();
        if (mc.level != null) {
            loadVisibleChunks();
        }
    }

    public static Dimension getDimension() {
        if (mc.level == null) return Dimension.Overworld;

        return switch (mc.level.dimension().identifier().getPath()) {
            case "the_nether" -> Dimension.Nether;
            case "the_end" -> Dimension.End;
            default -> Dimension.Overworld;
        };
    }

    public static Iterable<ChunkAccess> chunks(boolean onlyWithLoadedNeighbours) {
        return () -> new ChunkIterator(onlyWithLoadedNeighbours);
    }

    private void loadVisibleChunks() {
        if (mc.player == null) {
            return;
        }

        for (ChunkAccess chunk : chunks(false)) {
            doMathOnChunk(chunk);
        }
    }

    public void doMathOnChunk(ChunkAccess chunk) {
        var chunkPos = chunk.getPos();
        long chunkKey = chunkPos.pack();

        ClientLevel world = mc.level;

        if (chunkRenderers.containsKey(chunkKey) || world == null) {
            return;
        }

        Set<ResourceKey<Biome>> biomes = new HashSet<>();
        ChunkPos.rangeClosed(chunkPos, 1).forEach(chunkPosx -> {
            ChunkAccess chunkxx = world.getChunk(chunkPosx.x(), chunkPosx.z(), ChunkStatus.BIOMES, false);
            if (chunkxx == null) return;

            for(LevelChunkSection chunkSection : chunkxx.getSections()) {
                chunkSection.getBiomes().getAll(entry -> biomes.add(entry.unwrapKey().get()));
            }
        });
        Set<Ore> oreSet = biomes.stream().flatMap(b -> getDefaultOres(b).stream()).collect(Collectors.toSet());

        int chunkX = chunkPos.x() << 4;
        int chunkZ = chunkPos.z() << 4;
        WorldgenRandom random = new WorldgenRandom(WorldgenRandom.Algorithm.XOROSHIRO.newInstance(0));

        HashMap<Ore, Set<Vec3>> h = new HashMap<>();
        long populationSeed = random.setDecorationSeed(worldSeed, chunkX, chunkZ);

        for (Ore ore : oreSet) {

            HashSet<Vec3> ores = new HashSet<>();

            random.setFeatureSeed(populationSeed, ore.index, ore.step);

            int repeat = ore.count.sample(random);

            for (int i = 0; i < repeat; i++) {

                if (ore.rarity != 1F && random.nextFloat() >= 1/ore.rarity) {
                    continue;
                }

                int x = random.nextInt(16) + chunkX;
                int z = random.nextInt(16) + chunkZ;
                int y = ore.heightProvider.sample(random, ore.heightContext);
                BlockPos origin = new BlockPos(x,y,z);

                ResourceKey<Biome> biome = chunk.getNoiseBiome(x,y,z).unwrapKey().get();

                if (!getDefaultOres(biome).contains(ore)) {
                    continue;
                }

                if (ore.scattered) {
                    ores.addAll(generateHidden(world, random, origin, ore.size));
                } else {
                    ores.addAll(generateNormal(world, random, origin, ore.size, ore.discardOnAirChance));
                }
            }
            if (!ores.isEmpty()) {
                h.put(ore, ores);
            }
        }
        chunkRenderers.put(chunkKey, h);
    }

    private List<Ore> getDefaultOres(ResourceKey<Biome> biomeRegistryKey) {
        if (oreConfig.containsKey(biomeRegistryKey)) {
            return oreConfig.get(biomeRegistryKey);
        } else {
            return this.oreConfig.values().stream().findAny().get();
        }
    }

    // ====================================
    // Mojang code
    // ====================================

    private ArrayList<Vec3> generateNormal(ClientLevel world, WorldgenRandom random, BlockPos blockPos, int veinSize, float discardOnAir) {
        float f = random.nextFloat() * 3.1415927F;
        float g = (float) veinSize / 8.0F;
        int i = Mth.ceil(((float) veinSize / 16.0F * 2.0F + 1.0F) / 2.0F);
        double d = (double) blockPos.getX() + Math.sin(f) * (double) g;
        double e = (double) blockPos.getX() - Math.sin(f) * (double) g;
        double h = (double) blockPos.getZ() + Math.cos(f) * (double) g;
        double j = (double) blockPos.getZ() - Math.cos(f) * (double) g;
        double l = (blockPos.getY() + random.nextInt(3) - 2);
        double m = (blockPos.getY() + random.nextInt(3) - 2);
        int n = blockPos.getX() - Mth.ceil(g) - i;
        int o = blockPos.getY() - 2 - i;
        int p = blockPos.getZ() - Mth.ceil(g) - i;
        int q = 2 * (Mth.ceil(g) + i);
        int r = 2 * (2 + i);

        for (int s = n; s <= n + q; ++s) {
            for (int t = p; t <= p + q; ++t) {
                if (o <= world.getHeight(Heightmap.Types.MOTION_BLOCKING, s, t)) {
                    return this.generateVeinPart(world, random, veinSize, d, e, h, j, l, m, n, o, p, q, r, discardOnAir);
                }
            }
        }

        return new ArrayList<>();
    }

    private ArrayList<Vec3> generateVeinPart(ClientLevel world, WorldgenRandom random, int veinSize, double startX, double endX, double startZ, double endZ, double startY, double endY, int x, int y, int z, int size, int i, float discardOnAir) {

        BitSet bitSet = new BitSet(size * i * size);
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        double[] ds = new double[veinSize * 4];

        ArrayList<Vec3> poses = new ArrayList<>();

        int n;
        double p;
        double q;
        double r;
        double s;
        for (n = 0; n < veinSize; ++n) {
            float f = (float) n / (float) veinSize;
            p = Mth.lerp(f, startX, endX);
            q = Mth.lerp(f, startY, endY);
            r = Mth.lerp(f, startZ, endZ);
            s = random.nextDouble() * (double) veinSize / 16.0D;
            double m = ((double) (Mth.sin(3.1415927F * f) + 1.0F) * s + 1.0D) / 2.0D;
            ds[n * 4] = p;
            ds[n * 4 + 1] = q;
            ds[n * 4 + 2] = r;
            ds[n * 4 + 3] = m;
        }

        for (n = 0; n < veinSize - 1; ++n) {
            if (!(ds[n * 4 + 3] <= 0.0D)) {
                for (int o = n + 1; o < veinSize; ++o) {
                    if (!(ds[o * 4 + 3] <= 0.0D)) {
                        p = ds[n * 4] - ds[o * 4];
                        q = ds[n * 4 + 1] - ds[o * 4 + 1];
                        r = ds[n * 4 + 2] - ds[o * 4 + 2];
                        s = ds[n * 4 + 3] - ds[o * 4 + 3];
                        if (s * s > p * p + q * q + r * r) {
                            if (s > 0.0D) {
                                ds[o * 4 + 3] = -1.0D;
                            } else {
                                ds[n * 4 + 3] = -1.0D;
                            }
                        }
                    }
                }
            }
        }

        for (n = 0; n < veinSize; ++n) {
            double u = ds[n * 4 + 3];
            if (!(u < 0.0D)) {
                double v = ds[n * 4];
                double w = ds[n * 4 + 1];
                double aa = ds[n * 4 + 2];
                int ab = Math.max(Mth.floor(v - u), x);
                int ac = Math.max(Mth.floor(w - u), y);
                int ad = Math.max(Mth.floor(aa - u), z);
                int ae = Math.max(Mth.floor(v + u), ab);
                int af = Math.max(Mth.floor(w + u), ac);
                int ag = Math.max(Mth.floor(aa + u), ad);

                for (int ah = ab; ah <= ae; ++ah) {
                    double ai = ((double) ah + 0.5D - v) / u;
                    if (ai * ai < 1.0D) {
                        for (int aj = ac; aj <= af; ++aj) {
                            double ak = ((double) aj + 0.5D - w) / u;
                            if (ai * ai + ak * ak < 1.0D) {
                                for (int al = ad; al <= ag; ++al) {
                                    double am = ((double) al + 0.5D - aa) / u;
                                    if (ai * ai + ak * ak + am * am < 1.0D) {
                                        int an = ah - x + (aj - y) * size + (al - z) * size * i;
                                        if (!bitSet.get(an)) {
                                            bitSet.set(an);
                                            mutable.set(ah, aj, al);
                                            if (aj >= -64 && aj < 320 && (world.getBlockState(mutable).canOcclude())) {
                                                if (shouldPlace(world, mutable, discardOnAir, random)) {
                                                    poses.add(new Vec3(ah, aj, al));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return poses;
    }

    private boolean shouldPlace(ClientLevel world, BlockPos orePos, float discardOnAir, WorldgenRandom random) {
        if (discardOnAir == 0F || (discardOnAir != 1F && random.nextFloat() >= discardOnAir)) {
            return true;
        }

        for (Direction direction : Direction.values()) {
            if (!world.getBlockState(orePos.offset(direction.getUnitVec3i())).canOcclude() && discardOnAir != 1F) {
                return false;
            }
        }
        return true;
    }

    private ArrayList<Vec3> generateHidden(ClientLevel world, WorldgenRandom random, BlockPos blockPos, int size) {

        ArrayList<Vec3> poses = new ArrayList<>();

        int i = random.nextInt(size + 1);

        for (int j = 0; j < i; ++j) {
            size = Math.min(j, 7);
            int x = this.randomCoord(random, size) + blockPos.getX();
            int y = this.randomCoord(random, size) + blockPos.getY();
            int z = this.randomCoord(random, size) + blockPos.getZ();
            if (world.getBlockState(new BlockPos(x, y, z)).canOcclude()) {
                if (shouldPlace(world, new BlockPos(x, y, z), 1F, random)) {
                    poses.add(new Vec3(x, y, z));
                }
            }
        }

        return poses;
    }

    private int randomCoord(WorldgenRandom random, int size) {
        return Math.round((random.nextFloat() - random.nextFloat()) * (float) size);
    }
}

class ChunkIterator implements Iterator<ChunkAccess> {
    private final ClientChunkMapAccessor map = (ClientChunkMapAccessor) (Object) ((ClientChunkCacheAccessor) mc.level.getChunkSource()).getStorage();
    private final boolean onlyWithLoadedNeighbours;

    private int i = 0;
    private ChunkAccess chunk;

    public ChunkIterator(boolean onlyWithLoadedNeighbours) {
        this.onlyWithLoadedNeighbours = onlyWithLoadedNeighbours;

        getNext();
    }

    private ChunkAccess getNext() {
        ChunkAccess prev = chunk;
        chunk = null;

        while (i < map.getChunks().length()) {
            chunk = map.getChunks().get(i++);
            if (chunk != null && (!onlyWithLoadedNeighbours || isInRadius(chunk))) break;
        }

        return prev;
    }

    private boolean isInRadius(ChunkAccess chunk) {
        int x = chunk.getPos().x();
        int z = chunk.getPos().z();

        return mc.level.getChunkSource().hasChunk(x + 1, z) && mc.level.getChunkSource().hasChunk(x - 1, z) && mc.level.getChunkSource().hasChunk(x, z + 1) && mc.level.getChunkSource().hasChunk(x, z - 1);
    }

    @Override
    public boolean hasNext() {
        return chunk != null;
    }

    @Override
    public ChunkAccess next() {
        return getNext();
    }
}
