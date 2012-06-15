package syam.Honeychest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

import syam.Honeychest.config.ConfigurationManager;
import syam.Honeychest.config.MessageManager;
import syam.Honeychest.util.InventoryUtil;

/**
 * コンテナへのアクセスを管理するためのメソッドが含まれています
 */
public class ContainerAccessManager {
	public final static Logger log = Honeychest.log;
	private static final String logPrefix = Honeychest.logPrefix;
	private static final String msgPrefix = Honeychest.msgPrefix;

	private Honeychest plugin;
	private ConfigurationManager config;
	public ContainerAccessManager(Honeychest instance){
		plugin = instance;
		config = plugin.getHCConfig();
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

		// ラージチェストか判定
		boolean large = false;
		if (block.getType() == Material.CHEST){
			if (block.getRelative(BlockFace.NORTH).getType() == Material.CHEST ||
				block.getRelative(BlockFace.SOUTH).getType() == Material.CHEST ||
				block.getRelative(BlockFace.EAST).getType() == Material.CHEST ||
				block.getRelative(BlockFace.WEST).getType() == Material.CHEST){
				large = true;
			}
		}

		// アクセスリスト追加
		accessList.add(new
				ContainerAccess(
						container,
						player,
						InventoryUtil.
						compressInventory(InventoryUtil.getContainerContents(container)),
						block.getLocation(),
						large ));
	}

	/**
	 * プレイヤーがコンテナインベントリが開いていたかチェックして表示
	 * @param player チェックするプレイヤー
	 */
	public void checkInventoryClose(Player player) {
		// アクセスリストを取得
		ContainerAccess access = getAccess(player);

		// アクセスリスト(インベントリを開いた記録)がなければ返す
		if (access == null) return;

		// 処理中のフラグを立てて多重チェックしないようにする
		if (access.checking) return;
		access.checking = true;

		// 閉じた時点でのインベントリを取得
		HashMap<String, Integer> after = InventoryUtil.compressInventory(InventoryUtil.getContainerContents(access.container));
		// String diff = InventoryUtil.createDifferenceString(access.beforeInv, after);
		// String readble = InventoryUtil.createChangeString(InventoryUtil.interpretDifferenceString(diff));

		// ハニーチェストか判定
		String hc = HoneyData.getHc(access.loc);
		if (hc != null){
			// アイテムの窃盗があるか判定
			List<String> stealList = InventoryUtil.createSubList(access.beforeInv, after);
			if (stealList.size() > 0){
				// 窃盗あり ログ、通知用メッセージ用の座標文字列
				String locstr = Actions.getBlockLocationString(access.loc);
				// ログ、通知用の窃盗アイテム文字列
				String substr = InventoryUtil.createSubString(InventoryUtil.interpretSubString(InventoryUtil.joinListToString(stealList)));

				// 設定ファイル確認してアクションを行う
				if (config.getBanFlag()){ // BAN
					plugin.getBansHandler().ban(player, config.getKickBanSender(), config.getBanReason());
				}else if(config.getKickFlag()){ // Kick
					plugin.getBansHandler().kick(player, config.getKickBanSender(), config.getKickReason());
				}

				Actions.broadcastMessage("&4[Honeychest]&f "+ MessageManager.getString("Broadcast.alert", player.getName()));

				// ロギング
				String logfile = plugin.getHCConfig().getLogPath();
				String logmsg = "Player "+player.getName()+" was caught stealing from honeychest at location ("+locstr+")";
				Actions.log(logfile, logmsg);
				Actions.log(logfile,"Stolen Items: "+substr);
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
		ContainerAccess access = getAccess(player);
		// リストにあれば削除
		if (access != null)
			accessList.remove(access);
	}

	/**
	 * プレイヤーがコンテナにアクセスしている場合、ContaierAccessインスタンスを返す
	 * @param player チェックするプレイヤー
	 * @return ContainerAccess または アクセスしていない場合 null
	 */
	public ContainerAccess getAccess(Player player){
		// アクセスリストを取得
		ContainerAccess access = null;
		for (ContainerAccess acc : accessList){
			if (acc.player == player){
				access = acc;
			}
		}
		// 返す
		return access;
	}

	/**
	 * 座標のハニーチェストが開かれているかどうか返す
	 * @param loc チェックするハニーチェストの座標
	 * @return 使われていればtrue そうでなければfalse
	 */
	public boolean isAccessing(Location loc){
		// 通常のチェストはfalseを返す
		if (HoneyData.getHc(loc) == null)
			return false;

		Block block = loc.getBlock();

		// アクセスリストをループさせてチェック
		for (ContainerAccess acc : accessList){
			// 同じ座標のHCにアクセス中
			log.info("3");//debug
			if (acc.loc.getBlock().equals(block)){
				log.info("4");//debug
				return true;
			}else if(acc.large){
				log.info("5");//debug
				// ラージチェストは隣のチェストもチェック
				// 走査開始
				Block second = null;
				if (block.getRelative(BlockFace.NORTH).getType() == Material.CHEST)
		            second = block.getRelative(BlockFace.NORTH);
		        else if (block.getRelative(BlockFace.SOUTH).getType() == Material.CHEST)
		            second = block.getRelative(BlockFace.SOUTH);
		        else if (block.getRelative(BlockFace.EAST).getType() == Material.CHEST)
		            second = block.getRelative(BlockFace.EAST);
		        else if (block.getRelative(BlockFace.WEST).getType() == Material.CHEST)
		            second = block.getRelative(BlockFace.WEST);

				// エラー 不正 → アクセス不能にするため true
				if (second == null)
					return true;

				// もう一つのチェストのアクセスをチェック
				for (ContainerAccess acc2 : accessList){
					if (acc2.loc.getBlock().equals(second)) return true;
				}
			}
		}

		// チェック終わり
		return false;
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
		// ラージチェストかどうか
		public boolean large;
		// チェック中のフラグ
		public boolean checking;

		public ContainerAccess(InventoryHolder container, Player player, HashMap<String, Integer> beforeInv, Location loc, boolean large){
			this.container = container;
			this.player = player;
			this.beforeInv = beforeInv;
			this.loc = loc;
			this.large = large;
			this.checking = false;
		}
	}
}
