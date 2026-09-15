package com.codealpha.gradetracker.ui.chart;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import java.util.Map;

/**
 * Custom Swing component that renders grade distribution bar charts using vector Graphics2D rendering.
 */
public class GradeDistributionChart extends JPanel {

    private Map<String, Integer> distributionData;
    private static final String[] GRADE_KEYS = {"A+", "A", "A-", "B+", "B", "B-", "C+", "C", "D", "F"};
    private static final Color[] BAR_COLORS = {
            new Color(46, 204, 113),  // A+ (Green)
            new Color(39, 174, 96),   // A
            new Color(52, 152, 219),  // A- (Blue)
            new Color(41, 128, 185),  // B+
            new Color(155, 89, 182),  // B (Purple)
            new Color(142, 68, 173),  // B-
            new Color(241, 196, 15),  // C+ (Yellow)
            new Color(243, 156, 18),  // C (Orange)
            new Color(230, 126, 34),  // D
            new Color(231, 76, 60)    // F (Red)
    };

    public GradeDistributionChart() {
        setPreferredSize(new Dimension(500, 260));
        setBackground(new Color(245, 247, 250));
    }

    public void setData(Map<String, Integer> data) {
        this.distributionData = data;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Background panel card
        g2d.setColor(Color.WHITE);
        g2d.fillRoundRect(10, 10, width - 20, height - 20, 16, 16);
        g2d.setColor(new Color(220, 225, 230));
        g2d.drawRoundRect(10, 10, width - 20, height - 20, 16, 16);

        // Chart Title
        g2d.setColor(new Color(44, 62, 80));
        g2d.setFont(new Font("SansSerif", Font.BOLD, 15));
        g2d.drawString("Grade Distribution Breakdown", 25, 35);

        if (distributionData == null || distributionData.isEmpty()) {
            g2d.setFont(new Font("SansSerif", Font.ITALIC, 13));
            g2d.setColor(Color.GRAY);
            g2d.drawString("No grade data available to plot chart", 25, 70);
            return;
        }

        int maxVal = 1;
        for (Integer count : distributionData.values()) {
            if (count > maxVal) maxVal = count;
        }

        int chartLeft = 50;
        int chartRight = width - 40;
        int chartTop = 60;
        int chartBottom = height - 50;
        int availableWidth = chartRight - chartLeft;
        int availableHeight = chartBottom - chartTop;

        int numBars = GRADE_KEYS.length;
        int barWidth = Math.max(12, (availableWidth / numBars) - 10);
        int spacing = (availableWidth - (numBars * barWidth)) / (numBars + 1);

        // Draw horizontal grid lines
        g2d.setColor(new Color(235, 240, 245));
        g2d.setFont(new Font("SansSerif", Font.PLAIN, 10));
        for (int i = 0; i <= 4; i++) {
            int y = chartBottom - (int) ((i / 4.0) * availableHeight);
            int gridValue = (int) Math.round((i / 4.0) * maxVal);
            g2d.drawLine(chartLeft, y, chartRight, y);
            g2d.setColor(new Color(120, 130, 140));
            g2d.drawString(String.valueOf(gridValue), chartLeft - 22, y + 4);
            g2d.setColor(new Color(235, 240, 245));
        }

        // Render bars
        g2d.setFont(new Font("SansSerif", Font.BOLD, 11));
        FontMetrics fm = g2d.getFontMetrics();

        for (int i = 0; i < numBars; i++) {
            String gradeKey = GRADE_KEYS[i];
            int count = distributionData.getOrDefault(gradeKey, 0);

            int barHeight = (int) (((double) count / maxVal) * availableHeight);
            int x = chartLeft + spacing + i * (barWidth + spacing);
            int y = chartBottom - barHeight;

            // Bar shape
            Color barColor = BAR_COLORS[i % BAR_COLORS.length];
            g2d.setColor(barColor);
            g2d.fillRoundRect(x, y, barWidth, Math.max(barHeight, 4), 6, 6);

            // Draw Value count above bar if > 0
            if (count > 0) {
                g2d.setColor(new Color(44, 62, 80));
                String countStr = String.valueOf(count);
                int countX = x + (barWidth - fm.stringWidth(countStr)) / 2;
                g2d.drawString(countStr, countX, Math.max(y - 5, chartTop - 5));
            }

            // Draw Grade Label under bar
            g2d.setColor(new Color(100, 110, 120));
            int labelX = x + (barWidth - fm.stringWidth(gradeKey)) / 2;
            g2d.drawString(gradeKey, labelX, chartBottom + 18);
        }
    }
}
