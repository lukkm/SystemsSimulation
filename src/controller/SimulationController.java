package controller;

import java.util.ArrayList;
import java.util.List;

import model.Particle;
import utils.*;

import java.util.*;

public class SimulationController {

    private float l;
    private float w;
    private float d;
    private List<Particle> particleList;
    private List<Particle> obstacleList;
    private List<Particle> moveableObstacleList;
    private Particle sun;
    private double orbitL;

    public SimulationController(float l) {
        this.l = l;
        particleList = new ArrayList<>();
        obstacleList = new ArrayList<>();
        moveableObstacleList = new ArrayList<>();
    }

    public SimulationController(float l, float w) {
        this(l);
        this.w = w;
    }

    public SimulationController(float l, float w, float d) {
        this(l, w);
        this.d = d;
    }

    public SimulationController(float l, List<Particle> particleList) {
        this(l);
        this.particleList = particleList;
    }

    public SimulationController(float l, List<Particle> particleList, List<Particle> obstacleList, List<Particle> moveableObstacleList) {
        this(l, particleList);
        this.obstacleList = obstacleList;
        this.moveableObstacleList = moveableObstacleList;
    }

    public void addPartcile(Particle p) {
        particleList.add(p);
    }

    public void addObstacle(Particle p) {
        obstacleList.add(p);
    }

    public void addMoveableObstacle(Particle p) {
        moveableObstacleList.add(p);
    }

    public void setSun(Particle sun) {
        this.sun = sun;
    }

    public void setOrbitL(double orbitL) {
        this.orbitL = orbitL;
    }

    public List<Particle> getParticleList() {
        return particleList;
    }

    public List<Particle> getObstacleList() {
        return obstacleList;
    }

    public List<Particle> getMoveableObstacleList() {
        return moveableObstacleList;
    }

