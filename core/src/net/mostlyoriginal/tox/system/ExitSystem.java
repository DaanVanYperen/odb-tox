package net.mostlyoriginal.tox.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import net.mostlyoriginal.tox.EntityFactory;
import net.mostlyoriginal.tox.Tox;
import net.mostlyoriginal.tox.ToxUtil;
import net.mostlyoriginal.tox.component.Exit;
import net.mostlyoriginal.tox.component.Selectable;

/**
 * Allows players to step back to an earlier location.
 * @author Daan van Yperen
 */
public class ExitSystem extends EntityProcessingSystem {

    @Mapper
    ComponentMapper<Selectable> sm;
    public LifetimeSystem lifetimeSystem;
    private SoundSystem soundSystem;
    public StarPlopSystem starPlopSystem;
    private ScoreSystem scoreSystem;

    public ExitSystem() {
        super(Aspect.getAspectForAll(Selectable.class, Exit.class));
    }

    @Override
    protected void initialize() {
        super.initialize();
        lifetimeSystem = world.getSystem(LifetimeSystem.class);
        soundSystem = world.getSystem(SoundSystem.class);
        starPlopSystem = world.getSystem(StarPlopSystem.class);
        scoreSystem = world.getSystem(ScoreSystem.class);
    }

    @Override
    protected void begin() {
        super.begin();
    }

    @Override
    protected void process(Entity e) {
        if (sm.get(e).selected && !Tox.gameScreen.reachedExit ) {

            scoreSystem.scoreReward(e);

            EntityFactory.createScreen("dialog-stage-clear").addToWorld();

            int starsGained = Tox.gameScreen.getClearLevel();
            Tox.score.stars += starsGained;
            starPlopSystem.plopStars(starsGained);

            Tox.score.floor--;
            Tox.gameScreen.reachedExit = true;
            Tox.world.getSystem(ContinueSystem.class).setEnabled(true);

            soundSystem.play("tox_sfx_exit");

            // persist player entity through levels.
            Tox.legacyPlayer = ToxUtil.getPlayer();
        }
    }
}
