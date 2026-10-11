package ru.bizonrelax.detector.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;

import javax.swing.AbstractButton;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Applies the main-window appearance (background color with automatic font
 * contrast, UI font size) to any container: About window, Statistics window,
 * editor window, extended window. Shared so secondary windows always match
 * the main window look-and-feel settings.
 */
public final class AppearanceUtil {

    private AppearanceUtil() {
    }

    /** Background color currently stored in configuration. */
    public static Color backgroundOf(ru.bizonrelax.detector.config.AppConfig config) {
        return new Color(config.getBgR(), config.getBgG(), config.getBgB());
    }

    /**
     * Recursively paints the container with the configured background and
     * readable foreground, and sets component fonts to the given UI size.
     */
    public static void apply(Container root, Color bg, int uiFontSize) {
        Color fg = ContrastUtil.readableTextColor(bg);
        applyRecursive(root, bg, fg, uiFontSize);
    }

    private static void applyRecursive(Container c, Color bg, Color fg, int uiFontSize) {
        c.setBackground(bg);
        c.setForeground(fg);
        if (c instanceof JLabel) {
            ((JLabel) c).setOpaque(false);
        }
        for (Component comp : c.getComponents()) {
            if (comp instanceof AbstractButton || comp instanceof JLabel
                    || comp instanceof JSpinner || comp instanceof JTextField) {
                comp.setForeground(fg);
                Font f = comp.getFont();
                comp.setFont(f.deriveFont((float) uiFontSize));
            }
            if (comp instanceof Container) {
                applyRecursive((Container) comp, bg, fg, uiFontSize);
            }
        }
    }

    /** Convenience: applies current config appearance to a container's content pane. */
    public static void applyTo(Container contentPane,
                               ru.bizonrelax.detector.config.AppConfig config) {
        apply(contentPane, backgroundOf(config), config.getFontSize());
    }

    /** Runs the appearance update on the EDT for an already-visible window. */
    public static void refreshLater(final Container contentPane,
                                    final ru.bizonrelax.detector.config.AppConfig config) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                applyTo(contentPane, config);
                contentPane.revalidate();
                contentPane.repaint();
            }
        });
    }
}
