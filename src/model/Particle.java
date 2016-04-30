package model;

public class Particle {

    int id;
    double radius;
    double color;
    double x, y, v, angle;
    Double vx, vy;
    double mass;

    public Particle(int id, double radius, double color, double x, double y) {
        this.id = id;
        this.radius = radius;
        this.color = color;
        this.x = x;
        this.y = y;
    }

    public Particle(int id, double radius, double color, double x, double y, double v, double angle) {
        this(id, radius, color, x, y);
        this.v = v;
        this.angle = angle;
    }

    public Particle(
            int id,
            double radius,
            double color,
            double x,
            double y,
            double v,
            double angle,
            double mass,
            Double vx,
            Double vy) {
        this(id, radius, color, x, y, v, angle);
        this.mass = mass;
        this.vx = vx;
        this.vy = vy;
    }

    public int getId() {
        return id;
    }

    public double getRadius() {
        return radius;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getV() {
        return v;
    }

    public double getCalculatedV() {
        return Math.sqrt(Math.pow(vx, 2) + Math.pow(vy, 2));
    }

    public void setV(double v) {
        this.v = v;
    }

    public void setVx(Double vx) {
        this.vx = vx;
    }

    public double getVx() {
        return vx != null ? vx : v * Math.cos(angle);
    }

    public void setVy(Double vy) {
        this.vy = vy;
    }

    public double getVy() {
        return vy != null ? vy : v * Math.sin(angle);
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public Particle copy() {
        return new Particle(id, radius, color, x, y, v, angle, mass, vx, vy);
    }
}
