package jsfmlproject;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/** Native-library-free fallback. All Swing state remains on the event thread. */
public final class SwingGame {
    private SwingGame() { }
    public static void run() {
        SwingUtilities.invokeLater(() -> {
            GameModel model = new GameModel();
            BoardRenderer renderer = new BoardRenderer();
            boolean[] keys = new boolean[8];
            JFrame frame = new JFrame("Orbit Run");
            JPanel panel = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    super.paintComponent(g); renderer.paint((Graphics2D) g, model, getWidth(), getHeight());
                }
            };
            panel.setPreferredSize(new Dimension(900, 600));
            String[] names = {"LEFT", "RIGHT", "UP", "DOWN", "A", "D", "W", "S"};
            for (int i = 0; i < names.length; i++) {
                final int index = i;
                bind(panel, "pressed " + names[i], () -> keys[index] = true);
                bind(panel, "released " + names[i], () -> keys[index] = false);
            }
            bind(panel, "released P", () -> { model.togglePause(); panel.repaint(); });
            bind(panel, "released R", () -> { model.restart(); java.util.Arrays.fill(keys, false); panel.repaint(); });
            bind(panel, "pressed ESCAPE", frame::dispose);
            long[] previous = {System.nanoTime()};
            int[] lastTenth = {-1};
            Timer timer = new Timer(16, e -> {
                long now = System.nanoTime(); double seconds = (now - previous[0]) / 1e9; previous[0] = now;
                boolean moving = false;
                for (boolean key : keys) moving |= key;
                if (model.paused() || model.won() || (!model.started() && !moving)) return;
                model.update(seconds, (keys[1] || keys[5] ? 1 : 0) - (keys[0] || keys[4] ? 1 : 0),
                        (keys[3] || keys[7] ? 1 : 0) - (keys[2] || keys[6] ? 1 : 0));
                int tenth = (int) (model.elapsed() * 10);
                if (moving || tenth != lastTenth[0]) panel.repaint();
                lastTenth[0] = tenth;
            });
            timer.setCoalesce(true);
            frame.addWindowListener(new WindowAdapter() {
                @Override public void windowClosed(WindowEvent e) { timer.stop(); }
            });
            frame.addWindowFocusListener(new WindowAdapter() {
                @Override public void windowLostFocus(WindowEvent e) { model.pause(); java.util.Arrays.fill(keys, false); panel.repaint(); }
            });
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); frame.setContentPane(panel);
            frame.setMinimumSize(new Dimension(600, 440)); frame.pack(); frame.setLocationRelativeTo(null); frame.setVisible(true);
            timer.start();
        });
    }
    private static void bind(JPanel panel, String key, Runnable action) {
        panel.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key), key);
        panel.getActionMap().put(key, new AbstractAction() {
            @Override public void actionPerformed(ActionEvent event) { action.run(); }
        });
    }
}
