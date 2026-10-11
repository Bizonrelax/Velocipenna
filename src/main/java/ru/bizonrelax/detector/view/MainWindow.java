package ru.bizonrelax.detector.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ru.bizonrelax.detector.config.AppConfig;
import ru.bizonrelax.detector.config.ConfigStore;
import ru.bizonrelax.detector.i18n.Messages;
import ru.bizonrelax.detector.model.AnalysisResult;
import ru.bizonrelax.detector.model.CyrillicCharSet;
import ru.bizonrelax.detector.model.ReportExporter;
import ru.bizonrelax.detector.model.TextAnalyzer;

/**
 * Main Swing window. Delegates all analysis to model package, all persistence
 * to config package. Implements TZ.md items 1-7 and Dop-* extensions:
 * remembered directories, multi-format export, drag-and-drop with charset
 * auto-detection, background color (RGB spinners / palette) with automatic
 * text inversion, font size, About dialog, "Буф" clipboard button and the
 * "Да_^" extended window. Long-term statistics are accumulated here.
 */
public final class MainWindow extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final SimpleDateFormat REPORT_TS =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.ENGLISH);

    private final TextAnalyzer analyzer = new TextAnalyzer();
    private final ConfigStore store;
    private final AppConfig config;

    private final JTextArea reportArea = new JTextArea();
    private final JLabel statsLabel = new JLabel();
    private final JSpinner fontSpinner;
    private final JButton openButton;
    private final JButton exportButton;
    private final JButton bufferButton;
    private final JButton aboutButton;
    private final JButton extendedButton;
    private final JLabel lblFont;
    private final JLabel lblBgMode;
    private final JRadioButton radioSpinner;
    private final JRadioButton radioPalette;
    private final JSpinner spinR;
    private final JSpinner spinG;
    private final JSpinner spinB;
    private final JPanel toolbar;

    private File lastFile;
    private String currentReport = "";
    private ExtendedWindow extendedWindow;
    private TicTacToeWindow ticTacToeWindow;
    private boolean restoring = true;
    private final long sessionStartMillis = System.currentTimeMillis();
    private long lastFlushMillis = System.currentTimeMillis();

    public MainWindow(ConfigStore store) {
        super(Messages.get("ru", "app.title"));
        this.store = store;
        this.config = store.load();
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());

        // ---- toolbar ----
        toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        openButton = new JButton();
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseAndAnalyze();
            }
        });
        toolbar.add(openButton);

        exportButton = new JButton();
        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportReport();
            }
        });
        toolbar.add(exportButton);

        bufferButton = new JButton();
        bufferButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyCurrentReportToClipboard();
            }
        });
        toolbar.add(bufferButton);

        aboutButton = new JButton();
        aboutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StatisticsDialog.showAbout(MainWindow.this, config, MainWindow.this);
            }
        });
        toolbar.add(aboutButton);

        extendedButton = new JButton();
        extendedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showExtendedWindow();
            }
        });
        toolbar.add(extendedButton);

        toolbar.add(Box.createHorizontalStrut(16));
        lblFont = new JLabel();
        toolbar.add(lblFont);
        fontSpinner = new JSpinner(new SpinnerNumberModel(
                Integer.valueOf(config.getFontSize()), Integer.valueOf(8),
                Integer.valueOf(48), Integer.valueOf(1)));
        fontSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                config.setFontSize(((Number) fontSpinner.getValue()).intValue());
                applyFontSize();
            }
        });
        toolbar.add(fontSpinner);

        // ---- background controls (TZ.md item 5) ----
        toolbar.add(Box.createHorizontalStrut(16));
        lblBgMode = new JLabel();
        toolbar.add(lblBgMode);
        radioSpinner = new JRadioButton();
        radioPalette = new JRadioButton();
        ButtonGroup bgGroup = new ButtonGroup();
        bgGroup.add(radioSpinner);
        bgGroup.add(radioPalette);
        ItemListener modeListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (restoring || !e.getItem().equals(getSelectedBgSource())) {
                    return;
                }
                boolean spinnerMode = radioSpinner.isSelected();
                // atomic swap to the color remembered by the selected section;
                // NO dialog is opened here (bug-fix: palette must not pop up
                // when switching radio buttons)
                config.switchBgMode(spinnerMode);
                spinR.setEnabled(spinnerMode);
                spinG.setEnabled(spinnerMode);
                spinB.setEnabled(spinnerMode);
                syncSpinnersFromConfig();
                applyBackground();
            }
        };
        radioSpinner.addItemListener(modeListener);
        radioPalette.addItemListener(modeListener);
        toolbar.add(radioSpinner);
        toolbar.add(radioPalette);

        JButton pickPaletteButton = new JButton("🎨");
        pickPaletteButton.setToolTipText(Messages.get(config.getLanguage(), "radio.palette"));
        pickPaletteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pickFromPalette();
            }
        });
        toolbar.add(pickPaletteButton);

        spinR = createChannelSpinner(0);
        spinG = createChannelSpinner(1);
        spinB = createChannelSpinner(2);
        toolbar.add(new JLabel("R"));
        toolbar.add(spinR);
        toolbar.add(new JLabel("G"));
        toolbar.add(spinG);
        toolbar.add(new JLabel("B"));
        toolbar.add(spinB);

        add(toolbar, BorderLayout.NORTH);

        // ---- report area ----
        reportArea.setEditable(false);
        reportArea.setLineWrap(false);
        JScrollPane scroll = new JScrollPane(reportArea);
        scroll.setPreferredSize(new Dimension(900, 600));
        add(scroll, BorderLayout.CENTER);

        // ---- drag & drop of text files onto the whole window ----
        new DropTarget(this, DnDConstants.ACTION_COPY, new DropTargetAdapter() {
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
                            config.setLastInputDir(f.getParent());
                            runAnalysis(f);
                        }
                    }
                    event.dropComplete(true);
                } catch (Exception ex) {
                    event.dropComplete(false);
                }
            }
        }, true);

        // ---- status bar ----
        JPanel status = new JPanel();
        status.setLayout(new BoxLayout(status, BoxLayout.X_AXIS));
        status.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        status.add(statsLabel);
        add(status, BorderLayout.SOUTH);

        setSize(config.getWidth(), config.getHeight());
        setLocation(config.getX() >= 0 ? config.getX() : 80,
                config.getY() >= 0 ? config.getY() : 80);
        StatisticsDialog.enableMouseWheelScrolling(getContentPane());

        radioSpinner.setSelected(config.isBgSpinnerMode());
        radioPalette.setSelected(!config.isBgSpinnerMode());
        syncSpinnersFromConfig();
        spinR.setEnabled(config.isBgSpinnerMode());
        spinG.setEnabled(config.isBgSpinnerMode());
        spinB.setEnabled(config.isBgSpinnerMode());
        restoring = false;

        applyLanguage();
        applyFontSize();
        applyBackground();

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                persistAndExit();
            }
        });
    }

    // ------------------------------------------------------------------ API

    public AppConfig getConfig() {
        return config;
    }

    public ConfigStore getStore() {
        return store;
    }

    public void setStatus(String text) {
        statsLabel.setText(text);
    }

    public void copyToClipboard(String text) {
        try {
            java.awt.Toolkit.getDefaultToolkit().getSystemClipboard()
                    .setContents(new StringSelection(text), null);
            setStatus(Messages.get(config.getLanguage(), "status.copied"));
        } catch (Exception ex) {
            setStatus(Messages.get(config.getLanguage(), "status.copyerr") + ex.getMessage());
        }
    }

    private void copyCurrentReportToClipboard() {
        if (currentReport.isEmpty()) {
            setStatus(Messages.get(config.getLanguage(), "status.nothing"));
            return;
        }
        copyToClipboard(currentReport);
    }

    /** Called from the language popup: re-localize every visible string. */
    public void applyLanguage() {
        String lang = config.getLanguage();
        setTitle(Messages.get(lang, "app.title"));
        openButton.setText(Messages.get(lang, "btn.open"));
        exportButton.setText(Messages.get(lang, "btn.export"));
        bufferButton.setText(Messages.get(lang, "btn.buffer"));
        bufferButton.setToolTipText(Messages.get(lang, "tip.buffer"));
        aboutButton.setText(Messages.get(lang, "btn.about"));
        aboutButton.setToolTipText(Messages.get(lang, "tip.about"));
        extendedButton.setText(Messages.get(lang, "btn.extended"));
        extendedButton.setToolTipText(Messages.get(lang, "tip.extended"));
        lblFont.setText(Messages.get(lang, "lbl.font"));
        lblBgMode.setText(Messages.get(lang, "lbl.bgmode"));
        radioSpinner.setText(Messages.get(lang, "radio.spinner"));
        radioPalette.setText(Messages.get(lang, "radio.palette"));
        if (currentReport.isEmpty()) {
            statsLabel.setText(Messages.get(lang, "status.drop"));
        }
        if (extendedWindow != null) {
            extendedWindow.applyLanguage();
        }
        if (ticTacToeWindow != null) {
            ticTacToeWindow.applyLanguage();
        }
        revalidate();
    }

    void rebuildWindowsRegistry() {
        extendedWindow = null;
        ticTacToeWindow = null;
    }

    /** Immediate full save (geometry, fonts, colors, paths, text, stats). */
    public void persistNow() {
        flushSessionTime();
        config.setGeometry(getWidth(), getHeight(), getX(), getY());
        if (extendedWindow != null) {
            extendedWindow.rememberText();
        }
        store.save(config);
    }

    public void openReportEditor() {
        if (currentReport.isEmpty()) {
            setStatus(Messages.get(config.getLanguage(), "status.nothing"));
            return;
        }
        StatisticsDialog.showEditor(this, config, this, currentReport);
    }

    /** Replaces the displayed/exported report after editing (Dop-9). */
    public void replaceReport(String edited) {
        currentReport = edited;
        reportArea.setText(currentReport);
        reportArea.setCaretPosition(0);
    }

    // -------------------------------------------------------------- internals

    private JSpinner createChannelSpinner(final int channel) {
        JSpinner s = new JSpinner(new SpinnerNumberModel(
                Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), Integer.valueOf(1)));
        s.setPreferredSize(new Dimension(56, 24));
        s.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (!radioSpinner.isSelected()) {
                    return;
                }
                int v = ((Number) s.getValue()).intValue();
                if (channel == 0) {
                    config.setBackgroundRgb(v, config.getBgG(), config.getBgB());
                } else if (channel == 1) {
                    config.setBackgroundRgb(config.getBgR(), v, config.getBgB());
                } else {
                    config.setBackgroundRgb(config.getBgR(), config.getBgG(), v);
                }
                applyBackground();
            }
        });
        return s;
    }

    private Object getSelectedBgSource() {
        return radioSpinner.isSelected() ? radioSpinner : radioPalette;
    }

    /** Loads the currently active background RGB into the spinners silently. */
    private void syncSpinnersFromConfig() {
        spinR.setValue(Integer.valueOf(config.getBgR()));
        spinG.setValue(Integer.valueOf(config.getBgG()));
        spinB.setValue(Integer.valueOf(config.getBgB()));
    }

    /** Opens the palette chooser ONLY on explicit user action (button click). */
    private void pickFromPalette() {
        Color initial = new Color(config.getBgR(), config.getBgG(), config.getBgB());
        Color chosen = JColorChooser.showDialog(this,
                Messages.get(config.getLanguage(), "lbl.bgmode"), initial);
        if (chosen != null) {
            // remember as the palette-section color and make it active
            config.setPaletteColor(chosen.getRed(), chosen.getGreen(), chosen.getBlue());
            if (!config.isBgSpinnerMode()) {
                syncSpinnersFromConfig();
                applyBackground();
            } else {
                // user picked via button while in spinner mode: switch mode
                config.switchBgMode(false);
                radioPalette.setSelected(true);
                spinR.setEnabled(false);
                spinG.setEnabled(false);
                spinB.setEnabled(false);
                syncSpinnersFromConfig();
                applyBackground();
            }
        }
    }

    private void applyBackground() {
        Color bg = new Color(config.getBgR(), config.getBgG(), config.getBgB());
        getContentPane().setBackground(bg);
        toolbar.setBackground(bg);
        reportArea.setBackground(bg);
        // Bug-fix: do NOT repaint control letters (buttons, labels, spinners).
        // Only the report text and the status line get an auto-contrast color;
        // interface elements keep their native L&F colors so they never turn
        // white-on-white / dark-on-dark.
        Color fg = ContrastUtil.readableTextColor(bg);
        reportArea.setForeground(fg);
        statsLabel.setForeground(fg);
        // make plain panels transparent so native controls render normally
        for (Component c : toolbar.getComponents()) {
            if (c instanceof JPanel) {
                ((JPanel) c).setOpaque(false);
            }
        }
    }

    /**
     * Refreshes this window's appearance from the current config (used when
     * secondary windows like About change background/font settings live).
     */
    public void refreshAppearance() {
        applyBackground();
        getContentPane().revalidate();
        getContentPane().repaint();
    }

    private void showExtendedWindow() {
        if (extendedWindow == null) {
            extendedWindow = new ExtendedWindow(this, store);
        }
        extendedWindow.setVisible(true);
        extendedWindow.toFront();
    }

    /** Opens (or reuses) the tic-tac-toe window ("Кре" button in Доп). */
    public void showTicTacToe() {
        if (ticTacToeWindow == null) {
            ticTacToeWindow = new TicTacToeWindow(this);
        }
        ticTacToeWindow.applyAppearance();
        ticTacToeWindow.setVisible(true);
        ticTacToeWindow.toFront();
    }

    private void chooseAndAnalyze() {
        JFileChooser chooser = new JFileChooser(config.getLastInputDir());
        int option = chooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            lastFile = chooser.getSelectedFile();
            config.setLastInputDir(lastFile.getParent());
            runAnalysis(lastFile);
        }
    }

    private void exportReport() {
        if (currentReport.isEmpty()) {
            setStatus(Messages.get(config.getLanguage(), "status.nothing"));
            return;
        }
        final JFileChooser chooser = new JFileChooser(config.getLastExportDir());
        String base = lastFile != null
                ? lastFile.getName().replaceAll("(?i)\\.[^.]+$", "") + "_report"
                : "velocipenna_report";

        // Format dropdown inside the save dialog (TZ-2.md)
        JPanel builder = new JPanel(new FlowLayout(FlowLayout.LEFT));
        final JComboBox<ReportExporter.Format> formats =
                new JComboBox<ReportExporter.Format>(ReportExporter.Format.values());
        builder.add(new JLabel(Messages.get(config.getLanguage(), "dlg.format")));
        builder.add(formats);
        chooser.setAccessory(builder);
        chooser.setSelectedFile(new File(base + ".txt"));
        formats.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ReportExporter.Format f = (ReportExporter.Format) formats.getSelectedItem();
                String name = chooser.getSelectedFile().getName();
                name = name.replaceAll("(?i)\\.[^.]+$", "");
                chooser.setSelectedFile(new File(name + "." + f.getExtension()));
            }
        });

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File target = chooser.getSelectedFile();
            ReportExporter.Format fmt = detectFormat(target.getName());
            if (!target.getName().contains(".")) {
                target = new File(target.getParentFile(), target.getName() + "." + fmt.getExtension());
            }
            try {
                ReportExporter.export(currentReport, target, fmt);
                config.setLastExportDir(target.getParent());
                config.addExportedBytes(target.length());
                setStatus(Messages.get(config.getLanguage(), "status.saved") + target.getAbsolutePath());
            } catch (Exception ex) {
                setStatus(Messages.get(config.getLanguage(), "status.saveerr") + ex.getMessage());
            }
        }
    }

    private ReportExporter.Format detectFormat(String fileName) {
        int dot = fileName.lastIndexOf('.');
        if (dot < 0) {
            return ReportExporter.Format.TXT;
        }
        return ReportExporter.Format.fromName(fileName.substring(dot + 1));
    }

    private void runAnalysis(File file) {
        try {
            AnalysisResult result = analyzer.analyzeFileAutoDetect(file);
            renderResult(file, result);
            config.incrementOpenedFiles();
            config.setTotalAnalyzedChars(config.getTotalAnalyzedChars() + result.getTotalChars());
        } catch (Exception ex) {
            currentReport = "";
            reportArea.setText(ex.getClass().getSimpleName() + ": " + ex.getMessage());
            setStatus(Messages.get(config.getLanguage(), "status.nothing"));
        }
    }

    private void renderResult(File file, AnalysisResult r) {
        String lang = config.getLanguage();
        StringBuilder sb = new StringBuilder();
        sb.append(Messages.get(lang, "report.file")).append(file.getAbsolutePath()).append('\n');
        sb.append(Messages.get(lang, "report.charset")).append(r.getCharsetName()).append('\n');
        sb.append(Messages.get(lang, "report.total")).append(r.getTotalChars()).append('\n');
        sb.append(Messages.get(lang, "report.cyrillic")).append(r.getCyrillicCount()).append('\n');
        sb.append(String.format(Messages.get(lang, "report.density") + "%.2f%%%n", r.getDensityPercent()));
        sb.append(Messages.get(lang, "report.pure")).append(r.getPureCyrillicWords()).append('\n');
        sb.append(Messages.get(lang, "report.mixed")).append(r.getMixedScriptWords()).append("\n\n");

        sb.append(Messages.get(lang, "report.freq")).append('\n');
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
        sb.append('\n').append(localizeMixedHeader(r.getMixedScriptReport(), lang));
        sb.append('\n').append(Messages.get(lang, "report.created")).append(REPORT_TS.format(new Date())).append('\n');

        currentReport = sb.toString();
        reportArea.setText(currentReport);
        reportArea.setCaretPosition(0);
        setStatus(String.format("%.2f%% | %d | %s",
                r.getDensityPercent(), r.getMixedScriptWords(), r.getCharsetName()));
    }

    /** Model layer stays English-pure; the view localizes the anomaly header. */
    private String localizeMixedHeader(String report, String lang) {
        if (report.startsWith("Mixed-script anomalies: none detected.")) {
            return Messages.get(lang, "report.none");
        }
        if (report.startsWith("Found ")) {
            int colon = report.indexOf(':');
            if (colon > 0) {
                return Messages.get(lang, "report.found") + report.substring(colon);
            }
        }
        return report;
    }

    private void applyFontSize() {
        int size = config.getFontSize();
        reportArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, size));
        statsLabel.setFont(statsLabel.getFont().deriveFont((float) size));
        for (Component c : toolbar.getComponents()) {
            if (c instanceof AbstractButton || c instanceof JLabel || c instanceof JSpinner) {
                c.setFont(c.getFont().deriveFont((float) size));
            }
        }
        revalidate();
    }

    private void flushSessionTime() {
        long now = System.currentTimeMillis();
        long seconds = (now - lastFlushMillis) / 1000L;
        if (seconds > 0) {
            config.setTotalRunTimeSeconds(config.getTotalRunTimeSeconds() + seconds);
            lastFlushMillis = now - ((now - lastFlushMillis) % 1000L);
        }
    }

    private void persistAndExit() {
        persistNow();
        dispose();
        System.exit(0);
    }

    /** Registers launch counters; called once after setVisible(true). */
    public void finishRestore() {
        restoring = false;
        if (config.getFirstLaunchDate().isEmpty()) {
            config.setFirstLaunchDate(REPORT_TS.format(new Date(sessionStartMillis)));
        }
        config.setLaunchCount(config.getLaunchCount() + 1);
        persistNow();
    }
}
