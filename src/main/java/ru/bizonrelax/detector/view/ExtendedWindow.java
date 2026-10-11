package ru.bizonrelax.detector.view;

import java.awt.Dimension;
import java.awt.Font;


import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ru.bizonrelax.detector.config.AppConfig;
import ru.bizonrelax.detector.config.ConfigStore;
import ru.bizonrelax.detector.i18n.Messages;

/**
 * "Доп" window (TZ.md Dop-1..Dop-10): editable text field with its own font
 * size, service-files button, close-all button, save-settings button,
 * language switcher (5 languages), report editor and statistics dialog.
 */
public class ExtendedWindow extends javax.swing.JFrame {

    private static final long serialVersionUID = 1L;
    /** Program creation date shown in About (updated per spec to 11.10.2026). */
    public static final String PROGRAM_CREATED = "11.10.2026";

    private final transient MainWindow main;
    private final ConfigStore store;
    private AppConfig config;

    private JLabel lblTextFont;
    private JLabel lblUiFont;
    private JTextArea textField;
    private JSpinner textFontSpinner;
    private JSpinner uiFontSpinner;
    private JButton btnServFiles;
    private JButton btnCloseAll;
    private JButton btnSaveCfg;
    private JButton btnLang;
    private JButton btnEditReport;
    private JButton btnStats;
    private JButton btnTicTacToe;
    private JLabel lblExtTitle;
    private JSeparator sep;
    private JScrollPane scroll;

    public ExtendedWindow(MainWindow main, ConfigStore store) {
        this.main = main;
        this.store = store;
        this.config = main.getConfig();
        initComponents();
        applyLanguage();
        applyFonts();
        centerOnScreen();
    }

