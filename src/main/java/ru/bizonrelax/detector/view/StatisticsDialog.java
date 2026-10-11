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

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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

    public static void show(java.awt.Frame parent, AppConfig config, MainWindow main) {
        String lang = config.getLanguage();
        StringBuilder sb = new StringBuilder();
        sb.append(Messages.get(lang, "stats.firstlaunch"))
          .append(config.getFirstLaunchDate().isEmpty() ? "?" : config.getFirstLaunchDate())
          .append('\n');
        sb.append(Messages.get(lang, "stats.runtime")).append(formatDuration(config.getTotalRunTimeSeconds())).append('\n');
        sb.append(Messages.get(lang, "stats.launches")).append(config.getLaunchCount()).append('\n');
        sb.append(Messages.get(lang, "stats.chars")).append(config.getTotalAnalyzedChars()).append('\n');
        sb.append(Messages.get(lang, "stats.exports")).append(formatSize(config.getTotalExportedBytes())).append('\n');
        sb.append(Messages.get(lang, "stats.opened")).append(config.getTotalOpenedFiles()).append('\n');

        JOptionPane.showMessageDialog(parent, sb.toString(),
                Messages.get(lang, "stats.title"), JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showAbout(java.awt.Frame parent, AppConfig config, final MainWindow main) {
        final String lang = config.getLanguage();
        JDialog dlg = new JDialog(parent, Messages.get(lang, "about.title"), false);
        dlg.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

        JLabel lblCreated = new JLabel(Messages.get(lang, "about.created") + ExtendedWindow.PROGRAM_CREATED);
        JLabel lblSource = new JLabel(Messages.get(lang, "about.source"));
        JLabel lblUrl = new JLabel(Messages.get(lang, "about.url"));
        JTextAreaHolder txt = new JTextAreaHolder(
                Messages.get(lang, "about.desc") + "\n\n" + Messages.get(lang, "about.usage"));
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
                main.copyToClipboard(sb.toString());
            }
        });
        JButton btnClose = new JButton(Messages.get(lang, "btn.closeall"));
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
                .addComponent(lblCreated)
                .addComponent(lblSource)
                .addComponent(lblUrl)
                .addComponent(txt)
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
                                .addComponent(btnCopy)
                                .addComponent(btnClose))));
        dlg.pack();
        dlg.setSize(560, 340);
        dlg.setLocationRelativeTo(parent);
        dlg.setVisible(true);
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
        dlg.setVisible(true);
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
