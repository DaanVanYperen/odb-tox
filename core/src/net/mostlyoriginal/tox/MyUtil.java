package net.mostlyoriginal.tox;

import com.badlogic.gdx.math.Vector2;

/**
 * @author Daan van Yperen
 */
public class MyUtil {

    private static final Vector2 v2tmp = new Vector2();

    public static float distance( float x1, float y1, float x2, float y2 )
    {
        return v2tmp.set(x1,y1).dst(x2,y2);
    }
}
