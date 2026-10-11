package ru.bizonrelax.detector.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import ru.bizonrelax.detector.config.AppConfig;
import ru.bizonrelax.detector.i18n.Messages;

/**
 * "Крестики-нолики" (tic-tac-toe) window opened by the "Кре" button in the
 * "Доп" extended window. Human plays X against a simple computer opponent O.
 * Shares the main-window appearance settings (background color with automatic
 * font contrast, UI font size) via AppearanceUtil and supports mouse-wheel
 * scrolling like every other window of the application.
 */
public class TicTacToeWindow extends javax.swing.JFrame {

    private static final long serialVersionUID = 1L;

    /** Board state: 0 empty, 1 = X (human), 2 = O (computer). */
    private final int[] cells = new int[9];
    private boolean gameOver;
    private int moves;

    private final transient MainWindow main;
    private final AppConfig config;

    private JLabel lblTitle;
    private JLabel lblStatus;
    private final CellPanel[] cellPanels = new CellPanel[9];
    private JPanel board;
    private JButton btnNew;
    private JScrollPane scroll;

    public TicTacToeWindow(MainWindow main) {
        this.main = main;
        this.config = main.getConfig();
        initComponents();
        applyLanguage();
        applyFonts();
        resetGame();
        centerOnScreen();
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

        lblTitle = new JLabel();
        lblStatus = new JLabel();
        btnNew = new JButton();
        board = new JPanel(new GridLayout(3, 3, 6, 6));
        board.setOpaque(false);
        for (int i = 0; i < 9; i++) {
            final int idx = i;
            CellPanel cp = new CellPanel();
            cp.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    playHuman(idx);
                }
            });
            cellPanels[i] = cp;
            board.add(cp);
        }

        btnNew.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                resetGame();
            }
        });

        scroll = new JScrollPane(board,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setViewportBorder(null);
        scroll.setPreferredSize(new Dimension(280, 280));

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(lblTitle)
                .addComponent(scroll, GroupLayout.PREFERRED_SIZE,
                        GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblStatus)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(btnNew)));
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTitle)
                        .addComponent(scroll, GroupLayout.PREFERRED_SIZE, 280,
                                GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblStatus)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(btnNew))));
        pack();
        setMinimumSize(new Dimension(320, 400));
        StatisticsDialog.enableMouseWheelScrolling(getContentPane());
    }

    /** Applies all localized labels/tooltips for the current language. */
    public final void applyLanguage() {
        String lang = config.getLanguage();
        setTitle(Messages.get(lang, "ttt.title"));
        lblTitle.setText(Messages.get(lang, "ttt.title"));
        btnNew.setText(Messages.get(lang, "ttt.new"));
        btnNew.setToolTipText(Messages.get(lang, "ttt.tip.new"));
        updateStatus();
    }

    private void applyFonts() {
        Font ui = new Font(Font.DIALOG, Font.PLAIN, config.getExtUiFontSize());
        JComponent[] comps = {lblTitle, lblStatus, btnNew};
        for (JComponent c : comps) {
            c.setFont(ui);
        }
        for (CellPanel cp : cellPanels) {
            cp.setFont(ui);
        }
        getContentPane().revalidate();
        getContentPane().repaint();
    }

    /** Applies the shared main-window appearance (bg + contrast + font size). */
    public final void applyAppearance() {
        Color bg = AppearanceUtil.backgroundOf(config);
        AppearanceUtil.apply(getContentPane(), bg, config.getFontSize());
        board.setBackground(bg);
        for (CellPanel cp : cellPanels) {
            cp.setBackground(bg);
        }
        getContentPane().revalidate();
        getContentPane().repaint();
    }

    private void resetGame() {
        for (int i = 0; i < 9; i++) {
            cells[i] = 0;
        }
        gameOver = false;
        moves = 0;
        for (CellPanel cp : cellPanels) {
            cp.mark = 0;
        }
        updateStatus();
        repaint();
    }

    private void playHuman(int idx) {
        if (gameOver || cells[idx] != 0) {
            return;
        }
        place(idx, 1);
        if (checkEnd(1)) {
            return;
        }
        int comp = computerMove();
        if (comp >= 0) {
            place(comp, 2);
            checkEnd(2);
        }
    }

    private void place(int idx, int who) {
        cells[idx] = who;
        cellPanels[idx].mark = who;
        moves++;
        cellPanels[idx].repaint();
    }

    /** Returns true when the game finished (win or draw). */
    private boolean checkEnd(int who) {
        int[] win = winningLine(who);
        if (win != null) {
            gameOver = true;
            for (int i : win) {
                cellPanels[i].winCell = true;
                cellPanels[i].repaint();
            }
            updateStatus();
            return true;
        }
        if (moves == 9) {
            gameOver = true;
            updateStatus();
            return true;
        }
        updateStatus();
        return false;
    }

    private static int[][] LINES = {
        {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
        {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
        {0, 4, 8}, {2, 4, 6}
    };

    /** The first complete line for the given player, or null. */
    private int[] winningLine(int who) {
        for (int[] line : LINES) {
            if (cells[line[0]] == who && cells[line[1]] == who && cells[line[2]] == who) {
                return line;
            }
        }
        return null;
    }

    /** Simple AI: win > block > center > corner > any free cell. */
    private int computerMove() {
        int m = findLineMove(2);
        if (m < 0) {
            m = findLineMove(1);
        }
        if (m < 0 && cells[4] == 0) {
            m = 4;
        }
        if (m < 0) {
            int[] corners = {0, 2, 6, 8};
            for (int c : corners) {
                if (cells[c] == 0) {
                    m = c;
                    break;
                }
            }
        }
        if (m < 0) {
            for (int i = 0; i < 9; i++) {
                if (cells[i] == 0) {
                    m = i;
                    break;
                }
            }
        }
        return m;
    }

    /** Empty cell that completes a line for the given player, or -1. */
    private int findLineMove(int who) {
        for (int[] line : LINES) {
            int filled = 0;
            int empty = -1;
            boolean conflict = false;
            for (int i : line) {
                if (cells[i] == who) {
                    filled++;
                } else if (cells[i] == 0) {
                    empty = i;
                } else {
                    conflict = true;
                }
            }
            if (!conflict && filled == 2 && empty >= 0) {
                return empty;
            }
        }
        return -1;
    }

    private void updateStatus() {
        String lang = config.getLanguage();
        String text;
        if (gameOver && winningLine(1) != null) {
            text = Messages.get(lang, "ttt.win");
        } else if (gameOver && winningLine(2) != null) {
            text = Messages.get(lang, "ttt.lose");
        } else if (gameOver) {
            text = Messages.get(lang, "ttt.draw");
        } else {
            text = Messages.get(lang, "ttt.turn");
        }
        lblStatus.setText(text);
    }

    private void centerOnScreen() {
        java.awt.GraphicsEnvironment env =
                java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
        java.awt.Rectangle screen =
                env.getDefaultScreenDevice().getDefaultConfiguration().getBounds();
        setLocation(screen.x + (screen.width - getWidth()) / 2,
                screen.y + (screen.height - getHeight()) / 2);
    }

    /** One square of the 3x3 board. */
    private static final class CellPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        int mark; // 0 empty, 1 X, 2 O
        boolean winCell;

        CellPanel() {
            setPreferredSize(new Dimension(80, 80));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (winCell) {
                g.setColor(new Color(255, 213, 79, 120));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
            g.setColor(getForeground());
            int pad = 14;
            int w = getWidth() - pad * 2;
            int h = getHeight() - pad * 2;
            int side = Math.min(w, h);
            int x = (getWidth() - side) / 2;
            int y = (getHeight() - side) / 2;
            int stroke = Math.max(3, side / 10);
            ((java.awt.Graphics2D) g).setStroke(new java.awt.BasicStroke(stroke,
                    java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_ROUND));
            if (mark == 1) {
                g.drawLine(x, y, x + side, y + side);
                g.drawLine(x + side, y, x, y + side);
            } else if (mark == 2) {
                g.drawOval(x, y, side, side);
            }
        }
    }
}
