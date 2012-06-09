package syam.Honeychest;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HoneychestCommand implements CommandExecutor {
	public final static Logger log = Honeychest.log;
	private static final String logPrefix = Honeychest.logPrefix;
	private static final String msgPrefix = Honeychest.msgPerfix;

	private Honeychest plugin;
	public HoneychestCommand(Honeychest instance){
		this.plugin = instance;
	}

	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args){
		// 引数無しはハニーチェスト管理モード
		if (args.length == 0){
			// コンソールチェック
			if (!(sender instanceof Player)){
				Actions.message(sender, null, "&cコンソールからは使えません！");
				return true;
			}

			Player player = (Player) sender;
			// 権限チェック
			if (!player.hasPermission("honeychest.manage")){
				Actions.message(null, player, "&cこのコマンドを使う権限がありません");
				return true;
			}

			if (HoneyData.isCreator(player)){
				// 管理モード終了
				HoneyData.setCreator(player, false);
				Actions.message(null, player, "&aハニーチェスト管理モードを終了しました。");
			}else{
				// 管理モード開始
				HoneyData.setCreator(player, true);
				String tool = Material.getMaterial(plugin.getHCConfig().getToolId()).name();

				Actions.message(null, player, "&aハニーチェスト管理モードになりました。");
				Actions.message(null, player, "&a"+tool+" で右クリックするとハニーチェストになります。");
			}
			return true;
		}

		if (command.getName().equalsIgnoreCase("honeychest") || command.getName().equalsIgnoreCase("hc")){
			// /honeychest help コマンド
			if (args.length >= 1 && args[0].equalsIgnoreCase("help")){
				Actions.sendHelp(sender);
				return true;
			}
		}
		return false;
	}
}
