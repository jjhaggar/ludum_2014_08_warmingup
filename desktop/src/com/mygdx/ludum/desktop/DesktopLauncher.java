package com.mygdx.ludum.desktop;

import java.io.File;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.mygdx.ludum.LudumGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
	    createPacker(); // Commented this line for not create packing.
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new LudumGame(), config);
	}

	private static void createPacker() {
	    /* Automatic packing */
	    for (String folder: new String[]{"bunny", "rayaman"}) { // Added new folders here
	        // Remove old pack
	        for (String ext : new String[]{".png", ".pack"}) {
                File file = new File("../android/assets/" + folder + ext);
                file.delete();
	        }
	        // Create new pack
            TexturePacker.process("../ASSETS/" + folder, "../android/assets/", folder + ".pack");
	    }
	}
}
