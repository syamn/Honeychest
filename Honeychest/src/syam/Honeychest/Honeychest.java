package syam.Honeychest;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Honeychest extends JavaPlugin{
	public final static Logger log = Logger.getLogger("Minecraft");
	public final static String logPrefix = "[Honeychest] ";
	public final static String msgPerfix = "&c[Honeychest] &f";

	private final HoneychestPlayerListener playerListener = new HoneychestPlayerListener(this);
	private ConfigurationManager config;

	private static Honeychest instance;

	public static ContainerAccessManager containerManager;

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

		// コマンドを登録
		getServer().getPluginCommand("honeychest").setExecutor(new HoneychestCommand(this));
		log.info(logPrefix+"Initialized Command.");

		// ハニーチェストデータをファイルから読み出し
		if (!HoneyData.reloadData()){
			log.warning(logPrefix+"an error occured while trying to load the honeychest data.");
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
		// 設定ファイルパスを取得
		String filepath = getDataFolder() + System.getProperty("file.separator") + "config.yml";
		File file = new File(filepath);

		// 設定ファイルが見つからなければデフォルトのファイルをコピー
		if (!file.exists()){
			this.saveDefaultConfig();
			log.info(logPrefix+ "config.yml is not found! Created default config.yml!");
		}

		config = new ConfigurationManager();
		try{
			// 実際に読み込む
			config.load(this);
		}catch(Exception ex){
			log.warning(logPrefix+ "an error occured while trying to load the config file.");
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
	 * シングルトンパターンでない/ プラグインがアンロードされたイベントでnullを返す
	 * @return シングルトンインスタンス 何もない場合はnull
	 */
	public static Honeychest getInstance() {
    	return instance;
    }
}
