package com.zitemaker.amazingtitles.code.internal.utils;

import net.md_5.bungee.api.ChatColor;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorTranslator {

	/*
	 *
	 * Class Values
	 *
	 */

	private static Method COLOR_FROM_CHAT_COLOR;
	private static Method CHAT_COLOR_FROM_COLOR;
	private static final boolean hexSupport;
	private static final Pattern gradient = Pattern.compile("<(#[A-Za-z0-9]{6})>(.*?)</(#[A-Za-z0-9]{6})>");;
	private static final Pattern legacyGradient = Pattern.compile("<(&[A-Za-z0-9])>(.*?)</(&[A-Za-z0-9])>");;
	private static final Pattern rgb = Pattern.compile("&\\{(#......)}");;

	static {
		try {
			COLOR_FROM_CHAT_COLOR = ChatColor.class.getDeclaredMethod("getColor");
			CHAT_COLOR_FROM_COLOR = ChatColor.class.getDeclaredMethod("of", Color.class);
		} catch (NoSuchMethodException e) {
			COLOR_FROM_CHAT_COLOR = null;
			CHAT_COLOR_FROM_COLOR = null;
		}
		hexSupport = CHAT_COLOR_FROM_COLOR != null;
	}

	/*
	 *
	 * Class API
	 *
	 */

	public static boolean isHexSupport() {
		return hexSupport;
	}

	public static String colorize(String text) {
		return colorize(text, '&');
	}

	public static String colorize(String text, char colorSymbol) {
		Matcher g = gradient.matcher(text);
		Matcher l = legacyGradient.matcher(text);
		Matcher r = rgb.matcher(text);
		while (g.find()) {
			Color start = Color.decode(g.group(1));
			String between = g.group(2);
			Color end = Color.decode(g.group(3));
			if (hexSupport)
				text = text.replace(g.group(0), rgbGradient(between, start, end, colorSymbol));
			else
				text = text.replace(g.group(0), between);
		}
		while (l.find()) {
			char first = l.group(1).charAt(1);
			String between = l.group(2);
			char second = l.group(3).charAt(1);
			ChatColor firstColor = ChatColor.getByChar(first);
			ChatColor secondColor = ChatColor.getByChar(second);
			if (firstColor == null)
				firstColor = ChatColor.WHITE;
			if (secondColor == null)
				secondColor = ChatColor.WHITE;
			if (hexSupport)
				text = text.replace(l.group(0),
						rgbGradient(between, fromChatColor(firstColor), fromChatColor(secondColor), colorSymbol));
			else
				text = text.replace(l.group(0), between);
		}
		while (r.find()) {
			if (hexSupport) {
				ChatColor color = fromColor(Color.decode(r.group(1)));
				text = text.replace(r.group(0), color + "");
			} else {
				text = text.replace(r.group(0), "");
			}
		}
		return ChatColor.translateAlternateColorCodes(colorSymbol, text);
	}

	public static String removeColors(String text) {
		return ChatColor.stripColor(text);
	}

	public static String extractVisibleRange(String coloredText, int visibleStart, int visibleLength) {
		String stripped = removeColors(coloredText);
		int actualStart = visibleStart;
		int actualEnd = visibleStart + visibleLength;

		actualStart = expandToPlaceholderBoundary(stripped, actualStart, false);
		actualEnd = expandToPlaceholderBoundary(stripped, actualEnd, true);

		int coloredStart = mapVisibleToColored(coloredText, actualStart);
		int coloredEnd = mapVisibleToColored(coloredText, actualEnd);

		while (coloredStart > 0) {
			int prev = coloredStart - 1;
			if (prev >= 1 && coloredText.charAt(prev - 1) == '§') {
				coloredStart = prev - 1;
			} else {
				break;
			}
		}

		return coloredText.substring(coloredStart, Math.min(coloredEnd, coloredText.length()));
	}

	private static int expandToPlaceholderBoundary(String text, int pos, boolean forward) {
		if (pos <= 0)
			return 0;
		if (pos >= text.length())
			return text.length();

		int placeholderEnd = -1;
		boolean inPlaceholder = false;
		int phStart = -1;

		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c == '%') {
				if (inPlaceholder) {
					placeholderEnd = i + 1;
					if (pos > phStart && pos < placeholderEnd) {
						return forward ? placeholderEnd : phStart;
					}
					inPlaceholder = false;
				} else {
					inPlaceholder = true;
					phStart = i;
				}
			}
		}

		return pos;
	}

	public static boolean isInsidePlaceholder(String text, int index) {
		if (index <= 0 || index >= text.length())
			return false;

		boolean inPlaceholder = false;
		int phStart = -1;

		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c == '%') {
				if (inPlaceholder) {
					int phEnd = i + 1;
					if (index > phStart && index < phEnd) {
						return true;
					}
					inPlaceholder = false;
				} else {
					inPlaceholder = true;
					phStart = i;
				}
			}
		}
		return false;
	}

	private static int mapVisibleToColored(String coloredText, int visiblePos) {
		int visible = 0;
		boolean inColorCode = false;
		for (int i = 0; i < coloredText.length(); i++) {
			if (visible == visiblePos)
				return i;
			char c = coloredText.charAt(i);
			if (c == '§') {
				inColorCode = true;
				continue;
			}
			if (inColorCode) {
				inColorCode = false;
				continue;
			}
			visible++;
		}
		return coloredText.length();
	}

	public static List<Character> charactersWithoutColors(String text) {
		text = removeColors(text);
		final List<Character> result = new ArrayList<>();
		for (char var : text.toCharArray()) {
			result.add(var);
		}
		return result;
	}

	public static List<String> charactersWithColors(String text) {
		return charactersWithColors(text, '§');
	}

	public static List<String> charactersWithColors(String text, char colorSymbol) {
		List<String> result = new ArrayList<>();
		StringBuilder colorPrefix = new StringBuilder();
		StringBuilder placeholderBuffer = new StringBuilder();
		boolean inPlaceholder = false;
		boolean expectingColorCode = false;

		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);

			if (expectingColorCode) {
				colorPrefix.append(c);
				expectingColorCode = false;
				continue;
			}

			if (c == colorSymbol) {
				if (!inPlaceholder)
					colorPrefix = new StringBuilder();
				colorPrefix.append(c);
				expectingColorCode = true;
				continue;
			}

			if (c == '%') {
				if (inPlaceholder) {
					placeholderBuffer.append(c);
					result.add(colorPrefix.toString() + placeholderBuffer.toString());
					placeholderBuffer = new StringBuilder();
					inPlaceholder = false;
				} else {
					inPlaceholder = true;
					placeholderBuffer.append(c);
				}
			} else if (inPlaceholder) {
				placeholderBuffer.append(c);
			} else {
				result.add(colorPrefix.toString() + c);
			}
		}

		if (placeholderBuffer.length() > 0) {
			for (char c : placeholderBuffer.toString().toCharArray()) {
				result.add(colorPrefix.toString() + c);
			}
		}
		return result;
	}

	/*
	 *
	 * Class Utilities
	 *
	 */

	private static String rgbGradient(String text, Color start, Color end, char colorSymbol) {
		text = ChatColor.translateAlternateColorCodes(colorSymbol, text);
		List<String> tokens = tokenizePreservingPlaceholders(text);
		if (tokens.isEmpty())
			return text;
		if (tokens.size() == 1)
			return fromColor(end) + tokens.get(0);
		double[] red = linear(start.getRed(), end.getRed(), tokens.size());
		double[] green = linear(start.getGreen(), end.getGreen(), tokens.size());
		double[] blue = linear(start.getBlue(), end.getBlue(), tokens.size());
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < tokens.size(); i++) {
			String token = tokens.get(i);
			ChatColor color = fromColor(
					new Color((int) Math.round(red[i]), (int) Math.round(green[i]), (int) Math.round(blue[i])));
			builder.append(color).append(token.replace("§r", ""));
		}
		return builder.toString();
	}

	private static List<String> tokenizePreservingPlaceholders(String text) {
		List<String> tokens = new ArrayList<>();
		StringBuilder colorPrefix = new StringBuilder();
		StringBuilder placeholderBuffer = new StringBuilder();
		boolean inPlaceholder = false;
		boolean expectingColorCode = false;

		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);

			if (expectingColorCode) {
				colorPrefix.append(c);
				expectingColorCode = false;
				continue;
			}

			if (c == '§') {
				if (!inPlaceholder)
					colorPrefix = new StringBuilder();
				colorPrefix.append(c);
				expectingColorCode = true;
				continue;
			}

			if (c == '%') {
				if (inPlaceholder) {
					placeholderBuffer.append(c);
					tokens.add(colorPrefix.toString() + placeholderBuffer.toString());
					placeholderBuffer = new StringBuilder();
					inPlaceholder = false;
				} else {
					inPlaceholder = true;
					placeholderBuffer.append(c);
				}
			} else if (inPlaceholder) {
				placeholderBuffer.append(c);
			} else {
				tokens.add(colorPrefix.toString() + c);
			}
		}

		if (placeholderBuffer.length() > 0) {
			for (char c : placeholderBuffer.toString().toCharArray()) {
				tokens.add(colorPrefix.toString() + c);
			}
		}
		return tokens;
	}

	private static double[] linear(double from, double to, int max) {
		final double[] res = new double[max];
		for (int i = 0; i < max; i++) {
			res[i] = from + i * ((to - from) / (max - 1));
		}
		return res;
	}

	public static Color fromChatColor(ChatColor color) {
		try {
			return (Color) COLOR_FROM_CHAT_COLOR.invoke(color);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	public static ChatColor fromColor(Color color) {
		try {
			return (ChatColor) CHAT_COLOR_FROM_COLOR.invoke(null, color);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

}