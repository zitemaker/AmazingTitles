package com.zitemaker.amazingtitles.code.internal.commands.commandreaders.subs;

import net.md_5.bungee.api.chat.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.zitemaker.amazingtitles.code.api.AmazingTitles;
import com.zitemaker.amazingtitles.code.internal.commands.commandreaders.CommandHandler;
import com.zitemaker.amazingtitles.code.internal.commands.commandreaders.HandlerType;
import com.zitemaker.amazingtitles.code.internal.commands.commandreaders.InternalHandlerType;
import com.zitemaker.amazingtitles.code.internal.commands.commandreaders.readers.ArgsHelper;
import com.zitemaker.amazingtitles.code.internal.smartbar.SmartNotification;
import com.zitemaker.amazingtitles.code.internal.utils.ColorTranslator;
import com.zitemaker.amazingtitles.code.internal.utils.CommandUtils;
import com.zitemaker.amazingtitles.code.internal.utils.TextComponentBuilder;

import java.util.Collections;
import java.util.List;

public class CHNotifications implements CommandHandler {

	@Override
	public BaseComponent[] helpMessage() {
		if (ColorTranslator.isHexSupport()) {
			TextComponentBuilder builder = new TextComponentBuilder();
			builder.appendLegacy("\n<#a217ff>AmazingTitles ✎ </#ff7ae9> &fNotifications\n");
			builder.appendLegacy(
					" &7> <#dedede>/at sendNotification <Players> <Duration> <Symbol> <Message></#c7c7c7>\n",
					"&{#ffa6fc}Click to suggest command", ClickEvent.Action.SUGGEST_COMMAND, "/at sendNotification ");
			builder.appendLegacy("§f");
			return builder.createMessage();
		}
		TextComponentBuilder builder = new TextComponentBuilder();
		builder.appendLegacy("\n&5AmazingTitles ✎ &fNotifications\n");
		builder.appendLegacy(" &7> &7/at sendNotification <Players> <Duration> <Symbol> <Message>\n",
				"&{#ffa6fc}Click to suggest command", ClickEvent.Action.SUGGEST_COMMAND, "/at sendNotification ");
		builder.appendLegacy("§f");
		return builder.createMessage();
	}

	@Override
	public String permission() {
		return "at.notifications";
	}

	@Override
	public HandlerType handlerType() {
		return new InternalHandlerType();
	}

	@Override
	public boolean readAndExecute(CommandSender s, String[] args) {
		if (args.length < 4)
			return false;
		List<Player> players = ArgsHelper.readPlayers(args[0]);
		try {
			int duration = Integer.parseInt(args[1]);
			String symbol = ColorTranslator.colorize(args[2]);
			StringBuilder messageBuilder = new StringBuilder();
			for (int i = 3; i < args.length; i++) {
				messageBuilder.append(' ').append(args[i]);
			}
			String message = messageBuilder.length() > 0
					? ColorTranslator.colorize(messageBuilder.substring(1))
					: "Invalid notification message!";
			AmazingTitles.sendNotification(new SmartNotification(duration, symbol, message), players);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	@Override
	public List<String> readAndReturn(CommandSender s, String[] args) {
		if (args.length == 1) {
			return CommandUtils.copyAllStartingWith(ArgsHelper.preparePlayers(args[0]), args[0]);
		}
		if (args.length == 2) {
			return Collections.singletonList("Duration (Seconds)");
		}
		if (args.length == 3) {
			return Collections.singletonList("Symbol");
		}
		if (args.length > 3) {
			return Collections.singletonList("Notification message");
		}
		return Collections.emptyList();
	}

}
