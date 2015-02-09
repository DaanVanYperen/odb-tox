package net.mostlyoriginal.tox;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

import java.util.HashMap;

/**
 * @author Daan van Yperen
 */
public class ToxResource {


    public static final int TILE_SIZE = 40;
    public final BitmapFont font;
    private final Texture dialogs;
    public final BitmapFont middlefont;
    public final BitmapFont bigfont;
    public final Sound music;

    public Texture tileset;
    public HashMap<String, Animation> sprites = new HashMap<String, Animation>();
    public HashMap<String, Sound> sounds = new HashMap<String, Sound>();
    public Universe universe;

    public Animation get(final String identifier) {
        return sprites.get(identifier);
    }

    public Sound getSfx(final String identifier) {
        return sounds.get(identifier);
    }

    public Animation add(final String identifier, int x1, int y1, int w, int h, int repeatX) {
        return add(identifier, x1, y1, w, h, repeatX, 1, tileset);
    }

    public Animation add(final String identifier, int x1, int y1, int w, int h, int repeatX, int repeatY) {
        return add(identifier, x1, y1, w, h, repeatX, repeatY, tileset);
    }

    public Animation add(final String identifier, int x1, int y1, int w, int h, int repeatX, int repeatY, Texture texture) {

        TextureRegion[] regions = new TextureRegion[repeatX*repeatY];

        int count = 0;
        for (int y = 0; y < repeatY; y++) {
            for (int x = 0; x < repeatX; x++) {
                regions[count++] = new TextureRegion(texture, x1 + w * x, y1 + h * y, w, h);
            }
        }

        return sprites.put(identifier, new Animation(0.5f, regions));
    }

