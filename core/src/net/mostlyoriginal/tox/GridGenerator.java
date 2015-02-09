package net.mostlyoriginal.tox;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import net.mostlyoriginal.tox.system.CombatSystem;

/**
 * @author Daan van Yperen
 */
public class GridGenerator {

    public final int w;
    public final int h;
    private final int startingLevel;

    public TileData[][] tile;

    public int exitDepth;
    public int exitX;
    public int exitY;
    public int entranceX;
    public int entranceY;


    public GridGenerator(int w, int h, int startingLevel) {

        this.w = w;
        this.h = h;
        this.startingLevel = startingLevel ;
        tile = new TileData[w][h];
    }

    private void reset() {
        exitDepth = -1;
        exitX = -1;
        exitY = -1;
        entranceX = -1;
        entranceY = -1;

        for (int x = 0; x < w; x++)
            for (int y = 0; y < h; y++) {
                tile[x][y] = new TileData();
            }
    }

    public void generate() {

        final int playerSimulatedLevel = 1;
        final int playerSimulatedHealth = 0;

        //<=(distanceEntranceToExit() <= 4)
        while ( (exitDepth <= 6) || (distanceEntranceToExit() <= 4) )
        {
            exitDepth = -1;
            reset();
            generateBlockedSections();
            generateDrugs();
            generationStep(MathUtils.random(0, w - 1), MathUtils.random(0, h - 1), null);
            if ( exitDepth > -1 )
            {
                tile[exitX][exitY].type = TileType.EXIT;
                tile[exitX][exitY].conceal=false;
            }
        }
    }

    private void generateDrugs() {
        if ( MathUtils.random() < 0.6f ) tile[MathUtils.random(0, w - 1)][MathUtils.random(0, h - 1)].type =  TileType.DRUG_OTHER_WORLD;
        if ( MathUtils.random() < 0.5f ) tile[MathUtils.random(0, w - 1)][MathUtils.random(0, h - 1)].type =  TileType.DRUG_SWAP;
    }

    private float distanceEntranceToExit() {
        float dst = new Vector2(entranceX, entranceY).dst(exitX, exitY);
        return dst;
    }

    private void generateBlockedSections() {

        for ( int i=0,s=(MathUtils.random(1, 3)+MathUtils.random(1,8)); i <s;i++)
        {
            generateBlockedStep();
        }
    }

    private void generateBlockedStep() {
        tile[MathUtils.random(0, w - 1)][MathUtils.random(0, h - 1)].type =  TileType.BLOCKED;
    }

    private void generationStep(final int x, final int y, final TileData origin) {

        // out of bounds or already set? abort!
        if (x < 0 || y < 0 || x >= w || y >= h || tile[x][y].depth != -1
                || tile[x][y].type == TileType.BLOCKED
                || tile[x][y].type == TileType.DRUG_SWAP
                || tile[x][y].type == TileType.DRUG_OTHER_WORLD )
            return;

        TileData data = tile[x][y];
        if (origin == null) {
            data.type  = TileType.ENTRANCE;
            data.depth = 0;
            data.conceal=false;
            data.effectivePlayerLevel = startingLevel;
            entranceX = x;
            entranceY = y;
        } else {
            data.depth = origin.depth + 1;
            generateTile(x, y, origin);
            if (data.depth > exitDepth) {
                exitDepth = data.depth;
                exitX = x;
                exitY = y;
            }
        }

        for (Direction direction : shuffledDirections()) {
            generationStep(x + direction.dx, y + direction.dy, data);
        }
    }

