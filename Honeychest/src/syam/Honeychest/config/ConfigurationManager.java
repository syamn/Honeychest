package syam.Honeychest.config;

import java.io.File;
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

		// バージョンチェック
		double version = conf.getDouble("version");
		checkver(version);
	}

	/**
	 * 設定ファイルに設定を書き込む
	 * @throws Exception
	 */
	public boolean save() {
		plugin.saveConfig();
		try {
			plugin.saveConfig();
			/* この方法ではコメントがconfig.ymlに残らないので却下
			// 保存するデータをここに

			// 保存
			conf.save(new File(FileDirectoryStructure.getPluginDirectory(), "config.yml"));
			*/
		}catch (Exception ex){
			return false;
		}
		return true;
	}

	/**
	 * 設定ファイルのバージョンをチェックする
	 * @param ver
	 */
	private void checkver(final double ver){
		double configVersion = ver; // 設定ファイルのバージョン
		double nowVersion = 1.0D; // プラグインのバージョン
		try{
			nowVersion = Double.parseDouble(Honeychest.getInstance().getDescription().getVersion());
		}catch (NumberFormatException ex){
			log.warning(logPrefix+ "Cannot parse version string!");
		}

		// 比較 設定ファイルのバージョンが古ければ config.yml を上書きする
		if (configVersion < nowVersion){
			// 先に古い設定ファイルをリネームする
			String destName = "oldconfig-v"+configVersion+".yml";
			String srcPath = new File(FileDirectoryStructure.getPluginDirectory(), "config.yml").getPath();
			String destPath = new File(FileDirectoryStructure.getPluginDirectory(), destName).getPath();
			try{
				FileDirectoryStructure.copyTransfer(srcPath, destPath);
				log.info(logPrefix+ "Copied old config.yml to "+destName+"!");
			}catch(Exception ex){
				log.warning(logPrefix+ "Cannot copy old config.yml!");
			}

			// config.ymlと言語ファイルを強制コピー
			FileDirectoryStructure.extractResource("/config.yml", FileDirectoryStructure.getPluginDirectory(), true, false);
			MessageManager.extractLanguageFile(true);

			log.info(logPrefix+ "Deleted existing configuration file and generate a new one!");
		}
	}

	/* 以下設定取得用getter */

	/* Basic Config */
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

	public boolean getRollbackFlag(){
		return conf.getBoolean("rollback", true);
	}

	public boolean getRemoveDroppedFlag(){
		return conf.getBoolean("removeDropped", false);
	}

	/* Logging Config */
	public boolean getLogToFile(){
		return conf.getBoolean("logToFile", true);
	}
	public boolean getLogItems(){
		return conf.getBoolean("logItems", true);
	}
	public String getLogPath() {
		return conf.getString("logPath", defaultLogPath);
	}

	/* Messages Config */
	public String getMessageLocale() {
		return conf.getString("messageLocale", defaultMessageLocale);
	}
	public boolean getHideTrapMessages() {
		return conf.getBoolean("hideTrapMessages", false);
	}
	public boolean getBroadcastItems() {
		return conf.getBoolean("broadcastItems", true);
	}
	public boolean getHideIgnoreMessage() {
		return conf.getBoolean("hideIgnoreMessage", true);
	}

	/* Action Config */
	public String getKickReason(){
		return conf.getString("honeychestKickReason", defaultKickReason);
	}
	public String getBanReason() {
		return conf.getString("honeychestBanReason", defaultBanReason);
	}
}
