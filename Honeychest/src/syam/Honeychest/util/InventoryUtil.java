package syam.Honeychest.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class InventoryUtil {

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



}
