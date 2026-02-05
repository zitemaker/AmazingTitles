package com.zitemaker.amazingtitles.code.internal.commands.commandreaders.subs;

import net.md_5.bungee.api.chat.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.zitemaker.amazingtitles.code.api.AmazingTitles;
import com.zitemaker.amazingtitles.code.api.builders.AnimationBuilder;
import com.zitemaker.amazingtitles.code.internal.commands.commandreaders.CommandHandler;
import com.zitemaker.amazingtitles.code.internal.commands.commandreaders.HandlerType;
import com.zitemaker.amazingtitles.code.internal.commands.commandreaders.InternalHandlerType;
import com.zitemaker.amazingtitles.code.internal.commands.commandreaders.readers.ArgsHelper;
import com.zitemaker.amazingtitles.code.internal.components.AnimationComponent;
import com.zitemaker.amazingtitles.code.internal.components.ComponentArguments;
import com.zitemaker.amazingtitles.code.internal.utils.ColorTranslator;
import com.zitemaker.amazingtitles.code.internal.utils.CommandUtils;
import com.zitemaker.amazingtitles.code.internal.utils.TextComponentBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CHAnimations implements CommandHandler {

	@Override
	public BaseComponent[] helpMessage() {
		if (ColorTranslator.isHexSupport()) {
			TextComponentBuilder builder = new TextComponentBuilder();
			builder.appendLegacy("\n<#a217ff>AmazingTitles ✎ </#ff7ae9> &fAnimated Messages\n");
			builder.appendLegacy(
					" &7> <#dedede>/at sendAnimation <Players> <arguments/@> <Animation> [AnimationArguments] <Text%subtitle%subText></#c7c7c7>\n",
					"&{#ffa6fc}Click to suggest command", ClickEvent.Action.SUGGEST_COMMAND, "/at sendAnimation ");
			builder.appendLegacy("§f");
			return builder.createMessage();
		}
		TextComponentBuilder builder = new TextComponentBuilder();
		builder.appendLegacy("\n&5AmazingTitles ✎ &fAnimatedMessages\n");
		builder.appendLegacy(
				" &7> &7/at sendAnimation <Players> <arguments/@> <Animation> [AnimationArguments] <Text%subtitle%subText>\n",
				"&{#ffa6fc}Click to suggest command", ClickEvent.Action.SUGGEST_COMMAND, "/at sendAnimation ");
		builder.appendLegacy("§f");
		return builder.createMessage();
	}

	@Override
	public String permission() {
		return "at.animations";
	}

	@Override
	public HandlerType handlerType() {
		return new InternalHandlerType();
	}

	@Override
	public boolean readAndExecute(CommandSender s, String[] args) {
		if (args.length < 3)
			return false;
		try {
			List<Player> players = ArgsHelper.readPlayers(args[0]);
			ComponentArguments arguments = ArgsHelper.readArguments(args[1]);
			AnimationBuilder builder = AmazingTitles.getCustomAnimation(args[2]);
			if (builder == null)
				return false;

			int requiredArgs = builder.getTotalArguments();
			if (args.length < 3 + requiredArgs)
				return false;

			String[] totalArguments = new String[requiredArgs];
			if (requiredArgs > 0) {
				System.arraycopy(args, 3, totalArguments, 0, requiredArgs);
			}

			StringBuilder text = new StringBuilder();
			for (int i = 3 + requiredArgs; i < args.length; i++) {
				text.append(args[i]).append(' ');
			}
			String total = text.toString().replaceAll(" $", "");
			String[] subtitleParts = total.split("%subtitle%", 2);
			String mainText = subtitleParts[0];
			String subtitle = subtitleParts.length > 1
					? ColorTranslator.colorize(subtitleParts[1])
					: "";

			AnimationComponent component = builder.createComponent(
					ComponentArguments.create(mainText, subtitle, arguments.getComponentColor(),
							arguments.getDuration(), arguments.getFps(), arguments.getDisplayType()),
					totalArguments);
			component.addReceivers(players);
			component.prepare();
			component.run();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public List<String> readAndReturn(CommandSender s, String[] args) {
		if (args.length == 1) {
			return CommandUtils.copyAllStartingWith(ArgsHelper.preparePlayers(args[0]), args[0]);
		}
		if (args.length == 2) {
			return CommandUtils.copyAllStartingWith(ArgsHelper.prepareArguments(args[1]), args[1]);
		}
		if (args.length == 3) {
			return CommandUtils.copyAllStartingWith(new ArrayList<>(AmazingTitles.getAnimationNames()), args[2]);
		}
		if (args.length > 3) {
			AnimationBuilder animation = AmazingTitles.getCustomAnimation(args[2]);
			if (animation == null) {
				return Collections.singletonList("Invalid animation!");
			}
			return animation.getArgumentAt(args.length - 4);
		}
		return null;
	}

}
