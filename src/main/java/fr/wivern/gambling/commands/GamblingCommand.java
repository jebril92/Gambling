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
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("help")) {
                    if (player.hasPermission("command.gambling.help")) {
                        for (String message : helpMessage) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                        }
                    }
                    return true;
                }

                if (args[0].equalsIgnoreCase("admin")) {
                    if (player.hasPermission("command.gambling.admin")) {
                        for (String message : helpAdmin) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                        }
                    }
                    return true;
                }

                if (args[0].equalsIgnoreCase("leave")) {
                    if (!this.gambling.getMatchManager().getPlayerWaitMap().containsKey(player)) {
                        player.sendMessage(this.gambling.getConfigManager().getString("NOT-IN-GAMBLING"));
                        return true;
                    }

                    PlayerData playerData = this.gambling.getMatchManager().getPlayerWaitMap().get(player);
                    if (playerData != null && playerData.isUseMoney()) {
                        double money = playerData.getMoney();
                        this.gambling.getEconomy().depositPlayer(player, money);
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

                // Afficher l'aide si commande inconnue
                for (String message : helpMessage) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                }
                return true;
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

                    String kitName = args[1];

                    // Debug amélioré
                    player.sendMessage("§eDEBUG - Recherche du kit: '" + kitName + "'");
                    player.sendMessage("§eDEBUG - Kits chargés en mémoire: " + this.gambling.getKitManager().getKits().size());

                    // Vérification de l'existence du kit avec protection null
                    if (this.gambling.getKitManager() == null) {
                        player.sendMessage("§cErreur: KitManager non initialisé");
                        return true;
                    }

                    // Recharger les kits depuis le disque
                    this.gambling.getKitManager().setupKits();

                    if (this.gambling.getKitManager().getKitByName(kitName) == null) {
                        player.sendMessage(this.gambling.getConfigManager().getString("KIT-NOT-EXIST"));
                        player.sendMessage("§eDEBUG - Kit non trouvé: '" + kitName + "'");

                        // Afficher les kits disponibles pour debug
                        try {
                            if (this.gambling.getKitManager().getKits() != null) {
                                player.sendMessage("§eKits disponibles (" + this.gambling.getKitManager().getKits().size() + "):");
                                this.gambling.getKitManager().getKits().forEach(kit -> {
                                    if (kit != null && kit.getKitName() != null) {
                                        player.sendMessage("§e- '" + kit.getKitName() + "'");
                                    }
                                });

                                // Vérifier les fichiers sur le disque
                                java.io.File kitFolder = this.gambling.getKitStorage();
                                if (kitFolder.exists()) {
                                    java.io.File[] files = kitFolder.listFiles();
                                    if (files != null) {
                                        player.sendMessage("§eFichiers .yml dans /kits: " + files.length);
                                        for (java.io.File file : files) {
                                            player.sendMessage("§e- " + file.getName());
                                        }
                                    }
                                }
                            } else {
                                player.sendMessage("§eAucun kit disponible (liste null)");
                            }
                        } catch (Exception e) {
                            player.sendMessage("§cErreur lors de l'affichage des kits: " + e.getMessage());
                        }
                        return true;
                    }

                    // Vérification de l'icône du kit
                    try {
                        if (this.gambling.getKitManager().getIcon(kitName) != null &&
                                this.gambling.getKitManager().getIcon(kitName).getType() == Material.REDSTONE_BLOCK) {
                            player.sendMessage("§eAttention: Kit avec icône par défaut (redstone), mais il existe !");
                        }
                    } catch (Exception e) {
                        player.sendMessage("§cErreur lors de la vérification de l'icône du kit: " + e.getMessage());
                        return true;
                    }

                    // Seulement l'argent maintenant
                    try {
                        double money = Double.parseDouble(args[2]);

                        if (this.gambling.getClaimManager().fileWinExist(player) ||
                                this.gambling.getClaimManager().fileWaitExist(player) ||
                                this.gambling.getClaimManager().fileMatchExist(player) ||
                                this.gambling.getClaimManager().fileLeaveExist(player)) {
                            player.sendMessage(this.gambling.getConfigManager().getString("HAVE-TO-CLAIM"));
                            return true;
                        }

                        if (this.gambling.getEconomy() == null) {
                            player.sendMessage("§cErreur: Système économique non disponible");
                            return true;
                        }

                        if (this.gambling.getEconomy().getBalance(player) < money) {
                            player.sendMessage(this.gambling.getConfigManager().getString("NOT-MONEY").replace("<money>", String.valueOf((int)money)));
                            return true;
                        }

                        this.gambling.getEconomy().withdrawPlayer(player, money);
                        this.gambling.getMatchManager().putInWaitWithMoney(player, kitName, money);
                        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 4.0F, 4.0F);
                        this.gambling.getTitle().sendTitle(player, 20, 20, 40,
                                this.gambling.getConfigManager().getString("GAMBLING-TITLE"),
                                this.gambling.getConfigManager().getString("GAMBLING-SUBTITLE"));

                        player.sendMessage("§aDEBUG - Gambling créé avec succès avec le kit: " + kitName);

                    } catch (NumberFormatException e) {
                        player.sendMessage(this.gambling.getConfigManager().getString("NOT-NUMBER"));
                    } catch (Exception e) {
                        player.sendMessage("§cErreur lors de la création du gambling: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }

            return true;
        }
    }

    public boolean isRegionProtected(Location location) {
        try {
            WorldGuardPlugin worldGuard = this.gambling.getWorldGuardPlugin();
            if (worldGuard == null) {
                return false;
            }

            RegionManager regionManager = worldGuard.getRegionManager(location.getWorld());
            if (regionManager == null) {
                return false;
            }

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
        } catch (Exception e) {
            // En cas d'erreur avec WorldGuard, autoriser par défaut
            return true;
        }
    }
}