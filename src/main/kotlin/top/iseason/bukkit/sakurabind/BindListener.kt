package top.iseason.bukkit.sakurabind

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemConsumeEvent

object BindListener : Listener {

    /**
     * 不能互动
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {
        if (!Config.denyInteract) return
        val item = event.item ?: return
        if (item.type.isAir) return
        if (SakuraBindAPI.hasBind(item) && !SakuraBindAPI.isOwner(item, event.player)) {
            event.isCancelled = true
        }
    }

    /**
     * 不能实体互动
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onPlayerInteractEntityEvent(event: PlayerInteractEntityEvent) {
        if (!Config.denyInteractEntity) return
        event.player.inventory.itemInMainHand.apply {
            if (type.isAir) return
            if (SakuraBindAPI.hasBind(this) && !SakuraBindAPI.isOwner(this, event.player)) {
                event.isCancelled = true
                return
            }
        }
        event.player.inventory.itemInOffHand.apply {
            if (type.isAir) return
            if (SakuraBindAPI.hasBind(this) && !SakuraBindAPI.isOwner(this, event.player)) {
                event.isCancelled = true
                return
            }
        }
    }

    /**
     * 不能丢
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onPlayerDropItemEvent(event: PlayerDropItemEvent) {
        if (!Config.denyDrop) return
        val item = event.itemDrop.itemStack
        if (item.type.isAir) return
        if (SakuraBindAPI.hasBind(item)) {
            event.isCancelled = true
        }
    }

    /**
     * 不能捡起
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onPlayerPickupItemEvent(event: EntityPickupItemEvent) {
        if (!Config.denyPickup) return
        val player = event.entity as? Player ?: return
        val item = event.item.itemStack
        val cursor = player.openInventory.cursor
        if (cursor != null &&
            !cursor.type.isAir &&
            SakuraBindAPI.hasBind(cursor) &&
            !SakuraBindAPI.isOwner(item, player)
        ) {
            event.item.pickupDelay = 10
            event.isCancelled = true
            return
        }
        if (item.type.isAir) return
        if (SakuraBindAPI.hasBind(item) && !SakuraBindAPI.isOwner(item, player)) {
            event.isCancelled = true
            event.item.pickupDelay = 10
        }
    }

    /**
     * 不是你的物品不能点
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onInventoryClickEvent(event: InventoryClickEvent) {
        if (!Config.denyClick) return
        val player = event.whoClicked as? Player ?: return
        val item = event.currentItem ?: return
        if (item.type.isAir) return
        if (event.clickedInventory == event.view.topInventory &&
            SakuraBindAPI.hasBind(item) &&
            !SakuraBindAPI.isOwner(item, player)
        ) {
            event.isCancelled = true
        }
    }

    /**
     * 禁止用于铁砧
     */
    @EventHandler(priority = EventPriority.MONITOR)
    fun onPrepareAnvilEvent(event: PrepareAnvilEvent) {
        if (!Config.denyAnvil) return
        val item1 = event.inventory.getItem(0)
        val item2 = event.inventory.getItem(1)
        item1?.apply {
            if (type.isAir) return
            if (SakuraBindAPI.hasBind(this)) {
                event.result = null
                return
            }
        }
        item2?.apply {
            if (type.isAir) return
            if (SakuraBindAPI.hasBind(this)) {
                event.result = null
                return
            }
        }
    }

    /**
     * 禁止用于合成
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPrepareItemCraftEvent(event: PrepareItemCraftEvent) {
        if (!Config.denyCraft) return
        val inventory = event.inventory
        if (inventory.result == null) return
        for (matrix in inventory.matrix) {
            if (matrix.type.isAir) continue
            if (SakuraBindAPI.hasBind(matrix))
                inventory.result = null
            break
        }
    }

    /**
     * 禁止用于消耗
     */
    @EventHandler(priority = EventPriority.LOW)
    fun onPlayerItemConsumeEvent(event: PlayerItemConsumeEvent) {
        if (!Config.denyConsume) return
        val item = event.item
        if (item.type.isAir) return
        if (SakuraBindAPI.hasBind(item) &&
            !SakuraBindAPI.isOwner(item, event.player)
        ) {
            event.isCancelled = true
        }
    }
}