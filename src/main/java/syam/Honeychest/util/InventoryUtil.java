package syam.Honeychest.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/**
 * I've made this class with reference to Hawkeye.
 * Thanks to them!
 */
public class InventoryUtil {
	/**
	 * 忘れたときのためのメモ書き
	 *
	 * 1. インベントリ情報(ItemStack配列: ItemStack[])を compressInventory 関数で HashMap<String アイテム名, Integer 個数> にして返す
	 * 2. 1の処理を行ったインベントリ情報を createDifferenceString 関数で item:data,amount&...@item:data,amount&... の文字列にして返す
	 * 3. 2の処理を行った2つのインベントリの増減情報文字列を interpretDifferenceString 関数で、増加と減少データの2つのハッシュマップをリストに入れて返す
	 * 4. 3の処理を行ったリストを createChangeString 関数で、読みやすく整形された文字列を返す
	 *
	 * ファイルやデータベースに保存するときは、2番の処理で返された item:data,amount..@.. の文字列を保存、読み込む
	 * 読み込んだ文字列は interpretDifferenceString 関数または interpret[Add/Sub]String 関数を通じて HashMap<String, Integer> に変換を行う
	 * 変換後のハッシュマップは uncompressInventory 関数を通して、ゲーム内で直接参照可能な ItemStack[] に変換が可能である
	 *
	 */

	/**
	 * ItemStackの配列 を ハッシュマップ<アイテムID文字列(String), 個数(Integer)> に変換します
	 * @param inventory 変換するItemStack配列
	 * @return 変換後の HashMap<String, Integer>
	 */
	public static HashMap<String, Integer> compressInventory(ItemStack[] inventory) {
		HashMap<String, Integer> items = new HashMap<String, Integer>();
		for (ItemStack item : inventory){
			if (item == null) continue;
			// アイテムID文字列取得
			String itemString = ItemUtil.getItemString(item);
			// ハッシュマップに追加 既に存在すればアイテム個数を加えて上書き
			if (items.containsKey(itemString)) {
				items.put(itemString, items.get(itemString) + item.getAmount());
			}else{
				items.put(itemString, item.getAmount());
			}
		}
		// アイテムのハッシュマップを返す
		return items;
	}

	/**
	 * ハッシュマップ<String, Integer> を元のItemStack配列に戻す
	 * @param comp 変換された HashMap<String, Integer>
	 * @return ItemStack配列
	 */
	public static ItemStack[] uncompressInventory(HashMap<String, Integer> comp) {
		List<ItemStack> inv = new ArrayList<ItemStack>();
		// ハッシュマップの終わりまでループ
		for (Entry<String, Integer> item : comp.entrySet()) {
			int i = item.getValue(); // アイテム個数
			while (i > 0){
				if (i < 64){
					// 64個より少なければそのままリストに追加
					inv.add(ItemUtil.itemStringToStack(item.getKey(), i));
				}else{
					// 64個以上なら64個 = 1スタック分リストに追加
					inv.add(ItemUtil.itemStringToStack(item.getKey(), 64));
				}
				// 1スタック分(64個) を個数から引いて0以下になるまで繰り返す
				i = i - 64;
			}
		}
		// リストを新しいItmStack配列に変換して返す
		return inv.toArray(new ItemStack[0]);
	}

	/**
	 * 2つのハッシュマップ変換済みインベントリデータを比較して、違いのあるアイテムを整形して返す
	 * @param before インベントリ変更前のハッシュマップ HashMap<String, Integer>
	 * @param after インベントリ変更後のハッシュマップ HashMap<String, Integer>
	 * @return 違いのあるアイテム文字列 ＠以前は加わったもの 以降は減ったもの 例: item:data,amount&item:data,amount@item:data,amount&item:data
	 */
	public static String createDifferenceString(HashMap<String, Integer> before, HashMap<String, Integer> after) {
		List<String> add = new ArrayList<String>();
		List<String> sub = new ArrayList<String>();

		for (Entry<String, Integer> item : before.entrySet()){
			if (!after.containsKey(item.getKey())){
				// 変更後にアイテムが無かった場合
				sub.add(item.getKey() + "," + item.getValue());
			}else if(item.getValue() > after.get(item.getKey())){
				// 変更後にアイテムが減っていた場合
				sub.add(item.getKey() + "," + (item.getValue() - after.get(item.getKey())));
			}else if(item.getValue() < after.get(item.getKey())){
				// 変更後にアイテムが増えていた場合
				add.add(item.getKey() + "," + (after.get(item.getKey()) - item.getValue()));
			}
		}
		for (Entry<String, Integer> item : after.entrySet()) {
			if (!before.containsKey(item.getKey())){
				// 変更前に無いアイテムが変更後にあった場合
				add.add(item.getKey() + "," + item.getValue());
			}
		}

		// 文字列を結合して返す
		return Util.join(add, "&") + "@" + Util.join(sub, "&");
	}

