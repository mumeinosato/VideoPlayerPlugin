package mumeinosato.videoplayerplugin.common.util;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.jorel.commandapi.CommandAPIHandler;
import dev.jorel.commandapi.arguments.LocationType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.stream.Collector;

public class VUtils {
    public static float clamp(float value, float min, float max) {
        return Math.min(Math.max(value, min), max);
    }

    public static CommandSender getSender(CommandContext<?> ctx) {
        return CommandAPIHandler.getInstance().getNMS().getSenderForCommand((CommandContext) ctx, false);
    }

    public static Location getLocation(CommandContext<?> ctx, String s, LocationType locationType) throws CommandSyntaxException {
        return CommandAPIHandler.getInstance().getNMS().getLocation((CommandContext) ctx, s, locationType);
    }

    public static Location getLocation(CommandSender sender) {
        return (sender instanceof Entity)
                ? ((Entity) sender).getLocation()
                : (sender instanceof BlockCommandSender)
                ? ((BlockCommandSender) sender).getBlock().getLocation()
                : null;
    }

    public static Location getEyeLocation(CommandSender sender) {
        return (sender instanceof LivingEntity)
                ? ((Player) sender).getEyeLocation()
                : getLocation(sender);
    }

    public static Collector<BaseComponent[], ?, BaseComponent[]> joining() {
        return Collector.of(
                ComponentBuilder::new,
                ComponentBuilder::append,
                (r1, r2) -> r1.append(r2.create()),
                ComponentBuilder::create);
    }

    public static Collector<BaseComponent[], ?, BaseComponent[]> joining(BaseComponent[] joiner) {
        return Collector.of(
                ComponentBuilder::new,
                (r1, r2) -> {
                    if (!r1.getParts().isEmpty())
                        r1.append(joiner);
                    r1.append(r2);
                },
                (r1, r2) -> r1.append(r2.create()),
                ComponentBuilder::create);
    }
}
