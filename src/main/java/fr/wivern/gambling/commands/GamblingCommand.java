package fr.wivern.gambling.commands;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.wivern.gambling.Gambling;
import fr.wivern.gambling.claim.ClaimInventory;
import fr.wivern.gambling.data.PlayerData;
import fr.wivern.gambling.inventory.GamblingInventory;
import fr.wivern.gambling.util.command.ACommand;
import java.util.Iterator;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GamblingCommand extends ACommand {
    private Gambling gambling;

    public GamblingCommand(Gambling gambling) {
        super(gambling, "gambling", "command.gambling", false);
        this.gambling = gambling;
    }

    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player)sender;
        List<String> helpMessage = this.gambling.getConfigManager().getStringList("GAMBLING-HELP");
        List<String> helpAdmin = this.gambling.getConfigManager().getStringList("GAMBLING-HELP-ADMIN");
        GamblingInventory gamblingInventory = new GamblingInventory();
        if (args.length == 0) {
            if (this.gambling.getConfigManager().getBoolean("WORLDGUARD-GAMBLING-COMMAND-ACTIVE")) {
                if (this.isRegionProtected(player.getLocation())) {
                    gamblingInventory.openMainInventory(player, this.gambling);
                } else {
                    player.sendMessage(this.gambling.getConfigManager().getString("CANNOT-DO-COMMAND-HERE"));
                }

                return true;
            } else {
                gamblingInventory.openMainInventory(player, this.gambling);
                return true;
            }
        } else {
            String kitName;
            if (args.length == 1) {
                Iterator var7;
                if (args[0].equalsIgnoreCase("help")) {
                    if (player.hasPermission("command.gambling.help")) {
                        var7 = helpMessage.iterator();

                        while(var7.hasNext()) {
                            kitName = (String)var7.next();
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', kitName));
                        }
                    }

                    return true;
                }

                if (args[0].equalsIgnoreCase("admin")) {
                    if (player.hasPermission("command.gambling.admin")) {
                        var7 = helpAdmin.iterator();

                        while(var7.hasNext()) {
                            kitName = (String)var7.next();
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', kitName));
                        }
                    }

                    return true;
                }

                if (args[0].equalsIgnoreCase("leave")) {
                    if (!this.gambling.getMatchManager().getPlayerWaitMap().containsKey(player)) {
                        player.sendMessage(this.gambling.getConfigManager().getString("NOT-IN-GAMBLING"));
                        return true;
                    }

                    if (((PlayerData)this.gambling.getMatchManager().getPlayerWaitMap().get(player)).isUseMoney()) {
                        double money = ((PlayerData)this.gambling.getMatchManager().getPlayerWaitMap().get(player)).getMoney();
                        this.gambling.getEconomy().depositPlayer(player, money);
                    } else if (player.getInventory().firstEmpty() == -1) {
                        this.gambling.getClaimManager().saveLeaveItem(player);
                    } else {
                        ItemStack itemStack = ((PlayerData)this.gambling.getMatchManager().getPlayerWaitMap().get(player)).getItemStack();
                        player.getInventory().addItem(new ItemStack[]{itemStack});
                    }

                    this.gambling.getMatchManager().getPlayerWaitMap().remove(player);
                    player.sendMessage(this.gambling.getConfigManager().getString("LEAVE-GAMBLING"));
                    return true;
                }

                if (args[0].equalsIgnoreCase("claim")) {
                    ClaimInventory claimInventory = new ClaimInventory();
                    claimInventory.openClaimInventory(player, this.gambling);
                    return true;
                }

                var7 = helpMessage.iterator();

                while(var7.hasNext()) {
                    kitName = (String)var7.next();
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', kitName));
                }
            }

            if (args.length == 3) {
                int gamblingSize = this.gambling.getMatchManager().getMaxGambling();
                if (this.gambling.getMatchManager().getPlayerWaitMap().size() >= gamblingSize) {
                    player.sendMessage(this.gambling.getConfigManager().getString("GAMBLING-MAX"));
                    return true;
                }

                if (args[0].equalsIgnoreCase("create")) {
                    if (this.gambling.getMatchManager().getPlayerWaitMap().containsKey(player)) {
                        player.sendMessage(this.gambling.getConfigManager().getString("ALREADY-GAMBLING"));
                        player.closeInventory();
                        return true;
                    }

                    kitName = args[1];
                    if (this.gambling.getKitManager().getKitByName(kitName) == null || this.gambling.getKitManager().getIcon(kitName).getType() == Material.REDSTONE_BLOCK) {
                        player.sendMessage(this.gambling.getConfigManager().getString("KIT-NOT-EXIST"));
                        return true;
                    }

                    if (args[2].equalsIgnoreCase("item")) {
                        if (this.gambling.getClaimManager().fileWinExist(player) || this.gambling.getClaimManager().fileWaitExist(player) || this.gambling.getClaimManager().fileMatchExist(player) || this.gambling.getClaimManager().fileLeaveExist(player)) {
                            player.sendMessage(this.gambling.getConfigManager().getString("HAVE-TO-CLAIM"));
                            return true;
                        }

                        this.gambling.getMatchManager().putInWaitWithItemStack(player, kitName, (ItemStack)this.gambling.getGamblingManager().getPlayerItem().get(player));
                        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 4.0F, 4.0F);
                        this.gambling.getTitle().sendTitle(player, 20, 20, 40, this.gambling.getConfigManager().getString("GAMBLING-TITLE"), this.gambling.getConfigManager().getString("GAMBLING-SUBTITLE"));
                    } else {
                        try {
                            double money = (double)Integer.parseInt(args[2]);
                            this.gambling.getEconomy().withdrawPlayer(player, money);
                            this.gambling.getMatchManager().putInWaitWithMoney(player, kitName, money);
                            player.playSound(player.getLocation(), Sound.ORB_PICKUP, 4.0F, 4.0F);
                            this.gambling.getTitle().sendTitle(player, 20, 20, 40, this.gambling.getConfigManager().getString("GAMBLING-TITLE"), this.gambling.getConfigManager().getString("GAMBLING-SUBTITLE"));
                        } catch (NumberFormatException var11) {
                            player.sendMessage(this.gambling.getConfigManager().getString("NOT-NUMBER"));
                        }
                    }
                }
            }

            return true;
        }
    }

    public boolean isRegionProtected(Location location) {
        WorldGuardPlugin worldGuard = this.gambling.getWorldGuardPlugin();
        RegionManager regionManager = worldGuard.getRegionManager(location.getWorld());
        ApplicableRegionSet regions = regionManager.getApplicableRegions(location);
        if (regions.size() == 0) {
            return false;
        } else {
            Iterator var5 = regions.iterator();

            while(var5.hasNext()) {
                ProtectedRegion region = (ProtectedRegion)var5.next();
                Iterator var7 = this.gambling.getConfigManager().getStringList("WORLDGUARD-GAMBLING-COMMAND").iterator();

                while(var7.hasNext()) {
                    String protectedregion = (String)var7.next();
                    if (region.getId().equalsIgnoreCase(protectedregion)) {
                        return true;
                    }
                }
            }

            return false;
        }
    }
}
