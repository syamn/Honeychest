package syam.Honeychest.Config;

import java.io.File;
import java.util.logging.Logger;

import syam.Honeychest.Honeychest;

public class FileDirectoryStructure {
	public final static Logger log = Honeychest.log;
	private static final String logPrefix = Honeychest.logPrefix;
	private static final String msgPrefix = Honeychest.msgPrefix;

	private static File pluginDir = new File("plugins", "Honeychest");
	private static File languageDir;

	private static final String languageDirName = "lang";

	/**
	 * セットアップを行う
	 */
	public static void setup(){
		// プラグインディレクトリ取得
		pluginDir = Honeychest.getInstance().getDataFolder();
		// 言語ディレクトリ取得
		languageDir = new File(pluginDir, languageDirName);

		// plugins/Honeychest
		createDir(pluginDir);
		// plugins/Honeychest/lang
		createDir(languageDir);
	}

	/**
	 * 存在しないディレクトリを作成する
	 * @param dir File 作成するディレクトリ
	 */
	private static void createDir(File dir){
		// 既に存在すれば作らない
		if (dir.isDirectory()){
			return;
		}
		if (!dir.mkdir()){
			log.warning(logPrefix+ "Can't create directory: " + dir.getName());
		}
	}

	/* 以下 getter */

	public static File getJarFile(){
		return new File("plugins","HoneyChest.jar");
	}
	public static File getPluginDirectory(){
		return pluginDir;
	}
	public static File getLanguageDirectory(){
		return languageDir;
	}
}
