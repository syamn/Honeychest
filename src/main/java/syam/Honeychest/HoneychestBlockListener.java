package syam.Honeychest;

import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import syam.Honeychest.config.MessageManager;

public class HoneychestBlockListener implements Listener {
	public final static Logger log = Honeychest.log;

	private final Honeychest plugin;

	public HoneychestBlockListener(Honeychest plugin){
		this.plugin = plugin;
	}

	/* 登録するイベントはここから下に */

	// プレイヤーがブロックをクリックした
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event){
		Block block = event.getBlock();
		Player player = event.getPlayer();

		if (block != null) {
			Location loc = block.getLocation();

			if ( block.getType() == Material.FURNACE ||
					block.getType() == Material.DISPENSER ||
					block.getType() == Material.CHEST ) {
				// ハニーチェストか判定 ハニーチェストならイベントをキャンセルする
				String str = HoneyData.getHc(loc);
				if (str != null){
					// ハニーチェスト イベントキャンセル
					event.setCancelled(true);

					// メッセージを隠す設定をチェック
					if(!plugin.getHCConfig().getHideTrapMessages())
						Actions.message(null, player, MessageManager.getString("BlockListener.notBreakTrap"));
				}
			}
		}
	}

	// プレイヤーがブロックを設置した
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event){
		Block block = event.getBlock();
		Player player = event.getPlayer();

		// チェスト設置時は横にハニーチェストが無いかチェック
		if (block.getType() == Material.CHEST){
			if ((block.getRelative(BlockFace.NORTH).getType() == Material.CHEST&& HoneyData.getHc(block.getRelative(BlockFace.NORTH).getLocation()) != null) ||
				(block.getRelative(BlockFace.SOUTH).getType() == Material.CHEST && HoneyData.getHc(block.getRelative(BlockFace.SOUTH).getLocation()) != null) ||
				(block.getRelative(BlockFace.EAST).getType() == Material.CHEST && HoneyData.getHc(block.getRelative(BlockFace.EAST).getLocation()) != null) ||
				(block.getRelative(BlockFace.WEST).getType() == Material.CHEST && HoneyData.getHc(block.getRelative(BlockFace.WEST).getLocation()) != null)){
				// イベントキャンセル
				event.setCancelled(true);

				// メッセージを隠す設定をチェック
				if(!plugin.getHCConfig().getHideTrapMessages())
					Actions.message(null, player, MessageManager.getString("BlockListener.notPlaceChest"));
			}
		}
	}
}
