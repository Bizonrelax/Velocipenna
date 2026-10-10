package ru.bizonrelax.detector.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.util.List;

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
import ru.bizonrelax.detector.model.ReportExporter;
import ru.bizonrelax.detector.model.TextAnalyzer;

/**
 * Main Swing window. Delegates all analysis to model package,
 * all persistence to config package. No business logic here.
 * Supports: file chooser, drag-and-drop of text files,
 * automatic charset detection (UTF-8 / cp1251), report export.
 */
public final class MainWindow extends JFrame {

    private static final String APP_TITLE = "Velocipenna — Cyrillic Homoglyph Detector";

    private final TextAnalyzer analyzer = new TextAnalyzer();
    private final WindowConfigStore configStore = new WindowConfigStore();

    private final JTextArea reportArea = new JTextArea();
    private final JLabel statsLabel = new JLabel(" \u041f\u0435\u0440\u0435\u0442\u0430\u0449\u0438\u0442\u0435 .txt-\u0444\u0430\u0439\u043b \u0441\u044e\u0434\u0430 \u0438\u043b\u0438 \u043d\u0430\u0436\u043c\u0438\u0442\u0435 \"\u041e\u0442\u043a\u0440\u044b\u0442\u044c\u2026\"");
    private final JSpinner fontSpinner;
    private File lastFile;
    private String currentReport = "";
    private boolean restoring = true;

    public MainWindow() {
        super(APP_TITLE);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());

        WindowState saved = configStore.load();