    public ToxResource() {

        font = new BitmapFont(Gdx.files.internal("font/tahoma-10.fnt"), false);
        font.setColor(0, 0, 0, 0.9f);
        bigfont = new BitmapFont(Gdx.files.internal("font/tahoma-10.fnt"), false);
        bigfont.scale(3);
        bigfont.setColor(0, 0, 0, 0.9f);
        middlefont = new BitmapFont(Gdx.files.internal("font/tahoma-10.fnt"), false);
        middlefont.scale(2);
        middlefont.setColor(0, 0, 0, 0.9f);

        tileset = new Texture("tileset.png");
        dialogs = new Texture("dialogs.png");

        add("background", 56, 56, 240, 400, 1);

        loadPlayerModel(Tox.settings.playerModel);

        //add("player", 0, 560, TILE_SIZE, TILE_SIZE, 2);
        //add("player-dead", TILE_SIZE*2 , 560, TILE_SIZE, TILE_SIZE, 1);
        //add("player-withdrawal", 0, 520, TILE_SIZE, TILE_SIZE, 2);
        //add("player-overdose", TILE_SIZE*2, 520, TILE_SIZE, TILE_SIZE, 2);

        add("mystery", 0, 560 + TILE_SIZE * 5, TILE_SIZE, TILE_SIZE, 2);

        add("bullet", 255, 615, 10, 10, 1);

        add("bunnydrug", 0, 560 + TILE_SIZE * 6, TILE_SIZE, TILE_SIZE, 2);
        add("phasedrug", 0, 560 + TILE_SIZE * 7, TILE_SIZE, TILE_SIZE, 2);

        add("inventory-item-background", 360, 160, TILE_SIZE, TILE_SIZE, 1);

        add("music-on", 470, 140, 20,20, 1);
        add("music-off", 490, 140, 20,20, 1);
        add("sfx-on", 470, 120, 20,20, 1);
        add("sfx-off", 490, 120, 20,20, 1);



        add("knuckles", 0, 880, TILE_SIZE, TILE_SIZE, 1);
        add("handgun", TILE_SIZE, 880, TILE_SIZE, TILE_SIZE, 1);
        add("assaultrifle", TILE_SIZE * 2, 880, TILE_SIZE, TILE_SIZE, 1);
        add("shotgun", TILE_SIZE * 3, 880, TILE_SIZE, TILE_SIZE, 1);
        //add("weapon2", 0, 560 + TILE_SIZE * 9, TILE_SIZE, TILE_SIZE, 2);
        add("vest1", 0, 920, TILE_SIZE, TILE_SIZE, 1);
        add("vest2", TILE_SIZE, 920, TILE_SIZE, TILE_SIZE, 1);
        add("misc1", TILE_SIZE*2, 920, TILE_SIZE, TILE_SIZE, 1);
        add("misc2", TILE_SIZE*3, 920, TILE_SIZE, TILE_SIZE, 1);
        add("misc3", TILE_SIZE*4, 920, TILE_SIZE, TILE_SIZE, 1);
        add("misc4", TILE_SIZE*5, 920, TILE_SIZE, TILE_SIZE, 1);
        add("misc5", TILE_SIZE*6, 920, TILE_SIZE, TILE_SIZE, 1);

        add("health", 0, 560 + TILE_SIZE * 10, TILE_SIZE, TILE_SIZE, 2);

        add("indicator", 360, 200, TILE_SIZE, TILE_SIZE, 1);

        loadUniverse(Universe.REAL);

        add("cleared", 280, 920, TILE_SIZE, TILE_SIZE, 1);

        add("NORTH", 280, 680, TILE_SIZE, TILE_SIZE, 1);
        add("SOUTH", 280 + TILE_SIZE, 680, TILE_SIZE, TILE_SIZE, 1);
        add("EAST", 280 + TILE_SIZE * 2, 680, TILE_SIZE, TILE_SIZE, 1);
        add("WEST", 280 + TILE_SIZE * 3, 680, TILE_SIZE, TILE_SIZE, 1);

        add("skull-small", 360, 0, 6, 12, 1);
        add("skull-big", 367, 0, 7, 12, 1);

        add("p-fire", 360, 130, 10, 10, 4);
        add("p-soot", 370, 120, 10, 10, 3);
        add("p-cloud", 360, 150, 10, 10, 4);
        add("p-heal", 360, 120, 10, 10, 1);
        add("p-zap", 360, 140, 10, 10, 1);
        add("p-fight", 400, 120, 10, 10, 6);
        add("p-lvlup", 402, 130, 36, 9, 1);
        add("p-petal", 420, 150, 10, 10, 3);

        add("bar-health", 363, 42, 121, 13, 1);
        add("bar-tox", 363, 62, 121, 13, 1);

        add("dialog-welcome", 0, 0, 240, 400, 1, 1, dialogs);
        add("dialog-win", 240, 0, 240, 400, 1, 1, dialogs);
        add("dialog-loss", 240 * 2, 0, 240, 400, 1, 1, dialogs);
        add("dialog-stage-clear", 240 * 3, 0, 240, 400, 1, 1, dialogs);
        add("dialog-background", 1000, 0, 10, 512, 1,1,  dialogs);
        add("dialog-star", 777, 418, 44, 41, 1, 1, dialogs);

        loadSounds(new String[]{
                "Flaterectomy - Tox",
                "tox_sfx_bunny_dies",
                "tox_sfx_consume_drugs",
                "tox_sfx_consume_health",
                "tox_sfx_explosion",
                "health_low_alarm_loop",
                "tox_sfx_levelup",
                "tox_sfx_overdose_alarm",
                "tox_sfx_armor_pickup",
                "tox_sfx_glock_pickup",
                "tox_sfx_glock_fight",
                "tox_sfx_knuckles_pickup",
                "tox_sfx_knuckles_fight",
                "tox_sfx_ak47_pickup",
                "tox_sfx_ak47_fight",
                "tox_sfx_sawed-off_pickup",
                "tox_sfx_sawed-off_fight",
                "tox_sfx_grunt_male1",
                "tox_sfx_grunt_male2",
                "tox_sfx_grunt_male3",
                "tox_sfx_monster_dies1",
                "tox_sfx_monster_dies2",
                "tox_sfx_monster_dies3",
                "tox_sfx_player_dies_male",
                "tox_sfx_star",
                "tox_sfx_fast_breathing",
                "tox_sfx_exit",
                "tox_sfx_smokepuff",
                "sector_cleared_star_award",
                "tox_sfx_wasp_dies",
                "tox_sfx_octopus_dies",
                "tox_sfx_withdrawal_alarm",
        });

        music = getSfx("Flaterectomy - Tox");
        updateMusic();
    }

    public void updateMusic()
    {
        if ( Tox.settings.musicVolume > 0 && Tox.settings.musicOn )
        {
            music.loop(Tox.settings.musicVolume);
        } else {
            music.stop();
        }

    }

