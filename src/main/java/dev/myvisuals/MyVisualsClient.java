package dev.myvisuals;

import dev.myvisuals.hud.CooldownTracker;
import dev.myvisuals.hud.HudRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class MyVisualsClient implements ClientModInitializer {
    private static KeyBinding toggleHud;

    @Override
    public void onInitializeClient() {
        CooldownTracker cooldowns = new CooldownTracker();
        HudRenderer hud = new HudRenderer(cooldowns);

        toggleHud = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "MyVisuals: toggle HUD", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_M, "MyVisuals"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleHud.wasPressed()) {
                hud.enabled = !hud.enabled;
            }
            cooldowns.tick(client);
        });

        HudRenderCallback.EVENT.register(hud::render);
    }
}
