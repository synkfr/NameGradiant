package com.gradientcolor.namegradient.util;

import net.md_5.bungee.api.ChatColor;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtil {

    private static final Pattern HEX_PATTERN = Pattern.compile("#[a-fA-F0-9]{6}");
    private static final Pattern AMPERSAND_HEX_PATTERN = Pattern.compile("&#([a-fA-F0-9]{6})");
    private static final Pattern COLOR_CODE_PATTERN = Pattern.compile("(?i)[§&][0-9A-FK-OR]");

    /**
     * Colorize a string with legacy color codes and hex colors
     * Supports both #RRGGBB and &#RRGGBB formats
     */
    public static String colorize(String message) {
        if (message == null)
            return "";

        // Process &#RRGGBB format first (converts to MiniMessage)
        Matcher ampMatcher = AMPERSAND_HEX_PATTERN.matcher(message);
        StringBuffer ampBuffer = new StringBuffer();

        while (ampMatcher.find()) {
            String hex = ampMatcher.group(1);
            ampMatcher.appendReplacement(ampBuffer, hexToMiniMessage("#" + hex));
        }
        ampMatcher.appendTail(ampBuffer);
        message = ampBuffer.toString();

        // Process #RRGGBB format
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String hex = matcher.group();
            matcher.appendReplacement(buffer, hexToMiniMessage(hex));
        }
        matcher.appendTail(buffer);

        // We return the MiniMessage-compatible string. 
        // We do NOT translate alternate color codes here anymore, 
        // because we want to preserve MiniMessage tags.
        // Instead, we replace & with legacy tags if needed, 
        // but for NameGradiant, we want to stay in MiniMessage as much as possible.
        return buffer.toString().replace("&", "§"); 
    }

    /**
     * Convert hex color to MiniMessage tag
     */
    public static String hexToMiniMessage(String hex) {
        return "<" + hex + ">";
    }

    /**
     * Apply gradient to text
     */
    public static String applyGradient(String text, String startHex, String endHex) {
        if (text == null || text.isEmpty())
            return text;

        Color startColor = hexToColor(startHex);
        Color endColor = hexToColor(endHex);

        StringBuilder result = new StringBuilder();
        int length = text.length();

        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);

            // Skip spaces but include them in output
            if (c == ' ') {
                result.append(' ');
                continue;
            }

            float ratio = (float) i / Math.max(1, length - 1);
            Color interpolated = interpolateColor(startColor, endColor, ratio);
            String hex = colorToHex(interpolated);

            result.append(hexToMiniMessage(hex)).append(c);
        }

        return result.toString();
    }

    /**
     * Convert hex string to Color object
     */
    public static Color hexToColor(String hex) {
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }
        return new Color(
                Integer.parseInt(hex.substring(0, 2), 16),
                Integer.parseInt(hex.substring(2, 4), 16),
                Integer.parseInt(hex.substring(4, 6), 16));
    }

    /**
     * Convert Color object to hex string
     */
    public static String colorToHex(Color color) {
        return String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
    }

    /**
     * Interpolate between two colors
     */
    public static Color interpolateColor(Color start, Color end, float ratio) {
        int red = (int) (start.getRed() + ratio * (end.getRed() - start.getRed()));
        int green = (int) (start.getGreen() + ratio * (end.getGreen() - start.getGreen()));
        int blue = (int) (start.getBlue() + ratio * (end.getBlue() - start.getBlue()));

        // Clamp values
        red = Math.max(0, Math.min(255, red));
        green = Math.max(0, Math.min(255, green));
        blue = Math.max(0, Math.min(255, blue));

        return new Color(red, green, blue);
    }

    /**
     * Check if a string contains color codes
     */
    public static boolean hasColorCodes(String text) {
        if (text == null)
            return false;
        return COLOR_CODE_PATTERN.matcher(text).find() || HEX_PATTERN.matcher(text).find();
    }

    /**
     * Strip all color codes from a string
     */
    public static String stripColors(String text) {
        if (text == null)
            return null;
        return ChatColor.stripColor(colorize(text));
    }

    /**
     * Create a preview of the gradient (short sample)
     */
    public static String createGradientPreview(String startHex, String endHex) {
        return applyGradient("■■■■■", startHex, endHex);
    }
}
