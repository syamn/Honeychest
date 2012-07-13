package syam.Honeychest.bans;

import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.mcbans.firestar.mcbans.BukkitInterface;
import com.mcbans.firestar.mcbans.pluginInterface.Ban;
import com.mcbans.firestar.mcbans.pluginInterface.Kick;

import syam.Honeychest.Actions;
import syam.Honeychest.Honeychest;
import syam.Honeychest.config.ConfigurationManager;
import syam.Honeychest.util.Util;

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
	private BukkitInterface mcbans3;

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
		// MCBans (3.8以降)
		Plugin checkMCBans = plugin.getServer().getPluginManager().getPlugin("mcbans");
		if (checkMCBans == null){ // 古いMCBansプラグインもチェック
			checkMCBans = plugin.getServer().getPluginManager().getPlugin("MCBans");
		}

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
			// バージョンを調べる 初めの.が出現する前の文字列だけをStringBuilderで取り出す
			String verstr = checkMCBans.getDescription().getVersion().trim();

			if (Util.isDouble(verstr)){
				double ver = Double.parseDouble(verstr);
				// バージョンチェック 3.x 以上
				if (ver >= 3.8){
					mcbans3 = (BukkitInterface) checkMCBans;
					banMethod = BanMethod.MCBANS3;
				}else{
					log.warning("Old MCBans plugin found but Honeychest supports the version 3.8 or more.");
					banMethod = BanMethod.VANILLA;
				}

			}else{
				// 単純変換が失敗してもver4.xには対応させる
				StringBuilder sb = new StringBuilder(verstr);
				int dotIndex = sb.indexOf(".");
				if (dotIndex != -1){
					String[] versions = verstr.split(".");
					// メジャーバージョンが4以上
					if (Util.isInteger(versions[0]) && Integer.parseInt(versions[0]) > 3){
						mcbans3 = (BukkitInterface) checkMCBans;
						banMethod = BanMethod.MCBANS3;
					}else{
						log.warning("MCBans plugin found but unknown version.Please contact Honeychest plugin author.");
						banMethod = BanMethod.VANILLA;
					}
				}else{
					log.warning("MCBans plugin found but unknown version.Please contact Honeychest plugin author.");
					banMethod = BanMethod.VANILLA;
				}
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
			case MCBANS3: // MCBans 3.x
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
			case MCBANS3: // MCBans 3.x
				kick_MCBans3(player, sender, reason);
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
	 * MCBansのバージョン3.x以降を使ってBANを行う
	 * @param player BAN対象のプレイヤー
	 * @param sender BANの送信者
	 * @param reason BANの理由
	 */
	private void ban_MCBans3(Player player, String sender, String reason){
		// MCBansのBAN種類として "localBan" か "globalBan" が必要 (他:"tempBan","unBan")
		String banType = "localBan";

		if (config.isGlobalBan())
			banType = "globalBan";

		// MCBansプラグインに新規のBANを送る
		Ban banMCBans = new Ban(mcbans3, banType, player.getName(), player.getAddress().toString(), sender, reason, "","");
		// BANのためのスレッドを開始
		Thread triggerThread = new Thread(banMCBans);
		triggerThread.start();
	}
	/**
	 * MCBansのバージョン3.x以降を使ってKickを行う
	 * @param player Kick対象のプレイヤー
	 * @param sender Kickの送信者
	 * @param reason Kickの理由
	 */
	@SuppressWarnings("unused")
	private void kick_MCBans3(Player player, String sender, String reason){
		// MCBansプラグインに新規のKickを送る
		Kick kickMCBans = new Kick(mcbans3.Settings, mcbans3, player.getName(), sender, reason);
		Thread triggerThread = new Thread(kickMCBans);
		triggerThread.start();
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

}
