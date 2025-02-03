package jp.sailuna.swap.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import jp.sailuna.swap.schedule.SwapTask
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.format.NamedTextColor

object SwapCommand {
    fun register(): LiteralCommandNode<CommandSourceStack> {
        val root = Commands.literal("swap").requires { it.sender.isOp }
        root.then(
            Commands.literal("start").executes { _ ->
                SwapTask.start()
                return@executes Command.SINGLE_SUCCESS
            })
        root.then(
            Commands.literal("stop").executes { _ ->
                SwapTask.stop()
                return@executes Command.SINGLE_SUCCESS
            })
        root.then(
            Commands.literal("add").then(
                Commands.argument("target", ArgumentTypes.players()).executes { ctx ->
                    val playerResolver: PlayerSelectorArgumentResolver =
                        ctx.getArgument("target", PlayerSelectorArgumentResolver::class.java)
                    val players = playerResolver.resolve(ctx.source)
                    players.filter { !SwapTask.players.contains(it) }.forEach { player ->
                        SwapTask.players.add(player)
                        ctx.source.sender.sendMessage {
                            player.name().appendSpace().append(Component.text("を追加しました。", NamedTextColor.WHITE))
                        }
                    }
                    return@executes Command.SINGLE_SUCCESS
                })
        )
        root.then(
            Commands.literal("remove").then(
                Commands.argument("target", ArgumentTypes.players()).executes { ctx ->
                    val playerResolver: PlayerSelectorArgumentResolver =
                        ctx.getArgument("target", PlayerSelectorArgumentResolver::class.java)
                    val players = playerResolver.resolve(ctx.source)
                    players.filter { SwapTask.players.contains(it) }.forEach { player ->
                        SwapTask.players.remove(player)
                        ctx.source.sender.sendMessage {
                            player.name().appendSpace().append(Component.text("を除外しました。", NamedTextColor.WHITE))
                        }
                    }
                    return@executes Command.SINGLE_SUCCESS
                })
        )
        root.then(
            Commands.literal("list").executes { ctx ->
                if (SwapTask.players.isEmpty()) ctx.source.sender.sendMessage {
                    Component.text(
                        "入れ替わりリストに存在しませんでした。", NamedTextColor.WHITE
                    )
                }
                else {
                    ctx.source.sender.sendMessage {
                        Component.text("入れ替わりリスト", NamedTextColor.WHITE).appendNewline().append(
                            Component.join(JoinConfiguration.commas(true), SwapTask.players.map { it.name() })
                        ).appendNewline().append(Component.text("${SwapTask.players.size}人", NamedTextColor.GRAY))
                    }
                }
                return@executes Command.SINGLE_SUCCESS
            })
        root.then(
            Commands.literal("timer").then(
                Commands.argument("interval", IntegerArgumentType.integer(10)).executes { ctx ->
                    SwapTask.timer = ctx.getArgument("interval", Int::class.java)
                    ctx.source.sender.sendMessage {
                        Component.text("タイマーの時間を", NamedTextColor.GRAY).append(
                            Component.text("${SwapTask.timer}秒", NamedTextColor.WHITE)
                                .append(Component.text("に設定しました。", NamedTextColor.GRAY))
                        )
                    }
                    return@executes Command.SINGLE_SUCCESS
                })
        )
        return root.build()
    }
}