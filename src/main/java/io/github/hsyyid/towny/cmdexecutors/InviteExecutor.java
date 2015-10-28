package io.github.hsyyid.towny.cmdexecutors;

import io.github.hsyyid.towny.utils.Invite;
import io.github.hsyyid.towny.utils.ConfigManager;

import io.github.hsyyid.towny.Towny;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.scheduler.SchedulerService;
import org.spongepowered.api.service.scheduler.TaskBuilder;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.source.CommandBlockSource;
import org.spongepowered.api.util.command.source.ConsoleSource;
import org.spongepowered.api.util.command.spec.CommandExecutor;

import java.util.concurrent.TimeUnit;

public class InviteExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		Game game = Towny.game;
		Player p = ctx.<Player> getOne("player").get();

		if (src instanceof Player)
		{
			Player player = (Player) src;

			boolean isMember = false;
			String teamName = null;
			for (String team : ConfigManager.getTeams())
			{
				if (ConfigManager.getLeader(team).equals(player.getUniqueId().toString()))
				{
					teamName = team;
				}
				else if (ConfigManager.getMembers(team).contains(player.getUniqueId().toString()))
				{
					isMember = true;
				}
			}

			if (isMember)
			{
				src.sendMessage(Texts.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "You are not a leader of a Town! Please ask the leader of your team to invite " + p.getName()));
			}
			else if (teamName != null)
			{
				final Invite invite = new Invite(teamName, p.getUniqueId().toString());
				Towny.invites.add(invite);
				p.sendMessage(Texts.of(TextColors.GREEN, "[Towny]: ", TextColors.GOLD, teamName + " has invited you to join! You have 2 minutes to do /jointown " + teamName + " to accept!"));

				SchedulerService scheduler = game.getScheduler();
				TaskBuilder taskBuilder = scheduler.createTaskBuilder();

				taskBuilder.execute(new Runnable()
				{
					public void run()
					{
						Towny.invites.remove(invite);
					}
				}).delay(2, TimeUnit.MINUTES).name("Teams - remove invite").submit(game.getPluginManager().getPlugin("Teams").get().getInstance());

				src.sendMessage(Texts.of(TextColors.GREEN, "[Teams]: ", TextColors.YELLOW, "Successfully Invited " + p.getName()));
			}

		}
		else if (src instanceof ConsoleSource)
		{
			src.sendMessage(Texts.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "Must be an in-game player to use /invite!"));
		}
		else if (src instanceof CommandBlockSource)
		{
			src.sendMessage(Texts.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "Must be an in-game player to use /invite!"));
		}

		return CommandResult.success();
	}
}