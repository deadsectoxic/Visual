package dev.myvisuals.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class HudRenderer {
    private static final String[] ROMAN = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
    private static final int ROW_H = 18;
    private static final int PAD = 6;

    public boolean enabled = true;
    private final CooldownTracker cooldowns;

    private int frames, fps;
    private long lastFpsUpdate = System.currentTimeMillis();

    public HudRenderer(CooldownTracker cooldowns) {
        this.cooldowns = cooldowns;
    }

    public void render(MatrixStack ms, float tickDelta) {
        MinecraftClient mc = MinecraftClient.getInstance();

        frames++;
        long now = System.currentTimeMillis();
        if (now - lastFpsUpdate >= 1000) {
            fps = frames;
            frames = 0;
            lastFpsUpdate = now;
        }

        if (!enabled || mc.player == null || mc.options.hudHidden || mc.options.debugEnabled) return;

        renderInfoBar(ms, mc);
        renderCooldowns(ms, mc);
        renderPotions(ms, mc);
    }

    private void renderInfoBar(MatrixStack ms, MinecraftClient mc) {
        TextRenderer tr = mc.textRenderer;
        int ping = 0;
        if (mc.getNetworkHandler() != null) {
            PlayerListEntry entry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
            if (entry != null) ping = entry.getLatency();
        }
        String name = "MyVisuals";
        String rest = "  \u2022  " + fps + " FPS  \u2022  " + ping + " ms";
        int w = PAD + 6 + tr.getWidth(name) + tr.getWidth(rest) + PAD;
        int h = 18;
        int x = 6, y = 6;

        Draw.panel(ms, x, y, w, h);
        Draw.roundedRect(ms, x + PAD, y + 6, 6, 6, 2, Draw.ACCENT);
        tr.draw(ms, name, x + PAD + 10, y + 5, Draw.TEXT);
        tr.draw(ms, rest, x + PAD + 10 + tr.getWidth(name), y + 5, Draw.MUTED);
    }

    private void renderCooldowns(MatrixStack ms, MinecraftClient mc) {
        if (cooldowns.isEmpty()) return;
        TextRenderer tr = mc.textRenderer;

        List<CooldownTracker.Entry> rows = new ArrayList<>();
        int maxName = 0;
        for (CooldownTracker.Entry e : cooldowns.active()) {
            rows.add(e);
            maxName = Math.max(maxName, tr.getWidth(new ItemStack(e.item).getName().getString()));
        }
        int w = PAD + 20 + maxName + 16 + tr.getWidth("0:00") + PAD;
        int h = 18 + rows.size() * ROW_H + 4;
        int x = 6, y = 32;

        Draw.panel(ms, x, y, w, h);
        Draw.roundedRect(ms, x + PAD, y + 6, 6, 6, 2, Draw.ACCENT);
        tr.draw(ms, "Cooldowns", x + PAD + 10, y + 5, Draw.TEXT);

        int ry = y + 18;
        for (CooldownTracker.Entry e : rows) {
            ItemStack stack = new ItemStack(e.item);
            mc.getItemRenderer().renderGuiItemIcon(stack, x + PAD - 2, ry);
            tr.draw(ms, stack.getName().getString(), x + PAD + 18, ry + 5, Draw.TEXT);
            String t = e.remainingTicks < 0 ? "..." : Draw.formatTicks(e.remainingTicks);
            tr.draw(ms, t, x + w - PAD - tr.getWidth(t), ry + 5, Draw.ACCENT);
            ry += ROW_H;
        }
    }

    private void renderPotions(MatrixStack ms, MinecraftClient mc) {
        if (mc.player.getStatusEffects().isEmpty()) return;
        TextRenderer tr = mc.textRenderer;

        List<String> names = new ArrayList<>();
        List<String> times = new ArrayList<>();
        int maxName = 0;
        for (StatusEffectInstance fx : mc.player.getStatusEffects()) {
            int amp = fx.getAmplifier();
            String lvl = amp < ROMAN.length ? ROMAN[amp] : String.valueOf(amp + 1);
            String n = I18n.translate(fx.getEffectType().getTranslationKey()) + " " + lvl;
            names.add(n);
            times.add(fx.getDuration() > 72000 ? "--:--" : Draw.formatTicks(fx.getDuration()));
            maxName = Math.max(maxName, tr.getWidth(n));
        }
        int w = PAD + maxName + 16 + tr.getWidth("00:00") + PAD;
        int h = 18 + names.size() * ROW_H + 4;
        int x = mc.getWindow().getScaledWidth() - w - 6;
        int y = mc.getWindow().getScaledHeight() / 2 - h / 2;

        Draw.panel(ms, x, y, w, h);
        Draw.roundedRect(ms, x + PAD, y + 6, 6, 6, 2, Draw.ACCENT);
        tr.draw(ms, "Potions", x + PAD + 10, y + 5, Draw.TEXT);

        int ry = y + 18;
        for (int i = 0; i < names.size(); i++) {
            tr.draw(ms, names.get(i), x + PAD, ry + 5, Draw.TEXT);
            tr.draw(ms, times.get(i), x + w - PAD - tr.getWidth(times.get(i)), ry + 5, Draw.ACCENT);
            ry += ROW_H;
        }
    }
}
