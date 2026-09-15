package com.codealpha.gradetracker;

import com.codealpha.gradetracker.ui.ConsoleInterface;
import com.codealpha.gradetracker.ui.MainFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main launcher entry point for CodeAlpha Student Grade Tracker.
 */
public class Main {

    public static void main(String[] args) {
        // Check for CLI mode flag
        if (args.length > 0 && "--cli".equalsIgnoreCase(args[0])) {
            ConsoleInterface console = new ConsoleInterface();
            console.start();
            return;
        }

        // Set Look and Feel to System native UI for sleek desktop presentation
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
        }

        // Launch Application on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
