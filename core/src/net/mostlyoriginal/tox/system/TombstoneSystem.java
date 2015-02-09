package net.mostlyoriginal.tox.system;

import com.artemis.systems.VoidEntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.tox.Score;
import net.mostlyoriginal.tox.Tox;

/**
 * @author Daan van Yperen
 */
public class TombstoneSystem extends VoidEntitySystem {
    private final OrthographicCamera camera;
    private Batch batch = new SpriteBatch();
    public StarPlopSystem starPlopSystem;
    private boolean personalHighscore = false;

    public TombstoneSystem(OrthographicCamera camera) {
        this.camera = camera;
    }

    @Override
    protected void initialize() {
        starPlopSystem = world.getSystem(StarPlopSystem.class);

        float averageStars = Tox.score.stars / (Score.STARTING_FLOOR - Tox.score.floor + 1);

        // reward at least one star if no longer at starting floor.
        if ( Tox.score.floor != Score.STARTING_FLOOR )
        {
            averageStars=Math.max(1, averageStars);
        }

        int stars = MathUtils.clamp((int) ((averageStars / 3f) * 5f), 0, 5);
        starPlopSystem.plopStars(stars);

        if ( Tox.settings.personalHighscore < Tox.score.points )
        {
            personalHighscore= true;
            Tox.settings.personalHighscore = Tox.score.points;
            Tox.settings.personalHighscoreStars = stars;
            Tox.settings.save();
        }
    }

    @Override
    protected void processSystem() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.setColor(1f, 1f, 1f, 1f);
        BitmapFont font = Tox.resource.bigfont;

        int offsetY = -100;

        center(Tox.resource.bigfont,  "Here lies", 620 + offsetY);
        center(Tox.resource.bigfont,  "Killed by", 520 + offsetY);

        if ( Tox.score.killer == null)
        {
            center(Tox.resource.middlefont,  "The Jailer", 560 + offsetY);
            center(Tox.resource.middlefont,  funkyPlayerName(), 460 + offsetY);
        } else {
            center(Tox.resource.middlefont,  funkyPlayerName(), 560 + offsetY);
            center(Tox.resource.middlefont,  Tox.score.killer, 460 + offsetY);
        }

        center(Tox.resource.bigfont,  "On the", 420 + offsetY);
        center(Tox.resource.middlefont, Tox.score.floorName(), 360 + offsetY);

        center(Tox.resource.middlefont,  pointScore()+ " points", 230 + offsetY);


        batch.end();
    }

    private String pointScore() {
        return ""+Tox.score.points;
    }

    private String funkyPlayerName() {
        return "Level " + Tox.score.playerLevel + " Hero";
    }

    private void center(BitmapFont font,String str, int offsetY) {
        if ( str != null )
        {
            font.setColor(1f,1f,1f,1f);
            font.draw(batch, str, Gdx.graphics.getWidth()/2 - font.getBounds(str).width/2, offsetY );
        }
    }
}
