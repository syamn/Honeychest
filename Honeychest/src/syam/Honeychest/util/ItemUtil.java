package syam.Honeychest.util;

import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class ItemUtil {
	/**
	 * アイテムの文字列(itemid:datavalue)を取得 例: 94:3
	 * @param stack 変換する ItemStack オブジェクト
	 * @return アイテムIDの文字列
	 */
	public static String getItemString(ItemStack stack) {
		// データ値が存在すればidに続いて :datavalue を付けて返す
		if (stack.getData() != null && stack.getData().getData() != 0){
			return stack.getTypeId() + ":" + stack.getData().getData();
		}
		// データIDがnullまたは0の場合はIDをそのまま返す
		return Integer.toString(stack.getTypeId());
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
		ItemStack stack = new ItemStack(Integer.parseInt(itemArr[0], amount));
		// アイテム名にデータ値があれば付与
		if (itemArr.length > 1){
			stack.setData(new MaterialData(Integer.parseInt(itemArr[0]), Byte.parseByte(itemArr[1])));
		}
		// ItemStackを返す
		return stack;
	}
}
