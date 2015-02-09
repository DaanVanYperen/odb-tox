package net.mostlyoriginal.tox.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import net.mostlyoriginal.tox.Tox;
import net.mostlyoriginal.tox.ToxUtil;
import net.mostlyoriginal.tox.component.Animation;
import net.mostlyoriginal.tox.component.Position;
import net.mostlyoriginal.tox.component.Selectable;

/**
 * @author Daan van Yperen
 */
public class SelectableSystem extends EntityProcessingSystem {

    @Mapper
    ComponentMapper<Position> pm;
    @Mapper
    ComponentMapper<Animation> am;
    @Mapper
    ComponentMapper<Selectable> sm;
    public float selectionCooldown;

    private Entity forceSelected;
    private float forceSelectedCooldown;

    public SelectableSystem() {
        super(Aspect.getAspectForAll(Selectable.class, Position.class, Animation.class));
    }

    Vector2 tmp = new Vector2();

    @Override
    protected void begin() {
        super.begin();
        this.forceSelectedCooldown -= Tox.world.delta;
        this.selectionCooldown -= Tox.world.delta;
    }

    public void forceSelect(Entity forceSelected, float cooldown)
    {
        selectionCooldown(cooldown + 0.1f);
        this.forceSelected = forceSelected;
        this.forceSelectedCooldown = cooldown;
    }

    @Override
    protected void process(Entity e) {

        final Selectable selectable = sm.get(e);

        if ( this.forceSelected != null && this.forceSelected == e && this.forceSelectedCooldown <= 0 )
        {
            selectable.selected = true;
            this.forceSelected = null;
            return;
        }

        if (Gdx.input.justTouched() && this.selectionCooldown <= 0 ) {
            final Position p = pm.get(e);
            final Animation a = am.get(e);

            TextureRegion frame = Tox.resource.get(a.id).getKeyFrame(0);
            final float w = frame.getRegionWidth() * a.scale;
            final float h = frame.getRegionHeight() * a.scale;

            final int x = Gdx.input.getX();
            final int y = Gdx.graphics.getHeight() - Gdx.input.getY();

            selectable.selected = !(x < p.x || x > p.x + w || y < p.y || y > p.y + h) && (selectable.maxDistance >= 999 || ToxUtil.tileWithinReach(pm.get(ToxUtil.getPlayer()), p, selectable.maxDistance));
        } else selectable.selected = false;
    }

    /**
     * Prevent selection for a certain duration.
     * @param selectionCooldown Cooldown in seconds.
     */
    public void selectionCooldown(float selectionCooldown) {
        if (selectionCooldown > this.selectionCooldown) {
            this.selectionCooldown = selectionCooldown;
        }
    }
}
