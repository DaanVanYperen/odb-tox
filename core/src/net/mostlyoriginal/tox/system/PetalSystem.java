package net.mostlyoriginal.tox.system;

import com.artemis.systems.VoidEntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;

/**
 * @author Daan van Yperen
 */
public class PetalSystem extends VoidEntitySystem {


    float cooldown;
    public ParticleSystem particleSystem;

    @Override
    protected void initialize() {
        super.initialize();
        particleSystem = world.getSystem(ParticleSystem.class);
        setEnabled(false);
    }

    @Override
    protected void processSystem() {

        cooldown -= world.delta;
        if ( cooldown <= 0 )
        {
            cooldown = 0.5f;

            particleSystem.addPetalParticle(MathUtils.random(0, Gdx.graphics.getWidth()), MathUtils.random(0, Gdx.graphics.getHeight())).addToWorld();
        }
    }
}
