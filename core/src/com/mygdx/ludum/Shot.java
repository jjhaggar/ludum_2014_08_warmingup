package com.mygdx.ludum;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class Shot extends Image {
    final Vector2 position = new Vector2();

    float SHOT_VELOCITY = 200f;
    float stateTime = 0;
    boolean shotGoesRight;


    public Shot(float x, float y, boolean facesRight){
        this.position.x = x;
        this.position.y = y;
        this.shotGoesRight = facesRight;
    }

    public void updateShot(float deltaTime){
        if (this.shotGoesRight)
            this.position.x += deltaTime * this.SHOT_VELOCITY;
        else
            this.position.x -= deltaTime * this.SHOT_VELOCITY;

        this.stateTime += deltaTime;
    }
}
