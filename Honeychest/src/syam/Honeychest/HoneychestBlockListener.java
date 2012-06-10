package syam.Honeychest;

import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class HoneychestBlockListener implements Listener {
	public final static Logger log = Honeychest.log;
	private static final String logPrefix = Honeychest.logPrefix;
	private static final String msgPrefix = Honeychest.msgPerfix;

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

			switch (block.getType()){
				case FURNACE:
				case DISPENSER:
				case CHEST:
					// ハニーチェストか判定 ハニーチェストならイベントをキャンセルする
					String str = HoneyData.getHc(loc);
					if (str != null){
						// ハニーチェスト イベントキャンセル
						event.setCancelled(true);
						Actions.message(null, player, "&cハニーチェストは破壊できません");
					}
					break;
			}
		}
	}
}
