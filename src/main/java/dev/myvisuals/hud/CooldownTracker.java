package dev.myvisuals.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemCooldownManager;
import net.minecraft.item.Items;

import java.util.LinkedHashMap;
import java.util.Map;

public class CooldownTracker {
    private static final Item[] TRACKED = {
            Items.ENDER_PEARL, Items.TOTEM_OF_UNDYING, Items.GOLDEN_APPLE,
            Items.ENCHANTED_GOLDEN_APPLE, Items.CHORUS_FRUIT, Items.SHIELD
    };

    public static class Entry {
        public final Item item;
        float last;
        public int remainingTicks = -1;

        Entry(Item item, float last) {
            this.item = item;
            this.last = last;
        }
    }

    private final Map<Item, Entry> entries = new LinkedHashMap<>();

    public Iterable<Entry> active() {
        return entries.values();
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    public void tick(MinecraftClient mc) {
        if (mc.player == null) {
            entries.clear();
            return;
        }
        ItemCooldownManager manager = mc.player.getItemCooldownManager();
        for (Item item : TRACKED) {
            float p = manager.getCooldownProgress(item, 0f);
            if (p <= 0f) {
                entries.remove(item);
                continue;
            }
            Entry e = entries.get(item);
            if (e == null) {
                entries.put(item, new Entry(item, p));
                continue;
            }
            float d = e.last - p;
            if (d > 0f) {
                e.remainingTicks = Math.round(p / d);
            }
            e.last = p;
        }
    }
}
