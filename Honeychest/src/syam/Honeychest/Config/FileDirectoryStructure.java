package syam.Honeychest.Config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
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

	static void extractResource(String from, File to, boolean force){
		File of = to;

		// ファイル展開先がディレクトリならファイルに変換、ファイルでなければ返す
		if (to.isDirectory()){
			String filename = new File(from).getName();
			of = new File(to, filename);
		}else if(!of.isFile()){
			log.warning(logPrefix+ "not a file:" + of);
			return;
		}

		// ファイルが既に存在して、そのファイルの最終変更日時がJarファイルより後ろなら、forceフラグが真の場合を除いて返す
		if (of.exists() && of.lastModified() > getJarFile().lastModified() && !force){
			return;
		}

		OutputStream out = null;
		try{
			// jar内部のリソースファイルを取得
			URL res = Honeychest.class.getResource(from);
			if (res == null){
				log.warning(logPrefix+ "Can't find "+ from +" in plugin Jar file");
			}
			URLConnection resConn = res.openConnection();
			resConn.setUseCaches(false);
			InputStream in = resConn.getInputStream();

			if (in == null){
				log.warning(logPrefix+ "Can't get input stream from " + res);
			}else{
				out = new FileOutputStream(of);
				byte[] buf = new byte[1024]; // 一度に出力するバイト数
				int len;
				// 1KB毎にファイルの終わりまで出力
				while((len = in.read(buf)) > 0){
					out.write(buf, 0, len);
				}

				// 行儀良く後処理
				in.close();
				out.close();
			}
		}catch (Exception ex){
			ex.printStackTrace();
		}finally{
			try{
				if (out != null){
					out.close();
				}
			}catch (Exception ex){}
		}
	}

	public static void extractResource(String from, File to){
		extractResource(from, to, false);
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
