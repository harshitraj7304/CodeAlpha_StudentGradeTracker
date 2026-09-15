package com.codealpha.gradetracker.ui.chart;

import com.codealpha.gradetracker.model.AssessmentCategory;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Map;

/**
 * Custom Swing component that renders assessment category breakdown pie/donut charts.
 */
public class CategoryBreakdownChart extends JPanel {

    private Map<AssessmentCategory, Double> categoryPercentages;

    private static final Color[] SLICE_COLORS = {
            new Color(52, 152, 219),  // Assignments (Blue)
            new Color(155, 89, 182),  // Quiz (Purple)
            new Color(241, 196, 15),  // Midterm (Yellow)
            new Color(231, 76, 60),   // Final Exam (Red)
            new Color(46, 204, 113),  // Project (Green)
            new Color(149, 165, 166)  // Other (Gray)
    };

    public CategoryBreakdownChart() {
        setPreferredSize(new Dimension(400, 260));
        setBackground(new Color(245, 247, 250));
    }

    public void setData(Map<AssessmentCategory, Double> data) {
        this.categoryPercentages = data;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Background Card
        g2d.setColor(Color.WHITE);
        g2d.fillRoundRect(10, 10, width - 20, height - 20, 16, 16);
        g2d.setColor(new Color(220, 225, 230));
        g2d.drawRoundRect(10, 10, width - 20, height - 20, 16, 16);

        // Title
        g2d.setColor(new Color(44, 62, 80));
        g2d.setFont(new Font("SansSerif", Font.BOLD, 15));
        g2d.drawString("Assessment Category Breakdown", 25, 35);

        if (categoryPercentages == null || categoryPercentages.isEmpty()) {
            g2d.setFont(new Font("SansSerif", Font.ITALIC, 13));
            g2d.setColor(Color.GRAY);
            g2d.drawString("No assessment breakdown available", 25, 70);
            return;
        }

        double total = 0.0;
        for (Double val : categoryPercentages.values()) {
            total += val;
        }

        int pieDiameter = Math.min(width / 2 - 20, height - 80);
        int pieX = 30;
        int pieY = 55;

        double startAngle = 0.0;
        int colorIdx = 0;

        int legendX = pieX + pieDiameter + 30;
        int legendY = 65;

        g2d.setFont(new Font("SansSerif", Font.PLAIN, 12));

        for (Map.Entry<AssessmentCategory, Double> entry : categoryPercentages.entrySet()) {
            double val = entry.getValue();
            if (val <= 0) continue;

            double angle = (val / (total > 0 ? total : 1.0)) * 360.0;
            Color sliceColor = SLICE_COLORS[colorIdx % SLICE_COLORS.length];

            // Draw Pie Arc
            g2d.setColor(sliceColor);
            g2d.fillArc(pieX, pieY, pieDiameter, pieDiameter, (int) Math.round(startAngle), (int) Math.round(angle));

            // Draw Legend Entry
            g2d.fillRect(legendX, legendY, 12, 12);
            g2d.setColor(new Color(44, 62, 80));
            double pct = total > 0 ? (val / total) * 100.0 : 0.0;
            g2d.drawString(String.format("%s (%.1f%%)", entry.getKey().getDisplayName(), pct), legendX + 20, legendY + 11);

            legendY += 24;
            startAngle += angle;
            colorIdx++;
        }

        // Draw inner donut hole for modern aesthetic
        int donutSize = pieDiameter / 2;
        int donutX = pieX + (pieDiameter - donutSize) / 2;
        int donutY = pieY + (pieDiameter - donutSize) / 2;
        g2d.setColor(Color.WHITE);
        g2d.fillOval(donutX, donutY, donutSize, donutSize);
    }
}
