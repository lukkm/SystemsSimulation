package model;

public class Particle {

    int id;
    double radius;
    double color;
    double x, y, v, angle;

    public Particle(int id, double radius, double color, double x, double y) {
        this.id = id;
        this.radius = radius;
        this.color = color;
        this.x = x;
        this.y = y;
    }

    public Particle(int id, double radius, double color, double x, double y, double v, double angle) {
        this.id = id;
        this.radius = radius;
        this.color = color;
        this.x = x;
        this.y = y;
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

    public void setV(double v) {
        this.v = v;
    }

    public double getVx() {
        return v * Math.sin(angle);
    }

    public double getVy() {
        return v * Math.cos(angle);
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public Particle copy() {
        return new Particle(id, radius, color, x, y, v, angle);
    }
}
