package net.mostlyoriginal.tox.system;

import com.badlogic.gdx.Gdx;
import net.mostlyoriginal.tox.Tox;

/**
 * @author Daan van Yperen
 */
public class ContinueSystem extends PassiveSystem {

    private final boolean startNewGame;
    private float cooldown = 0.75f;

    public ContinueSystem( boolean startNewGame ) {
        this.startNewGame = startNewGame;
    }

    @Override
    protected void begin() {
        cooldown -= world.delta;
        if ( cooldown <= 0 && Gdx.input.isTouched() )
        {
            SelectableSystem selectableSystem = world.getSystem(SelectableSystem.class);
            if (selectableSystem != null && selectableSystem.selectionCooldown > 0 )
                return;

            if ( startNewGame ) {
                Tox.game.newGame();
            } else {
                Tox.game.nextLevel();
            }
        }
    }

    @Override
    protected boolean checkProcessing() {
        return true;
    }
}
