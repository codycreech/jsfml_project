package jsfmlproject;

/** No test framework, desktop, textures or JSFML native libraries required. */
public final class GameModelTest {
    private static int checks;
    private static void check(boolean value, String message) { checks++; if (!value) throw new AssertionError(message); }
    private static void near(double value, double expected, String message) { check(Math.abs(value - expected) < 0.00001, message + ": " + value); }
    private static Level empty() { return new Level(900, 600, 100, 450, 300, new Level.Circle[0], new Level.Circle[] {new Level.Circle(850, 500, 10)}, new Level.Circle(50, 500, 20)); }
    public static void main(String[] args) {
        GameModel slow = new GameModel(empty()), fast = new GameModel(empty());
        for (int i = 0; i < 30; i++) slow.update(1.0 / 30, 1, 0);
        for (int i = 0; i < 144; i++) fast.update(1.0 / 144, 1, 0);
        near(slow.x(), fast.x(), "Movement must not depend on frame rate"); near(slow.elapsed(), fast.elapsed(), "Timer must not depend on frame rate");
        GameModel diagonal = new GameModel(empty()), straight = new GameModel(empty());
        diagonal.update(.2, 1, 1); straight.update(.2, 1, 0);
        near(Math.hypot(diagonal.x() - 450, diagonal.y() - 300), straight.x() - 450, "Diagonal movement must be normalized");
        Level collision = new Level(900, 600, 100, 450, 300, new Level.Circle[] {new Level.Circle(550, 300, 30)}, new Level.Circle[0], new Level.Circle(50, 500, 20));
        GameModel model = new GameModel(collision);
        for (int i = 0; i < 10; i++) model.update(.25, 1, 0);
        check(model.x() <= 504.00001 && model.x() >= 502, "Player must stop at a circle without tunneling or being pushed back");
        GameModel thin = new GameModel(new Level(900, 600, 100, 450, 300,
                new Level.Circle[] {new Level.Circle(476, 300, 2)}, new Level.Circle[0], new Level.Circle(50, 500, 20)));
        thin.update(.25, 1, 0);
        check(thin.x() <= 458.00001, "A thin obstacle must not be skipped between samples");
        double contact = model.x(); model.update(.1, -1, 0); check(model.x() < contact, "Player must be able to move away from contact");
        check(!Physics.squareOverlapsCircle(500, 250, 16, new Level.Circle(550, 300, 30)), "Transparent corners must not act like square obstacles");
        GameModel boundary = new GameModel(empty());
        for (int i = 0; i < 30; i++) boundary.update(.25, -1, -1);
        near(boundary.x(), 16, "Left boundary"); near(boundary.y(), 116, "Top HUD boundary");
        for (int i = 0; i < 40; i++) boundary.update(.25, 1, 1);
        near(boundary.x(), 884, "Right boundary"); near(boundary.y(), 584, "Bottom boundary");
        double before = boundary.x(); boundary.update(Double.NaN, 1, 0); boundary.update(.1, Double.NaN, 0); near(boundary.x(), before, "Non-finite input must be ignored");
        GameModel paused = new GameModel(empty()); paused.update(.1, 0, 0); near(paused.elapsed(), 0, "Timer starts when movement starts");
        paused.pause(); paused.update(.2, 1, 0); near(paused.x(), 450, "Paused position"); near(paused.elapsed(), 0, "Paused timer");
        paused.togglePause(); paused.update(.1, 1, 0); check(paused.x() > 450, "Resume must restore movement");
        paused.restart(); near(paused.x(), 450, "Restart position"); check(!paused.started() && !paused.paused() && !paused.won() && paused.collectedCount() == 0, "Restart clears all game state");
        GameModel victory = new GameModel(new Level(900, 600, 100, 100, 300, new Level.Circle[0], new Level.Circle[] {new Level.Circle(150, 300, 10)}, new Level.Circle(250, 300, 25)));
        for (int i = 0; i < 120; i++) victory.update(1.0 / 60, 1, 0);
        check(victory.won() && victory.collectedCount() == 1, "Collecting sparks and entering gate must finish the run");
        double finished = victory.elapsed(); victory.update(.2, 1, 0); near(victory.elapsed(), finished, "Winning stops the timer");
        // Exercise a complete route through the real level to prove it is finishable.
        GameModel standard = new GameModel();
        double[][] route = {{756,150},{756,480},{682,480},{500,400},{460,340},{410,300},{360,250},{330,210},{136,150},{100,150},{100,500},{136,500},{100,500},{92,300}};
        for (double[] waypoint : route) {
            for (int i = 0; i < 1800 && !standard.won(); i++) {
                double dx = waypoint[0] - standard.x(), dy = waypoint[1] - standard.y();
                if (Math.hypot(dx, dy) < 3) break;
                double length = Math.hypot(dx, dy);
                standard.update(1.0 / 120, dx / length, dy / length);
            }
        }
        check(standard.won(), "The shipped level must have a complete playable route");
        System.out.println("Passed " + checks + " game checks.");
    }
}