    public List<List<Particle>> simulateObstacles(int steps) {
        Particle human = particleList.get(0);

        List<List<Particle>> particleSteps = new ArrayList<>();

        List<Particle> lastStep = new ArrayList<>(moveableObstacleList);

        List<Particle> completeList = new ArrayList<>();
        completeList.add(human);
        completeList.addAll(obstacleList);
        completeList.addAll(moveableObstacleList);
        particleSteps.add(completeList);

        for (Particle moveableObstacle : moveableObstacleList) {
            ObstacleUtils.setRandomGoal(moveableObstacle);
        }

        double dt = 0.0001;
        int outputDt = 2000;

        Particle newParticle = human;

        for (int i = 0; i < steps; i++) {
            List<Particle> newStep = new ArrayList<>();
            double fx, fy;

            System.out.println(i * 0.2);

            ObstacleUtils.ObstacleGoal prevGoal = ObstacleUtils.MAIN_GOAL;

            if (DistanceUtils.calculateDistance(
                    newParticle.getX(),
                    newParticle.getY(),
                    newParticle.getRadius(),
                    ObstacleUtils.MAIN_GOAL.getX(),
                    ObstacleUtils.MAIN_GOAL.getY(),
                    0) < newParticle.getRadius()) {
                break;
            }

            // Evolve slowly to simulate continuous time
            for (int j = 0; j < outputDt; j++) {
                newStep.clear();

                for (Particle p : lastStep) {
                    fx = 0;
                    fy = 0;

                    // Calculate obstacle collisions
                    for (Particle p2 : obstacleList) {
                        VerletUtils.CollisionForces forces = VerletUtils.calculateParticleCollisionForce(p, p2);
                        if (forces != null) {
                            fx += forces.getFx();
                            fy += forces.getFy();
                        }
                    }
                    for (Particle p2 : lastStep) {
                        if (p2 != p) {
                            VerletUtils.CollisionForces forces = VerletUtils.calculateParticleCollisionForce(p, p2);
                            if (forces != null) {
                                fx += forces.getFx();
                                fy += forces.getFy();
                            }
                        }
                    }

                    VerletUtils.CollisionForces humanForces = VerletUtils.calculateParticleCollisionForce(p, newParticle);
                    if (humanForces != null) {
                        fx += humanForces.getFx();
                        fy += humanForces.getFy();
                    }

                    // Add goal force
                    ParticleForce goalForces = ObstacleUtils.calculateGoalForces(p, p.getGoal());
                    fx += goalForces.getFx();
                    fy += goalForces.getFy();

                    // Calculate social forces
                    for (Particle p2 : obstacleList) {
                        ParticleForce forces = EscapeUtils.calculateSocialForces(p, p2);
                        if (forces != null) {
                            fx += forces.getFx();
                            fy += forces.getFy();
                        }
                    }
                    for (Particle p2 : lastStep) {
                        if (p != p2) {
                            ParticleForce forces = EscapeUtils.calculateSocialForces(p, p2);
                            if (forces != null) {
                                fx += forces.getFx();
                                fy += forces.getFy();
                            }
                        }
                    }

                    if (DistanceUtils.calculateDistance(
                            p.getX(),
                            p.getY(),
                            p.getRadius(),
                            p.getGoal().getX(),
                            p.getGoal().getY(),
                            0) < p.getRadius()) {
                        ObstacleUtils.setRandomGoal(p);
                    }

                    newStep.add(VerletUtils.integrate(p, dt, fx, fy));
                }
                lastStep = new ArrayList<>(newStep);

                // Here
                // Starts
                // Human
                if (DistanceUtils.calculateDistance(
                        newParticle.getX(),
                        newParticle.getY(),
                        newParticle.getRadius(),
                        ObstacleUtils.MAIN_GOAL.getX(),
                        ObstacleUtils.MAIN_GOAL.getY(),
                        0) > newParticle.getRadius()) {
                    for (ObstacleUtils.ObstacleGoal goal : ObstacleUtils.GOALS_LIST) {
                        goal.revive();
                    }

                    fx = 0;
                    fy = 0;

                    // Calculate obstacle collisions
                    for (Particle p : obstacleList) {
                        VerletUtils.CollisionForces forces = VerletUtils.calculateParticleCollisionForce(newParticle, p);
                        if (forces != null) {
                            fx += forces.getFx();
                            fy += forces.getFy();
                        }
                    }
                    for (Particle p : lastStep) {
                        VerletUtils.CollisionForces forces = VerletUtils.calculateParticleCollisionForce(newParticle, p);
                        if (forces != null) {
                            fx += forces.getFx();
                            fy += forces.getFy();
                        }
                    }

                    // Calculate desired goal
                    ObstacleUtils.ObstacleGoal goal = null;
                    if (canReachGoal(newParticle, ObstacleUtils.MAIN_GOAL)) {
                        goal = ObstacleUtils.MAIN_GOAL;
                    } else {
                        if (ObstacleUtils.MAIN_GOAL.isValid()) goal = ObstacleUtils.MAIN_GOAL;
                        if (goal == null || !canMoveTowardsGoal(newParticle, goal, lastStep)) {
                            for (ObstacleUtils.ObstacleGoal goalTrial : ObstacleUtils.GOALS_LIST) {
                                if ((goalTrial.getY() == 19 && canReachGoal(newParticle, goalTrial))) {
                                    goal = goalTrial;
                                    break;
                                }
                                if (goalTrial.isValid()) {
                                    if (canMoveTowardsGoal(newParticle, goalTrial, lastStep)) {
                                        goal = goalTrial;
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    if (goal == null) goal = ObstacleUtils.MAIN_GOAL;

                    // If goal reached and not useful, invalidate it for good
                    if (goal != ObstacleUtils.MAIN_GOAL) {
                        if (DistanceUtils.calculateDistance(
                                newParticle.getX(),
                                newParticle.getY(),
                                newParticle.getRadius(),
                                goal.getX(),
                                goal.getY(),
                                0) < 0) {
                            goal.invalidate(Integer.MAX_VALUE);
                        }
                    }

                    if (goal != prevGoal) {
                        prevGoal.invalidate(50000);
                    }

                    prevGoal = goal;

                    // Add goal force
                    ParticleForce goalForces = ObstacleUtils.calculateGoalForces(newParticle, goal);
                    fx += goalForces.getFx();
                    fy += goalForces.getFy();

                    // Calculate social forces
                    for (Particle p : obstacleList) {
                        ParticleForce forces = EscapeUtils.calculateSocialForces(newParticle, p);
                        if (forces != null) {
                            fx += forces.getFx();
                            fy += forces.getFy();
                        }
                    }
                    for (Particle p : lastStep) {
                        ParticleForce forces = EscapeUtils.calculateSocialForces(newParticle, p);
                        if (forces != null) {
                            fx += forces.getFx();
                            fy += forces.getFy();
                        }
                    }

                    newParticle = VerletUtils.integrate(newParticle, dt, fx, fy);
                }
            }

            completeList = new ArrayList<>();
            completeList.add(newParticle);
            completeList.addAll(obstacleList);
            completeList.addAll(lastStep);

            particleSteps.add(completeList);
        }

        return particleSteps;
    }

    private boolean canMoveTowardsGoal(Particle p, ObstacleUtils.ObstacleGoal goal, List<Particle> moveableObstacles) {
        double deltaX = goal.getX() - p.getX();
        double deltaY = goal.getY() - p.getY();
        double angle = Math.atan2(deltaX, deltaY);

        double xDiff = Math.sin(angle) * p.getRadius() * 2;
        double yDiff = Math.cos(angle) * p.getRadius() * 2;
        for (int k = 1; k <= 5; k++) {
            double newDeltaX = xDiff * k;
            double newDeltaY = yDiff * k;

            for (Particle obstacle : obstacleList) {
                if (DistanceUtils.calculateDistance(
                        p.getX() + newDeltaX,
                        p.getY() + newDeltaY,
                        p.getRadius(),
                        obstacle.getX(),
                        obstacle.getY(),
                        obstacle.getRadius()) < p.getRadius()) {
                    return false;
                }
            }
            for (Particle obstacle : moveableObstacles) {
                if (DistanceUtils.calculateDistance(
                        p.getX() + newDeltaX,
                        p.getY() + newDeltaY,
                        p.getRadius(),
                        obstacle.getX(),
                        obstacle.getY(),
                        obstacle.getRadius()) < p.getRadius()) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean canReachGoal(Particle p, ObstacleUtils.ObstacleGoal goal) {
        double deltaX = goal.getX() - p.getX();
        double deltaY = goal.getY() - p.getY();
        double angle = Math.atan2(deltaX, deltaY);

        double xDiff = Math.sin(angle) * p.getRadius() * 2;
        double yDiff = Math.cos(angle) * p.getRadius() * 2;

        int k = 2;
        double newX = p.getX() + xDiff;
        double newY = p.getY() + yDiff;
        while (DistanceUtils.calculateDistance(
                newX,
                newY,
                p.getRadius(),
                goal.getX(),
                goal.getY(),
                0) > p.getRadius()) {
            newX = p.getX() + xDiff * k;
            newY = p.getY() + yDiff * k;

            for (Particle obstacle : obstacleList) {
                if (DistanceUtils.calculateDistance(
                        newX,
                        newY,
                        p.getRadius(),
                        obstacle.getX(),
                        obstacle.getY(),
                        obstacle.getRadius()) < 0) {
                    return false;
                }
            }
            for (Particle obstacle : moveableObstacleList) {
                if (DistanceUtils.calculateDistance(
                        newX,
                        newY,
                        p.getRadius(),
                        obstacle.getX(),
                        obstacle.getY(),
                        obstacle.getRadius()) < 0) {
                    return false;
                }
            }

            k++;
        }

        return true;
    }

    public List<List<Particle>> simulateEscape(int steps) {
        List<List<Particle>> particleSteps = new ArrayList<>();
        List<Particle> lastStep = new ArrayList<>(particleList);
        particleSteps.add(particleList);

        double dt = 0.0001;
        int outputDt = 2000;

        for (int i = 0; i < steps; i++) {
            List<Particle> newStep = new ArrayList<>();

            System.out.println(((double)i * 0.2) + " " + lastStep.size());

            double fx, fy;

            // Evolve slowly to simulate continuous time
            for (int j = 0; j < outputDt; j++) {
                newStep.clear();
                for (Particle p : lastStep) {
                    fx = 0;
                    fy = 0;

                    // Calculate particle collisions
                    for (Particle p2 : lastStep) {
                        if (p != p2) {
                            VerletUtils.CollisionForces forces = VerletUtils.calculateParticleCollisionForce(p, p2);
                            if (forces != null) {
                                fx += forces.getFx();
                                fy += forces.getFy();
                            }
                        }
                    }

                    // Add goal force
                    ParticleForce goalForces = EscapeUtils.calculateGoalForces(p);
                    fx += goalForces.getFx();
                    fy += goalForces.getFy();

                    // Calculate social forces
                    for (Particle p2 : lastStep) {
                        if (p != p2) {
                            ParticleForce forces = EscapeUtils.calculateSocialForces(p, p2);
                            if (forces != null) {
                                fx += forces.getFx();
                                fy += forces.getFy();
                            }
                        }
                    }

                    // Add wall collisions
                    if (EscapeUtils.isYColliding(p, w)) {
                        p.setPrevY(p.getY());
                        p.setVy(0.0);
                        fy = 0;
                    }
                    if (EscapeUtils.isDoorXColliding(p, w, l) || EscapeUtils.isXColliding(p, w)) {
                        fx = 0;
                        p.setPrevX(p.getX());
                        p.setVx(0.0);
                    }

                    Particle newParticle = VerletUtils.integrate(p, dt, fx, fy);

                    // Remove particles outside the system
                    if (newParticle.getX() <= EscapeUtils.ESCAPE_X) newStep.add(newParticle);
                }
                lastStep = new ArrayList<>(newStep);
            }

            particleSteps.add(lastStep);
        }

        return particleSteps;
    }

    public List<List<Particle>> simulateSilum(int steps) {
        List<List<Particle>> particleSteps = new ArrayList<>();
        List<Particle> lastStep = new ArrayList<>(particleList);
        particleSteps.add(particleList);

        double dt = 0.000001;
        int outputDt = 10000;

        for (int i = 0; i < steps; i++) {
            List<Particle> newStep = new ArrayList<>();

            double K = 0;
            for (Particle p : lastStep) {
                K += (p.getMass() * (Math.pow(p.getVx(), 2) + Math.pow(p.getVy(), 2))) / 2;
            }

            System.out.println(i + " " + lastStep.size() + " " + K);

            double fx, fy;

            // Evolve slowly to simulate continuous time
            for (int j = 0; j < outputDt; j++) {
                newStep.clear();
                for (Particle p : lastStep) {
                    fx = 0;
                    fy = p.getMass() * VerletUtils.GRAVITY_CONSTANT;

                    // Calculate particle collisions
                    for (Particle p2 : lastStep) {
                        if (p != p2) {
                            VerletUtils.CollisionForces forces = VerletUtils.calculateParticleCollisionForce(p, p2);
                            if (forces != null) {
                                fx += forces.getFx();
                                fy += forces.getFy();
                            }
                        }
                    }

                    // Add wall collision forces
                    fy += VerletUtils.calculateWallYCollisionForce(p, w, l, d);
                    fx += VerletUtils.calculateWallXCollisionForce(p, w);

                    Particle newParticle = VerletUtils.integrate(p, dt, fx, fy);

                    // Remove particles outside the system
                    if (newParticle.getY() >= -1) newStep.add(newParticle);
                }
                lastStep = new ArrayList<>(newStep);
            }

            particleSteps.add(lastStep);
        }

        return particleSteps;
    }

    public List<List<Particle>> simulateOrbits(int steps) {
        List<List<Particle>> particleSteps = new ArrayList<>();
        List<Particle> lastStep = new ArrayList<>(particleList);
        particleSteps.add(particleList);

        double dt = 1;
        int outputDt = 400;

        for (int i = 0; i < steps; i++) {
            List<Particle> newStep = new ArrayList<>();

            // Evolve slowly to simulate continuous time
            for (int j = 0; j < outputDt; j++) {
                newStep.clear();
                newStep.add(sun);
                for (Particle p : lastStep) {
                    Particle newParticle = evolveParticle(p, dt);

                    if (p != sun) {
                        OrbitUtils.updateVelocity(sun, newParticle, orbitL);
                        newStep.add(newParticle);
                    }
                }
                lastStep = new ArrayList<>(newStep);
            }

            lastStep = OrbitUtils.calculateCollisions(sun, orbitL, lastStep);
            particleSteps.add(lastStep);
        }

        return particleSteps;
    }

    public List<List<Particle>> simulateCollisions(int steps) {
        double dt = 0.1;
        List<List<Particle>> particleSteps = new ArrayList<>();
        List<Particle> lastStep = new ArrayList<>(particleList);
        particleSteps.add(particleList);
        for (int i = 0; i < steps; i++) {
            CollisionUtils.CollidingParticleTime nextCollision = CollisionUtils.getNextCollision(lastStep, l);

            if (nextCollision.getTime() > dt) {
                // Evolve particles
                List<Particle> newStep = new ArrayList<>();
                for (Particle p : lastStep) {
                    Particle newParticle = evolveParticle(p, dt);
                    newStep.add(newParticle);
                }

                particleSteps.add(newStep);
                lastStep = new ArrayList<>(newStep);
            } else {
                // Initialize current time
                double currentTime = dt;
                List<Particle> newStep = new ArrayList<>();
                while (nextCollision.getTime() < currentTime) {
                    // Start new step
                    newStep.clear();

                    // Evolve particles
                    for (Particle p : lastStep) {
                        Particle newParticle = evolveParticle(p, nextCollision.getTime());

                        // Set the new particles to be updated
                        if (p.getId() == nextCollision.getP1().getId()) {
                            nextCollision.setP1(newParticle);
                        }
                        if (nextCollision.getP2() != null && p.getId() == nextCollision.getP2().getId()) {
                            nextCollision.setP2(newParticle);
                        }

                        newStep.add(newParticle);
                    }

                    // Collide!
                    if (nextCollision.getP2() == null) {
                        // Wall collision
                        Particle p = nextCollision.getP1();
                        if (nextCollision.isxCollision()) {
                            p.setVx(-p.getVx());
                        } else {
                            p.setVy(-p.getVy());
                        }
                    } else {
                        // Particle collision
                        CollisionUtils.collideParticles(nextCollision);
                    }
                    currentTime -= nextCollision.getTime();

                    // Recalculate next collision
                    nextCollision = CollisionUtils.getNextCollision(newStep, l);

                    // Move steps along
                    lastStep = new ArrayList<>(newStep);
                }
                particleSteps.add(newStep);
            }
        }

        return particleSteps;
    }

    public List<List<Particle>> simulateSteps(int steps, double dt, double n) {
        double newX, newY, newAngle;

        List<List<Particle>> particleSteps = new ArrayList<>();
        particleSteps.add(particleList);

        List<Particle> lastStep = new ArrayList<>(particleList);

        for (int i = 0; i < steps; i++) {
            List<Particle> newStep = new ArrayList<>();
            for (Particle p : lastStep) {
                Particle newParticle = p.copy();
                double angleAverage = AngleUtils.getAngleAverage(p, lastStep);
                newX = ((p.getX() + p.getVy() * dt) + l) % l;
                newY = ((p.getY() + p.getVx() * dt) + l) % l;
                newAngle = angleAverage + (Math.random() * n - n/2);

                newParticle.setX(newX);
                newParticle.setY(newY);
                newParticle.setAngle(newAngle);

                newStep.add(newParticle);
            }
            particleSteps.add(newStep);
            lastStep = new ArrayList<>(newStep);
        }

        return particleSteps;
    }

    @SuppressWarnings("unchecked")
    public Map<Particle, Set<Particle>> calculateDistance(int m, float rc, boolean crossMap) {
        Map<Particle, Set<Particle>> closeParticles = new LinkedHashMap<>();
        List<Particle>[][] cells = new List[m][m];

        double cellSize = (double)l / m;

        for (Particle p : particleList) {
            int xCell = (int)Math.floor(p.getX()/cellSize);
            int yCell = (int)Math.floor(p.getY()/cellSize);

            if (xCell > 0 && xCell < cells.length && yCell > 0 && yCell < cells.length) {
                if (cells[xCell][yCell] == null) cells[xCell][yCell] = new ArrayList<>();
                cells[xCell][yCell].add(p);
            }
        }


        for (int i = 0; i < m; i++){
            for(int j = 0; j < m; j++) {
                List<Particle> cellParticles = cells[i][j];

                NeighborUtils.calculateNeighbors(cellParticles, closeParticles, cells, l, i, j, rc, crossMap);
                NeighborUtils.calculateNeighbors(cellParticles, closeParticles, cells, l, i + 1, j, rc, crossMap);
                NeighborUtils.calculateNeighbors(cellParticles, closeParticles, cells, l, i + 1, j + 1, rc, crossMap);
                NeighborUtils.calculateNeighbors(cellParticles, closeParticles, cells, l, i, j + 1, rc, crossMap);
                NeighborUtils.calculateNeighbors(cellParticles, closeParticles, cells, l, i + 1, j - 1, rc, crossMap);
            }
        }

        return closeParticles;
    }

    private Particle evolveParticle(Particle p, double time) {
        Particle newParticle = p.copy();

        newParticle.setX(p.getX() + p.getVx() * time);
        newParticle.setY(p.getY() + p.getVy() * time);
        return newParticle;
    }

}
