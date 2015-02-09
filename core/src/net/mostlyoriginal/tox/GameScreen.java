package net.mostlyoriginal.tox;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import net.mostlyoriginal.tox.component.Animation;
import net.mostlyoriginal.tox.component.Level;
import net.mostlyoriginal.tox.system.*;

/**
 * @author Daan van Yperen
 */
public class GameScreen implements Screen {

    private final OrthographicCamera camera;
    public float isLossCooldown = 0;
    public float isWinCooldown = 0;
    public boolean reachedExit;

    public GameScreen() {

        Tox.resource.loadUniverse(ToxResource.Universe.REAL);

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();

        Tox.gameScreen = this;

        Tox.world = new World();

        Tox.world.setManager(new GroupManager());
        Tox.world.setManager(new TagManager());

        Tox.world.setSystem(new PetalSystem());

        Tox.world.setSystem(new CameraShakeSystem(camera));
        Tox.world.setSystem(new SelectableSystem());
        Tox.world.setSystem(new PhysicsSystem());

        // Should run after selectablesystem and before ineraction systems.
        Tox.world.setSystem(new LifetimeSystem());

        Tox.world.setSystem(new DissolveSystem());
        Tox.world.setSystem(new BacktrackSystem());
        Tox.world.setSystem(new TrapSystem());
        Tox.world.setSystem(new CombatSystem());
        Tox.world.setSystem(new LootSystem());
        Tox.world.setSystem(new ConcealSystem());
        //Tox.world.setSystem(new SettingSystem());

        Tox.world.setSystem(new InventorySystem());

        Tox.world.setSystem(new ColorAnimationSystem());

        Tox.world.setSystem(new AnimationRenderSystem(camera));
        Tox.world.setSystem(new LabelRenderSystem(camera));
        Tox.world.setSystem(new ThreatRenderSystem());
        Tox.world.setSystem(new HudSystem(camera), false);
        Tox.world.setSystem(new TitleSystem(camera));

        // Passive systems
        Tox.world.setSystem(new PlayerSystem(), true);
        Tox.world.setSystem(new ParticleSystem(), true);
        Tox.world.setSystem(new UniverseSystem(), true);
        Tox.world.setSystem(new SoundSystem());
        Tox.world.setSystem(new ScoreSystem(), true);

        Tox.world.setSystem(new SoundAlertSystem());
        Tox.world.setSystem(new BulletSystem());
        Tox.world.setSystem(new StarPlopSystem());
        Tox.world.setSystem(new ExitSystem());

        ContinueSystem continueSystem = new ContinueSystem(false);
        continueSystem.setEnabled(false);
        Tox.world.setSystem(continueSystem);

        Tox.world.initialize();

        EntityFactory.createBackground().addToWorld();
        initializeGrid();
    }


    public int getClearLevel() {

        ImmutableBag<Entity> groups = Tox.world.getManager(GroupManager.class).getEntities(EntityFactory.MONSTER_GROUP);

        if ( groups.size() >= 15 ) return 0;
        if ( groups.size() > 12 ) return 1;
        if ( groups.size() > 0 ) return 2;
        return 3;
    }

