package net.mostlyoriginal.tox.system;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.tox.Tox;
import net.mostlyoriginal.tox.ToxUtil;
import net.mostlyoriginal.tox.component.Animation;
import net.mostlyoriginal.tox.component.Health;
import net.mostlyoriginal.tox.component.Toxication;

/**
 * @author Daan van Yperen
 */
public class HudSystem extends PassiveSystem {

    public SoundAlertSystem soundAlertSystem;

    @Override
    protected boolean checkProcessing() {
        return true;
    }

    @Mapper
    private ComponentMapper<Health> hm;
    @Mapper
    private ComponentMapper<Toxication> tm;

    public final static TextureRegion tmpR = new TextureRegion();
    public final TextureRegion health;
    public final TextureRegion toxicity;

    private SpriteBatch batch = new SpriteBatch();
    private OrthographicCamera camera;


    // damage monsters deal per monster level.

    public HudSystem(OrthographicCamera camera) {
        this.camera = camera;

        health = new TextureRegion(Tox.resource.get("bar-health").getKeyFrame(0));
        toxicity = new TextureRegion(Tox.resource.get("bar-tox").getKeyFrame(0));
        tmpR.setTexture(health.getTexture());
    }

    @Override
    protected void initialize() {
        soundAlertSystem = world.getSystem(SoundAlertSystem.class);
    }

    @Override
    protected void begin() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.setColor(1f, 1f, 1f, 1f);


        final Entity player = ToxUtil.getPlayer();
        float toxFactor = 0;
        float healthFactor = 0;
        if (player != null && hm.has(player) && tm.has(player)) {
            final Health playerHealth = hm.get(player);
            final Toxication playerToxication = tm.get(player);

            healthFactor = (playerHealth.maxHealth - playerHealth.damage) / (float) playerHealth.maxHealth;
            toxFactor = 1 - (playerToxication.maxToxication - playerToxication.toxication) / (float) playerToxication.maxToxication;
        }
        renderBar(health, 3 * Animation.DEFAULT_SCALE, 385 * Animation.DEFAULT_SCALE, healthFactor);
        renderBar(toxicity, 3 * Animation.DEFAULT_SCALE, 365 * Animation.DEFAULT_SCALE, toxFactor);
    }

    private void renderBar(TextureRegion region, int x, int y, float factor) {

        int emptySize = (int) (region.getRegionWidth() * MathUtils.clamp(factor, 0f, 1f));

        if (emptySize < region.getRegionWidth()) {
            tmpR.setRegion(
                    region.getRegionX() + emptySize,
                    region.getRegionY(),
                    region.getRegionWidth() - emptySize,
                    region.getRegionHeight());

            batch.draw(tmpR, x + emptySize * Animation.DEFAULT_SCALE, y, tmpR.getRegionWidth() * Animation.DEFAULT_SCALE, tmpR.getRegionHeight() * Animation.DEFAULT_SCALE);
        }
    }

    @Override
    protected void end() {
        batch.end();
    }

}
