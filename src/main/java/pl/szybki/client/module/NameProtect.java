package pl.szybki.client.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import pl.szybki.client.Category;
import pl.szybki.client.Config;
import pl.szybki.client.Module;
import pl.szybki.client.ModuleManager;

import java.util.Optional;

/** Podmienia twoj prawdziwy nick na alias w czacie i tabie. Alias: /nameprotect <nick>. */
public class NameProtect extends Module {
    public NameProtect() {
        super("nameprotect", Category.HELPERS);
        hintKey = "hint.nameprotect";
    }

    public static Text protect(Text in) {
        NameProtect m = ModuleManager.NAMEPROTECT;
        if (m == null || !m.enabled || in == null) return in;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.getSession() == null) return in;
        String real = mc.getSession().getUsername();
        String alias = Config.alias;
        if (real == null || real.isEmpty() || alias == null || alias.isEmpty()) return in;
        if (!in.getString().contains(real)) return in;

        MutableText out = Text.empty();
        in.<Boolean>visit((Style style, String str) -> {
            out.append(Text.literal(str.replace(real, alias)).setStyle(style));
            return Optional.empty();
        }, Style.EMPTY);
        return out;
    }
}
