package fr.wivern.gambling.util.command;

import fr.wivern.gambling.Gambling;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class ACommand implements CommandExecutor {
    private final String commandName;
    private final String permission;
    private final boolean consoleCanExecute;
    private final Gambling gambling;

    public ACommand(Gambling gambling, String commandName, String permission, boolean consoleCanExecute) {
        this.permission = permission;
        this.commandName = commandName;
        this.consoleCanExecute = consoleCanExecute;
        this.gambling = gambling;
        gambling.getCommand(commandName).setExecutor(this);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getLabel().equalsIgnoreCase(this.commandName)) {
            return true;
        } else if (!this.consoleCanExecute && !(sender instanceof Player)) {
            sender.sendMessage(this.gambling.getConfigManager().getString("NOT-PLAYER"));
            return true;
        } else if (!sender.hasPermission(this.permission)) {
            sender.sendMessage(this.gambling.getConfigManager().getString("NO-PERMISSION"));
            return true;
        } else {
            return this.execute(sender, args);
        }
    }

    public abstract boolean execute(CommandSender var1, String[] var2);
}
