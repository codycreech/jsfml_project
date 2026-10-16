package jsfmlproject;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.Locale;

/** Java2D view and headless screenshot renderer; it never changes game state. */
public final class BoardRenderer {
    private static final Color BACKGROUND = new Color(14, 22, 34);
    private static final Color[] OBSTACLE_COLORS = {new Color(111, 102, 178), new Color(72, 132, 158), new Color(164, 99, 122), new Color(83, 145, 129)};
    private final Font titleFont = new Font(Font.SANS_SERIF, Font.BOLD, 26);
    private final Font bodyFont = new Font(Font.SANS_SERIF, Font.PLAIN, 13);
    private final Font largeFont = new Font(Font.SANS_SERIF, Font.BOLD, 32);
    private BufferedImage backdrop;
    private Level backdropLevel;

    private void buildBackdrop(Level level) {
        backdropLevel = level;
        backdrop = new BufferedImage((int) level.width, (int) level.height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = backdrop.createGraphics();
        g.setColor(BACKGROUND); g.fillRect(0, 0, backdrop.getWidth(), backdrop.getHeight());
        g.setColor(new Color(23, 34, 48));
        for (int x = 0; x < level.width; x += 30) g.drawLine(x, (int) level.top, x, (int) level.height);
        for (int y = (int) level.top; y < level.height; y += 30) g.drawLine(0, y, (int) level.width, y);
        g.setColor(new Color(38, 53, 68)); g.drawLine(0, (int) level.top, (int) level.width, (int) level.top);
        g.dispose();
    }
    public void paint(Graphics2D graphics, GameModel model, int width, int height) {
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            Level level = model.level();
            if (backdrop == null || backdropLevel != level) buildBackdrop(level);
            g.setColor(BACKGROUND); g.fillRect(0, 0, width, height);
            double scale = Math.min(width / level.width, height / level.height);
            g.translate((width - level.width * scale) / 2, (height - level.height * scale) / 2); g.scale(scale, scale);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.drawImage(backdrop, 0, 0, null);
            g.setFont(titleFont); g.setColor(new Color(223, 235, 245)); g.drawString("ORBIT RUN", 26, 39);
            g.setFont(bodyFont); g.setColor(new Color(133, 156, 178)); g.drawString("MOVE  Arrows / WASD     PAUSE  P     RESTART  R     QUIT  Esc", 26, 65);
            g.setColor(new Color(241, 190, 95)); g.drawString("SPARKS  " + model.collectedCount() + " / " + level.beaconCount(), 610, 37);
            g.setColor(new Color(129, 205, 218)); g.drawString(String.format(Locale.ROOT, "TIME  %.1f s", model.elapsed()), 748, 37);
            g.setColor(new Color(172, 187, 200)); g.drawString(model.status(), 26, 89);
            g.setStroke(new BasicStroke(3));
            Level.Circle goal = level.goal;
            g.setColor(model.collectedCount() == level.beaconCount() ? new Color(102, 223, 164) : new Color(57, 94, 80));
            g.draw(new RoundRectangle2D.Double(goal.x - goal.radius, goal.y - goal.radius, goal.radius * 2, goal.radius * 2, 12, 12));
            g.setFont(bodyFont); g.drawString("GATE", (float) goal.x - 18, (float) goal.y + 5);
            for (int i = 0; i < level.obstacleCount(); i++) {
                Level.Circle c = level.obstacle(i);
                g.setColor(new Color(0, 0, 0, 60)); g.fill(new Ellipse2D.Double(c.x - c.radius + 3, c.y - c.radius + 6, c.radius * 2, c.radius * 2));
                g.setColor(OBSTACLE_COLORS[i % OBSTACLE_COLORS.length]); g.fill(new Ellipse2D.Double(c.x - c.radius, c.y - c.radius, c.radius * 2, c.radius * 2));
                g.setColor(new Color(255, 255, 255, 45)); g.draw(new Ellipse2D.Double(c.x - c.radius + 7, c.y - c.radius + 7, c.radius * 2 - 14, c.radius * 2 - 14));
            }
            for (int i = 0; i < level.beaconCount(); i++) {
                if (model.collected(i)) continue;
                Level.Circle c = level.beacon(i);
                g.setColor(new Color(240, 185, 89, 35)); g.fill(new Ellipse2D.Double(c.x - 18, c.y - 18, 36, 36));
                g.setColor(new Color(245, 192, 100)); g.fill(new Ellipse2D.Double(c.x - c.radius, c.y - c.radius, c.radius * 2, c.radius * 2));
            }
            g.setColor(new Color(91, 210, 234)); g.fill(new RoundRectangle2D.Double(model.x() - 16, model.y() - 16, 32, 32, 7, 7));
            g.setColor(new Color(214, 251, 255)); g.draw(new RoundRectangle2D.Double(model.x() - 11, model.y() - 11, 22, 22, 4, 4));
            if (model.paused() || model.won()) {
                g.setColor(new Color(7, 13, 21, 210)); g.fill(new RoundRectangle2D.Double(205, 220, 490, 170, 20, 20));
                g.setFont(largeFont); g.setColor(new Color(233, 243, 248));
                g.drawString(model.won() ? "All sparks accounted for." : "Take a breather.", 235, 275);
                g.setFont(bodyFont); g.setColor(new Color(161, 183, 200));
                g.drawString(model.won() ? String.format(Locale.ROOT, "Completed in %.1f seconds. Press R for another run.", model.elapsed()) : "Press P when you are ready to continue.", 235, 320);
            }
        } finally { g.dispose(); }
    }
}
