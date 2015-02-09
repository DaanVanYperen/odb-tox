package net.mostlyoriginal.tox;

import com.artemis.Entity;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.math.Vector2;
import net.mostlyoriginal.tox.component.Position;

/**
 * @author Daan van Yperen
 */
public class ToxUtil {

    private static final Vector2 v2tmp = new Vector2();

    public static float distance( float x1, float y1, float x2, float y2 )
    {
        return v2tmp.set(x1,y1).dst(x2,y2);
    }

    public static Entity getPlayer()
    {
        return Tox.world.getManager(TagManager.class).getEntity("player");
    }

    public static boolean tileWithinReach(Position playerPos, Position tilePos, float maxDistance) {

        if ( maxDistance >= 999 ) return true;

        return  ( tilePos.x != playerPos.x || tilePos.y != playerPos.y ) && ToxUtil.distance(tilePos.x, tilePos.y, playerPos.x, playerPos.y) <= maxDistance;
    }

    public static boolean isPlayer(Entity e) {
        return getPlayer() == e;
    }

}
