package fr.wivern.gambling.restrictions;

import fr.wivern.gambling.Gambling;
import java.util.ArrayList;
import java.util.List;

public class AllowedManager {
    private Gambling gambling;
    private List<String> commands;

    public AllowedManager(Gambling gambling) {
        this.gambling = gambling;
        this.commands = new ArrayList();
        this.commands.addAll(this.gambling.getConfig().getStringList("COMMANDS-ENABLED"));
    }

    public List<String> getCommands() {
        return this.commands;
    }
}
