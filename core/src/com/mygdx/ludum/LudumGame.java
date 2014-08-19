package com.mygdx.ludum;

import com.badlogic.gdx.Game;

public class LudumGame extends Game {

    @Override
    public void create() {
        this.setScreen(new MainScreen());
    }

}
