package syam.Honeychest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

/**
 * コンテナへのアクセスを管理するためのメソッドが含まれています
 * @author syam
 */
public class ContainerAccessManager {
	private final List<ContainerAccess> accessList = new ArrayList<ContainerAccess>();

	/**
	 * 開いているプレイヤーインベントリとコンテナインベントリの現在の内容を格納
	 * @param player チェックするプレイヤー
	 * @param block インベントリインターフェースを持つブロック
	 */
	public void checkInventoryOpen(Player player, Block block){
		// インベントリインターフェースを持たないブロックは返す
		if (!(block.getState() instanceof InventoryHolder)){
			return;
		}
		InventoryHolder container = (InventoryHolder) block.getState();
		accessList.add(new ContainerAccess(container, player, null,block.getLocation()));
	}

	/**
	 * インベントリインターフェースを持つブロックへのアクセスを表すクラス
	 * @author syam
	 */
	public class ContainerAccess {
		public InventoryHolder container;
		public Player player;
		public HashMap<String, Integer> beforeInv;
		public Location loc;

		public ContainerAccess(InventoryHolder container, Player player, HashMap<String, Integer> beforeInv, Location loc){
			this.container = container;
			this.player = player;
			this.beforeInv = beforeInv;
			this.loc = loc;
		}
	}
}
