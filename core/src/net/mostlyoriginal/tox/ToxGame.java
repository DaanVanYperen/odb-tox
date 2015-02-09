package net.mostlyoriginal.tox;

import com.badlogic.gdx.Game;
import net.mostlyoriginal.tox.screen.DialogScreen;
import net.mostlyoriginal.tox.screen.WelcomeScreen;

public class ToxGame extends Game {

    public void nextLevel()
    {
        setScreen(new GameScreen());
    }

	@Override
	public void create () {

        Tox.game = this;
        Tox.resource = new ToxResource();

        newGame();
    }

    public void newGame()
    {
        Tox.score = new Score();
        setScreen(new WelcomeScreen());
    }

    public void gotoMessage(String message, boolean startNewGame, boolean showPerformance) {
        setScreen(new DialogScreen(message, startNewGame, showPerformance));
    }

    @Override
    public void dispose() {
        Tox.resource.dispose();
    }
}
