package syam.Honeychest;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import syam.Honeychest.Config.ConfigurationManager;
import syam.Honeychest.Config.FileDirectoryStructure;
import syam.Honeychest.Config.MessageManager;
import syam.Honeychest.bans.BanHandler;

public class Honeychest extends JavaPlugin{
	// Logger
	public final static Logger log = Logger.getLogger("Minecraft");
	public final static String logPrefix = "[Honeychest] ";
	public final static String msgPrefix = "&c[Honeychest] &f";

	// Listener
	private final HoneychestPlayerListener playerListener = new HoneychestPlayerListener(this);
	private final HoneychestBlockListener blockListener = new HoneychestBlockListener(this);

	// Honeychest private classes
	private ConfigurationManager config;
	private BanHandler banHandler;
	// Honeychest public classes
	public static ContainerAccessManager containerManager;

	// Instance
	private static Honeychest instance;

	/**
	 * プラグイン停止処理
	 */
	public void onDisable(){
		// ハニーチェストデータをファイルに書き込み
		if (!HoneyData.saveData()){
			log.warning(logPrefix+"an error occured while trying to save the honeychest data.");
		}

		// メッセージ表示
		PluginDescriptionFile pdfFile=this.getDescription();
		log.info("["+pdfFile.getName()+"] version "+pdfFile.getVersion()+" is disabled!");
	}

	/**
	 * プラグイン起動処理
	 */
	public void onEnable(){
		instance = this;

		// 設定ファイル読み込み
		loadConfig();

		// イベントを登録
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(playerListener, this);
		pm.registerEvents(blockListener, this);

		// コマンドを登録
		getServer().getPluginCommand("honeychest").setExecutor(new HoneychestCommand(this));
		log.info(logPrefix+"Initialized Command.");

		// ハニーチェストデータをファイルから読み出し
		if (!HoneyData.reloadData()){
			log.warning(logPrefix+"an error occured while trying to load the honeychest data.");
		}

		// BANを行うプラグインの決定とハンドラ初期化
		banHandler = new BanHandler(this);
		switch (banHandler.setupBanHandler(this)){
			case VANILLA:
				log.info(logPrefix+"Didn't Find ban plugin, using vanilla.");
				break;
			case MCBANS3:
				log.info(logPrefix+"MCBans 3.x plugin found, using that.");
				break;
			default:
				log.warning(logPrefix+"Error occurred on setupBanHandler (Honeychest.class)");
				break;
		}

		// コンテナマネージャを初期化
		containerManager = new ContainerAccessManager(this);

		// メッセージ表示
		PluginDescriptionFile pdfFile=this.getDescription();
		log.info("["+pdfFile.getName()+"] version "+pdfFile.getVersion()+" is enabled!");
	}

	/**
	 * 設定ファイルを読み込む
	 */
	private void loadConfig(){
		// ファイルマネージャセットアップ
		FileDirectoryStructure.setup();

		// 設定ファイルパスを取得
		String filepath = getDataFolder() + System.getProperty("file.separator") + "config.yml";
		File file = new File(filepath);

		// 設定ファイルが見つからなければデフォルトのファイルをコピー
		if (!file.exists()){
			this.saveDefaultConfig();
			log.info(logPrefix+ "config.yml is not found! Created default config.yml!");
		}

		// 設定ファイルを読み込む
		config = new ConfigurationManager();
		try{
			config.load(this);
		}catch(Exception ex){
			log.warning(logPrefix+ "an error occured while trying to load the config file.");
			ex.printStackTrace();
		}

		// 設定ファイルに記述された言語を取得
		String lang = config.getMessageLocale();
		// 言語ファイルをセットアップ
		MessageManager.init(lang);
	}

	/**
	 * 設定マネージャを返す
	 * @return ConfigurationManager
	 */
	public ConfigurationManager getHCConfig(){
		return config;
	}

	/**
	 * BANハンドラを返す
	 * @return BanHandler
	 */
	public BanHandler getBansHandler(){
		return banHandler;
	}

	/**
	 * シングルトンパターンでない/ プラグインがアンロードされたイベントでnullを返す
	 * @return シングルトンインスタンス 何もない場合はnull
	 */
	public static Honeychest getInstance() {
    	return instance;
    }
}
