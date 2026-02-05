package com.zitemaker.amazingtitles.code.internal.components.implementations;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import com.zitemaker.amazingtitles.code.api.AmazingTitles;
import com.zitemaker.amazingtitles.code.api.enums.DisplayType;
import com.zitemaker.amazingtitles.code.Iintegrations.PlaceholderAPIIntegration;
import com.zitemaker.amazingtitles.code.internal.Booter;
import com.zitemaker.amazingtitles.code.internal.components.AnimationComponent;

import java.util.*;

public class LightAnimationComponent implements AnimationComponent {

	/*
	 *
	 * Values
	 *
	 */

	private BukkitTask task;
	private Runnable runnable;
	private BossBar bossBar;
	private int loopedSeconds = 0;
	private final List<Player> receivers = new ArrayList<>();
	private final Plugin plugin;
	private final String frame;
	private final String mainText;
	private final String subText;
	private final int duration;
	private final DisplayType displayType;
	private final BarColor componentColor;

	/*
	 *
	 * Constructor
	 *
	 */

	public LightAnimationComponent(Plugin plugin, String frame, String mainText, String subText, int duration,
			DisplayType displayType, BarColor componentColor) {
		this.plugin = plugin;
		this.frame = frame;
		this.mainText = mainText;
		this.subText = subText;
		this.duration = duration;
		this.displayType = displayType;
		this.componentColor = componentColor;
	}

	/*
	 *
	 * Values management
	 *
	 */

	@Override
	public List<String> frames() {
		return Collections.singletonList(frame);
	}

	@Override
	public String mainText() {
		return mainText;
	}

	@Override
	public Optional<String> subText() {
		return Optional.of(subText);
	}

	@Override
	public int duration() {
		return duration;
	}

	@Override
	public int fps() {
		return 20;
	}

	@Override
	public DisplayType display() {
		return displayType;
	}

	@Override
	public Optional<BarColor> componentColor() {
		return AnimationComponent.super.componentColor();
	}

	/*
	 *
	 * Player management
	 *
	 */

	@Override
	public void addReceivers(Player... players) {
		receivers.addAll(Arrays.asList(players));
	}

	@Override
	public void addReceivers(Collection<Player> players) {
		receivers.addAll(players);
	}

	@Override
	public void removeReceivers(Player... players) {
		receivers.removeAll(Arrays.asList(players));
	}

	@Override
	public void removeReceivers(Collection<Player> players) {
		receivers.removeAll(players);
	}

	/*
	 *
	 * Animation management
	 *
	 */

	@Override
	public String callCurrentFrame() {
		return frame;
	}

	@Override
	public void prepare() {
		for (Player p : receivers) {
			AmazingTitles.removeAnimation(p);
			AmazingTitles.insertAnimation(p, this);
		}
		if (displayType == DisplayType.BOSS_BAR) {
			bossBar = Bukkit.createBossBar("", componentColor, BarStyle.SOLID);
			for (Player p : receivers) {
				bossBar.addPlayer(p);
			}
			bossBar.setVisible(false);
		}
		if (displayType == DisplayType.TITLE) {
			this.runnable = () -> {
				if (next()) {
					for (Player p : receivers) {
						if (p == null)
							continue;
						String resolved = PlaceholderAPIIntegration.resolve(p, frame);
						String resolvedSub = PlaceholderAPIIntegration.resolve(p, subText);
						Object[] packets = Booter.getNmsProvider().createTitlePacket(resolved, resolvedSub, 0, 20, 0);
						Booter.getNmsProvider().sendTitles(p, packets);
					}
				} else {
					end();
				}
			};
		}
		if (displayType == DisplayType.SUBTITLE) {
			this.runnable = () -> {
				if (next()) {
					for (Player p : receivers) {
						if (p == null)
							continue;
						String resolvedSub = PlaceholderAPIIntegration.resolve(p, subText);
						String resolved = PlaceholderAPIIntegration.resolve(p, frame);
						Object[] packets = Booter.getNmsProvider().createTitlePacket(resolvedSub, resolved, 0, 20, 0);
						Booter.getNmsProvider().sendTitles(p, packets);
					}
				} else {
					end();
				}
			};
		}
		if (displayType == DisplayType.ACTION_BAR) {
			this.runnable = () -> {
				if (next()) {
					for (Player p : receivers) {
						if (p == null)
							continue;
						String resolved = PlaceholderAPIIntegration.resolve(p, frame);
						Object packet = Booter.getNmsProvider().createActionbarPacket(resolved);
						Booter.getNmsProvider().sendActionbar(p, packet);
					}
				} else {
					end();
				}
			};
		}
		if (displayType == DisplayType.BOSS_BAR) {
			this.runnable = () -> {
				if (next()) {
					String resolvedFrame = frame;
					if (!receivers.isEmpty()) {
						Player first = receivers.get(0);
						if (first != null) {
							resolvedFrame = PlaceholderAPIIntegration.resolve(first, frame);
						}
					}
					bossBar.setTitle(resolvedFrame);
				} else {
					end();
				}
			};
		}
	}

	@Override
	public void run() {
		if (displayType == DisplayType.BOSS_BAR && bossBar != null) {
			bossBar.setVisible(true);
		}
		task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this.runnable, 0, 20);
	}

	@Override
	public void end() {
		for (Player p : receivers) {
			AmazingTitles.removeAnimationFromCache(p.getUniqueId());
		}
		if (task != null) {
			task.cancel();
		}
		if (bossBar != null) {
			bossBar.setVisible(false);
			bossBar.removeAll();
		}
		receivers.clear();
	}

	private boolean next() {
		if (loopedSeconds >= duration) {
			return false;
		}
		++loopedSeconds;
		return true;
	}

}
