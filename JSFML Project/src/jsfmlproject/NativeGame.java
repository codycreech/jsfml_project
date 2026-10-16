package jsfmlproject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import org.jsfml.graphics.CircleShape;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.Font;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Text;
import org.jsfml.system.Clock;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard;
import org.jsfml.window.Keyboard.Key;
import org.jsfml.window.VideoMode;
import org.jsfml.window.WindowStyle;
import org.jsfml.window.event.Event;

/** JSFML renderer for machines supported by the original bundled native library. */
public final class NativeGame {
    private NativeGame() { }
    public static void run() throws IOException {
        GameModel model = new GameModel();
        Level level = model.level();
        RenderWindow window = new RenderWindow(new VideoMode(900, 600), "Orbit Run - JSFML",
                WindowStyle.TITLEBAR | WindowStyle.CLOSE);
        try {
            if (!window.isOpen()) throw new IOException("The native window could not be created.");
            window.setFramerateLimit(60);
            window.setKeyRepeatEnabled(false);
            Font font = loadSystemFont();
            Text title = text("ORBIT RUN", font, 26, 26, 12, new Color(223, 235, 245));
            Text controls = text("MOVE Arrows / WASD   |   P Pause   |   R Restart   |   Esc Quit", font, 13, 26, 50, new Color(133, 156, 178));
            Text status = text("", font, 12, 26, 77, new Color(172, 187, 200));
            Text score = text("", font, 14, 610, 20, new Color(241, 190, 95));
            Text timer = text("", font, 14, 754, 20, new Color(129, 205, 218));
            Text overlayTitle = text("", font, 30, 240, 245, Color.WHITE);
            Text overlayBody = text("", font, 14, 240, 300, new Color(161, 183, 200));
            RectangleShape overlay = new RectangleShape(new Vector2f(490, 160));
            overlay.setPosition(205, 220); overlay.setFillColor(new Color(7, 13, 21, 235));
            RectangleShape divider = new RectangleShape(new Vector2f(900, 1));
            divider.setPosition(0, 100); divider.setFillColor(new Color(38, 53, 68));
            RectangleShape player = new RectangleShape(new Vector2f(32, 32));
            player.setOrigin(16, 16); player.setFillColor(new Color(91, 210, 234));
            player.setOutlineColor(new Color(214, 251, 255)); player.setOutlineThickness(-2);
            RectangleShape goal = new RectangleShape(new Vector2f(54, 54));
            goal.setOrigin(27, 27); goal.setPosition((float) level.goal.x, (float) level.goal.y);
            goal.setFillColor(Color.TRANSPARENT); goal.setOutlineThickness(3);
            CircleShape[] obstacles = new CircleShape[level.obstacleCount()];
            Color[] colors = {new Color(111, 102, 178), new Color(72, 132, 158), new Color(164, 99, 122), new Color(83, 145, 129)};
            for (int i = 0; i < obstacles.length; i++) obstacles[i] = circle(level.obstacle(i), colors[i % colors.length]);
            CircleShape[] beacons = new CircleShape[level.beaconCount()];
            for (int i = 0; i < beacons.length; i++) beacons[i] = circle(level.beacon(i), new Color(245, 192, 100));
            Clock clock = new Clock();
            boolean focused = true;
            String lastStatus = "", lastScore = "", lastTime = "";
            while (window.isOpen()) {
                double seconds = clock.restart().asSeconds();
                for (Event event : window.pollEvents()) {
                    if (event.type == Event.Type.CLOSED) window.close();
                    else if (event.type == Event.Type.LOST_FOCUS) { focused = false; model.pause(); }
                    else if (event.type == Event.Type.GAINED_FOCUS) focused = true;
                    else if (event.type == Event.Type.KEY_PRESSED) {
                        Key key = event.asKeyEvent().key;
                        if (key == Key.ESCAPE) window.close();
                        else if (key == Key.R) model.restart();
                        else if (key == Key.P) model.togglePause();
                    }
                }
                if (!window.isOpen()) break;
                double dx = focused ? (down(Key.RIGHT, Key.D) ? 1 : 0) - (down(Key.LEFT, Key.A) ? 1 : 0) : 0;
                double dy = focused ? (down(Key.DOWN, Key.S) ? 1 : 0) - (down(Key.UP, Key.W) ? 1 : 0) : 0;
                model.update(seconds, dx, dy);
                player.setPosition((float) model.x(), (float) model.y());
                goal.setOutlineColor(model.collectedCount() == level.beaconCount() ? new Color(102, 223, 164) : new Color(57, 94, 80));
                String nextStatus = model.status();
                String nextScore = "SPARKS " + model.collectedCount() + " / " + level.beaconCount();
                String nextTime = String.format(Locale.ROOT, "%.1f s", model.elapsed());
                if (!lastStatus.equals(nextStatus)) { status.setString(nextStatus); lastStatus = nextStatus; }
                if (!lastScore.equals(nextScore)) { score.setString(nextScore); lastScore = nextScore; }
                if (!lastTime.equals(nextTime)) { timer.setString(nextTime); lastTime = nextTime; }
                window.clear(new Color(14, 22, 34));
                window.draw(divider); window.draw(goal);
                for (CircleShape obstacle : obstacles) window.draw(obstacle);
                for (int i = 0; i < beacons.length; i++) if (!model.collected(i)) window.draw(beacons[i]);
                window.draw(player); window.draw(title); window.draw(controls); window.draw(status); window.draw(score); window.draw(timer);
                if (model.paused() || model.won()) {
                    overlayTitle.setString(model.won() ? "Run complete!" : "Take a breather.");
                    overlayBody.setString(model.won() ? "Press R for another run." : "Press P to continue.");
                    window.draw(overlay); window.draw(overlayTitle); window.draw(overlayBody);
                }
                window.display();
            }
        } finally { window.close(); }
    }
    private static boolean down(Key first, Key second) { return Keyboard.isKeyPressed(first) || Keyboard.isKeyPressed(second); }
    private static CircleShape circle(Level.Circle circle, Color color) {
        CircleShape shape = new CircleShape((float) circle.radius, 48);
        shape.setOrigin((float) circle.radius, (float) circle.radius);
        shape.setPosition((float) circle.x, (float) circle.y); shape.setFillColor(color);
        shape.setOutlineThickness(-2); shape.setOutlineColor(new Color(255, 255, 255, 45));
        return shape;
    }
    private static Text text(String value, Font font, int size, float x, float y, Color color) {
        Text text = new Text(value, font, size); text.setPosition(x, y); text.setColor(color); return text;
    }
    private static Font loadSystemFont() throws IOException {
        String windows = System.getenv("WINDIR");
        Path[] paths = {Paths.get(windows == null ? "C:/Windows" : windows, "Fonts", "segoeui.ttf"),
                Paths.get(windows == null ? "C:/Windows" : windows, "Fonts", "arial.ttf"),
                Paths.get("/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf"),
                Paths.get("/System/Library/Fonts/Supplemental/Arial.ttf")};
        for (Path path : paths) if (Files.isRegularFile(path)) { Font font = new Font(); font.loadFromFile(path); return font; }
        throw new IOException("No supported system font was found. Run with --swing instead.");
    }
}
