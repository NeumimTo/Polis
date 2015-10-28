package io.github.hsyyid.towny.cmdexecutors;

import io.github.hsyyid.towny.utils.Invite;
import io.github.hsyyid.towny.utils.ConfigManager;

import io.github.hsyyid.towny.Towny;
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

public class JoinTownExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		String townName = ctx.<String> getOne("town name").get();
		
		if (src instanceof Player)
		{
			Player player = (Player) src;
			if (ConfigManager.getTeams().contains(townName))
			{
				for (String team : ConfigManager.getTeams())
				{
					ArrayList<String> uuids = ConfigManager.getMembers(team);
					if (uuids.contains(player.getUniqueId().toString()))
					{
						src.sendMessage(Texts.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "You are in a town, first leave that team by doing /leavetown!"));
						return CommandResult.success();
					}
					else if (ConfigManager.getLeader(team).equals(player.getUniqueId().toString()))
					{
						src.sendMessage(Texts.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "You are the leader of a town set a different leader using /setleader!"));
						return CommandResult.success();
					}
				}

				try
				{
					Invite inv = null;
					for (Invite invite : Towny.invites)
					{
						if (invite.getPlayerUUID().equals(player.getUniqueId().toString()))
						{
							inv = invite;
						}
					}
					if (inv != null)
					{
						ConfigManager.addTeamMember(townName, player.getUniqueId().toString());
						for(Player p : Towny.game.getServer().getOnlinePlayers())
						{
							if((p.getUniqueId().toString()).equals(ConfigManager.getLeader(townName)))
							{
								p.sendMessage(Texts.of(TextColors.GREEN, "[Towny]: ", TextColors.YELLOW, player.getName() + " has joined your Town!"));
							}
						}
						player.sendMessage(Texts.of(TextColors.GREEN, "[Towny]: ", TextColors.YELLOW, "Joined town " + townName));
						Towny.invites.remove(inv);
					}
					else
					{
						player.sendMessage(Texts.of(TextColors.GREEN, "[Towny]: ", TextColors.DARK_RED, "Error! ", TextColors.RED, "You don't have an invite to join that town!"));
					}
				}
				catch (NullPointerException e)
				{
					player.sendMessage(Texts.of(TextColors.GREEN, "[Towny]: ", TextColors.DARK_RED, "Error! ", TextColors.RED, "Failed to add you to town!"));
				}
			}
			else
			{
				player.sendMessage(Texts.of(TextColors.GREEN, "[Towny]: ", TextColors.DARK_RED, "Error! ", TextColors.RED, "Town does not exist!"));
			}
		}
		else if (src instanceof ConsoleSource)
		{
			src.sendMessage(Texts.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "Must be an in-game player to use /jointown!"));
		}
		else if (src instanceof CommandBlockSource)
		{
			src.sendMessage(Texts.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "Must be an in-game player to use /jointown!"));
		}

		return CommandResult.success();
	}
}