package syam.Honeychest.bans;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import syam.Honeychest.Actions;
import syam.Honeychest.Honeychest;
import syam.Honeychest.config.ConfigurationManager;

import com.mcbans.firestar.mcbans.MCBans;
import com.mcbans.firestar.mcbans.api.MCBansAPI;

/**
 * I've made this class with reference to Honeypot.
 * Thanks to them!
 */
public class BanHandler {
	public final static Logger log = Honeychest.log;
	private static final String logPrefix = Honeychest.logPrefix;
	private static final String msgPrefix = Honeychest.msgPrefix;

	private Honeychest plugin;
	private ConfigurationManager config;
	private BanMethod banMethod = BanMethod.VANILLA; // デフォルトメソッド
	private MCBansAPI mcbansAPI;

	public BanHandler(Honeychest instance){
		plugin = instance;
		config = plugin.getHCConfig();
	}

	/**
	 * BANを行うプラグインをセットアップする
	 * @param plugin プラグインのインスタンス
	 * @return BANを行うプラグインを列挙したBanMethod列挙体
	 */
	public BanMethod setupBanHandler(JavaPlugin plugin){
		// MCBans (4.0以降)
		Plugin checkMCBans = plugin.getServer().getPluginManager().getPlugin("MCBans");

		// glizer
		Plugin checkGl = plugin.getServer().getPluginManager().getPlugin("glizer");
		if (checkGl == null){
			checkGl = plugin.getServer().getPluginManager().getPlugin("Glizer");
		}

		// EasyBan
		Plugin checkEB = plugin.getServer().getPluginManager().getPlugin("EasyBan");
		if (checkEB == null){
			checkEB = plugin.getServer().getPluginManager().getPlugin("easyban");
		}

		// UltraBan
		Plugin checkUB = plugin.getServer().getPluginManager().getPlugin("UltraBan");
		if (checkUB == null){
			checkUB = plugin.getServer().getPluginManager().getPlugin("ultraban");
		}

		// DynamicBan
		Plugin checkDynB =plugin.getServer().getPluginManager().getPlugin("DynamicBan");
		if (checkDynB == null){
			checkDynB = plugin.getServer().getPluginManager().getPlugin("dynamicban");
		}

		// 他のBAN関係のプラグインを追加する時はここに

		// MCBans
		if (checkMCBans != null){
			if (checkMCBans.getDescription().getVersion().trim().startsWith("3")){
				log.warning("Old MCBans plugin (3.x) found but Honeychest supports the version 4.0+");
				banMethod = BanMethod.VANILLA;
			}else{
				mcbansAPI = ((MCBans) checkMCBans).getAPI(plugin);
				banMethod = BanMethod.MCBANS;
			}
		}else if (checkGl != null){
			banMethod = BanMethod.GLIZER;
		}else if (checkEB != null){
			banMethod = BanMethod.EASYBAN;
		}else if (checkUB != null){
			banMethod = BanMethod.ULTRABAN;
		}else if (checkDynB != null){
			banMethod = BanMethod.DYNBAN;
		}else{
			// サポートしているBANプラグインが見つからなかった
			banMethod = BanMethod.VANILLA;
		}

		// BANを行うプラグインを返す
		return banMethod;
	}

	/**
	 * プレイヤーをBANする
	 * @param player BAN対象のプレイヤー
	 * @param sender BANを行ったプレイヤー(String)
	 * @param reason BANの理由
	 */
	public void ban(Player player, String sender, String reason){
		// 連携プラグインによって処理を分ける
		switch (banMethod){
			case VANILLA: // バニラ サポートプラグインが入っていない場合は通常のBAN処理
				player.kickPlayer(config.getKickReason());
				// コンソールから ban (playername) 実行
				Actions.executeCommandOnConsole("ban " + player.getName());
				break;
			case MCBANS: // MCBans
				player.kickPlayer(config.getKickReason());
				ban_MCBans3(player, sender, reason);
				break;
			case GLIZER: // glizer
				ban_glizer(player, reason);
				break;
			case EASYBAN: // EasyBan
				ban_EB(player, reason);
				break;
			case ULTRABAN: // UltraBan
				ban_UB(player, reason);
				break;
			case DYNBAN: // DynamicBan
				ban_DynB(player, reason);
				break;
			default: // Exception: Undefined banMethod
				log.warning(logPrefix+"Error occurred on banning player (BanHandler.class)");
				break;
		}
	}
	/**
	 * プレイヤーをKickする
	 * @param player Kick対象のプレイヤー
	 * @param sender Kickを行ったプレイヤー(String)
	 * @param reason Kickの理由
	 */
	public void kick(Player player, String sender, String reason){
		// 連携プラグインによって処理を分ける
		switch (banMethod){
			case VANILLA: // バニラ サポートプラグインが入っていない場合は通常のKick処理
				player.kickPlayer(config.getKickReason());
				break;
			case MCBANS: // MCBans
				kick_MCBans(player, sender, reason);
				break;
			case GLIZER: // glizer
				kick_glizer(player, reason);
				break;
			case EASYBAN: // EasyBan
				kick_EB(player, reason);
				break;
			case ULTRABAN: // UltraBan
				kick_UB(player, reason);
				break;
			case DYNBAN: // DynamicBan
				kick_DynB(player, reason);
				break;
			default: // Exception: Undefined banMethod
				player.kickPlayer(config.getKickReason());
				log.warning(logPrefix+"Error occurred on kicking player (BanHandler.class)");
				break;
		}
	}

