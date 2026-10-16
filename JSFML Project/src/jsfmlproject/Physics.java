package jsfmlproject;

/** Geometry independent of JSFML, textures, GPU drivers, and frame timing. */
public final class Physics {
    private Physics() { }
    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
    public static boolean squareOverlapsCircle(double x, double y, double halfSize, Level.Circle circle) {
        double closestX = clamp(circle.x, x - halfSize, x + halfSize);
        double closestY = clamp(circle.y, y - halfSize, y + halfSize);
        double dx = circle.x - closestX, dy = circle.y - closestY;
        return dx * dx + dy * dy < circle.radius * circle.radius - 1e-8;
    }
}
