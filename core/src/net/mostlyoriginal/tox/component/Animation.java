package net.mostlyoriginal.tox.component;

import com.artemis.Component;
import com.badlogic.gdx.graphics.Color;

/**
 * @author Daan van Yperen
 */
public class Animation extends Component {

    public static enum Layer
    {
       GLOBAL_BACKGROUND,
       TILE_BACKGROUND,
       TILE,
       TILE_UNDER_COVER,
       TILE_COVER,
       PARTICLE,
       PARTICLE2,
       SCREEN,
       SCREEN_PARTICLE,
       SCREEN_PARTICLE2

    };

    public static final int DEFAULT_SCALE = 2;

    public final Color color = new Color(1,1,1,1);
    public String id;
    public String behindId;
    public float scale = DEFAULT_SCALE;
    public float stretchX = 1;
    public float age = 0;
    public float speed = 1;
    public float rotation;
    public boolean frozen = false;
    public Layer layer = Layer.TILE;

    public Animation(String id) {
        this.id = id;
    }
    public Animation(String id, Layer layer ) {
        this.id = id;
        this.layer = layer;
    }
}
