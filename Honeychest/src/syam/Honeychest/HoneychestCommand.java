package syam.Honeychest;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HoneychestCommand implements CommandExecutor {
	public final static Logger log = Honeychest.log;
	private static final String logPrefix = Honeychest.logPrefix;
	private static final String msgPrefix = Honeychest.msgPerfix;

	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args){
		if (command.getName().equalsIgnoreCase("honeychest") || command.getName().equalsIgnoreCase("hc")){
			// /honeychest help コマンド
			if (args.length >= 1 && args[0].equalsIgnoreCase("help")){
				Actions.sendHelp(sender);
				return true;
			}

			// /honeychest create コマンド
			if (args.length >= 1 && (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("c"))){

			}

			return true;
		}
		// honeychestコマンドで無ければfalse
		return false;
	}
}
