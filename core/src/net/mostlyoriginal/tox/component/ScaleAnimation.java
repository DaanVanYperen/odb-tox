package net.mostlyoriginal.tox.component;

import com.badlogic.gdx.math.Interpolation;

/**
 * @author Daan van Yperen
 */
public class ScaleAnimation {
    float scaleXMin=1, scaleXMax=1, durationX=1;
    Interpolation scaleX=Interpolation.bounceIn;
    float scaleYMin=1, scaleYMax=1, durationY=1;
    Interpolation scaleY=Interpolation.bounceIn;
}
