package io.github.hsyyid.polis.cmdexecutors;

import io.github.hsyyid.polis.utils.ConfigManager;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.source.CommandBlockSource;
import org.spongepowered.api.util.command.source.ConsoleSource;
import org.spongepowered.api.util.command.spec.CommandExecutor;

import java.util.ArrayList;

public class RemoveEnemyExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		String townName = ctx.<String> getOne("town name").get();

		if (src instanceof Player)
		{
			Player player = (Player) src;

			if (ConfigManager.getTeams().contains(townName))
			{
				String playerTeamName = null;
				boolean playerIsAMember = false;

				for (String team : ConfigManager.getTeams())
				{
					ArrayList<String> uuids = ConfigManager.getMembers(team);
					if (uuids.contains(player.getUniqueId().toString()))
					{
						playerIsAMember = true;
						break;
					}
					else if (ConfigManager.getLeader(team).equals(player.getUniqueId().toString()))
					{
						playerTeamName = team;
						break;
					}
				}

				if (playerTeamName != null)
				{
					try
					{
						if (ConfigManager.getEnemies(playerTeamName) != null && ConfigManager.getEnemies(playerTeamName).contains(townName))
						{
							ConfigManager.removeEnemy(playerTeamName, townName);
							ConfigManager.removeEnemy(townName, playerTeamName);
							player.sendMessage(Texts.of(TextColors.GREEN, "[Polis]: ", TextColors.GOLD, "Successfully removed town ", TextColors.GREEN, townName, TextColors.GOLD, " as an enemy!"));
							return CommandResult.success();
						}
						else
						{
							player.sendMessage(Texts.of(TextColors.GREEN, "[Polis]: ", TextColors.DARK_RED, "Error! ", TextColors.RED, "You are not enemies!"));
							return CommandResult.success();
						}
					}
					catch (NullPointerException e)
					{
						;
					}
				}
				else if (playerIsAMember)
				{
					player.sendMessage(Texts.of(TextColors.GREEN, "[Polis]: ", TextColors.DARK_RED, "Error! ", TextColors.RED, "Ask your leader to remove enemies!"));
				}
				else
				{
					player.sendMessage(Texts.of(TextColors.GREEN, "[Polis]: ", TextColors.DARK_RED, "Error! ", TextColors.RED, "You're not part of a town!"));
				}
			}
			else
			{
				player.sendMessage(Texts.of(TextColors.GREEN, "[Polis]: ", TextColors.DARK_RED, "Error! ", TextColors.RED, "Town does not exist!"));
			}
		}
		else if (src instanceof ConsoleSource)
		{
			src.sendMessage(Texts.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "Must be an in-game player to use /removeenemy!"));
		}
		else if (src instanceof CommandBlockSource)
		{
			src.sendMessage(Texts.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "Must be an in-game player to use /removeenemy!"));
		}

		return CommandResult.success();
	}
}