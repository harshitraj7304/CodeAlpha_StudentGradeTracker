package com.codealpha.gradetracker.ui.components;

import com.codealpha.gradetracker.ui.theme.ThemeManager;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Collapsible Modern Sidebar Navigation component with active indicators.
 */
public class SidebarNav extends JPanel {

    private final Consumer<Integer> onTabSelected;
    private final List<JButton> navButtons;
    private int activeIndex = 0;

    private static final String[] TAB_NAMES = {
            "  📊  Dashboard Overview  ",
            "  🎓  Student Directory  ",
            "  📚  Semester Gradebook ",
            "  🏆  Class Leaderboard   ",
            "  📈  Academic Analytics  "
    };

    public SidebarNav(Consumer<Integer> onTabSelected) {
        this.onTabSelected = onTabSelected;
        this.navButtons = new ArrayList<>();

        setPreferredSize(new Dimension(240, 0));
        setLayout(new BorderLayout());
        updateThemeColors();

        initUI();
    }

    private void initUI() {
        // App Logo Header
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setOpaque(false);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel logoLabel = new JLabel("B.TECH OS");
        logoLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        logoLabel.setForeground(ThemeManager.PRIMARY_ACCENT);

        JLabel subLogo = new JLabel("Academic Performance");
        subLogo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subLogo.setForeground(ThemeManager.getMutedTextColor());

        logoPanel.add(logoLabel, BorderLayout.NORTH);
        logoPanel.add(subLogo, BorderLayout.SOUTH);

        add(logoPanel, BorderLayout.NORTH);

        // Nav Buttons Menu
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (int i = 0; i < TAB_NAMES.length; i++) {
            final int index = i;
            JButton btn = new JButton(TAB_NAMES[i]);
            btn.setFont(new Font("SansSerif", Font.BOLD, 13));
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setMaximumSize(new Dimension(220, 42));
            btn.setPreferredSize(new Dimension(220, 42));
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setOpaque(true);

            btn.addActionListener(e -> selectTab(index));

            navButtons.add(btn);
            menuPanel.add(btn);
            menuPanel.add(javax.swing.Box.createRigidArea(new Dimension(0, 8)));
        }

        add(menuPanel, BorderLayout.CENTER);
        updateButtonStyles();
    }

    public void selectTab(int index) {
        this.activeIndex = index;
        updateButtonStyles();
        if (onTabSelected != null) {
            onTabSelected.accept(index);
        }
    }

    public void updateThemeColors() {
        setBackground(ThemeManager.getCardColor());
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, ThemeManager.getCardBorderColor()));
        updateButtonStyles();
    }

    private void updateButtonStyles() {
        for (int i = 0; i < navButtons.size(); i++) {
            JButton btn = navButtons.get(i);
            if (i == activeIndex) {
                btn.setBackground(ThemeManager.PRIMARY_ACCENT);
                btn.setForeground(Color.WHITE);
            } else {
                btn.setBackground(ThemeManager.getCardColor());
                btn.setForeground(ThemeManager.getMainTextColor());
            }
        }
        repaint();
    }
}