	/**
	 * 2つのハッシュマップ変換済みインベントリデータを比較して、減っているアイテムだけをリストに入れて返す
	 * @param before インベントリ変更前のハッシュマップ HashMap<String, Integer>
	 * @param after インベントリ変更後のハッシュマップ HashMap<String, Integer>
	 * @return 減っているアイテムのStringList 例: item:data,amount || item:data,amount || ..
	 */
	public static List<String> createSubList(HashMap<String, Integer> before, HashMap<String, Integer> after) {
		List<String> sub = new ArrayList<String>();

		for (Entry<String, Integer> item : before.entrySet()){
			if (!after.containsKey(item.getKey())){
				// 変更後にアイテムが無かった場合
				sub.add(item.getKey() + "," + item.getValue());
			}else if(item.getValue() > after.get(item.getKey())){
				// 変更後にアイテムが減っていた場合
				sub.add(item.getKey() + "," + (item.getValue() - after.get(item.getKey())));
			}
		}
		// リストを返す
		return sub;
	}
	/**
	 * 2つのハッシュマップ変換済みインベントリデータを比較して、増えているアイテムだけをリストに入れて返す
	 * @param before インベントリ変更前のハッシュマップ HashMap<String, Integer>
	 * @param after インベントリ変更後のハッシュマップ HashMap<String, Integer>
	 * @return 増えているアイテムのStringList 例: item:data,amount || item:data,amount || ..
	 */
	public static List<String> createAddList(HashMap<String, Integer> before, HashMap<String, Integer> after) {
		List<String> add = new ArrayList<String>();

		for (Entry<String, Integer> item : before.entrySet()){
			if(item.getValue() < after.get(item.getKey())){
				// 変更後にアイテムが増えていた場合
				add.add(item.getKey() + "," + (after.get(item.getKey()) - item.getValue()));
			}
		}
		for (Entry<String, Integer> item : after.entrySet()) {
			if (!before.containsKey(item.getKey())){
				// 変更前に無いアイテムが変更後にあった場合
				add.add(item.getKey() + "," + item.getValue());
			}
		}
		// リストを返す
		return add;
	}

	/**
	 * 違いのある(増加分または減少分のどちらかのみ)アイテムリストを整形して返す
	 * @param diff 違いのある(増加分または減少分のどちらかのみ)アイテムリスト
	 * @return ex) item:data,amount&item:data,amount
	 */
	public static String joinListToString(List<String> diff){
		return Util.join(diff, "&");
	}

