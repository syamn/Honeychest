package syam.Honeychest;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import syam.Honeychest.bans.BanHandler;
import syam.Honeychest.config.ConfigurationManager;
import syam.Honeychest.config.FileDirectoryStructure;
import syam.Honeychest.config.MessageManager;

public class Honeychest extends JavaPlugin{
	// Logger
	public static Logger log;
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
			log.warning("an error occured while trying to save the honeychest data.");
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
		log = instance.getLogger();

		// 設定ファイル読み込み
		loadConfig();

		// Setup Metrics
		setupMetrics();

		// イベントを登録
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(playerListener, this);
		pm.registerEvents(blockListener, this);

		// コマンドを登録
		getServer().getPluginCommand("honeychest").setExecutor(new HoneychestCommand(this));
		log.info("Initialized Command.");

		// ハニーチェストデータをファイルから読み出し
		if (!HoneyData.reloadData()){
			log.warning("an error occured while trying to load the honeychest data.");
		}

		// BANを行うプラグインの決定とハンドラ初期化
		banHandler = new BanHandler(this);
		boolean gban = config.isGlobalBan();
		switch (banHandler.setupBanHandler(this)){
			case VANILLA:
				log.info("Didn't Find ban plugin, using vanilla.");
				break;
			case MCBANS:
				log.info("MCBans 4.3.4+ plugin found, using that.");
				if (gban)
					log.info("Enabled Global BAN!");
				else
					log.info("Disabled Global BAN. Using local type BAN.");
				break;
			case GLIZER:
				log.info("glizer plugin found, using that.");
				if (gban)
					log.info("Disabled Global BAN. Using local type BAN.");
				break;
			case EASYBAN:
				log.info("EasyBan plugin found, using that.");
				break;
			case ULTRABAN:
				log.info("UltraBan plugin found, using that.");
				break;
			case DYNBAN:
				log.info("DynamicBan plugin found, using that.");
				break;
			default:
				log.warning("Error occurred on setupBanHandler (Honeychest.class)");
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
			log.info("config.yml is not found! Created default config.yml!");
		}

		// 設定ファイルを読み込む
		config = new ConfigurationManager();
		try{
			config.load(this);
		}catch(Exception ex){
			log.warning("an error occured while trying to load the config file.");
			ex.printStackTrace();
		}

		// 設定ファイルに記述された言語を取得
		String lang = config.getMessageLocale();
		// 言語ファイルをセットアップ
		MessageManager.init(lang);
	}

	/**
	 * Metricsセットアップ
	 */
	public void setupMetrics(){
		try {
		    Metrics metrics = new Metrics(this);
		    metrics.start();
		} catch (IOException ex) {
			log.warning("cant send metrics data!");
		    ex.printStackTrace();
		}
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
