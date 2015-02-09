package net.mostlyoriginal.tox.system;

import com.artemis.systems.VoidEntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import net.mostlyoriginal.tox.Tox;

/**
 * @author Daan van Yperen
 */
public class HighscoreSystem extends VoidEntitySystem {
    private final OrthographicCamera camera;
    private Batch batch = new SpriteBatch();

    public HighscoreSystem(OrthographicCamera camera) {
        this.camera = camera;
    }

    @Override
    protected void processSystem() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.setColor(1f, 1f, 1f, 1f);
        BitmapFont font = Tox.resource.bigfont;

        int offsetY = 0;

        if ( Tox.settings.personalHighscore > 0 )
        {
            center(Tox.resource.bigfont,  "Personal Highscore", 620 + offsetY);
            center(Tox.resource.middlefont, ""+Tox.settings.personalHighscore+" points", 560 + offsetY);
        }

        batch.end();
    }

    private void center(BitmapFont font,String str, int offsetY) {
        if ( str != null )
        {
            font.setColor(1f,1f,1f,1f);
            font.draw(batch, str, Gdx.graphics.getWidth()/2 - font.getBounds(str).width/2, offsetY );
        }
    }
}
