package ru.bizonrelax.detector.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.nio.charset.Charset;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ru.bizonrelax.detector.config.WindowConfigStore;
import ru.bizonrelax.detector.config.WindowState;
import ru.bizonrelax.detector.model.AnalysisResult;
import ru.bizonrelax.detector.model.CyrillicCharSet;
import ru.bizonrelax.detector.model.TextAnalyzer;

/**
 * Main Swing window. Delegates all analysis to model package,
 * all persistence to config package. No business logic here.
 */
public final class MainWindow extends JFrame {

    private static final String APP_TITLE = "Velocipenna — Cyrillic Homoglyph Detector";

    private final TextAnalyzer analyzer = new TextAnalyzer();
    private final WindowConfigStore configStore = new WindowConfigStore();
    private final Charset charset = Charset.forName("UTF-8");

    private final JTextArea reportArea = new JTextArea();
    private final JLabel statsLabel = new JLabel(" ");
    private final JSpinner fontSpinner;
    private File lastFile;
    private boolean restoring = true;

    public MainWindow() {
        super(APP_TITLE);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());

        WindowState saved = configStore.load();

        // ---- toolbar ----
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton openButton = new JButton("Open file…");
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseAndAnalyze();
            }
        });
        toolbar.add(openButton);

        toolbar.add(Box.createHorizontalStrut(20));
        toolbar.add(new JLabel("Font size:"));
        fontSpinner = new JSpinner(new SpinnerNumberModel(
                Integer.valueOf(saved.getFontSize()), Integer.valueOf(8), Integer.valueOf(48), Integer.valueOf(1)));
        fontSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                applyFontSize();
            }
        });
        toolbar.add(fontSpinner);
        add(toolbar, BorderLayout.NORTH);

        // ---- report area ----
        reportArea.setEditable(false);
        reportArea.setLineWrap(false);
        JScrollPane scroll = new JScrollPane(reportArea);
        scroll.setPreferredSize(new Dimension(900, 600));
        add(scroll, BorderLayout.CENTER);

        // ---- status bar with density metric ----
        JPanel status = new JPanel();
        status.setLayout(new BoxLayout(status, BoxLayout.X_AXIS));
        status.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        status.add(statsLabel);
        add(status, BorderLayout.SOUTH);

        setSize(saved.getWidth(), saved.getHeight());
        if (saved.hasPosition()) {
            setLocation(saved.getX(), saved.getY());
        } else {
            setLocationRelativeTo(null);
        }
        applyFontSize();

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // no-op, persisted on close
            }
        });

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                persistAndExit();
            }
        });
    }

    private void chooseAndAnalyze() {
        JFileChooser chooser = new JFileChooser();
        if (lastFile != null) {
            chooser.setCurrentDirectory(lastFile.getParentFile());
        }
        int option = chooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            lastFile = chooser.getSelectedFile();
            runAnalysis(lastFile);
        }
    }

    private void runAnalysis(File file) {
        try {
            AnalysisResult result = analyzer.analyzeFile(file, charset);
            renderResult(file, result);
        } catch (Exception ex) {
            reportArea.setText("Failed to analyze file:\n" + ex.getMessage());
            statsLabel.setText("Error");
        }
    }

    private void renderResult(File file, AnalysisResult r) {
        StringBuilder sb = new StringBuilder();
        sb.append("File: ").append(file.getAbsolutePath()).append('\n');
        sb.append("Total characters: ").append(r.getTotalChars()).append('\n');
        sb.append("Cyrillic characters: ").append(r.getCyrillicCount()).append('\n');
        sb.append(String.format("Density (КИС): %.2f%%%n", r.getDensityPercent()));
        sb.append("Pure Cyrillic words: ").append(r.getPureCyrillicWords()).append('\n');
        sb.append("Mixed-script words: ").append(r.getMixedScriptWords()).append("\n\n");

        sb.append("Per-letter tallies (upper / lower):\n");
        int size = CyrillicCharSet.baseSize();
        for (int i = 0; i < size; i++) {
            int u = r.getUpperCount(i);
            int l = r.getLowerCount(i);
            if (u > 0 || l > 0) {
                sb.append("  ").append(CyrillicCharSet.UPPER_BASE[i])
                  .append("/").append(CyrillicCharSet.LOWER_BASE[i])
                  .append(": ").append(u).append(" / ").append(l).append('\n');
            }
        }
        if (r.getYoUpper() > 0 || r.getYoLower() > 0) {
            sb.append("  Ё/ё: ").append(r.getYoUpper()).append(" / ").append(r.getYoLower()).append('\n');
        }
        sb.append('\n').append(r.getMixedScriptReport());

        reportArea.setText(sb.toString());
        reportArea.setCaretPosition(0);
        statsLabel.setText(String.format("Density: %.2f%%  |  Mixed words: %d",
                r.getDensityPercent(), r.getMixedScriptWords()));
    }

    private void applyFontSize() {
        int size = ((Number) fontSpinner.getValue()).intValue();
        reportArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, size));
        statsLabel.setFont(statsLabel.getFont().deriveFont((float) size));
    }

    private void persistAndExit() {
        if (!restoring) {
            WindowState state = new WindowState(
                    getWidth(), getHeight(), getX(), getY(),
                    ((Number) fontSpinner.getValue()).intValue());
            configStore.save(state);
        }
        dispose();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // let the window dispose before exiting
            }
        });
        System.exit(0);
    }

    /** Marks restore phase complete so early resize events are not misread. */
    public void finishRestore() {
        restoring = false;
    }
}
