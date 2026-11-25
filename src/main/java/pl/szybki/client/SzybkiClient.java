package pl.szybki.client;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import pl.szybki.client.gui.ClientScreen;

public class SzybkiClient implements ClientModInitializer {
    private static KeyBinding menuKey, saveKey, restoreKey;

    @Override
    public void onInitializeClient() {
        ModuleManager.init();
        Config.load();

        String cat = "key.categories.szybkiclient";
        menuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.szybkiclient.menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, cat));
        saveKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.szybkiclient.save", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, cat));
        restoreKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.szybkiclient.restore", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, cat));

        ClientTickEvents.START_CLIENT_TICK.register(ModuleManager::tick);

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (menuKey.wasPressed()) {
                if (mc.currentScreen == null) mc.setScreen(new ClientScreen());
            }
            while (saveKey.wasPressed()) ModuleManager.ANTYKOSTKA.save();
            while (restoreKey.wasPressed()) ModuleManager.ANTYKOSTKA.restore();
        });

        HudRenderCallback.EVENT.register((ctx, tickCounter) ->
                ModuleManager.renderHud(ctx, tickCounter.getTickDelta(true)));

        // przycisk w menu glownym + przyciski AntyKostki w ekwipunku
        ScreenEvents.AFTER_INIT.register((client, screen, w, h) -> {
            if (screen instanceof TitleScreen) {
                Screens.getButtons(screen).add(
                        ButtonWidget.builder(Text.literal("SZYBKI CLIENT"), b -> client.setScreen(new ClientScreen()))
                                .dimensions(6, 6, 110, 20).build());
            }
            if (screen instanceof InventoryScreen && ModuleManager.ANTYKOSTKA.enabled) {
                Screens.getButtons(screen).add(
                        ButtonWidget.builder(Text.literal(Lang.t("btn.save")), b -> ModuleManager.ANTYKOSTKA.save())
                                .dimensions(6, 6, 130, 20).build());
                Screens.getButtons(screen).add(
                        ButtonWidget.builder(Text.literal(Lang.t("btn.restore")), b -> ModuleManager.ANTYKOSTKA.restore())
                                .dimensions(6, 30, 130, 20).build());
            }
        });

        // /nameprotect <nick>
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(ClientCommandManager.literal("nameprotect")
                        .then(ClientCommandManager.argument("nick", StringArgumentType.word())
                                .executes(ctx -> {
                                    Config.alias = StringArgumentType.getString(ctx, "nick");
                                    Config.save();
                                    ctx.getSource().sendFeedback(Text.literal(Lang.t("msg.alias") + Config.alias));
                                    return 1;
                                }))));

        System.out.println("[SZYBKI CLIENT] Zaladowano.");
    }
}
