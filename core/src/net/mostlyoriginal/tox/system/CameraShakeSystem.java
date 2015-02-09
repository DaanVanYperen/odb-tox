package net.mostlyoriginal.tox.system;

import com.artemis.systems.VoidEntitySystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * @author Daan van Yperen
 */
public class CameraShakeSystem extends VoidEntitySystem {
    private OrthographicCamera camera;
    public Vector3 originalCameraPos;
    public float shake;
    public Vector2 push = new Vector2();

    public CameraShakeSystem(OrthographicCamera camera) {
        this.camera = camera;
        originalCameraPos = new Vector3(camera.position);
        shake = 0;
    }

    public void shake(float pixels) {
        shake = pixels;
    }

    public void push(float x, float y) {
        push.x = x;
        push.y = y;
    }

    @Override
    protected void processSystem() {
        camera.position.x = (int)(originalCameraPos.x + MathUtils.random(push.x) + (shake != 0 ? MathUtils.random(-shake, shake) : 0));
        camera.position.y = (int)(originalCameraPos.y + MathUtils.random(push.y) + (shake != 0 ? MathUtils.random(-shake, shake) : 0));
        camera.update();

        if ( shake > 0 )
        {
            shake -= world.delta * 4f;
            if ( shake < 0 ) shake=0;
        }
        decrease(push, world.delta * 16f);
    }

    private void decrease(final Vector2 v, final float delta) {
        if (v.x > 0) {
            v.x -= delta;
            if (v.x < 0) {
                v.x = 0;
            }
        }
        if (v.x < 0) {
            v.x += delta;
            if (v.x > 0) {
                v.x = 0;
            }
        }
        if (v.y > 0) {
            v.y -= delta;
            if (v.y < 0) {
                v.y = 0;
            }
        }
        if (v.y < 0) {
            v.y += delta;
            if (v.y > 0) {
                v.y = 0;
            }
        }
    }
}
