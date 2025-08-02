package fr.wivern.gambling.claim;

import fr.wivern.gambling.Gambling;
import fr.wivern.gambling.util.item.ItemBuilder;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class ClaimInventory {
    public void openClaimInventory(Player player, Gambling gambling) {
        Inventory inventory = Bukkit.createInventory((InventoryHolder)null, gambling.getConfigManager().getInt("GAMBLING-CLAIM.INVENTORY-SIZE"), gambling.getConfigManager().getString("GAMBLING-CLAIM.INVENTORY-NAME"));
        List<String> winLore = new ArrayList(gambling.getConfigManager().getStringList("GAMBLING-CLAIM.WIN.LORE"));
        List<String> leaveLore = new ArrayList(gambling.getConfigManager().getStringList("GAMBLING-CLAIM.LEAVE.LORE"));
        List<String> crashLore = new ArrayList(gambling.getConfigManager().getStringList("GAMBLING-CLAIM.CRASH.LORE"));
        List<String> betLore = new ArrayList(gambling.getConfigManager().getStringList("GAMBLING-CLAIM.BET.LORE"));
        inventory.setItem(gambling.getConfigManager().getInt("GAMBLING-CLAIM.WIN.SLOT"), (new ItemBuilder(Material.getMaterial(gambling.getConfigManager().getString("GAMBLING-CLAIM.WIN.MATERIAL")))).setName(gambling.getConfigManager().getString("GAMBLING-CLAIM.WIN.NAME")).setLore((List)winLore).toItemStack());
        inventory.setItem(gambling.getConfigManager().getInt("GAMBLING-CLAIM.LEAVE.SLOT"), (new ItemBuilder(Material.getMaterial(gambling.getConfigManager().getString("GAMBLING-CLAIM.LEAVE.MATERIAL")))).setName(gambling.getConfigManager().getString("GAMBLING-CLAIM.LEAVE.NAME")).setLore((List)leaveLore).toItemStack());
        inventory.setItem(gambling.getConfigManager().getInt("GAMBLING-CLAIM.CRASH.SLOT"), (new ItemBuilder(Material.getMaterial(gambling.getConfigManager().getString("GAMBLING-CLAIM.CRASH.MATERIAL")))).setName(gambling.getConfigManager().getString("GAMBLING-CLAIM.CRASH.NAME")).setLore((List)crashLore).toItemStack());
        inventory.setItem(gambling.getConfigManager().getInt("GAMBLING-CLAIM.BET.SLOT"), (new ItemBuilder(Material.getMaterial(gambling.getConfigManager().getString("GAMBLING-CLAIM.BET.MATERIAL")))).setName(gambling.getConfigManager().getString("GAMBLING-CLAIM.BET.NAME")).setLore((List)betLore).toItemStack());
        player.openInventory(inventory);
    }
}
