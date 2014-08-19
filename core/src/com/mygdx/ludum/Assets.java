package com.mygdx.ludum;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;

public class Assets {
	static AssetManager assetManager;
	static Animation stand, walk, jump, standingShot, shotAnim;

	static void loadAnimation() {
        final String TEXTURE_ATLAS_OBJECTS = "rayaman.pack";
		assetManager = new AssetManager();
		assetManager.load(TEXTURE_ATLAS_OBJECTS, TextureAtlas.class);
		assetManager.finishLoading();

        TextureAtlas atlas = assetManager.get(TEXTURE_ATLAS_OBJECTS);
		Array<AtlasRegion> regions;

		regions = atlas.findRegions("rayaman_walking");
		walk = new Animation(0.15f, regions);
		walk.setPlayMode(Animation.PlayMode.LOOP);

		regions = atlas.findRegions("rayaman_standing");
		stand = new Animation(0, regions);

		regions = atlas.findRegions("rayaman_jumping");
		jump = new Animation(0, regions);

		regions = atlas.findRegions("rayaman_standing_shot");
		standingShot = new Animation(0.15f, regions);
		System.out.println(regions.first().offsetX);
		System.out.println(regions.first().offsetY);
		//positionOffsetOfCharacter.x = regions.first().offsetX;
		//positionOffsetOfCharacter.y = regions.first().offsetY;

		regions = atlas.findRegions("rayaman_shot");
		shotAnim = new Animation(0.15f, regions);
		System.out.println(regions.first().offsetX);
		System.out.println(regions.first().offsetY);
		//positionOffsetForShot.x = regions.first().offsetX;
		//positionOffsetForShot.y = regions.first().offsetY;
	}
}
