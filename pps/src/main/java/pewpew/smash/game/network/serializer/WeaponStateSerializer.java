package pewpew.smash.game.network.serializer;

import java.util.HashMap;
import java.util.Map;
import pewpew.smash.game.entities.Player;
import pewpew.smash.game.network.packets.WeaponStatePacket;
import pewpew.smash.game.objects.ItemFactory;
import pewpew.smash.game.objects.MeleeWeapon;
import pewpew.smash.game.objects.RangedWeapon;
import pewpew.smash.game.objects.Weapon;
import pewpew.smash.game.objects.WeaponType;

public class WeaponStateSerializer {

    public static WeaponStatePacket serializeWeaponState(Player player) {
        Weapon weapon = player.getEquippedWeapon();
        if (weapon == null) {
            return new WeaponStatePacket(Integer.MIN_VALUE, null, null);
        }

        Map<String, Object> stateData = new HashMap<>();
        WeaponType weaponType = getWeaponType(weapon);

        if (weapon instanceof MeleeWeapon) {
            MeleeWeapon meleeWeapon = (MeleeWeapon) weapon;
            stateData.put("attackProgress", meleeWeapon.getAttackProgress());
            stateData.put("isReturning", meleeWeapon.isReturning());
        } else if (weapon instanceof RangedWeapon) {
            RangedWeapon rangedWeapon = (RangedWeapon) weapon;
            stateData.put("currentAmmo", rangedWeapon.getCurrentAmmo());
        }

        return new WeaponStatePacket(player.getId(), weaponType, stateData);
    }

    public static void deserializeWeaponState(WeaponStatePacket weaponState, Player player) {
        WeaponType weaponType = weaponState.getWeaponType();
        Weapon weapon = player.getEquippedWeapon();

        if (weapon == null || getWeaponType(weapon) != weaponType) {
            weapon = ItemFactory.createItem(weaponType);
            weapon.pickup(player);
            player.setEquippedWeapon(weapon);
        }

        Map<String, Object> stateData = weaponState.getWeaponStateData();

        if (weapon instanceof MeleeWeapon) {
            MeleeWeapon meleeWeapon = (MeleeWeapon) weapon;
            meleeWeapon.setAttackProgress((float) stateData.get("attackProgress"));
            meleeWeapon.setReturning((boolean) stateData.get("isReturning"));
        } else if (weapon instanceof RangedWeapon) {
            RangedWeapon rangedWeapon = (RangedWeapon) weapon;
            rangedWeapon.setCurrentAmmo((int) stateData.get("currentAmmo"));
        }
    }

    private static WeaponType getWeaponType(Weapon weapon) {
        for (WeaponType type : WeaponType.values()) {
            if (type.getDamage() == weapon.getDamage() &&
                    type.getRange() == weapon.getRange() &&
                    type.getAttackSpeed() == weapon.getAttackSpeed()) {
                return type;
            }
        }
        return null;
    }
}
