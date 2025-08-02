package fr.wivern.gambling.restrictions;

import fr.wivern.gambling.Gambling;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;

public class DisabledMaterialManager {
    private Gambling gambling;
    private List<Material> materialList;

    public DisabledMaterialManager(Gambling gambling) {
        this.gambling = gambling;
        this.materialList = new ArrayList();
        this.gambling.getConfigManager().getStringList("DISABLED-ITEM").forEach((line) -> {
            this.materialList.add(Material.getMaterial(line));
        });
    }

    public List<Material> getMaterialList() {
        return this.materialList;
    }
}
