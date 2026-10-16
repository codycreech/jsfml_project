package jsfmlproject;

/** Immutable level data shared by both renderers and the physics tests. */
public final class Level {
    public static final class Circle {
        public final double x, y, radius;
        public Circle(double x, double y, double radius) {
            if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(radius) || radius <= 0) {
                throw new IllegalArgumentException("Circle coordinates and radius must be finite and valid.");
            }
            this.x = x; this.y = y; this.radius = radius;
        }
    }
    public final double width, height, top, startX, startY;
    public final Circle goal;
    private final Circle[] obstacles, beacons;

    public Level(double width, double height, double top, double startX, double startY,
                 Circle[] obstacles, Circle[] beacons, Circle goal) {
        if (!Double.isFinite(width) || !Double.isFinite(height) || !Double.isFinite(top)
                || width < 100 || height - top < 100 || top < 0 || goal == null) {
            throw new IllegalArgumentException("Invalid level bounds.");
        }
        this.width = width; this.height = height; this.top = top;
        this.startX = startX; this.startY = startY;
        this.obstacles = obstacles.clone(); this.beacons = beacons.clone(); this.goal = goal;
        if (!Double.isFinite(startX) || !Double.isFinite(startY)
                || startX < GameModel.HALF_SIZE || startX > width - GameModel.HALF_SIZE
                || startY < top + GameModel.HALF_SIZE || startY > height - GameModel.HALF_SIZE) {
            throw new IllegalArgumentException("The player must start inside the arena.");
        }
        for (Circle circle : this.obstacles) {
            if (Physics.squareOverlapsCircle(startX, startY, GameModel.HALF_SIZE, circle)) {
                throw new IllegalArgumentException("The player cannot start inside an obstacle.");
            }
        }
    }
    public int obstacleCount() { return obstacles.length; }
    public Circle obstacle(int index) { return obstacles[index]; }
    public int beaconCount() { return beacons.length; }
    public Circle beacon(int index) { return beacons[index]; }
    public static Level standard() {
        return new Level(900, 600, 100, 818, 300,
                new Circle[] {new Circle(270, 300, 64), new Circle(410, 166, 64),
                        new Circle(410, 434, 64), new Circle(550, 300, 64)},
                new Circle[] {new Circle(756, 150, 10), new Circle(682, 480, 10),
                        new Circle(410, 300, 10), new Circle(136, 500, 10), new Circle(136, 150, 10)},
                new Circle(92, 300, 27));
    }
}
