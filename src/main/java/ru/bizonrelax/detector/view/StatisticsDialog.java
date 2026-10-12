package ru.bizonrelax.detector.view;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;

import ru.bizonrelax.detector.config.AppConfig;
import ru.bizonrelax.detector.i18n.Messages;
import ru.bizonrelax.detector.model.ReportExporter;

/**
 * Statistics dialog (TZ.md Dop-10) and the "Оп/About" dialog (TZ.md item 7).
 * Long-term counters are accumulated in AppConfig by MainWindow.
 */
public final class StatisticsDialog {

    private static final SimpleDateFormat TS =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

    private StatisticsDialog() {
    }

    /**
     * Non-modal statistics window. Shares the main-window appearance settings
     * (background color with auto font contrast, UI font size) via
     * AppearanceUtil, as required by TZ.md (Dop-10).
     */
    public static void show(java.awt.Frame parent, AppConfig config, MainWindow main) {
        final String lang = config.getLanguage();
        StringBuilder sb = new StringBuilder();
        sb.append(Messages.get(lang, "stats.firstlaunch"))
          .append(config.getFirstLaunchDate().isEmpty() ? "?" : config.getFirstLaunchDate())
          .append('\n');
        sb.append(Messages.get(lang, "stats.runtime")).append(formatDuration(config.getTotalRunTimeSeconds())).append('\n');
        sb.append(Messages.get(lang, "stats.launches")).append(config.getLaunchCount()).append('\n');
        sb.append(Messages.get(lang, "stats.chars")).append(config.getTotalAnalyzedChars()).append('\n');
        sb.append(Messages.get(lang, "stats.exports")).append(formatSize(config.getTotalExportedBytes())).append('\n');
        sb.append(Messages.get(lang, "stats.opened")).append(config.getTotalOpenedFiles()).append('\n');

        final JDialog dlg = new JDialog(parent, Messages.get(lang, "stats.title"), false);
        dlg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        final JTextAreaHolder txt = new JTextAreaHolder(sb.toString());
        // The statistics window used to render with a too-small font: bump the
        // base UI size and let the user tune it live with a dedicated spinner.
        final int statsBaseFont = Math.max(16, config.getFontSize());
        txt.setFont(txt.getFont().deriveFont((float) statsBaseFont));
        final JSpinner fontSpinner = new JSpinner(
                new SpinnerNumberModel(statsBaseFont, 8, 48, 1));
        JLabel lblFont = new JLabel(Messages.get(lang, "lbl.font"));
        fontSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            @Override
            public void stateChanged(javax.swing.event.ChangeEvent e) {
                int fs = ((Number) fontSpinner.getValue()).intValue();
                AppearanceUtil.apply(dlg.getContentPane(),
                        AppearanceUtil.backgroundOf(config), fs);
                txt.setFont(txt.getFont().deriveFont((float) fs));
                dlg.getContentPane().revalidate();
                dlg.getContentPane().repaint();
            }
        });
        JButton btnCopy = new JButton(Messages.get(lang, "btn.buffer"));
        btnCopy.setToolTipText(Messages.get(lang, "tip.buffer"));
        btnCopy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.copyToClipboard(txt.getText());
            }
        });
        JButton btnClose = new JButton(Messages.get(lang, "btn.close"));
        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dlg.dispose();
            }
        });
        GroupLayout layout = new GroupLayout(dlg.getContentPane());
        dlg.getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(txt)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(lblFont)
                        .addComponent(fontSpinner, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createSequentialGroup().addComponent(btnCopy).addComponent(btnClose)));
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(txt)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblFont)
                                .addComponent(fontSpinner))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(btnCopy).addComponent(btnClose))));
        AppearanceUtil.apply(dlg.getContentPane(), AppearanceUtil.backgroundOf(config), statsBaseFont);
        txt.setFont(txt.getFont().deriveFont((float) statsBaseFont));
        dlg.pack();
        dlg.setSize(520, 300);
        dlg.setLocationRelativeTo(parent);
        enableMouseWheelScrolling(dlg.getContentPane());
        dlg.setVisible(true);
    }

    /**
     * Non-modal About window. Has the same appearance settings as the main
     * window (TZ.md item 7): background color, auto font contrast, font size.
     */
    public static void showAbout(java.awt.Frame parent, AppConfig config, final MainWindow main) {
        final String lang = config.getLanguage();
        JDialog dlg = new JDialog(parent, Messages.get(lang, "about.title"), false);
        dlg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        // Font size spinner: changes the text size inside the About window live.
        final JSpinner fontSpinner = new JSpinner(
                new SpinnerNumberModel(config.getFontSize(), 8, 48, 1));
        JLabel lblFont = new JLabel(Messages.get(lang, "lbl.font"));

        // Background color spinners R/G/B: change the About window background
        // live (and are reflected in the shared appearance of all windows).
        final JSpinner spinR = new JSpinner(
                new SpinnerNumberModel(config.getBgR(), 0, 255, 1));
        final JSpinner spinG = new JSpinner(
                new SpinnerNumberModel(config.getBgG(), 0, 255, 1));
        final JSpinner spinB = new JSpinner(
                new SpinnerNumberModel(config.getBgB(), 0, 255, 1));

        // Label components must be created once and reused in both the
        // horizontal and vertical groups; otherwise GroupLayout throws
        // "is not attached to a vertical group".
        final JLabel lblR = new JLabel("R");
        final JLabel lblG = new JLabel("G");
        final JLabel lblB = new JLabel("B");
        JLabel lblCreated = new JLabel(Messages.get(lang, "about.created") + ExtendedWindow.PROGRAM_CREATED);
        JLabel lblSource = new JLabel(Messages.get(lang, "about.source"));
        JLabel lblUrl = new JLabel(Messages.get(lang, "about.url"));
        JTextAreaHolder txt = new JTextAreaHolder(
                Messages.get(lang, "about.desc") + "\n\n" + Messages.get(lang, "about.usage")
                        + "\n\n" + Messages.get(lang, "about.build")
                        + "\n\n" + Messages.get(lang, "about.thirdparty"));
        JButton btnCopy = new JButton(Messages.get(lang, "btn.buffer"));
        btnCopy.setToolTipText(Messages.get(lang, "tip.buffer"));
        btnCopy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StringBuilder sb = new StringBuilder();
                sb.append(Messages.get(lang, "about.title")).append('\n');
                sb.append(Messages.get(lang, "about.created")).append(ExtendedWindow.PROGRAM_CREATED).append('\n');
                sb.append(Messages.get(lang, "about.source")).append('\n');
                sb.append(Messages.get(lang, "about.url")).append('\n');
                sb.append(Messages.get(lang, "about.desc")).append('\n');
                sb.append(Messages.get(lang, "about.usage")).append('\n');
                sb.append(Messages.get(lang, "about.build")).append('\n');
                sb.append(Messages.get(lang, "about.thirdparty")).append('\n');
                main.copyToClipboard(sb.toString());
            }
        });
        JButton btnClose = new JButton(Messages.get(lang, "btn.close"));
        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dlg.dispose();
            }
        });

        // Live appearance updates from the spinners (font size + R/G/B color).
        final JTextAreaHolder txtRef = txt;
        javax.swing.event.ChangeListener appearanceListener = new javax.swing.event.ChangeListener() {
            @Override
            public void stateChanged(javax.swing.event.ChangeEvent e) {
                int fs = ((Number) fontSpinner.getValue()).intValue();
                config.setFontSize(fs);
                int r = ((Number) spinR.getValue()).intValue();
                int g = ((Number) spinG.getValue()).intValue();
                int b = ((Number) spinB.getValue()).intValue();
                config.setBackgroundRgb(r, g, b);
                AppearanceUtil.apply(dlg.getContentPane(),
                        AppearanceUtil.backgroundOf(config), fs);
                txtRef.setFont(txtRef.getFont().deriveFont((float) Math.max(8, fs)));
                dlg.getContentPane().revalidate();
                dlg.getContentPane().repaint();
                main.refreshAppearance();
            }
        };
        fontSpinner.addChangeListener(appearanceListener);
        spinR.addChangeListener(appearanceListener);
        spinG.addChangeListener(appearanceListener);
        spinB.addChangeListener(appearanceListener);

        GroupLayout layout = new GroupLayout(dlg.getContentPane());
        dlg.getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(lblCreated)
                .addComponent(lblSource)
                .addComponent(lblUrl)
                .addComponent(txt)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(lblFont)
                        .addComponent(fontSpinner, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblR)
                        .addComponent(spinR, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblG)
                        .addComponent(spinG, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblB)
                        .addComponent(spinB, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createSequentialGroup()
                        .addComponent(btnCopy)
                        .addComponent(btnClose)));
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(lblCreated)
                        .addComponent(lblSource)
                        .addComponent(lblUrl)
                        .addComponent(txt)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblFont)
                                .addComponent(fontSpinner)
                                .addComponent(lblR)
                                .addComponent(spinR)
                                .addComponent(lblG)
                                .addComponent(spinG)
                                .addComponent(lblB)
                                .addComponent(spinB))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(btnCopy)
                                .addComponent(btnClose))));
        applyMainWindowAppearance(dlg.getContentPane(), config);
        dlg.pack();
        dlg.setSize(560, 420);
        dlg.setLocationRelativeTo(parent);
        enableMouseWheelScrolling(dlg.getContentPane());
        dlg.setVisible(true);
    }

    /** Applies main-window look (bg + contrast + font size) to a secondary window. */
    private static void applyMainWindowAppearance(java.awt.Container content,
                                                  AppConfig config) {
        AppearanceUtil.apply(content, AppearanceUtil.backgroundOf(config), config.getFontSize());
    }

    /** Editable report window (Dop-9): shows current report text, can be saved back. */
    public static void showEditor(final java.awt.Frame parent, final AppConfig config,
                                  final MainWindow main, final String reportText) {
        final JDialog dlg = new JDialog(parent,
                Messages.get(config.getLanguage(), "btn.editreport"), false);
        dlg.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        final javax.swing.JTextArea area = new javax.swing.JTextArea(reportText, 20, 60);
        area.setFont(new java.awt.Font(java.awt.Font.MONOSPACED, java.awt.Font.PLAIN,
                config.getExtTextFontSize()));
        area.setLineWrap(false);
        javax.swing.JScrollPane sp = new javax.swing.JScrollPane(area);
        JButton save = new JButton(Messages.get(config.getLanguage(), "btn.savecfg"));
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.replaceReport(area.getText());
                dlg.dispose();
            }
        });
        JButton close = new JButton(Messages.get(config.getLanguage(), "btn.closeall"));
        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dlg.dispose();
            }
        });
        GroupLayout layout = new GroupLayout(dlg.getContentPane());
        dlg.getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(sp, GroupLayout.PREFERRED_SIZE, 620, Short.MAX_VALUE)
                .addGroup(layout.createSequentialGroup().addComponent(save).addComponent(close)));
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(sp, GroupLayout.PREFERRED_SIZE, 360, GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(save).addComponent(close))));
        dlg.pack();
        dlg.setLocationRelativeTo(parent);
        enableMouseWheelScrolling(dlg.getContentPane());
        dlg.setVisible(true);
    }

    /**
     * Makes the mouse wheel scroll any JScrollPane found inside the given
     * container, even when the pointer is over a child component that does
     * not propagate wheel events (text areas, labels, buttons). Attached to
     * every window: main, extended, statistics, about and report editor.
     */
    static void enableMouseWheelScrolling(java.awt.Container root) {
        for (java.awt.Component comp : root.getComponents()) {
            if (comp instanceof JScrollPane) {
                final JScrollPane sp = (JScrollPane) comp;
                sp.addMouseWheelListener(new MouseWheelListener() {
                    @Override
                    public void mouseWheelMoved(MouseWheelEvent e) {
                        javax.swing.JScrollBar vbar = sp.getVerticalScrollBar();
                        if (vbar != null && vbar.isEnabled()) {
                            int notches = e.getWheelRotation();
                            vbar.setValue(vbar.getValue() + notches * vbar.getUnitIncrement());
                        }
                    }
                });
            }
            if (comp instanceof java.awt.Container) {
                enableMouseWheelScrolling((java.awt.Container) comp);
            }
        }
    }

    public static String formatDuration(long seconds) {
        long h = seconds / 3600;
        long m = (seconds % 3600) / 60;
        long s = seconds % 60;
        return h + " ч " + m + " мин " + s + " с";
    }

    public static String formatSize(long bytes) {
        double mb = bytes / 1048576.0;
        return String.format(Locale.US, "%.2f МБ", mb);
    }

    /** Simple non-editable wrapped text holder for About paragraphs. */
    private static final class JTextAreaHolder extends javax.swing.JTextArea {
        private static final long serialVersionUID = 1L;
        JTextAreaHolder(String text) {
            super(text, 6, 50);
            setEditable(false);
            setLineWrap(true);
            setWrapStyleWord(true);
            setOpaque(false);
        }
    }
}
