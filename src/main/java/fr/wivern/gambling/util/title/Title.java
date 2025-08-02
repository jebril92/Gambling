package fr.wivern.gambling.util.title;

import java.lang.reflect.Field;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Title {
    public void sendTitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String message) {
        this.sendTitle(player, fadeIn, stay, fadeOut, message, (String)null);
    }

    /** @deprecated */
    @Deprecated
    public void sendSubtitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String message) {
        this.sendTitle(player, fadeIn, stay, fadeOut, (String)null, message);
    }

    /** @deprecated */
    @Deprecated
    public void sendFullTitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle) {
        this.sendTitle(player, fadeIn, stay, fadeOut, title, subtitle);
    }

    public void sendTitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle) {
        PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;
        PacketPlayOutTitle packetPlayOutTimes = new PacketPlayOutTitle(EnumTitleAction.TIMES, (IChatBaseComponent)null, fadeIn, stay, fadeOut);
        connection.sendPacket(packetPlayOutTimes);
        IChatBaseComponent titleMain;
        PacketPlayOutTitle packetPlayOutTitle;
        if (subtitle != null) {
            subtitle = subtitle.replaceAll("%player%", player.getDisplayName());
            subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);
            titleMain = ChatSerializer.a("{\"text\": \"" + subtitle + "\"}");
            packetPlayOutTitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, titleMain);
            connection.sendPacket(packetPlayOutTitle);
        }

        if (title != null) {
            title = title.replaceAll("%player%", player.getDisplayName());
            title = ChatColor.translateAlternateColorCodes('&', title);
            titleMain = ChatSerializer.a("{\"text\": \"" + title + "\"}");
            packetPlayOutTitle = new PacketPlayOutTitle(EnumTitleAction.TITLE, titleMain);
            connection.sendPacket(packetPlayOutTitle);
        }

    }

    public void sendTabTitle(Player player, String header, String footer) {
        if (header == null) {
            header = "";
        }

        header = ChatColor.translateAlternateColorCodes('&', header);
        if (footer == null) {
            footer = "";
        }

        footer = ChatColor.translateAlternateColorCodes('&', footer);
        header = header.replaceAll("%player%", player.getDisplayName());
        footer = footer.replaceAll("%player%", player.getDisplayName());
        PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;
        IChatBaseComponent tabTitle = ChatSerializer.a("{\"text\": \"" + header + "\"}");
        IChatBaseComponent tabFoot = ChatSerializer.a("{\"text\": \"" + footer + "\"}");
        PacketPlayOutPlayerListHeaderFooter headerPacket = new PacketPlayOutPlayerListHeaderFooter(tabTitle);

        try {
            Field field = headerPacket.getClass().getDeclaredField("b");
            field.setAccessible(true);
            field.set(headerPacket, tabFoot);
        } catch (Exception var12) {
            var12.printStackTrace();
        } finally {
            connection.sendPacket(headerPacket);
        }

    }

    public void sendActionBar(Player player, String message) {
        CraftPlayer p = (CraftPlayer)player;
        IChatBaseComponent cbc = ChatSerializer.a("{\"text\": \"" + message + "\"}");
        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, (byte)2);
        p.getHandle().playerConnection.sendPacket(ppoc);
    }
}
