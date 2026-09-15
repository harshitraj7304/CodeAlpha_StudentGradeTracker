package com.codealpha.gradetracker.ui.theme;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

/**
 * Shared Design System Factory providing UI styling, badges, card containers,
 * table renderers, and empty state components.
 */
public class UIFactory {

    /**
     * Creates a styled action button matching the design system theme.
     */
    public static JButton createButton(String text, Color bgColor, ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(ThemeManager.FONT_BOLD_BODY);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        if (listener != null) {
            button.addActionListener(listener);
        }
        return button;
    }

    /**
     * Creates a KPI Card container.
     */
    public static JPanel createKpiCard(String title, JLabel valueLabel, String subtitle, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(10, 6));
        card.setBackground(ThemeManager.getCardColor());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, accentColor),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ThemeManager.getCardBorderColor(), 1),
                        BorderFactory.createEmptyBorder(14, 16, 14, 16)
                )
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(ThemeManager.FONT_SUBTITLE);
        titleLabel.setForeground(ThemeManager.getMutedTextColor());

        valueLabel.setFont(ThemeManager.FONT_VALUE);
        valueLabel.setForeground(ThemeManager.getMainTextColor());

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        if (subtitle != null && !subtitle.isEmpty()) {
            JLabel subLabel = new JLabel(subtitle);
            subLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
            subLabel.setForeground(ThemeManager.getMutedTextColor());
            card.add(subLabel, BorderLayout.SOUTH);
        }

        return card;
    }

    /**
     * Creates an Empty State placeholder card for views with no data.
     */
    public static JPanel createEmptyStatePanel(String iconStr, String title, String description) {
        JPanel panel = new JPanel(new GridLayout(3, 1, 5, 5));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));

        JLabel iconLabel = new JLabel(iconStr, SwingConstants.CENTER);
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 36));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(ThemeManager.FONT_TITLE);
        titleLabel.setForeground(ThemeManager.getMainTextColor());

        JLabel descLabel = new JLabel(description, SwingConstants.CENTER);
        descLabel.setFont(ThemeManager.FONT_BODY);
        descLabel.setForeground(ThemeManager.getMutedTextColor());

        panel.add(iconLabel);
        panel.add(titleLabel);
        panel.add(descLabel);

        return panel;
    }

    /**
     * Custom Cell Renderer for Status Badges (PASS, PROMOTED, AT RISK, BACKLOG).
     */
    public static DefaultTableCellRenderer getStatusBadgeRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setFont(new Font("SansSerif", Font.BOLD, 12));

                String text = value != null ? value.toString() : "";
                if (text.contains("PASS") && !text.contains("BACKLOG")) {
                    label.setForeground(ThemeManager.SUCCESS_ACCENT);
                } else if (text.contains("PROMOTED") || text.contains("AT RISK")) {
                    label.setForeground(ThemeManager.WARNING_ACCENT);
                } else if (text.contains("BACKLOG") || text.contains("FAIL")) {
                    label.setForeground(ThemeManager.DANGER_ACCENT);
                } else {
                    label.setForeground(ThemeManager.getMainTextColor());
                }

                if (!isSelected) {
                    label.setBackground(row % 2 == 0 ? ThemeManager.getCardColor() : new Color(248, 250, 252));
                }
                return label;
            }
        };
    }
}
