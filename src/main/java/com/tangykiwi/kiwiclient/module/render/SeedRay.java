package com.tangykiwi.kiwiclient.module.render;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.command.Command;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.event.WorldRenderEvent;
import com.tangykiwi.kiwiclient.module.Category;
import com.tangykiwi.kiwiclient.module.Module;
import com.tangykiwi.kiwiclient.module.setting.SliderSetting;
import com.tangykiwi.kiwiclient.module.setting.ToggleSetting;
import com.tangykiwi.kiwiclient.util.render.RenderUtils;
import com.tangykiwi.kiwiclient.util.seedray.Dimension;
import com.tangykiwi.kiwiclient.util.seedray.Ore;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;

public class SeedRay extends Module{
    private final Map<Long, Map<Ore, Set<Vec3d>>> chunkRenderers = new ConcurrentHashMap<>();
    public Long worldSeed = null;
    private Map<RegistryKey<Biome>, List<Ore>> oreConfig;
    private ChunkPos prevOffset = new ChunkPos(0, 0);

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
    public void onWorldRender(WorldRenderEvent.Post event) {
        if (mc.player == null) return;

        if (worldSeed != null) {
            int chunkX = mc.player.getChunkPos().x;
            int chunkZ = mc.player.getChunkPos().z;

            int rangeVal = getSetting(10).asSlider().getValueInt();
            for (int range = 0; range <= rangeVal; range++) {
                for (int x = -range + chunkX; x <= range + chunkX; x++) {
                    renderChunk(x, chunkZ + range - rangeVal);
                }
                for (int x = (-range) + 1 + chunkX; x < range + chunkX; x++) {
                    renderChunk(x, chunkZ - range + rangeVal + 1);
                }
            }
        }
    }