	/**
	 * 違いのあるアイテムの文字列を受け取り、増加と減少データの2つのハッシュマップをリストに入れて返す
	 * @param diff 処理済みの整形済みアイテム比較文字列
	 * @return 増加分、減少分、2つのハッシュマップが入ったリストを返す 1つめの要素が増減分、2つめが減少分
	 */
	public static List<HashMap<String, Integer>> interpretDifferenceString(String diff) {
		List<HashMap<String, Integer>> ops = new ArrayList<HashMap<String, Integer>>();

		// ＠で区切られた増えた部分と減った部分を分けて処理する
		for (String changes : diff.split("@")){
			HashMap<String, Integer> op = new HashMap<String, Integer>();
			// 一つの アイテム:個数 データごとに処理する
			for (String change : changes.split("&")){
				if (change.length() == 0) continue;
				// アイテム部分と個数部分を分ける
				String[] item = change.split(",");
				op.put(item[0], Integer.parseInt(item[1]));
			}
			ops.add(op);
		}
		// 減少分がなく、opsリストサイズが1なら空のハッシュマップを追加してサイズを2に揃える
		if (ops.size() == 1) {
			ops.add(new HashMap<String, Integer>());
		}
		return ops;
	}
	/**
	 * 違いのあるアイテムの文字列を受け取り、増加と減少データの2つのハッシュマップをリストに入れて返す
	 * @param diff 処理済みの整形済みアイテム比較文字列
	 * @return 増加分、減少分、2つのハッシュマップが入ったリストを返す 1つめの要素が増減分、2つめが減少分
	 */
	public static HashMap<String, Integer> interpretSubString(String diff) {
		HashMap<String, Integer> op = new HashMap<String, Integer>();

		// 一つの アイテム:個数 データごとに処理する
		for (String change : diff.split("&")){
			if (change.length() == 0) continue;
			// アイテム部分と個数部分を分ける
			String[] item = change.split(",");
			op.put(item[0], Integer.parseInt(item[1]));
		}

		return op;
	}
	/**
	 * 読みやすく整形した、チェスト増減データを作る
	 * @param ops interpretDifferenceString関数で作った、増加分と減少分のハッシュマップが入ったリスト
	 * @return 整形済み文字列
	 */
	public static String createChangeString(List<HashMap<String, Integer>> ops) {
		if (ops.size() == 0) return "";
		String changeString = "";

		// 増加分をリストに追加
		List<String> add = new ArrayList<String>();
		for (Entry<String, Integer> item : ops.get(0).entrySet()){
			add.add(item.getValue() + "x " + ItemUtil.getItemStringName(item.getKey()));
		}
		// 減少分をリストに追加
		List<String> sub = new ArrayList<String>();
		for (Entry<String, Integer> item : ops.get(1).entrySet()){
			sub.add(item.getValue() + "x " + ItemUtil.getItemStringName(item.getKey()));
		}

		// それぞれのリストを組み立て
		if (add.size() > 0) changeString += "&a+(" + Util.join(add, ", ") + ")";
		if (sub.size() > 0) changeString += "&4-(" + Util.join(sub, ", ") + ")";

		// 整形済み文字列を返す
		return changeString;
	}
	/**
	 * 読みやすく整形した、チェスト減少分データを作る
	 * @param ops interpretDifferenceString関数で作った、減少分のハッシュマップが入ったリスト
	 * @return 整形済み文字列
	 */
	public static String createSubString(HashMap<String, Integer> op) {
		if (op.size() == 0) return "";
		String changeString = "";

		// 減少分をリストに追加
		List<String> sub = new ArrayList<String>();
		for (Entry<String, Integer> item : op.entrySet()){
			sub.add(item.getValue() + "x " + ItemUtil.getItemStringName(item.getKey()));
		}

		// それぞれのリストを組み立て
		if (sub.size() > 0) changeString += "-(" + Util.join(sub, ", ") + ")";

		// 整形済み文字列を返す
		return changeString;
	}

	/**
	 * インベントリインターフェースを持つブロックから完全なインベントリ情報を得るための関数
	 * @param container チェックするブロック
	 * @return ItemStack[]
	 */
	public static ItemStack[] getContainerContents(InventoryHolder container) {
		return container.getInventory().getContents();

		// ダブルチェストの扱いはそのままで大丈夫みたいなのでコメントアウトしてます

		/*
		// チェスト以外は問題ないのでそのまま返す
		if (!(container instanceof Chest)) return container.getInventory().getContents();

		Chest chest = (Chest) container;
		Chest second = null;

		// 隣にチェストがないか4面を探す
		if (chest.getBlock().getRelative(BlockFace.NORTH).getType() == Material.CHEST)
			second = (Chest) chest.getBlock().getRelative(BlockFace.NORTH).getState();
		else if (chest.getBlock().getRelative(BlockFace.SOUTH).getType() == Material.CHEST)
            second = (Chest) chest.getBlock().getRelative(BlockFace.SOUTH).getState();
        else if (chest.getBlock().getRelative(BlockFace.EAST).getType() == Material.CHEST)
            second = (Chest) chest.getBlock().getRelative(BlockFace.EAST).getState();
        else if (chest.getBlock().getRelative(BlockFace.WEST).getType() == Material.CHEST)
            second = (Chest) chest.getBlock().getRelative(BlockFace.WEST).getState();

		// 周辺になければ、この一つだけを返す
		if (second == null){
			return chest.getInventory().getContents();
		}else{
			// ダブルチェストの扱いをここに
		}
		*/
	}
}