    private void generateTile(int x, int y, TileData origin) {

        TileData data = tile[x][y];

        data.effectivePlayerLevel = origin.effectivePlayerLevel;
        data.expectedPlayerDamage = origin.expectedPlayerDamage;

        // 1. determine health we can work with.
        int health = origin.getHealth();

        boolean hasHighHealth = origin.effectivePlayerLevel / 2 < health;
        boolean hasLowHealth = health < origin.effectivePlayerLevel / 3;

        // conceal any tile.
        float concealPercentage = 15;

        // 2. Place tile based on probability.
        // 2a. High health? Big chance of monster.
        float monsterChance = 25;
        float healChance = 25;
        float trapChance = 10;
        float itemChance = 8;

        if ( hasLowHealth  )
        {
            healChance = 90;
            monsterChance = 0;
            trapChance = 0;
            itemChance = 20;
        } else if ( hasHighHealth )
        {
            trapChance = 40;
            healChance = 0;
            monsterChance = 60;
        }

        if ( MathUtils.random(0,100) < concealPercentage )
        {
           data.conceal = true;
        }

        float chance = MathUtils.random(0, healChance + monsterChance + trapChance + itemChance );

        chance -= healChance;
        if ( chance < 0 && healChance > 0 )
        {
            // all damage gone!
            data.type = TileType.HEALTH;
            data.expectedPlayerDamage = 0;
            return;
        }

        chance -= monsterChance;
        if ( chance < 0 && monsterChance > 0 )
        {
            data.type = TileType.MONSTER;

            // Since monsters hit damage below their level, we can take higher level monsters.
            float maxMonsterLevel = (origin.getHealth() / CombatSystem.MONSTER_DAMAGE_FACTOR) - 1;

            data.effectLevel = MathUtils.random(1, (int)maxMonsterLevel);

            // level up for winning!
            data.effectivePlayerLevel += CombatSystem.DEFEAT_MONSTER_LEVEL_REWARD;

            // but player gets damaged. Combat is brutal.
            data.expectedPlayerDamage += (int)(CombatSystem.getDamageByMonsterLevel(data.effectLevel));

            return;
        }

        chance -= trapChance;
        if ( chance < 0 && trapChance > 0 )
        {
            data.type = TileType.TRAP;

            // traps are basically monsters that you don't level from. :p

            // Since monsters hit damage below their level, we can take higher level monsters.
            float maxTrapLevel = (origin.getHealth() / CombatSystem.TRAP_DAMAGE_FACTOR) - 1;
            data.effectLevel = MathUtils.random(1, (int)maxTrapLevel);

            // but player gets damaged. Combat is brutal.
            data.expectedPlayerDamage += (int)(CombatSystem.getDamageByMonsterLevel(data.effectLevel));

            return;
        }

        chance -= itemChance;
        if ( chance < 0 && itemChance > 0 )
        {

            // all damage gone!
            data.type =
                    MathUtils.random() < 0.40f ? TileType.TRINKET :  MathUtils.random() < 0.20f ? TileType.ARMOR : TileType.WEAPON;
            // @todo weapons give a level boost.
            return;
        }

    }

    private Direction[] shuffledDirections() {
        Direction[] list = Direction.values();
        for (int index = 0; index < list.length; index++) {
            Direction tmp = list[index];
            int index2 = index + MathUtils.random(list.length - index - 1);
            list[index] = list[index2];
            list[index2] = tmp;
        }
        return list;
    }

    private TileType randomStep(int d) {

        return TileType.NOTHING;
                                /*
        switch ( MathUtils.random(0, 3)) {
            case 1 : return TileType.TRAP;
            case 2 : return TileType.WEAPON;
            case 3 : return TileType.RANDOM;
            default: return TileType.MONSTER;
        }                         */


    }

    enum Direction {
        NORTH(0, 1), SOUTH(0, -1), EAST(1, 0), WEST(-1, 0);
        public final int dx;
        public final int dy;

        Direction(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }
    }

    public static class TileData {
        public TileType type = TileType.NOTHING;
        public int effectLevel = -1;
        public int effectivePlayerLevel = 1;
        public int expectedPlayerDamage = 0;
        public int depth = -1;
        public boolean conceal = false;

        public int getHealth() { return effectivePlayerLevel - expectedPlayerDamage ; }
    }
}
