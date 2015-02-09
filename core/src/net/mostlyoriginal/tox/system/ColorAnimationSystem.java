package net.mostlyoriginal.tox.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import net.mostlyoriginal.tox.Tox;
import net.mostlyoriginal.tox.component.Animation;
import net.mostlyoriginal.tox.component.ColorAnimation;

/**
 * @author Daan van Yperen
 */
public class ColorAnimationSystem extends EntityProcessingSystem {

    @Mapper
    ComponentMapper<Animation> am;

    @Mapper
    ComponentMapper<ColorAnimation> cm;

    public ColorAnimationSystem() {
        super(Aspect.getAspectForAll(Animation.class, ColorAnimation.class));
    }

    @Override
    protected void process(final Entity entity) {
        final Animation animation = am.get(entity);
        final ColorAnimation colorAnimation = cm.get(entity);

        // age colors individually.
        colorAnimation.age.r += colorAnimation.speed.r * Tox.world.delta;
        colorAnimation.age.g += colorAnimation.speed.g * Tox.world.delta;
        colorAnimation.age.b += colorAnimation.speed.b * Tox.world.delta;
        colorAnimation.age.a += colorAnimation.speed.a * Tox.world.delta;

        // tween colors individually.
        animation.color.r = colorAnimation.tween.apply( colorAnimation.minColor.r, colorAnimation.maxColor.r, 1- Math.abs(colorAnimation.age.r % 2f - 1));
        animation.color.g = colorAnimation.tween.apply( colorAnimation.minColor.g, colorAnimation.maxColor.g, 1- Math.abs(colorAnimation.age.g % 2f - 1));
        animation.color.b = colorAnimation.tween.apply( colorAnimation.minColor.b, colorAnimation.maxColor.b, 1- Math.abs(colorAnimation.age.b % 2f - 1));
        animation.color.a = colorAnimation.tween.apply( colorAnimation.minColor.a, colorAnimation.maxColor.a, 1- Math.abs(colorAnimation.age.a % 2f - 1));

        if ( colorAnimation.duration != -1 )
        {
            colorAnimation.duration -= Tox.world.delta;
            if ( colorAnimation.duration <= 0 )
            {
                animation.color.set(1f,1f,1f,1f);
                entity.removeComponent(ColorAnimation.class).changedInWorld();
            }

        }
    }

    @Override
    protected boolean checkProcessing() {
        return true;
    }

}
