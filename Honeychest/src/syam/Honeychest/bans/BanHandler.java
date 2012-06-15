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
		// MCBans (3.x以降)
		Plugin checkMCBans = plugin.getServer().getPluginManager().getPlugin("mcbans");
		if (checkMCBans == null){ // 古いMCBansプラグインもチェック
			checkMCBans = plugin.getServer().getPluginManager().getPlugin("MCBans");
		}

		// 他のBAN関係のプラグインを追加する時はここに

		// MCBansのバージョンを調べる
		if (checkMCBans != null){
			// 初めの.が出現する前の文字列だけをStringBuilderで取り出す
			StringBuilder sb = new StringBuilder(checkMCBans.getDescription().getVersion().trim());
			int dotIndex = sb.indexOf(".");
			sb.delete(dotIndex, sb.length() - 1);

			String majorVersionString = sb.toString();

			// 数値かチェック
			if (Util.isInteger(majorVersionString)){
				int majorVersion = Integer.parseInt(majorVersionString);
				if (majorVersion >= 3){
					// メジャーバージョンが3以上
					mcbans3 = (BukkitInterface) checkMCBans;
					banMethod = BanMethod.MCBANS3;
				}else{
					// 2以下の古いバージョンはエラー
					log.warning(logPrefix+"Old MCBans plugin found but Honeychest support 3.x mcbans plugin.");
					banMethod = BanMethod.VANILLA;
				}
			}else{
				// メジャーバージョン文字列が数値に変換出来なかった
				log.warning(logPrefix+"MCBans plugin found but unknown version.Please contact Honeychest plugin author.");
				banMethod = BanMethod.VANILLA;
			}
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
			default:
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
			case VANILLA: // バニラ サポートプラグインが入っていない場合は通常のBAN処理
				player.kickPlayer(config.getKickReason());
				break;
			case MCBANS3: // MCBans 3.x
				kick_MCBans3(player, sender, reason);
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
		Ban banMCBans3 = new Ban(mcbans3, banType, player.getName(), player.getAddress().toString(), sender, reason, "","");
		// BANのためのスレッドを開始
		banMCBans3.start();
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
		Kick kickMCBans3 = new Kick(mcbans3.Settings, mcbans3, player.getName(), sender, reason);
		kickMCBans3.start();
	}

}
