package net.mostlyoriginal.tox.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import net.mostlyoriginal.tox.EntityFactory;
import net.mostlyoriginal.tox.Tox;
import net.mostlyoriginal.tox.component.Animation;
import net.mostlyoriginal.tox.component.Selectable;
import net.mostlyoriginal.tox.component.Setting;

/**
 * @author Daan van Yperen
 */
public class SettingSystem extends EntityProcessingSystem {

    @Mapper ComponentMapper<Setting> setm;
    @Mapper ComponentMapper<Selectable> sm;
    @Mapper ComponentMapper<Animation> am;

    public SoundSystem soundSystem;
    public SelectableSystem selectableSystem;


    public SettingSystem() {
        super(Aspect.getAspectForAll(Setting.class, Selectable.class));
    }

    @Override
    protected void initialize() {
        soundSystem = world.getSystem(SoundSystem.class);
        selectableSystem = world.getSystem(SelectableSystem.class);
        EntityFactory.addSfxToggle(Gdx.graphics.getWidth() -   22*Animation.DEFAULT_SCALE, Gdx.graphics.getHeight() - 44).addToWorld();
        EntityFactory.addMusicToggle(Gdx.graphics.getWidth() - 44*Animation.DEFAULT_SCALE, Gdx.graphics.getHeight() - 44).addToWorld();
    }

    @Override
    protected void process(Entity e) {
        if (sm.get(e).selected) {
            Setting setting = setm.get(e);
            switch (setting.type) {

                case MUSIC:
                    Tox.settings.musicOn = !Tox.settings.musicOn;
                    Tox.resource.updateMusic();
                    break;
                case SFX:
                    Tox.settings.sfxOn = !Tox.settings.sfxOn;
                    break;
            }
            selectableSystem.selectionCooldown(0.2f);
            Tox.settings.save();
        }

        Setting setting = setm.get(e);
        switch (setting.type) {
            case MUSIC:
                am.get(e).id = Tox.settings.musicOn ? "music-on" : "music-off";
                break;
            case SFX:
                am.get(e).id = Tox.settings.sfxOn ? "sfx-on" : "sfx-off";
                break;
        }
    }
}
