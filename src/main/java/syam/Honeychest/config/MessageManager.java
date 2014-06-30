package syam.Honeychest.config;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;

import syam.Honeychest.Honeychest;

import com.google.common.base.Joiner;

public class MessageManager {
	public final static Logger log = Honeychest.log;

	static Configuration fallbackMessages = null;
	static Configuration messages = null;

	/****************************************/
	// 初期化・読み込み
	/****************************************/

	public static void init(String lang){
		// 言語ファイル展開
		extractLanguageFile(false);

		// デフォルトのメッセージファイルを読み込む
		try{
			fallbackMessages = loadMessageFile("default");
		}catch (Exception e){
			log.warning("Can't load default messages file!");
		}

		// 設定ファイルで指定した言語のメッセージファイルを読み込む
		try{
			setMessagesLanguage(lang);
		}catch (Exception e){
			log.warning("Can't load messages for "+lang+": using default.yml");
			messages = fallbackMessages;
		}
	}

	/**
	 * 言語設定ファイルを出力する
	 * @param force 既存ファイルを上書きするかどうか
	 */
	public static void extractLanguageFile(boolean force){
		File langDir = FileDirectoryStructure.getLanguageDirectory();

		// 言語設定ファイルを出力する - エンコードをクライアントによって決定
		FileDirectoryStructure.extractResource("/lang/default.yml", langDir, force, true);
		FileDirectoryStructure.extractResource("/lang/ja_jp.yml", langDir, force, true);
	}

	/**
	 * 指定した言語ファイルを読み込んで設定する
	 * @param lang
	 */
	public static void setMessagesLanguage(String lang){
		try{
			messages = loadMessageFile(lang);
		}catch(Exception ex){
			log.warning("Error occured on setMessagesLanguage");
		}
	}
	/**
	 * 言語ファイルを読み込む
	 * @param lang 言語ファイル名
	 * @return 読み込んだ設定
	 */
	private static Configuration loadMessageFile(String lang){
		// 言語ファイルを格納するディレクトリを取得
		File langDir = FileDirectoryStructure.getLanguageDirectory();
		// 探す対象の言語ファイル
		File unLocatedLangFile = new File(langDir, lang + ".yml");
		// 探した結果を入れる
		File locatedLangFile = locateLanguageFile(unLocatedLangFile);

		// ファイルが無かった
		if (locatedLangFile == null){
			log.warning("Unknown language file: "+lang);
			return null;
		}

		YamlConfiguration conf = YamlConfiguration.loadConfiguration(locatedLangFile);


		// すべてのメッセージが設定されていることを確認

		// カスタム言語ファイルとデフォルト言語ファイルのキー数を比較
		if (fallbackMessages != null && conf.getKeys(true).size() != fallbackMessages.getKeys(true).size()){
			List<String> missingKeys = new ArrayList<String>();
			for (String key : fallbackMessages.getKeys(true)){
				if (!conf.contains(key) && !fallbackMessages.isConfigurationSection(key)){
					conf.set(key, fallbackMessages.get(key));
					missingKeys.add(key);
				}
			}
			// 翻訳できていないキーをカスタム言語ファイル末尾に追記
			conf.set("NEEDS_TRANSLATION", missingKeys);
			try{
				conf.save(locatedLangFile);
			}catch (IOException e){
				log.warning("Can't write "+locatedLangFile+": "+e.getMessage());
			}

		}
		return conf;
	}

	/**
	 * 言語ファイルが存在して読み込めるか試す
	 * @param langFile File 読み込む言語ファイル
	 * @return 読み込める言語ファイル または null
	 */
	private static File locateLanguageFile(File langFile){
		if (langFile == null){
			return null;
		}
		// ファイルが存在し、かつ読み込むことが出来るか
		if (langFile.isFile() && langFile.canRead()){
			return langFile;
		}else{
			// ja_jp.yml → ja_jp に変換
			String basename = langFile.getName().replaceAll("\\.yml$", "");
			// _(アンダーバー)があれば、_を含むそこから後ろの文字列を削除 ja_jp → ja
			if (basename.contains("_")){
				basename.replaceAll("_.+$", "");
			}
			// ja.yml が存在し、かつ読み込むことが出来るかチェック
			File actual = new File(langFile.getParent(), basename + ".yml");
			if (actual.isFile() && actual.canRead()){
				return actual;
			}else{
				// それでも無ければnullを返す
				return null;
			}
		}
	}



	/****************************************/
	// 言語設定の取得
	/****************************************/

	/**
	 * メッセージを読み込む
	 * @param key 読み込むメッセージのキー
	 * @return 表示するメッセージ
	 */
	public static String getString(String key){
		// カスタムされた言語ファイルがきちんと読み込めていない
		if (messages == null){
			log.warning("Localized messages file is NOT loaded..");
			return "!" + key + "!";
		}

		// カスタムされた言語ファイルからメッセージを取得
		String s = getString(messages, key);

		// カスタムされた言語ファイルに特定のキーが存在しない
		if (s == null){
			log.warning("Missing message key '"+ key +"'");

			// デフォルトの言語ファイルから読み込みを試す
			s = getString(fallbackMessages, key);
			if (s == null){
				s = "!" + key + "!";
			}
		}

		// 表示するメッセージを返す
		return s;
	}

	/**
	 * 引数付きのメッセージを読み込む
	 * @param key 読み込むメッセージのキー
	 * @param args
	 * @return 表示するメッセージ
	 */
	public static String getString(String key, Object... args){
		try{
			return MessageFormat.format(getString(key), args);
		}catch (Exception e){
			log.warning("Error formatting message for "+ key + ": "+e.getMessage());
			return getString(key);
		}
	}

	/**
	 * 内部的に呼び出す: メッセージを読み込む
	 * @param conf 読み込む対象の Configuration
	 * @param key 読み込むキー
	 * @return 表示するメッセージ キーが存在しなければ null
	 */
	private static String getString(Configuration conf, String key){
		String s = null;
		Object o = conf.get(key); // オブジェクト型として初めは取る

		// 文字列型ならそのまま変換する
		if (o instanceof String) {
			s = o.toString();
		} // リスト形式なら、一つずつのリストに改行文字を付けて結合する
		else if (o instanceof List<?>) {
			List<String> l = (List<String>) o;
			s = Joiner.on("\n").join(l);
		}

		// 表示するメッセージを返す
		return s;
	}

}
