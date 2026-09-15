package com.codealpha.gradetracker.ui.theme;

import java.awt.Color;
import java.awt.Font;

/**
 * Visual Design System Theme Manager for Light Mode and Dark Mode palettes.
 */
public class ThemeManager {

    private static boolean isDarkMode = true;

    // Dark Mode Palette (Navy / Slate HSL tailored)
    public static final Color DARK_BG = new Color(15, 23, 42);          // #0F172A
    public static final Color DARK_CARD = new Color(30, 41, 59);        // #1E293B
    public static final Color DARK_CARD_BORDER = new Color(51, 65, 85);   // #334155
    public static final Color DARK_TEXT_MAIN = new Color(248, 250, 252); // #F8FAFC
    public static final Color DARK_TEXT_MUTED = new Color(148, 163, 184);// #94A3B8

    // Light Mode Palette (Slate Light tailored)
    public static final Color LIGHT_BG = new Color(248, 250, 252);      // #F8FAFC
    public static final Color LIGHT_CARD = new Color(255, 255, 255);     // #FFFFFF
    public static final Color LIGHT_CARD_BORDER = new Color(226, 232, 240);// #E2E8F0
    public static final Color LIGHT_TEXT_MAIN = new Color(15, 23, 42);    // #0F172A
    public static final Color LIGHT_TEXT_MUTED = new Color(100, 116, 139); // #64748B

    // Brand Accent Colors
    public static final Color PRIMARY_ACCENT = new Color(59, 130, 246);  // Blue #3B82F6
    public static final Color SUCCESS_ACCENT = new Color(16, 185, 129);  // Emerald #10B981
    public static final Color WARNING_ACCENT = new Color(245, 158, 11);  // Amber #F59E0B
    public static final Color DANGER_ACCENT = new Color(239, 68, 68);    // Rose #EF4444
    public static final Color PURPLE_ACCENT = new Color(139, 92, 246);  // Violet #8B5CF6

    // Typography System
    public static final Font FONT_HEADER = new Font("SansSerif", Font.BOLD, 18);
    public static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 16);
    public static final Font FONT_SUBTITLE = new Font("SansSerif", Font.BOLD, 14);
    public static final Font FONT_BODY = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FONT_BOLD_BODY = new Font("SansSerif", Font.BOLD, 13);
    public static final Font FONT_VALUE = new Font("SansSerif", Font.BOLD, 22);

    public static boolean isDarkMode() {
        return isDarkMode;
    }

    public static void setDarkMode(boolean darkMode) {
        isDarkMode = darkMode;
    }

    public static void toggleTheme() {
        isDarkMode = !isDarkMode;
    }

    public static Color getBackgroundColor() {
        return isDarkMode ? DARK_BG : LIGHT_BG;
    }

    public static Color getCardColor() {
        return isDarkMode ? DARK_CARD : LIGHT_CARD;
    }

    public static Color getCardBorderColor() {
        return isDarkMode ? DARK_CARD_BORDER : LIGHT_CARD_BORDER;
    }

    public static Color getMainTextColor() {
        return isDarkMode ? DARK_TEXT_MAIN : LIGHT_TEXT_MAIN;
    }

    public static Color getMutedTextColor() {
        return isDarkMode ? DARK_TEXT_MUTED : LIGHT_TEXT_MUTED;
    }
}
