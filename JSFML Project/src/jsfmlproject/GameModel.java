package jsfmlproject;

/** One deterministic game state; collision response never moves the player backwards. */
public final class GameModel {
    public static final double HALF_SIZE = 16, SPEED = 220, MAX_FRAME_TIME = 0.25;
    private final Level level;
    private final boolean[] collected;
    private double x, y, elapsed;
    private boolean paused, started, won;
    private int collectedCount;

    public GameModel() { this(Level.standard()); }
    public GameModel(Level level) {
        this.level = level;
        collected = new boolean[level.beaconCount()];
        restart();
    }
    public void restart() {
        x = level.startX; y = level.startY; elapsed = 0; paused = false; started = false; won = false;
        collectedCount = 0;
        java.util.Arrays.fill(collected, false);
    }
    public void pause() { if (!won) paused = true; }
    public void togglePause() { if (!won) paused = !paused; }
    public void update(double seconds, double inputX, double inputY) {
        if (paused || won || !Double.isFinite(seconds) || seconds <= 0
                || !Double.isFinite(inputX) || !Double.isFinite(inputY)) return;
        seconds = Math.min(seconds, MAX_FRAME_TIME);
        inputX = Physics.clamp(inputX, -1, 1); inputY = Physics.clamp(inputY, -1, 1);
        double length = Math.hypot(inputX, inputY);
        if (length > 0) started = true;
        if (!started) return;
        elapsed += seconds;
        if (length > 1) { inputX /= length; inputY /= length; }
        double dx = inputX * SPEED * seconds, dy = inputY * SPEED * seconds;
        // Small spatial steps prevent tunneling even after a stalled frame.
        int steps = Math.max(1, (int) Math.ceil(Math.max(Math.abs(dx), Math.abs(dy)) / 2));
        for (int i = 0; i < steps && !won; i++) {
            double nextX = Physics.clamp(x + dx / steps, HALF_SIZE, level.width - HALF_SIZE);
            if (!blocked(nextX, y)) x = nextX;
            double nextY = Physics.clamp(y + dy / steps, level.top + HALF_SIZE, level.height - HALF_SIZE);
            if (!blocked(x, nextY)) y = nextY;
            collect();
        }
    }
    private boolean blocked(double candidateX, double candidateY) {
        for (int i = 0; i < level.obstacleCount(); i++) {
            if (Physics.squareOverlapsCircle(candidateX, candidateY, HALF_SIZE, level.obstacle(i))) return true;
        }
        return false;
    }
    private void collect() {
        for (int i = 0; i < collected.length; i++) {
            if (!collected[i] && Physics.squareOverlapsCircle(x, y, HALF_SIZE, level.beacon(i))) {
                collected[i] = true; collectedCount++;
            }
        }
        if (collectedCount == collected.length && Physics.squareOverlapsCircle(x, y, HALF_SIZE, level.goal)) won = true;
    }
    public Level level() { return level; }
    public double x() { return x; }
    public double y() { return y; }
    public double elapsed() { return elapsed; }
    public boolean paused() { return paused; }
    public boolean won() { return won; }
    public boolean started() { return started; }
    public int collectedCount() { return collectedCount; }
    public boolean collected(int index) { return collected[index]; }
    public String status() {
        if (won) return "Run complete! Press R to play again.";
        if (paused) return "Paused. Press P to resume.";
        if (!started) return "Collect all " + level.beaconCount() + " sparks, then enter the green gate.";
        return collectedCount == collected.length ? "All sparks collected. Head for the green gate!" : "Find the remaining sparks. Circle edges are solid.";
    }
}