	/**
	 * MCBansを使ってBANを行う
	 * @param player BAN対象のプレイヤー
	 * @param sender BANの送信者
	 * @param reason BANの理由
	 */
	private void ban_MCBans3(Player player, String sender, String reason){
		String targetUUID = player.getUniqueId().toString();
		Player senderPlayer = getPlayer(sender);
		String senderUUID = (senderPlayer != null) ? senderPlayer.getUniqueId().toString() : "";
		if (config.isGlobalBan()){
			mcbansAPI.globalBan(player.getName(), targetUUID, sender, senderUUID, reason);
		}else{
			mcbansAPI.localBan(player.getName(), targetUUID, sender, senderUUID, reason);
		}
	}
	/**
	 * MCBansを使ってKickを行う
	 * @param player Kick対象のプレイヤー
	 * @param sender Kickの送信者
	 * @param reason Kickの理由
	 */
	private void kick_MCBans(Player player, String sender, String reason){
		Player senderPlayer = getPlayer(sender);
		String senderUUID = (senderPlayer != null) ? senderPlayer.getUniqueId().toString() : "";
		mcbansAPI.kick(player.getName(), "", sender, senderUUID, reason);
	}

	/**
	 * glizerを使ってローカル/グローバルBANを行う
	 * @param player 対象プレイヤー
	 * @param reason 理由
	 */
	private void ban_glizer(Player player, String reason){
		if (config.isGlobalBan()){ // グローバル
			Actions.executeCommandOnConsole("globalban " + player.getName() + " " + reason);
		}else{ // ローカル
			Actions.executeCommandOnConsole("localban " + player.getName() + " " + reason);
		}
	}
	/**
	 * glizerを使ってKickを行う
	 * @param player 対象プレイヤー
	 * @param reason 理由
	 */
	private void kick_glizer(Player player, String reason){
		Actions.executeCommandOnConsole("kick " + player.getName() + " " + reason);
	}

	/**
	 * EasyBanを使ってBANを行う
	 * @param player 対象プレイヤー
	 * @param reason 理由
	 */
	private void ban_EB(Player player, String reason){
		Actions.executeCommandOnConsole("eban " + player.getName() + " " + reason);
	}
	/**
	 * EasyBanを使ってKickを行う
	 * @param player 対象プレイヤー
	 * @param reason 理由
	 */
	private void kick_EB(Player player, String reason){
		Actions.executeCommandOnConsole("ekick " + player.getName() + " " + reason);
	}

	/**
	 * UltraBanを使ってBANを行う
	 * @param player 対象プレイヤー
	 * @param reason 理由
	 */
	private void ban_UB(Player player, String reason){
		Actions.executeCommandOnConsole("ban " + player.getName() + " " + reason);
		// IPBANも可能
		//Actions.executeCommandOnConsole("ipban " + player.getName() + " " + reason);
	}
	/**
	 * UltraBanを使ってKickを行う
	 * @param player 対象プレイヤー
	 * @param reason 理由
	 */
	private void kick_UB(Player player, String reason){
		Actions.executeCommandOnConsole("eban " + player.getName() + " " + reason);
	}

	/**
	 * DynamicBanを使ってBANを行う
	 * @param player 対象プレイヤー
	 * @param reason 理由
	 */
	private void ban_DynB(Player player, String reason){
		Actions.executeCommandOnConsole("dynban " + player.getName() + " " + reason);
	}
	/**
	 * DynamicBanを使ってKickを行う
	 * @param player 対象プレイヤー
	 * @param reason 理由
	 */
	private void kick_DynB(Player player, String reason){
		Actions.executeCommandOnConsole("dynkick " + player.getName() + " " + reason);
	}

	/**
	 * 指定した名前のプレイヤーを取得する
	 * @param name プレイヤー名
	 * @return プレイヤー、見つからない（オフライン）ならnullになることに注意
	 */
	private static Player getPlayer(String name) {
		for ( Player player : Bukkit.getOnlinePlayers() ) {
			if ( player.getName().equals(name) ) {
				return player;
			}
		}
		return null;
	}
}
