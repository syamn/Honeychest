package syam.Honeychest.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.util.logging.Logger;

import syam.Honeychest.Honeychest;

public class FileDirectoryStructure {
	public final static Logger log = Honeychest.log;

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
	 * コピー元のパス[srcPath]から、コピー先のパス[destPath]へファイルのコピーを行います。
	 * コピー処理にはFileChannel#transferToメソッドを利用します。
	 * コピー処理終了後、入力・出力のチャネルをクローズします。
	 * @param srcPath コピー元のパス
	 * @param destPath  コピー先のパス
	 * @throws IOException 何らかの入出力処理例外が発生した場合
	 */
	public static void copyTransfer(String srcPath, String destPath) throws IOException {
		FileChannel srcChannel = null;
		FileChannel destChannel = null;
		try {
			srcChannel = new FileInputStream(srcPath).getChannel();
			destChannel = new FileOutputStream(destPath).getChannel();
			srcChannel.transferTo(0, srcChannel.size(), destChannel);
		} finally {
			if ( srcChannel != null )
				srcChannel.close();
			if ( destChannel != null )
				destChannel.close();
		}
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
			log.warning("Can't create directory: " + dir.getName());
		}
	}

	/**
	 * リソースファイルをファイルに出力する
	 * @param from 出力元のファイルパス
	 * @param to 出力先のファイルパス
	 * @param force jarファイルの更新日時より新しいファイルが既にあっても強制的に上書きするか
	 * @param checkenc 出力元のファイルを環境によって適したエンコードにするかどうか
	 */
	static void extractResource(String from, File to, boolean force, boolean checkenc){
		File of = to;

		// ファイル展開先がディレクトリならファイルに変換、ファイルでなければ返す
		if (to.isDirectory()){
			String filename = new File(from).getName();
			of = new File(to, filename);
		}else if(!of.isFile()){
			log.warning("not a file:" + of);
			return;
		}

		/* v1.2 forceフラグだけをチェックする
		// ファイルが既に存在して、そのファイルの最終変更日時がJarファイルより後ろなら、forceフラグが真の場合を除いて返す

		if (of.exists() && of.lastModified() > getJarFile().lastModified() && !force){
			return;
		}
		*/

		// ファイルが既に存在する場合は、forceフラグがtrueでない限り展開しない
		if (of.exists() && !force){
			return;
		}

		OutputStream out = null;
		InputStream in = null;
		InputStreamReader reader = null;
		OutputStreamWriter writer =null;
		try{
			// jar内部のリソースファイルを取得
			URL res = Honeychest.class.getResource(from);
			if (res == null){
				log.warning("Can't find "+ from +" in plugin Jar file");
				return;
			}
			URLConnection resConn = res.openConnection();
			resConn.setUseCaches(false);
			in = resConn.getInputStream();

			if (in == null){
				log.warning("Can't get input stream from " + res);
			}else{
				// 出力処理 ファイルによって出力方法を変える
				if (checkenc){
					// 環境依存文字を含むファイルはこちら環境

					reader = new InputStreamReader(in, "UTF-8");
					writer = new OutputStreamWriter(new FileOutputStream(of)); // 出力ファイルのエンコードは未指定 = 自動で変わるようにする

					int text;
					while ((text = reader.read()) != -1){
						writer.write(text);
					}
				}else{
					// そのほか

					out = new FileOutputStream(of);
					byte[] buf = new byte[1024]; // バッファサイズ
					int len = 0;
					while((len = in.read(buf)) >= 0){
						out.write(buf, 0, len);
					}
				}
			}
		}catch (Exception ex){
			ex.printStackTrace();
		}finally{
			// 後処理
			try{
				if (out != null)
					out.close();
				if (in != null)
					in.close();
				if (reader != null)
					reader.close();
				if (writer != null)
					writer.close();
			}catch (Exception ex){
				// do nothing.
			}
		}
	}

	public static void extractResource(String from, File to){
		extractResource(from, to, false, false);
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
