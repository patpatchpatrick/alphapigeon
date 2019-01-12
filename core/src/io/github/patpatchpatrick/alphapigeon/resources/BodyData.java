package io.github.patpatchpatrick.alphapigeon.resources;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;

import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.EnergyBall;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.Teleport;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.UFO;

public class BodyData {

    // Class used to store data on bodies

    private Boolean flaggedForDelete;

    private long spawnTime = 9999;

    //TELEPORT DATA
    private Teleport oppositeTeleport;

    //ROCKET DATA
    private float rocketYForce = 0f;
    private long lastRocketExplosionTime = 99999;

    //UFO DATA
    private float energyBeamDirection;
    private UFO ufo;

    //POWERUP DATA
    public int powerUpType = 0;


    public BodyData(boolean flagForDelete) {
        flaggedForDelete = flagForDelete;
    }

    public Boolean isFlaggedForDelete() {
        if (flaggedForDelete != null) {
            return flaggedForDelete;
        } else {
            return false;
        }
    }

    public void setFlaggedForDelete(boolean flaggedForDelete) {
        this.flaggedForDelete = flaggedForDelete;
    }

    public void setSpawnTime(long spawnTime) {
        this.spawnTime = spawnTime;
    }

    public long getSpawnTime() {
        return this.spawnTime;
    }

    //TELEPORT DATA

    public void setOppositeTeleport(Teleport oppositeTeleport) {
        // Method used for teleport bodies, to store info about its counterpart teleport
        this.oppositeTeleport = oppositeTeleport;
    }

    public Teleport getOppositeTeleport() {
        // Method used for teleport bodies, to return info about its counterpart teleport
        return this.oppositeTeleport;
    }

    //ROCKET DATA

    public void setRocketData(float torque, boolean rocketSpawnedInBottomHalfOfScreen) {
        // Based on the rockets torque and where the rocket is spawned, calculate what force should
        // be applied to rocket in the Y direction
        if (rocketSpawnedInBottomHalfOfScreen) {
            rocketYForce = -0.2f * torque - 0.2f;
        } else {
            rocketYForce = -0.3f * torque - 0.6f;
        }
    }

    public void setExplosionData(long lastRocketExplosionTime) {
        this.lastRocketExplosionTime = lastRocketExplosionTime;
    }

    public long getExplosionTime() {
        return this.lastRocketExplosionTime;
    }

    public float getRocketYForce() {
        return rocketYForce;
    }

    //UFO DATA

    public void setUfo(UFO ufo) {
        this.ufo = ufo;
    }

    public UFO getUfo() {
        return this.ufo;
    }


    public void setEnergyBeamDirection(float energyBeamDirection) {
        this.energyBeamDirection = energyBeamDirection;
    }

    public float getEnergyBeamDirection() {
        return this.energyBeamDirection;
    }




}