    private void initComponents() {
        setTitle(Messages.get(config.getLanguage(), "ext.title"));
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

        lblExtTitle = new JLabel();
        lblTextFont = new JLabel();
        lblUiFont = new JLabel();
        textField = new JTextArea(6, 40);
        textField.setLineWrap(true);
        textField.setText(config.getExtText());
        scroll = new JScrollPane(textField);

        textFontSpinner = new JSpinner(new SpinnerNumberModel(
                config.getExtTextFontSize(), 8, 48, 1));
        uiFontSpinner = new JSpinner(new SpinnerNumberModel(
                config.getExtUiFontSize(), 8, 48, 1));
        textFontSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int v = ((Number) textFontSpinner.getValue()).intValue();
                config.setExtTextFontSize(v);
                applyFonts();
            }
        });
        uiFontSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int v = ((Number) uiFontSpinner.getValue()).intValue();
                config.setExtUiFontSize(v);
                applyFonts();
            }
        });

        btnServFiles = new JButton();
        btnCloseAll = new JButton();
        btnSaveCfg = new JButton();
        btnLang = new JButton();
        btnEditReport = new JButton();
        btnStats = new JButton();
        btnTicTacToe = new JButton();
        sep = new JSeparator();

        btnServFiles.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openServiceFiles();
            }
        });
        btnCloseAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeSecondaryWindows();
            }
        });
        btnSaveCfg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveSettings();
            }
        });
        btnLang.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showLanguageMenu();
            }
        });
        btnEditReport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.openReportEditor();
            }
        });
        btnStats.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StatisticsDialog.show(ExtendedWindow.this, config, main);
            }
        });
        btnTicTacToe.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.showTicTacToe();
            }
        });

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(lblExtTitle)
                .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(lblTextFont)
                                .addComponent(lblUiFont))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(textFontSpinner)
                                .addComponent(uiFontSpinner)))
                .addComponent(scroll, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE)
                .addComponent(sep)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(btnServFiles)
                        .addComponent(btnCloseAll)
                        .addComponent(btnSaveCfg)
                        .addComponent(btnLang)
                        .addComponent(btnEditReport)
                        .addComponent(btnStats)
                        .addComponent(btnTicTacToe)));
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(lblExtTitle)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblTextFont)
                                .addComponent(textFontSpinner))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblUiFont)
                                .addComponent(uiFontSpinner))
                        .addComponent(scroll, GroupLayout.PREFERRED_SIZE, 140,
                                GroupLayout.PREFERRED_SIZE)
                        .addComponent(sep)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(btnServFiles)
                                .addComponent(btnCloseAll)
                                .addComponent(btnSaveCfg)
                                .addComponent(btnLang)
                                .addComponent(btnEditReport)
                                .addComponent(btnStats)
                                .addComponent(btnTicTacToe))));
        pack();
        setMinimumSize(new Dimension(520, 320));
        StatisticsDialog.enableMouseWheelScrolling(getContentPane());
    }

    /** Applies all localized labels/tooltips for the current language. */
    public final void applyLanguage() {
        String lang = config.getLanguage();
        setTitle(Messages.get(lang, "ext.title"));
        lblExtTitle.setText(Messages.get(lang, "ext.title"));
        lblTextFont.setText(Messages.get(lang, "lbl.font"));
        textFontSpinner.setToolTipText(Messages.get(lang, "ext.tip.textfont"));
        lblUiFont.setText("UI " + Messages.get(lang, "lbl.font"));
        uiFontSpinner.setToolTipText(Messages.get(lang, "ext.tip.uifont"));
        btnServFiles.setText(Messages.get(lang, "btn.servfiles"));
        btnServFiles.setToolTipText(Messages.get(lang, "tip.servfiles"));
        btnCloseAll.setText(Messages.get(lang, "btn.closeall"));
        btnCloseAll.setToolTipText(Messages.get(lang, "tip.closeall"));
        btnSaveCfg.setText(Messages.get(lang, "btn.savecfg"));
        btnSaveCfg.setToolTipText(Messages.get(lang, "tip.savecfg"));
        btnLang.setText(Messages.get(lang, "btn.lang"));
        btnLang.setToolTipText(Messages.get(lang, "tip.lang"));
        btnEditReport.setText(Messages.get(lang, "btn.editreport"));
        btnEditReport.setToolTipText(Messages.get(lang, "tip.editreport"));
        btnStats.setText(Messages.get(lang, "btn.stats"));
        btnStats.setToolTipText(Messages.get(lang, "tip.stats"));
        btnTicTacToe.setText(Messages.get(lang, "btn.tictactoe"));
        btnTicTacToe.setToolTipText(Messages.get(lang, "tip.tictactoe"));
        applyFonts();
    }

    private void applyFonts() {
        Font base = new Font(Font.MONOSPACED, Font.PLAIN, config.getExtTextFontSize());
        textField.setFont(base);
        Font ui = new Font(Font.DIALOG, Font.PLAIN, config.getExtUiFontSize());
        JComponent[] uiComponents = {lblExtTitle, lblTextFont, lblUiFont,
                btnServFiles, btnCloseAll, btnSaveCfg, btnLang, btnEditReport, btnStats,
                btnTicTacToe};
        for (JComponent c : uiComponents) {
            c.setFont(ui);
        }
        textFontSpinner.setFont(ui);
        uiFontSpinner.setFont(ui);
    }

    /** Dop-2: reveal the folder containing velocipenna.properties in the OS file manager. */
    private void openServiceFiles() {
        File dir = store.getConfigFile().getParentFile();
        try {
            String os = System.getProperty("os.name", "").toLowerCase();
            Process p;
            if (os.contains("win")) {
                p = new ProcessBuilder("explorer.exe", dir.getAbsolutePath()).start();
            } else if (os.contains("mac")) {
                p = new ProcessBuilder("open", dir.getAbsolutePath()).start();
            } else {
                p = new ProcessBuilder("xdg-open", dir.getAbsolutePath()).start();
            }
            main.setStatus(dir.getAbsolutePath());
        } catch (Exception ex) {
            main.setStatus(store.getConfigFile().getAbsolutePath());
        }
    }

    /** Dop-3: dispose every secondary window, keep only the main one. */
    private void closeSecondaryWindows() {
        Window[] windows = Window.getWindows();
        for (java.awt.Window w : windows) {
            if (w instanceof javax.swing.JFrame && w != main) {
                ((javax.swing.JFrame) w).dispose();
            }
        }
        main.rebuildWindowsRegistry();
    }

    /** Dop-4: persist everything right now (text, fonts, geometry, stats). */
    private void saveSettings() {
        config.setExtText(textField.getText());
        main.persistNow();
        main.setStatus(Messages.get(config.getLanguage(), "ext.saved"));
    }

    /** Dop-8: language selection popup with five buttons. */
    private void showLanguageMenu() {
        final LanguagePopup popup = new LanguagePopup(this, config);
        popup.setVisible(true);
    }

    void rememberText() {
        config.setExtText(textField.getText());
    }

    /** Small modal-ish chooser listing the five supported languages. */
    private static final class LanguagePopup extends javax.swing.JDialog {
        private static final long serialVersionUID = 1L;

        LanguagePopup(final ExtendedWindow owner, final AppConfig cfg) {
            super(owner, Messages.get(cfg.getLanguage(), "btn.lang"), true);
            JPanelHolder holder = new JPanelHolder();
            for (int i = 0; i < Messages.LANG_CODES.length; i++) {
                final String code = Messages.LANG_CODES[i];
                JButton b = new JButton(Messages.LANG_NAMES[i]);
                b.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cfg.setLanguage(code);
                        owner.applyLanguage();
                        owner.main.applyLanguage();
                        owner.main.persistNow();
                        dispose();
                    }
                });
                holder.add(b);
            }
            add(holder);
            pack();
            setLocationRelativeTo(owner);
        }

        private static final class JPanelHolder extends javax.swing.JPanel {
            private static final long serialVersionUID = 1L;
            JPanelHolder() {
                setLayout(new java.awt.GridLayout(1, 0, 6, 0));
                setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            }
        }
    }

    private void centerOnScreen() {
        java.awt.GraphicsEnvironment env = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
        java.awt.Rectangle screen = env.getDefaultScreenDevice().getDefaultConfiguration().getBounds();
        int x = screen.x + (screen.width - getWidth()) / 2;
        int y = screen.y + (screen.height - getHeight()) / 2;
        setLocation(x, y);
    }
}