    private void renderChunk(int x, int z) {
        long chunkKey = ChunkPos.toLong(x,z);

        if (chunkRenderers.containsKey(chunkKey)) {
            Map<Ore, Set<Vec3d>> chunk = chunkRenderers.get(chunkKey);

            for (Map.Entry<Ore, Set<Vec3d>> oreRenders : chunk.entrySet()) {
                if (oreRenders.getKey().enabled) {
                    for (Vec3d pos : oreRenders.getValue()) {
                        Box box = new Box(new BlockPos(new Vec3i((int) pos.x, (int) pos.y, (int) pos.z)));
                        RenderUtils.drawBoxOutline(box, oreRenders.getKey().color.getRGB(), 1);
                    }
                }
            }
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onTick(TickEvent event) {
        if (mc.player == null || mc.world == null) {
            return;
        }
        long chunkX = mc.player.getChunkPos().x;
        long chunkZ = mc.player.getChunkPos().z;
        ClientWorld world = mc.world;
        int renderdistance = mc.options.getViewDistance().getValue();

        int chunkCounter = 5;

        loop:
        while (true) {
            for (long offsetX = prevOffset.x; offsetX <= renderdistance; offsetX++) {
                for (long offsetZ = prevOffset.z; offsetZ <= renderdistance; offsetZ++) {
                    prevOffset = new ChunkPos((int) offsetX, (int) offsetZ);
                    if (chunkCounter <= 0) {
                        break loop;
                    }
                    long chunkKey = (chunkX + offsetX) + ((chunkZ + offsetZ) << 32);

                    if (chunkRenderers.containsKey(chunkKey)) {
                        chunkRenderers.get(chunkKey).values().forEach(oreSet -> oreSet.removeIf(ore -> !world.getBlockState(new BlockPos((int) ore.x, (int) ore.y, (int) ore.z)).isOpaque()));
                    }
                    chunkCounter--;
                }
                prevOffset = new ChunkPos((int) offsetX, -renderdistance);
            }
            prevOffset = new ChunkPos(-renderdistance, -renderdistance);
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
        if (mc.world != null) {
            loadVisibleChunks();
        }
    }

    public static Dimension getDimension() {
        if (mc.world == null) return Dimension.Overworld;

        return switch (mc.world.getRegistryKey().getValue().getPath()) {
            case "the_nether" -> Dimension.Nether;
            case "the_end" -> Dimension.End;
            default -> Dimension.Overworld;
        };
    }

    private void loadVisibleChunks() {
        int renderdistance = mc.options.getViewDistance().getValue();

        if (mc.player == null) {
            return;
        }
        int playerChunkX = mc.player.getChunkPos().x;
        int playerChunkZ = mc.player.getChunkPos().z;

        for (int i = playerChunkX - renderdistance; i < playerChunkX + renderdistance; i++) {
            for (int j = playerChunkZ - renderdistance; j < playerChunkZ + renderdistance; j++) {
                doMathOnChunk(i, j);
            }
        }
    }

    public void doMathOnChunk(int chunkX, int chunkZ) {
        if (worldSeed == null || !this.isEnabled()) {
            this.disable();
            return;
        }
        long chunkKey = (long) chunkX + ((long) chunkZ << 32);

        ClientWorld world = mc.world;

        if (chunkRenderers.containsKey(chunkKey) || world == null) {
            return;
        }

        if (world.getChunkManager().getChunk(chunkX, chunkZ, ChunkStatus.FULL, false) == null) {
            return;
        }
        Chunk chunk = world.getChunkManager().getChunk(chunkX, chunkZ, ChunkStatus.FULL, false);

        Set<RegistryKey<Biome>> biomes = new HashSet<>();
        ChunkPos pos = new ChunkPos(chunkX, chunkZ);
        ChunkPos.stream(pos, 1).forEach(chunkPosx -> {
            Chunk chunkxx = world.getChunk(chunkPosx.x, chunkPosx.z, ChunkStatus.BIOMES, false);
            if (chunkxx == null) return;

            for(ChunkSection chunkSection : chunkxx.getSectionArray()) {
                chunkSection.getBiomeContainer().forEachValue(entry -> biomes.add(entry.getKey().get()));
            }
        });
        Set<Ore> oreSet = biomes.stream().flatMap(b -> getDefaultOres(b).stream()).collect(Collectors.toSet());

        chunkX = chunkX << 4;
        chunkZ = chunkZ << 4;
        ChunkRandom random = new ChunkRandom(ChunkRandom.RandomProvider.XOROSHIRO.create(0));

        HashMap<Ore, Set<Vec3d>> h = new HashMap<>();
        long populationSeed = random.setPopulationSeed(worldSeed, chunkX, chunkZ);

        for (Ore ore : oreSet) {

            HashSet<Vec3d> ores = new HashSet<>();

            random.setDecoratorSeed(populationSeed, ore.index, ore.step);

            int repeat = ore.count.get(random);

            for (int i = 0; i < repeat; i++) {

                if (ore.rarity != 1F && random.nextFloat() >= 1/ore.rarity) {
                    continue;
                }

                int x = random.nextInt(16) + chunkX;
                int z = random.nextInt(16) + chunkZ;
                int y = ore.heightProvider.get(random, ore.heightContext);
                BlockPos origin = new BlockPos(x,y,z);

                RegistryKey<Biome> biome = chunk.getBiomeForNoiseGen(x,y,z).getKey().get();

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

    private List<Ore> getDefaultOres(RegistryKey<Biome> biomeRegistryKey) {
        if (oreConfig.containsKey(biomeRegistryKey)) {
            return oreConfig.get(biomeRegistryKey);
        } else {
            return this.oreConfig.values().stream().findAny().get();
        }
    }

    // ====================================
    // Mojang code
    // ====================================

    private ArrayList<Vec3d> generateNormal(ClientWorld world, ChunkRandom random, BlockPos blockPos, int veinSize, float discardOnAir) {
        float f = random.nextFloat() * 3.1415927F;
        float g = (float) veinSize / 8.0F;
        int i = MathHelper.ceil(((float) veinSize / 16.0F * 2.0F + 1.0F) / 2.0F);
        double d = (double) blockPos.getX() + Math.sin(f) * (double) g;
        double e = (double) blockPos.getX() - Math.sin(f) * (double) g;
        double h = (double) blockPos.getZ() + Math.cos(f) * (double) g;
        double j = (double) blockPos.getZ() - Math.cos(f) * (double) g;
        double l = (blockPos.getY() + random.nextInt(3) - 2);
        double m = (blockPos.getY() + random.nextInt(3) - 2);
        int n = blockPos.getX() - MathHelper.ceil(g) - i;
        int o = blockPos.getY() - 2 - i;
        int p = blockPos.getZ() - MathHelper.ceil(g) - i;
        int q = 2 * (MathHelper.ceil(g) + i);
        int r = 2 * (2 + i);

        for (int s = n; s <= n + q; ++s) {
            for (int t = p; t <= p + q; ++t) {
                if (o <= world.getTopY(Heightmap.Type.MOTION_BLOCKING, s, t)) {
                    return this.generateVeinPart(world, random, veinSize, d, e, h, j, l, m, n, o, p, q, r, discardOnAir);
                }
            }
        }

        return new ArrayList<>();
    }

    private ArrayList<Vec3d> generateVeinPart(ClientWorld world, ChunkRandom random, int veinSize, double startX, double endX, double startZ, double endZ, double startY, double endY, int x, int y, int z, int size, int i, float discardOnAir) {

        BitSet bitSet = new BitSet(size * i * size);
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        double[] ds = new double[veinSize * 4];

        ArrayList<Vec3d> poses = new ArrayList<>();

        int n;
        double p;
        double q;
        double r;
        double s;
        for (n = 0; n < veinSize; ++n) {
            float f = (float) n / (float) veinSize;
            p = MathHelper.lerp(f, startX, endX);
            q = MathHelper.lerp(f, startY, endY);
            r = MathHelper.lerp(f, startZ, endZ);
            s = random.nextDouble() * (double) veinSize / 16.0D;
            double m = ((double) (MathHelper.sin(3.1415927F * f) + 1.0F) * s + 1.0D) / 2.0D;
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
                int ab = Math.max(MathHelper.floor(v - u), x);
                int ac = Math.max(MathHelper.floor(w - u), y);
                int ad = Math.max(MathHelper.floor(aa - u), z);
                int ae = Math.max(MathHelper.floor(v + u), ab);
                int af = Math.max(MathHelper.floor(w + u), ac);
                int ag = Math.max(MathHelper.floor(aa + u), ad);

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
                                            if (aj >= -64 && aj < 320 && (world.getBlockState(mutable).isOpaque())) {
                                                if (shouldPlace(world, mutable, discardOnAir, random)) {
                                                    poses.add(new Vec3d(ah, aj, al));
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

    private boolean shouldPlace(ClientWorld world, BlockPos orePos, float discardOnAir, ChunkRandom random) {
        if (discardOnAir == 0F || (discardOnAir != 1F && random.nextFloat() >= discardOnAir)) {
            return true;
        }

        for (Direction direction : Direction.values()) {
            if (!world.getBlockState(orePos.add(direction.getVector())).isOpaque() && discardOnAir != 1F) {
                return false;
            }
        }
        return true;
    }

    private ArrayList<Vec3d> generateHidden(ClientWorld world, ChunkRandom random, BlockPos blockPos, int size) {

        ArrayList<Vec3d> poses = new ArrayList<>();

        int i = random.nextInt(size + 1);

        for (int j = 0; j < i; ++j) {
            size = Math.min(j, 7);
            int x = this.randomCoord(random, size) + blockPos.getX();
            int y = this.randomCoord(random, size) + blockPos.getY();
            int z = this.randomCoord(random, size) + blockPos.getZ();
            if (world.getBlockState(new BlockPos(x, y, z)).isOpaque()) {
                if (shouldPlace(world, new BlockPos(x, y, z), 1F, random)) {
                    poses.add(new Vec3d(x, y, z));
                }
            }
        }

        return poses;
    }

    private int randomCoord(ChunkRandom random, int size) {
        return Math.round((random.nextFloat() - random.nextFloat()) * (float) size);
    }
}
