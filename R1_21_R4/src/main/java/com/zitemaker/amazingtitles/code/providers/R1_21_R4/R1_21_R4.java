package com.zitemaker.amazingtitles.code.providers.R1_21_R4;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import org.bukkit.craftbukkit.v1_21_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_21_R4.util.CraftChatMessage;
import org.bukkit.entity.Player;
import com.zitemaker.amazingtitles.code.internal.spi.NmsProvider;

import java.lang.reflect.Method;

public class R1_21_R4 implements NmsProvider {

	private static Method sendMethod;

	@Override
	public Object createActionbarPacket(String text) {
		if (text.isEmpty())
			text = " ";
		return new ClientboundSetActionBarTextPacket(CraftChatMessage.fromStringOrNull(text));
	}

	@Override
	public Object[] createTitlePacket(String title, String subtitle, int in, int keep, int out) {
		if (title.isEmpty())
			title = " ";
		if (subtitle.isEmpty())
			subtitle = " ";
		ClientboundSetTitlesAnimationPacket animation = new ClientboundSetTitlesAnimationPacket(in, keep, out);
		ClientboundSetTitleTextPacket text = new ClientboundSetTitleTextPacket(
				CraftChatMessage.fromStringOrNull(title));
		ClientboundSetSubtitleTextPacket subtext = new ClientboundSetSubtitleTextPacket(
				CraftChatMessage.fromStringOrNull(subtitle));
		return new Object[] { animation, text, subtext };
	}

	@Override
	public void sendTitles(Player player, Object... packets) {
		Object connection = getPlayerConnection(player);
		sendPacket(connection, (Packet<?>) packets[0]);
		sendPacket(connection, (Packet<?>) packets[1]);
		sendPacket(connection, (Packet<?>) packets[2]);
	}

	@Override
	public void sendActionbar(Player player, Object packet) {
		Object connection = getPlayerConnection(player);
		sendPacket(connection, (Packet<?>) packet);
	}

	private Object getPlayerConnection(Player player) {
		return ((CraftPlayer) player).getHandle().f;
	}

	private void sendPacket(Object connection, Packet<?> packet) {
		try {
			if (sendMethod == null) {
				for (Method method : connection.getClass().getMethods()) {
					if (method.getParameterCount() == 1 &&
							Packet.class.isAssignableFrom(method.getParameterTypes()[0])) {
						String name = method.getName();
						if (name.equals("send") || name.equals("sendPacket") || name.equals("a") || name.equals("b")) {
							sendMethod = method;
							sendMethod.setAccessible(true);
							break;
						}
					}
				}
			}
			if (sendMethod != null) {
				sendMethod.invoke(connection, packet);
			}
		} catch (Exception e) {
			throw new RuntimeException("Failed to send packet", e);
		}
	}

}
