package net.mostlyoriginal.tox;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.managers.TagManager;
import com.artemis.utils.Bag;
import net.mostlyoriginal.tox.component.Health;
import net.mostlyoriginal.tox.component.Inventory;
import net.mostlyoriginal.tox.component.Position;
import net.mostlyoriginal.tox.component.Toxication;

/**
 * @author Daan van Yperen
 */
public class EntityUtils {

    /**
     * Copy entity from old world into the new one.
     *
     * This assumes the old world is no longer in use, and is potentially unsafe if it isn't. (The components are not cloned, just reused!)
     */
    public static Entity copyEntityFromOldWorld( Entity old )
    {
        Entity entity = Tox.world.createEntity();

        Bag<Component> fillBag = new Bag<Component>();

        old.getComponents(fillBag);
        for ( Component comp : fillBag )
        {
            entity.addComponent(comp);
        }

        return entity;
    }


    /**
     * Quick hack to transfer player state to the next level.
     *
     * Kinda funky stuff here. Be weary!
     *
     * @param x
     * @param y
     * @param legacyPlayer
     * @return
     */
    public static Entity cloneLegacyPlayer(int x, int y, Entity legacyPlayer) {

        Entity player = copyEntityFromOldWorld(legacyPlayer);

        player.getComponent(Toxication.class).toxication = 0.5f;
        player.getComponent(Health.class).damage = 0;

        Position position = player.getComponent(Position.class);
        position.x = x; position.y = y;

        // copy over the old inventory.
        Inventory oldInventory = legacyPlayer.getComponent(Inventory.class);


        Inventory inventory = player.getComponent(Inventory.class);
        {
            for ( int i =0; i<3;i++)
            {
                if ( oldInventory.carried[i] != null )
                {
                    inventory.carried[i] = copyEntityFromOldWorld(oldInventory.carried[i]);
                    inventory.carried[i].addToWorld();
                }
            }

        }

        Tox.world.getManager(TagManager.class).register(EntityFactory.PLAYER_TAG, player);

        return player;
    }
}
