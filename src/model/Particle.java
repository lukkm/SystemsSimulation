package model;

public class Particle {

    int id;
    float radius;
    float color;
    float x, y, vx, vy;

    public Particle(int id, float radius, float color, float x, float y) {
        this.id = id;
        this.radius = radius;
        this.color = color;
        this.x = x;
        this.y = y;
    }

    public int getId() {
        return id;
    }

    public float getRadius() {
        return radius;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getVx() {
        return vx;
    }

    public void setVx(float vx) {
        this.vx = vx;
    }

    public float getVy() {
        return vy;
    }

    public void setVy(float vy) {
        this.vy = vy;
    }

    public Particle copy() {
        return new Particle(id, radius, color, x, y);
    }
}
