package net.mostlyoriginal.tox;

import com.artemis.Entity;
import com.artemis.World;

/**
 * @author Daan van Yperen
 */
public abstract class Tox {
    public static ToxGame game;
    public static GameScreen gameScreen;
    public static ToxResource resource;
    public static World world;

    public static Settings settings = new Settings();

    public static Entity legacyPlayer;
    public static Score score;
}
