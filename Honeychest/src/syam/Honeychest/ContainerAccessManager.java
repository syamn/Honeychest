package syam.Honeychest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

import syam.Honeychest.util.InventoryUtil;

/**
 * コンテナへのアクセスを管理するためのメソッドが含まれています
 */
public class ContainerAccessManager {
	public final static Logger log = Honeychest.log;
	private static final String logPrefix = Honeychest.logPrefix;
	private static final String msgPrefix = Honeychest.msgPerfix;

	public static Honeychest plugin;
	public ContainerAccessManager(Honeychest instance){
		plugin = instance;
	}

	// コンテナへのアクセスリスト コンテナインベントリを開いているプレイヤーがここに入る
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
		accessList.add(new ContainerAccess(container, player, InventoryUtil.compressInventory(InventoryUtil.getContainerContents(container)), block.getLocation()));
	}

	/**
	 * プレイヤーがコンテナインベントリが開いていたかチェックして表示
	 * @param player チェックするプレイヤー
	 */
	public void checkInventoryClose(Player player) {
		// アクセスリストを取得
		ContainerAccess access = null;
		for (ContainerAccess acc : accessList){
			if (acc.player == player){
				access = acc;
			}
		}

		// アクセスリスト(インベントリを開いた記録)がなければ返す
		if (access == null) return;

		// 今のインベントリを取得して、アイテムの増減分をプレイヤーに送信
		HashMap<String, Integer> after = InventoryUtil.compressInventory(InventoryUtil.getContainerContents(access.container));
		// String diff = InventoryUtil.createDifferenceString(access.beforeInv, after);
		// String readble = InventoryUtil.createChangeString(InventoryUtil.interpretDifferenceString(diff));

		// ハニーチェストか判定
		String hc = HoneyData.getHc(access.loc);
		if (hc != null){
			// アイテムの窃盗があるか判定
			String stealString = InventoryUtil.createStealString(access.beforeInv, after);
			if (stealString.length() > 0){
				// 窃盗あり
				String locstr = Actions.getBlockLocationString(access.loc);
				//Actions.executeCommandOnConsole("kick " + player.getName() + " [HoneyChest] Stealing from HoneyChest(" + locstr + ")");
				Actions.broadcastMessage("&4[Honeychest] &7Player &4"+player.getName()+" &7was caught stealing from honeychest.");
				player.kickPlayer("[Honeychest] Stealing from HoneyChest (" + locstr + ")");

				// ロギング
				String logfile = plugin.getHCConfig().getLogPath();
				String logmsg = "Player "+player.getName()+" was caught stealing from honeychest at location ("+locstr+")";
				Actions.log(logfile, logmsg);
			}
		}


		// アクセスリストから削除
		accessList.remove(access);
	}

	/**
	 * アクセスリストにあればデータを削除
	 * @param player
	 */
	public void removeAccessList(Player player) {
		// アクセスリストを取得
		ContainerAccess access = null;
		for (ContainerAccess acc : accessList){
			if (acc.player == player){
				access = acc;
			}
		}
		// リストにあれば削除
		if (access != null)
			accessList.remove(access);
	}

	/**
	 * インベントリインターフェースを持つブロックへのアクセスを表すクラス
	 * @author syam
	 */
	public class ContainerAccess {
		// 開いたコンテナのインスタンス
		public InventoryHolder container;
		// 開いたプレイヤー
		public Player player;
		// 開いた時点のコンテナインベントリ
		public HashMap<String, Integer> beforeInv;
		// コンテナの座標
		public Location loc;

		public ContainerAccess(InventoryHolder container, Player player, HashMap<String, Integer> beforeInv, Location loc){
			this.container = container;
			this.player = player;
			this.beforeInv = beforeInv;
			this.loc = loc;
		}
	}
}
