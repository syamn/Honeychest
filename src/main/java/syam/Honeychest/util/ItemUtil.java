package syam.Honeychest.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;


public class ItemUtil {
	/**
	 * アイテムの文字列(itemMaterial:datavalue)を取得 例: WOOL:3
	 * @param stack 変換する ItemStack オブジェクト
	 * @return アイテムIDの文字列
	 */
	public static String getItemString(ItemStack stack) {
		// データ値が存在すればidに続いて :datavalue を付けて返す
		if (stack.getData() != null && stack.getDurability() != 0){
			return stack.getType().name() + ":" + stack.getDurability();
		}
		// データIDがnullまたは0の場合はIDをそのまま返す
		return stack.getType().name();
	}

	/**
	 * アイテム名からItemStackを返す
	 * @param item アイテムIDとデータ値の文字列
	 * @param amount アイテム個数
	 * @return ItemStack
	 */
	public static ItemStack itemStringToStack(String item, Integer amount) {
		// アイテムID文字列を配列に分ける
		String[] itemArr = item.split(":");
		if ( itemArr.length == 0 ) {
			return null;
		}
		ItemStack stack = new ItemStack(Material.matchMaterial(itemArr[0]), amount);
		// アイテム名にデータ値があれば付与
		if (itemArr.length > 1 && itemArr[1].matches("[0-9]+")){
			stack.setDurability(Short.parseShort(itemArr[1]));
		}
		// ItemStackを返す
		return stack;
	}
}
