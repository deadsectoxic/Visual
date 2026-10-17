package dev.myvisuals.hud;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

public final class Draw {
    public static final int PANEL  = 0xD0141418;
    public static final int ACCENT = 0xFFFF7B1D;
    public static final int TEXT   = 0xFFFFFFFF;
    public static final int MUTED  = 0xFF9A9AA3;

    private Draw() {}

    /** Скруглённый прямоугольник из горизонтальных полос. */
    public static void roundedRect(MatrixStack ms, int x, int y, int w, int h, int r, int color) {
        r = Math.max(0, Math.min(r, Math.min(w, h) / 2));
        DrawableHelper.fill(ms, x, y + r, x + w, y + h - r, color);
        for (int i = 0; i < r; i++) {
            double dy = r - i - 0.5;
            int inset = r - (int) Math.round(Math.sqrt(r * r - dy * dy));
            DrawableHelper.fill(ms, x + inset, y + i, x + w - inset, y + i + 1, color);
            DrawableHelper.fill(ms, x + inset, y + h - i - 1, x + w - inset, y + h - i, color);
        }
    }

    public static void panel(MatrixStack ms, int x, int y, int w, int h) {
        roundedRect(ms, x, y, w, h, 4, PANEL);
    }

    public static String formatTicks(int ticks) {
        int s = Math.max(0, (ticks + 19) / 20);
        return (s / 60) + ":" + String.format("%02d", s % 60);
    }
}
