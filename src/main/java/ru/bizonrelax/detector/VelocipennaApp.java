package ru.bizonrelax.detector;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import ru.bizonrelax.detector.view.MainWindow;

/**
 * Application entry point.
 */
public final class VelocipennaApp {

    private VelocipennaApp() {
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // fall back to default L&F
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainWindow window = new MainWindow();
                window.setVisible(true);
                window.finishRestore();
            }
        });
    }
}
