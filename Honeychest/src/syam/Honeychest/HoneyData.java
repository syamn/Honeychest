package syam.Honeychest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class HoneyData {
	public final static Logger log = Honeychest.log;
	private static final String logPrefix = Honeychest.logPrefix;
	private static final String msgPrefix = Honeychest.msgPerfix;

	public static Honeychest plugin;
	public HoneyData(Honeychest instance){
		plugin = instance;
	}

	// ハニーチェストを作っているプレイヤーの名前リスト
	private static List<String> hcCreator = new ArrayList<String>();
	// ハニーチェストの座標とアイテム
	private static Map<Location, String> hcMap = new HashMap<Location, String>();

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
