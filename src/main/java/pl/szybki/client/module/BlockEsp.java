package pl.szybki.client.module;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import pl.szybki.client.Category;
import pl.szybki.client.Module;
import pl.szybki.client.util.Draw;
import pl.szybki.client.util.Projection;

import java.util.ArrayList;
import java.util.List;

/** Podswietla cenne bloki (rudy, skrzynie, spawnery) przez sciany - ramki rzutowane w 2D. */
public class BlockEsp extends Module {
    private static final class Hit {
        final int x, y, z, color;

        Hit(int x, int y, int z, int color) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.color = color;
        }
    }

    private static final int[][] EDGES = {
            {0, 0, 0, 1, 0, 0}, {0, 0, 1, 1, 0, 1}, {0, 1, 0, 1, 1, 0}, {0, 1, 1, 1, 1, 1},
            {0, 0, 0, 0, 1, 0}, {1, 0, 0, 1, 1, 0}, {0, 0, 1, 0, 1, 1}, {1, 0, 1, 1, 1, 1},
            {0, 0, 0, 0, 0, 1}, {1, 0, 0, 1, 0, 1}, {0, 1, 0, 0, 1, 1}, {1, 1, 0, 1, 1, 1}
    };

    private volatile List<Hit> hits = new ArrayList<>();
    private int timer = 0;

    public BlockEsp() {
        super("blockesp", Category.RADAR);
        num("range", 20, 8, 40, 4);
        flag("ores", true);
        flag("chests", true);
        flag("spawners", true);
    }

    @Override
    public void onTick(MinecraftClient mc) {
        if (!enabled || mc.world == null || mc.player == null) {
            if (!hits.isEmpty()) hits = new ArrayList<>();
            return;
        }
        if (timer-- > 0) return;
        timer = 15;
        scan(mc);
    }

    private void scan(MinecraftClient mc) {
        int r = (int) d("range");
        boolean ores = b("ores"), chests = b("chests"), spawners = b("spawners");
        BlockPos c = mc.player.getBlockPos();
        int minY = Math.max(mc.world.getBottomY(), c.getY() - r);
        int maxY = Math.min(mc.world.getBottomY() + mc.world.getHeight() - 1, c.getY() + r);
        List<Hit> found = new ArrayList<>();
        BlockPos.Mutable pos = new BlockPos.Mutable();
        for (int x = c.getX() - r; x <= c.getX() + r; x++) {
            for (int z = c.getZ() - r; z <= c.getZ() + r; z++) {
                for (int y = minY; y <= maxY; y++) {
                    pos.set(x, y, z);
                    BlockState st = mc.world.getBlockState(pos);
                    if (st.isAir()) continue;
                    int col = color(st, ores, chests, spawners);
                    if (col != 0) {
                        found.add(new Hit(x, y, z, col));
                        if (found.size() > 600) {
                            hits = found;
                            return;
                        }
                    }
                }
            }
        }
        hits = found;
    }

    private int color(BlockState st, boolean ores, boolean chests, boolean spawners) {
        Block b = st.getBlock();
        if (ores) {
            if (st.isIn(BlockTags.DIAMOND_ORES)) return 0xFF33E6FF;
            if (st.isIn(BlockTags.EMERALD_ORES)) return 0xFF2ECC71;
            if (st.isIn(BlockTags.GOLD_ORES)) return 0xFFFFD43B;
            if (b == Blocks.ANCIENT_DEBRIS) return 0xFFB5651D;
        }
        if (chests) {
            if (b == Blocks.CHEST || b == Blocks.TRAPPED_CHEST || b == Blocks.BARREL) return 0xFFFF9F1C;
            if (b == Blocks.ENDER_CHEST) return 0xFFA855F7;
            if (st.isIn(BlockTags.SHULKER_BOXES)) return 0xFFE879F9;
        }
        if (spawners && b == Blocks.SPAWNER) return 0xFFFF3B3B;
        return 0;
    }

    @Override
    public void onHud(DrawContext c, float td) {
        double[] o = new double[4];
        for (Hit h : hits) {
            double dx = h.x + 0.5 - Projection.cx, dy = h.y + 0.5 - Projection.cy, dz = h.z + 0.5 - Projection.cz;
            if (dx * dx + dy * dy + dz * dz > 4096) continue;
            for (int[] e : EDGES) {
                if (Projection.line(h.x + e[0], h.y + e[1], h.z + e[2], h.x + e[3], h.y + e[4], h.z + e[5], o)) {
                    Draw.line(c, (float) o[0], (float) o[1], (float) o[2], (float) o[3], 0.8f, h.color);
                }
            }
        }
    }
}
