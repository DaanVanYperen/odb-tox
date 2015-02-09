package net.mostlyoriginal.tox.system;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.VoidEntitySystem;
import net.mostlyoriginal.tox.ToxUtil;
import net.mostlyoriginal.tox.component.*;

/**
 * @author Daan van Yperen
 */
public class InventorySystem extends VoidEntitySystem {

    @Mapper
    ComponentMapper<Inventory> im;

    @Mapper
    ComponentMapper<Position> pm;

    @Mapper
    ComponentMapper<Animation> am;

    @Mapper
    ComponentMapper<EquipBonus> ebm;

    public Inventory getPlayerInventory() {
        Entity player = ToxUtil.getPlayer();
        if (player != null && im.has(player)) {
            return im.get(player);
        }
        return null;
    }

    public void equip(Entity item, Inventory.Slot slot) {
        emptySlot(slot);
        equipInSlot(slot, item);
    }

    private void equipInSlot(Inventory.Slot slot, Entity item) {
        Inventory inventory = getPlayerInventory();
        if (inventory != null) {
            inventory.carried[slot.ordinal()] = item;
            item.removeComponent(Selectable.class).changedInWorld();

            if ( am.has(item) )
            {
                final Animation animation = am.get(item);
                animation.behindId = "inventory-item-background";
            }

            // move item to correct slot.
            if (pm.has(item)) {
                Position p = pm.get(item);
                switch (slot) {
                    case BODY:
                        p.x = 125 * Animation.DEFAULT_SCALE;
                        p.y = 360 * Animation.DEFAULT_SCALE;
                        break;
                    case HAND:
                        p.x = 162 * Animation.DEFAULT_SCALE;
                        p.y = 360 * Animation.DEFAULT_SCALE;
                        break;
                    case MISC:
                        p.x = 199 * Animation.DEFAULT_SCALE;
                        p.y = 360 * Animation.DEFAULT_SCALE;
                        break;
                }
            }
        }
    }

    private void emptySlot(Inventory.Slot slot) {
        Inventory inventory = getPlayerInventory();
        if (inventory != null) {
            Entity item = inventory.carried[slot.ordinal()];
            if (item != null) {
                item.deleteFromWorld();
                inventory.carried[slot.ordinal()] = null;
            }
        }
    }

    @Override
    protected void processSystem() {

    }

    /**
     * @param entity entity to check.
     * @return All inventory bonuses stacked.
     */
    public EquipBonus getTotalBonus(Entity entity) {
        EquipBonus sumEquipBonus = new EquipBonus();

        Inventory inventory = im.get(entity);
        if (inventory != null) {
            for (int i = 0, s = inventory.carried.length; i < s; i++) {
                if (inventory.carried[i] != null && ebm.has(inventory.carried[i])) {
                    sumEquipBonus.stack(ebm.get(inventory.carried[i]));
                }
            }
        }

        return sumEquipBonus;
    }

    /**
     * Check if any of our inventory items has specified effect.
     * @param effect
     * @return
     */
    public boolean hasEffect(EquipBonus.Effect effect) {
        Inventory inventory = im.get(ToxUtil.getPlayer());
        if (inventory != null) {
            for (int i = 0, s = inventory.carried.length; i < s; i++) {
                if (inventory.carried[i] != null && ebm.has(inventory.carried[i])) {
                    if ( ebm.get(inventory.carried[i]).effect == effect )
                        return true;
                }
            }
        }
        return false;
    }

    public void discardByEffect(EquipBonus.Effect effect) {
        Inventory inventory = im.get(ToxUtil.getPlayer());
        if (inventory != null) {
            for (int i = 0, s = inventory.carried.length; i < s; i++) {
                if (inventory.carried[i] != null && ebm.has(inventory.carried[i])) {
                    if ( ebm.get(inventory.carried[i]).effect == effect )
                    {
                        inventory.carried[i].deleteFromWorld();
                        inventory.carried[i] = null;
                    }
                }
            }
        }
    }

    public Entity get(Inventory.Slot slot) {
        Inventory inventory = im.get(ToxUtil.getPlayer());
        if (inventory != null) {
            return inventory.carried[slot.ordinal()];
        }
        return null;
    }
}
