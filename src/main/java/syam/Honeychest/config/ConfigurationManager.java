package syam.Honeychest.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import syam.Honeychest.Honeychest;
import syam.Honeychest.util.Util;

public class ConfigurationManager {
	public final static Logger log = Honeychest.log;

	private JavaPlugin plugin;
	private FileConfiguration conf;

	// デフォルトの設定定数
	private final String defaultKickReason = "[Honeychest] You have been caught steal items from honeychest.";
	private final String defaultBanReason = "Steal items from HoneyChest. Goodbye!";  // BANの理由
    private final String defaultLogPath = "plugins/Honeychest/honeychest.log"; // デフォルトのログ出力先
    private final String defaultMessageLocale = "default"; // デフォルトの言語ファイル
    private final Material defaultToolMaterial = Material.WOOD_SPADE;
    private final List<String> defaultCommands = new ArrayList<String>(0);

    // action
    private TakeAction takeAction = null;

	/**
	 * 設定ファイルから設定を読み込む
	 * @param plugin JavaPlugin
	 */
	public void load(final JavaPlugin plugin){
		this.plugin = plugin;
		// 設定ファイルを読み込む
		conf = plugin.getConfig();

		// バージョンチェック
		String version = conf.getString("version", "1.0.0");
		checkver(version, plugin.getDescription().getVersion());

		takeAction = null;
		String takeActionStr = conf.getString("takeAction", "kick").trim();
		for (TakeAction ta : TakeAction.values()){
			if (ta.name().equalsIgnoreCase(takeActionStr)){
				this.takeAction = ta;
				break;
			}
		}
		if (takeAction == null){
			log.warning("Specified NOT valid action! Use default kick action!");
			takeAction = TakeAction.KICK;
		}
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
	 * @param configVersion 設定ファイルのバージョン
	 * @param nowVersion プラグインのバージョン
	 */
	private void checkver(final String configVersion, final String nowVersion){

		// 比較 設定ファイルのバージョンが古ければ config.yml を上書きする
		if (!Util.isUpperVersion(configVersion, nowVersion)){
			// 先に古い設定ファイルをリネームする
			String destName = "oldconfig-v"+configVersion+".yml";
			String srcPath = new File(FileDirectoryStructure.getPluginDirectory(), "config.yml").getPath();
			String destPath = new File(FileDirectoryStructure.getPluginDirectory(), destName).getPath();
			try{
				FileDirectoryStructure.copyTransfer(srcPath, destPath);
				log.info("Copied old config.yml to "+destName+"!");
			}catch(Exception ex){
				log.warning("Cannot copy old config.yml!");
			}

			// config.ymlと言語ファイルを強制コピー
			FileDirectoryStructure.extractResource("/config.yml", FileDirectoryStructure.getPluginDirectory(), true, false);
			MessageManager.extractLanguageFile(true);

			log.info("Deleted existing configuration file and generate a new one!");
		}
	}

	/* 以下設定取得用getter */

	/* Basic Config */
	public Material getToolMaterial() {
		String name = conf.getString("toolMaterial", defaultToolMaterial.name());
		Material material = Material.matchMaterial(name.toUpperCase());
		if ( material == null ) {
			material = defaultToolMaterial;
		}
		return material;
	}
	public TakeAction getTakeAction() {
		return this.takeAction;
	}

	public boolean isGlobalBan(){
		return conf.getBoolean("globalBan", false);
	}

	public List<String> getCommands(){
		if(conf.get("commands") != null){
			return conf.getStringList("commands");
		}else{
			return defaultCommands;
		}
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
