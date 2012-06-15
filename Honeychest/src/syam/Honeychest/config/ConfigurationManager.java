package syam.Honeychest.config;

import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import syam.Honeychest.Honeychest;

public class ConfigurationManager {
	public final static Logger log = Honeychest.log;
	private static final String logPrefix = Honeychest.logPrefix;
	private static final String msgPrefix = Honeychest.msgPrefix;

	private JavaPlugin plugin;
	private FileConfiguration conf;

	// デフォルトの設定定数
	public static final String defaultKickReason = "[Honeychest] You have been caught steal items from honeychest.";
    public static final String defaultBanReason = "Steal items from HoneyChest. Goodbye!";  // BANの理由
    public static final String defaultKickBanSender = "[Honeychest]"; // MCBansに送信するBANを行ったユーザ名
    public static final String defaultLogPath = "plugins/Honeychest/honeychest.log"; // デフォルトのログ出力先
    public static final String defaultMessageLocale = "default"; // デフォルトの言語ファイル
    public static final int defaultToolID = 271;

	/**
	 * 設定ファイルから設定を読み込む
	 * @param plugin JavaPlugin
	 */
	public void load(final JavaPlugin plugin){
		this.plugin = plugin;
		// 設定ファイルを読み込む
		conf = plugin.getConfig();
	}

	/**
	 * 設定ファイルに設定を書き込む
	 * @throws Exception
	 */
	public void save() throws Exception{
		plugin.saveConfig();
	}

	/* 以下設定取得用getter */

	public int getToolId() {
		return conf.getInt("toolID", defaultToolID);
	}
	public boolean getKickFlag() {
		return conf.getBoolean("kickFlag", true);
	}
	public boolean getBanFlag() {
		return conf.getBoolean("banFlag", false);
	}

	public boolean isGlobalBan(){
		return conf.getBoolean("globalBan", false);
	}
	public String getLogPath() {
		return conf.getString("logPath", defaultLogPath);
	}

	public String getMessageLocale() {
		return conf.getString("messageLocale", defaultMessageLocale);
	}
	public boolean getHiddenTrapMessages() {
		return conf.getBoolean("hiddenTrapMessages", false);
	}
	public boolean getBroadcastItems() {
		return conf.getBoolean("broadcastItems", true);
	}

	public String getKickBanSender() {
		return conf.getString("honeychestKickBanSender", defaultKickBanSender);
	}
	public String getKickReason(){
		return conf.getString("honeychestKickReason", defaultKickReason);
	}
	public String getBanReason() {
		return conf.getString("honeychestBanReason", defaultBanReason);
	}







}