    private void loadPlayerModel( PlayerModel model ) {

        switch ( model )
        {
            case MALE:
                add("player", 0, 560, TILE_SIZE, TILE_SIZE, 2);
                add("player-dead", TILE_SIZE * 2, 560, TILE_SIZE, TILE_SIZE, 1);
                add("player-withdrawal", 0, 520, TILE_SIZE, TILE_SIZE, 2);
                add("player-overdose", TILE_SIZE * 2, 520, TILE_SIZE, TILE_SIZE, 2);
                break;
                case FEMALE:
                add("player", 120, 560, TILE_SIZE, TILE_SIZE, 2);
                add("player-dead", 120 + TILE_SIZE * 2, 560, TILE_SIZE, TILE_SIZE, 1);
                add("player-withdrawal", 160, 520, TILE_SIZE, TILE_SIZE, 2);
                add("player-overdose", 240, 520, TILE_SIZE, TILE_SIZE, 1, 2);
                break;
        }
    }

    public static enum PlayerModel {
        MALE,
        FEMALE
    }

    public static enum Universe {
        REAL,
        BUNNY,
        PHASE
    }


    public void loadUniverse(Universe universe) {
        this.universe = universe;

        add("boss", 80, 600, TILE_SIZE, TILE_SIZE, 2);
        add("boss-fire", 160, 600, TILE_SIZE, TILE_SIZE, 2);

        switch (universe) {
            case REAL:
                add("tile", 280, 520, TILE_SIZE, TILE_SIZE, 4);
                add("blocked-tile", 280, 560, TILE_SIZE, TILE_SIZE, 4);
                add("monster1", 0, 560 + TILE_SIZE, TILE_SIZE, TILE_SIZE, 2);
                add("monster2", 280, 560 + TILE_SIZE * 5, TILE_SIZE, TILE_SIZE, 2);
                add("monster3", 280, 560 + TILE_SIZE * 7, TILE_SIZE, TILE_SIZE, 2);
                add("trap", 0, 560 + TILE_SIZE * 3, TILE_SIZE, TILE_SIZE, 2);
                add("exit", 0, 560 + TILE_SIZE * 4, TILE_SIZE, TILE_SIZE, 2);
                break;
            case BUNNY:
                add("tile", 280, 600, TILE_SIZE, TILE_SIZE, 4);
                add("blocked-tile", 280, 640, TILE_SIZE, TILE_SIZE, 4);
                add("monster1", 0, 560 + TILE_SIZE * 2, TILE_SIZE, TILE_SIZE, 2);
                add("monster2", 280, 560 + TILE_SIZE * 6, TILE_SIZE, TILE_SIZE, 2);
                add("monster3", 280, 560 + TILE_SIZE * 8, TILE_SIZE, TILE_SIZE, 2);
                add("trap", TILE_SIZE * 2, 560 + TILE_SIZE * 3, TILE_SIZE, TILE_SIZE, 2);
                add("exit", TILE_SIZE * 2, 560 + TILE_SIZE * 4, TILE_SIZE, TILE_SIZE, 2);
                break;
            case PHASE:
                add("tile", 360, 440, TILE_SIZE, TILE_SIZE, 2, 2);
                add("blocked-tile", 360, 360, TILE_SIZE, TILE_SIZE, 2, 2);
                add("monster1", 0+TILE_SIZE*2, 560 + TILE_SIZE * 2, TILE_SIZE, TILE_SIZE, 2);
                add("monster2", 280+TILE_SIZE*2, 560 + TILE_SIZE * 6, TILE_SIZE, TILE_SIZE, 2);
                add("monster3", 280+TILE_SIZE*2, 560 + TILE_SIZE * 8, TILE_SIZE, TILE_SIZE, 2);
                add("trap", TILE_SIZE * 4, 560 + TILE_SIZE * 3, TILE_SIZE, TILE_SIZE, 2);
                add("exit", TILE_SIZE * 4, 560 + TILE_SIZE * 4, TILE_SIZE, TILE_SIZE, 2);
                break;

        }
    }

    private void loadSounds(String[] soundnames) {
        for (String identifier : soundnames) {
            sounds.put(identifier, Gdx.audio.newSound(Gdx.files.internal("sfx/" + identifier + ".mp3")));
        }
    }

    public void playSfx(String name) {
        if (Tox.settings.sfxVolume > 0 && Tox.settings.sfxOn) getSfx(name).play(Tox.settings.sfxVolume, MathUtils.random(1f,1.04f),0);
    }

    public void dispose() {
        sprites.clear();
        tileset.dispose();
        tileset = null;
    }
}
