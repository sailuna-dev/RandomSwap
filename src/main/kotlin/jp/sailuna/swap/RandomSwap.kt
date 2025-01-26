package jp.sailuna.swap

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import jp.sailuna.swap.command.SwapCommand
import org.bukkit.plugin.java.JavaPlugin

class RandomSwap : JavaPlugin() {
    companion object {
        @JvmStatic
        lateinit var plugin: RandomSwap
    }

    override fun onEnable() { // プラグインが有効化されたときに実行される
        plugin = this

        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS.newHandler { commands ->
            commands.registrar().register(SwapCommand.register(), "A Sailuna provided command.", listOf("shuffle"))
        })
    }

    override fun onDisable() { // プラグインが無効化されたときに実行される
        plugin = this
    }
}