package jp.sailuna.swap.schedule

import jp.sailuna.swap.RandomSwap
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import kotlin.random.Random

object SwapTask {
    private val plugin = RandomSwap.plugin
    private val locations = ArrayList<Location>()
    val players = ArrayList<Player>()

    private var task: BukkitRunnable? = null

    // タイマーのデフォルト設定
    var timer: Int = 30

    private fun countdown(interval: Int) = Component.text("入れ替わりまで...", NamedTextColor.GRAY).appendSpace()
        .append(Component.text(interval, NamedTextColor.WHITE))

    fun start() { // タイマーが開始されているかの確認
        if (task == null) {
            if (players.size > 1) {
                plugin.server.broadcast(Component.text("入れ替わりタイマーを開始しました。", NamedTextColor.GRAY))
            } else {
                plugin.server.broadcast(Component.text("人数が不足しています", NamedTextColor.GRAY))
                return
            }
        } else {
            plugin.server.broadcast(Component.text("既にタイマーが開始されています", NamedTextColor.GRAY))
            return
        }

        // タイマーを開始する
        task = object : BukkitRunnable() {
            var interval = timer
            override fun run() { // 人数が不足していないかの確認
                if (players.size <= 1) {
                    plugin.server.broadcast(
                        Component.text(
                            "人数が不足しているため停止しています。。。", NamedTextColor.GRAY
                        )
                    )
                    stop()
                } // タイマーが10以下かつ0以外になったときの処理
                if (interval <= 10 && interval != 0) {
                    players.forEach { player ->
                        player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BELL, 10.0f, 1.0f)
                        player.sendMessage(countdown(interval))
                    }
                } // タイマーが0になったときの処理
                if (interval <= 0) {
                    swap(players)
                    interval = timer
                }

                // タイマーの値を減らす
                interval--
            }
        } // タスクを20tick(1秒)ごとに実行する
        task?.runTaskTimer(plugin, 0L, 20L)
    }

    fun stop() { // タイマーを停止する
        if (task != null) plugin.server.broadcast(
            Component.text(
                "入れ替わりタイマーを停止しました。", NamedTextColor.GRAY
            )
        ) else plugin.server.broadcast(Component.text("入れ替わりタイマーは開始されていません。", NamedTextColor.GRAY))
        task?.cancel()
        task = null
    }

    private fun swap(players: MutableList<Player>) {
        // locations を空にする
        locations.clear()
        locations.addAll(players.map { it.location.clone() })
        // 入れ替わりの処理
        players.shuffled(Random(System.currentTimeMillis())).forEachIndexed { index, player ->
            player.teleportAsync(locations[(index + 1) % players.size])
        }
    }
}