    private void initializeGrid() {

        boolean bossLevel = Tox.score.floor==1;

        GridGenerator grid = new GridGenerator(6, 9, getStartingLevel());
        grid.generate();
        for (int gx = 0; gx < grid.w; gx++)
            for (int gy = 0; gy < grid.h; gy++) {
                final int x = (int) ((0 + gx * ToxResource.TILE_SIZE) * Animation.DEFAULT_SCALE);
                final int y = (int) ((0 + gy * ToxResource.TILE_SIZE) * Animation.DEFAULT_SCALE);

                GridGenerator.TileData tileData = grid.tile[gx][gy];
                EntityFactory.createBackgroundTile(x, y, "tile").addToWorld();
/*                .addComponent(new Label(
                                        Integer.toString(tileData.getHealth()) + "hp/l" +
                                                                        Integer.toString(tileData.effectivePlayerLevel)
                                )*/


                /*
                if ( MathUtils.random() < 0.25f && (tileData.type != TileType.ENTRANCE && tileData.type != TileType.EXIT ) )
                {
                    // @todo remove.
                    System.out.println("DEBUGGING, REMOVE ME.");
                    tileData.type = TileType.TRINKET;
                } */

                Entity entity = null;
                switch (tileData.type) {
                    case ENTRANCE:
                        EntityFactory.createClearedLocation(x, y).addToWorld();

                        if ( Tox.legacyPlayer != null )
                        {
                            EntityUtils.cloneLegacyPlayer(x, y, Tox.legacyPlayer).addToWorld();
                            Tox.legacyPlayer = null;
                        } else {
                            EntityFactory.createPlayer(x, y, 1).addToWorld();
                        }
                        EntityFactory.createIndicator(x,y).addToWorld();

                        Tox.world.getSystem(ParticleSystem.class).cloud((int)(x + ToxResource.TILE_SIZE * Animation.DEFAULT_SCALE * 0.5f),(int)(y + ToxResource.TILE_SIZE * Animation.DEFAULT_SCALE * 0.5f),40);
                        break;
                    case EXIT:
                        entity= bossLevel ? EntityFactory.createBoss(x,y, getStartingLevel() + 3) : EntityFactory.createExit(x, y);
                        break;
                    case DRUG_SWAP:
                        entity=EntityFactory.createDrugSwap(x, y);
                        break;
                    case DRUG_OTHER_WORLD:
                        entity=EntityFactory.createDrugOtherWorld(x, y);
                        break;
                    case TRAP:
                        entity=EntityFactory.createTrap(x, y, tileData.effectLevel);
                        break;
                    case MONSTER:
                        entity=EntityFactory.createMonster(x, y, tileData.effectLevel);
                        break;
                    case WEAPON:
                        entity=EntityFactory.createRandomWeapon(x, y);
                        break;
                    case ARMOR:
                        entity=EntityFactory.createRandomArmor(x, y);
                        break;
                    case TRINKET:
                        entity=EntityFactory.createRandomTrinket(x, y);
                        break;
                    case HEALTH:
                        entity=EntityFactory.createHealth(x, y);
                        break;
                    case NOTHING:
                    case BLOCKED:
                        entity=EntityFactory.createBackgroundTile(x, y, "blocked-tile");
                        break;
                }

                if ( entity != null )
                {
                    if (tileData.conceal) {
                        EntityFactory.createConcealment(x, y, entity).addToWorld();
                    } else entity.addToWorld();
                }

            }


        for (int gx = 0; gx < grid.w; gx++)
            for (int gy = 0; gy < grid.h; gy++) {
                final int x = (int) ((0 + gx * ToxResource.TILE_SIZE) * Animation.DEFAULT_SCALE);
                final int y = (int) ((0 + gy * ToxResource.TILE_SIZE) * Animation.DEFAULT_SCALE);

                for (GridGenerator.Direction dir : GridGenerator.Direction.values()) {
                    int nx = gx + dir.dx;
                    int ny = gy + dir.dy;
                    if (nx >= 0 && ny >= 0 && nx < grid.w && ny < grid.h && grid.tile[nx][ny].depth != -1 && grid.tile[gx][gy].depth != -1 && grid.tile[nx][ny].depth == grid.tile[gx][gy].depth + 1) {
                        //EntityFactory.createBackgroundTile(x + dir.dx * 8 * Animation.DEFAULT_SCALE, y + dir.dy * 8 * Animation.DEFAULT_SCALE, dir.name()).addToWorld();
                    }
                }
            }
    }

    /**
     * @return Pre-existing character level, if any.
     */
    private int getStartingLevel() {
        return ( Tox.legacyPlayer != null ) ? Tox.legacyPlayer.getComponent(Level.class).level : 1;
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Tox.world.setDelta(delta);
        Tox.world.process();

        if ( isLossCooldown > 0 )
        {
            isLossCooldown -= Tox.world.delta;
            if (isLossCooldown <= 0)
            {
                Tox.game.gotoMessage("dialog-loss",true, true);
            }
        }
        if ( isWinCooldown > 0 )
        {
            isWinCooldown -= Tox.world.delta;
            if (isWinCooldown <= 0)
            {
                Tox.game.gotoMessage("dialog-win",true, true);
            }
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

}