        // ---- toolbar ----
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton openButton = new JButton("\u041e\u0442\u043a\u0440\u044b\u0442\u044c\u2026");
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseAndAnalyze();
            }
        });
        toolbar.add(openButton);

        JButton exportButton = new JButton("\u042d\u043a\u0441\u043f\u043e\u0440\u0442 \u043e\u0442\u0447\u0451\u0442\u0430\u2026");
        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportReport();
            }
        });
        toolbar.add(exportButton);

        toolbar.add(Box.createHorizontalStrut(20));
        toolbar.add(new JLabel("\u0428\u0440\u0438\u0444\u0442:"));
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

        // ---- drag & drop of text files onto the whole window ----
        new DropTarget(this, DnDConstants.ACTION_COPY, new java.awt.dnd.DropTargetAdapter() {
            @Override
            @SuppressWarnings("unchecked")
            public void drop(DropTargetDropEvent event) {
                try {
                    event.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> dropped = (List<File>) event.getTransferable()
                            .getTransferData(DataFlavor.javaFileListFlavor);
                    if (dropped != null && !dropped.isEmpty()) {
                        File f = dropped.get(0);
                        if (f.isFile()) {
                            lastFile = f;
                            runAnalysis(f);
                        }
                    }
                    event.dropComplete(true);
                } catch (Exception ex) {
                    event.dropComplete(false);
                }
            }
        }, true);

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

    private void exportReport() {
        if (currentReport.isEmpty()) {
            statsLabel.setText("\u041d\u0435\u0447\u0435\u0433\u043e \u044d\u043a\u0441\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u0442\u044c: \u0441\u043d\u0430\u0447\u0430\u043b\u0430 \u043e\u0442\u0430\u043d\u0430\u043b\u0438\u0437\u0438\u0440\u0443\u0439\u0442\u0435 \u0444\u0430\u0439\u043b");
            return;
        }
        JFileChooser chooser = new JFileChooser();
        String base = lastFile != null
                ? lastFile.getName().replaceAll("(?i)\\.[^.]+$", "") + "_report.txt"
                : "velocipenna_report.txt";
        chooser.setSelectedFile(new File(base));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File target = chooser.getSelectedFile();
            try {
                ReportExporter.export(currentReport, target);
                statsLabel.setText("\u041e\u0442\u0447\u0451\u0442 \u0441\u043e\u0445\u0440\u0430\u043d\u0451\u043d: " + target.getAbsolutePath());
            } catch (Exception ex) {
                statsLabel.setText("\u041e\u0448\u0438\u0431\u043a\u0430 \u0441\u043e\u0445\u0440\u0430\u043d\u0435\u043d\u0438\u044f: " + ex.getMessage());
            }
        }
    }

    private void runAnalysis(File file) {
        try {
            AnalysisResult result = analyzer.analyzeFileAutoDetect(file);
            renderResult(file, result);
        } catch (Exception ex) {
            currentReport = "";
            reportArea.setText("\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u043f\u0440\u043e\u0430\u043d\u0430\u043b\u0438\u0437\u0438\u0440\u043e\u0432\u0430\u0442\u044c \u0444\u0430\u0439\u043b:\n" + ex.getMessage());
            statsLabel.setText("\u041e\u0448\u0438\u0431\u043a\u0430");
        }
    }

    private void renderResult(File file, AnalysisResult r) {
        StringBuilder sb = new StringBuilder();
        sb.append("\u0424\u0430\u0439\u043b: ").append(file.getAbsolutePath()).append('\n');
        sb.append("\u041a\u043e\u0434\u0438\u0440\u043e\u0432\u043a\u0430 (\u043e\u043f\u0440\u0435\u0434\u0435\u043b\u0435\u043d\u0430): ").append(r.getCharsetName()).append('\n');
        sb.append("\u0412\u0441\u0435\u0433\u043e \u0441\u0438\u043c\u0432\u043e\u043b\u043e\u0432: ").append(r.getTotalChars()).append('\n');
        sb.append("\u041a\u0438\u0440\u0438\u043b\u043b\u0438\u0447\u0435\u0441\u043a\u0438\u0445 \u0441\u0438\u043c\u0432\u043e\u043b\u043e\u0432: ").append(r.getCyrillicCount()).append('\n');
        sb.append(String.format("\u041f\u043b\u043e\u0442\u043d\u043e\u0441\u0442\u044c \u041a\u0418\u0421: %.2f%%%n", r.getDensityPercent()));
        sb.append("\u0427\u0438\u0441\u0442\u044b\u0435 \u043a\u0438\u0440\u0438\u043b\u043b\u0438\u0447\u0435\u0441\u043a\u0438\u0435 \u0441\u043b\u043e\u0432\u0430: ").append(r.getPureCyrillicWords()).append('\n');
        sb.append("\u0421\u043b\u043e\u0432\u0430 \u0441\u043e \u0441\u043c\u0435\u0448\u0435\u043d\u0438\u0435\u043c \u0433\u0440\u0430\u0444\u0438\u043a: ").append(r.getMixedScriptWords()).append("\n\n");

        sb.append("\u0427\u0430\u0441\u0442\u043e\u0442\u044b \u043f\u043e \u0431\u0443\u043a\u0432\u0430\u043c (\u0432\u0435\u0440\u0445\u043d\u0438\u0439 / \u043d\u0438\u0436\u043d\u0438\u0439 \u0440\u0435\u0433\u0438\u0441\u0442\u0440):\n");
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
            sb.append("  \u0401/\u0451: ").append(r.getYoUpper()).append(" / ").append(r.getYoLower()).append('\n');
        }
        sb.append('\n').append(translateMixedHeader(r.getMixedScriptReport()));

        currentReport = sb.toString();
        reportArea.setText(currentReport);
        reportArea.setCaretPosition(0);
        statsLabel.setText(String.format("\u041f\u043b\u043e\u0442\u043d\u043e\u0441\u0442\u044c: %.2f%%  |  \u0421\u043c\u0435\u0448\u0430\u043d\u043d\u044b\u0445 \u0441\u043b\u043e\u0432: %d  |  %s",
                r.getDensityPercent(), r.getMixedScriptWords(), r.getCharsetName()));
    }

    /** Model layer stays English-pure; the view localizes the anomaly header. */
    private String translateMixedHeader(String report) {
        if (report.startsWith("Mixed-script anomalies: none detected.")) {
            return "\u0410\u043d\u043e\u043c\u0430\u043b\u0438\u0438 \u0441\u043c\u0435\u0448\u0435\u043d\u0438\u044f \u0433\u0440\u0430\u0444\u0438\u043a \u043d\u0435 \u043e\u0431\u043d\u0430\u0440\u0443\u0436\u0435\u043d\u044b.";
        }
        if (report.startsWith("Found ")) {
            int colon = report.indexOf(':');
            if (colon > 0) {
                return "\u041d\u0430\u0439\u0434\u0435\u043d\u043e \u0441\u043b\u043e\u0432 \u0441\u043e \u0441\u043c\u0435\u0448\u0435\u043d\u0438\u0435\u043c \u0433\u0440\u0430\u0444\u0438\u043a:"
                        + report.substring(colon + 1);
            }
        }
        return report;
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
