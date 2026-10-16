package jsfmlproject;

import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public final class Main {
    private Main() { }
    public static void main(String[] args) {
        boolean forceNative = false, forceSwing = false;
        for (int i = 0; i < args.length; i++) {
            if ("--screenshot".equals(args[i]) && i + 1 < args.length) {
                try { saveScreenshot(args[++i]); } catch (IOException e) { System.err.println(e.getMessage()); System.exit(1); }
                return;
            }
            if ("--jsfml".equals(args[i])) forceNative = true;
            else if ("--swing".equals(args[i])) forceSwing = true;
            else if ("--help".equals(args[i])) {
                System.out.println("Orbit Run: arrows/WASD move, P pause, R restart, Esc quit.\nOptions: --swing, --jsfml, --screenshot <file.png>"); return;
            } else {
                System.err.println("Unknown or incomplete option: " + args[i]); System.exit(1); return;
            }
        }
        if (forceNative && forceSwing) { System.err.println("Choose one renderer: --jsfml or --swing."); System.exit(1); return; }
        if (GraphicsEnvironment.isHeadless()) {
            System.err.println("No desktop display is available. Use --screenshot <file.png> for a headless render."); System.exit(1); return;
        }
        boolean windows = System.getProperty("os.name", "").toLowerCase(java.util.Locale.ROOT).startsWith("windows");
        if (forceNative || (!forceSwing && windows)) {
            try { NativeGame.run(); return; }
            catch (LinkageError | IOException error) {
                System.err.println("The bundled legacy JSFML renderer could not start: " + error.getMessage());
                if (forceNative) { System.exit(1); return; }
                System.err.println("Starting the Java2D renderer instead.");
            }
        }
        SwingGame.run();
    }
    private static void saveScreenshot(String filename) throws IOException {
        BufferedImage image = new BufferedImage(900, 600, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        try { new BoardRenderer().paint(graphics, new GameModel(), 900, 600); } finally { graphics.dispose(); }
        if (!ImageIO.write(image, "png", new File(filename))) throw new IOException("PNG output is unavailable.");
        System.out.println("Saved screenshot: " + filename);
    }
}
