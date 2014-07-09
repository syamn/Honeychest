package syam.Honeychest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import syam.Honeychest.util.TextFileHandler;

public class HoneyData {
	public final static Logger log = Honeychest.log;

	// ハニーチェストのデータ保存用ファイルパス
	private static final String hcDataPath = "plugins/Honeychest/list.ncsv";
	// ハニーチェストを作っているプレイヤーの名前リスト
	private static List<String> hcCreator = new ArrayList<String>();
	// ハニーチェストの座標とアイテム
	private static Map<Location, String> hcMap = new HashMap<Location, String>();

	/**
	 * ハニーチェストデータファイルからすべてのハニーチェストデータを読み込み直す
	 * @return 成功したらtrue 失敗したらfalse
	 */
	public static boolean reloadData(){
		TextFileHandler datafile = new TextFileHandler(hcDataPath);
		hcMap.clear(); // ハッシュマップをクリア
		try{
			List<String> list = datafile.readLines();
			String[] data;
			String[] coord;

			int line = 0;
			// 行を格納したリストが空になるまで繰り返す
			while (!list.isEmpty()){
				line++; // ログ用のデータファイル行数
				String thisLine = list.remove(0); // リストの先頭にある要素を格納して削除

				// デリミタで配列に分ける
				data = thisLine.split("#");
				coord = data[0].split(",");

				// 行の形式がおかしい場合はその行をスキップ
				if (data.length != 2 || coord.length != 4){
					log.warning("Skipping line "+line+": incorrect format");
					continue;
				}

				World world = Honeychest.getInstance().getServer().getWorld(coord[0]);
				// ワールドが存在しなければその行をスキップ
				if (world == null){
					log.warning("Skipping line "+line+": no World defined for world "+coord[0]);
					continue;
				}

				// ハッシュマップにハニーチェストデータを設置
				hcMap.put(new Location(world, new Double(coord[1]), new Double(coord[2]), new Double(coord[3])), data[1]);
			}
		}catch (FileNotFoundException ex){
			return false;
		}catch (IOException ex){
			return false;
		}
		return true;
	}

	/**
	 * ハニーチェストのハッシュマップデータをファイルに保存する
	 * @return
	 */
	public static boolean saveData(){
		TextFileHandler datafile = new TextFileHandler(hcDataPath);
		// 実際に書き込むデータリスト
		List<String> wdata = new ArrayList<String>();

		// ハニーチェストのハッシュマップをループで回す
		for (Entry<Location, String> hc : hcMap.entrySet()){
			Location loc = hc.getKey();
			String items = hc.getValue();
			// 書き込むリストに追加
			wdata.add(loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ() + "#" + items);
		}

		try{
			datafile.writeLines(wdata);
		}catch (IOException ex){
			return false;
		}
		return true;
	}


	/**
	 * 指定した座標のブロックがハニーチェストか判定
	 * @param loc チェックする座標
	 * @return ハニーチェストなら対象アイテムID文字列、null なら通常のチェスト
	 */
	public static String getHc(Location loc){
		return hcMap.get(loc);
	}

	/**
	 * ハニーチェストを作成
	 * @param loc 作成するチェストの座標
	 * @param items アイテムID文字列
	 */
	public static void setHc(Location loc, String items){
		if (getHc(loc) == null){
			hcMap.put(loc, items);
		}
	}

	/**
	 * ハニーチェストを削除
	 * @param loc 削除するハニーチェストの座標
	 */
	public static void removeHc(Location loc){
		if (getHc(loc) != null){
			hcMap.remove(loc);
		}
	}

	/**
	 * ハニーチェスト作成モードにする/しない
	 * @param player 設定対象のプレイヤー
	 * @param state true = 作成モードにする / false = しない
	 */
	public static void setCreator(Player player, boolean state){
		if(state){
			if (!hcCreator.contains(player.getName()))
				hcCreator.add(player.getName());
		}else{
			if (hcCreator.contains(player.getName()))
				hcCreator.remove(player.getName());
		}
	}
	/**
	 * プレイヤーがハニーチェスト作成モードか返す
	 * @param player チェックするプレイヤー
	 * @return trueなら作成モード falseなら作成モードでない
	 */
	public static boolean isCreator(Player player){
		if (hcCreator.contains(player.getName())){
			return true;
		}else{
			return false;
		}
	}

}
