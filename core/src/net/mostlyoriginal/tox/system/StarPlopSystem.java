package net.mostlyoriginal.tox.system;

import com.artemis.systems.VoidEntitySystem;

/**
 * @author Daan van Yperen
 */
public class StarPlopSystem extends VoidEntitySystem {

    public int starsRemaining;
    private float starCooldown=0;
    private int starsPlaced=0;

    public ParticleSystem particleSystem;

    @Override
    protected void initialize() {
        particleSystem = world.getSystem(ParticleSystem.class);
    }

    @Override
    protected void processSystem() {
        starCooldown -= world.delta;
        if ( starsRemaining > 0 && starCooldown <= 0 )
        {
            starCooldown = 0.6f;
            starsRemaining--;
            int x = 118 + starsPlaced++ * 80;
            int y = 120;

            if ( starsPlaced == 4 ) { x= 118 + 40; y = 110; }
            if ( starsPlaced == 5 ) { x= 118 + 40 + 80; y = 110; }

            particleSystem.addSparticle(x, y);
        }
    }

    public void plopStars(int stars) {
        starCooldown = 0.5f;
        starsRemaining = stars;
    }
}
