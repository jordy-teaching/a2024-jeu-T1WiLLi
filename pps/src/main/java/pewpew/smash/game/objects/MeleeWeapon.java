package pewpew.smash.game.objects;

import java.awt.Shape;
import java.awt.image.BufferedImage;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
@Setter
public abstract class MeleeWeapon extends Weapon {

    protected boolean isAttacking;
    protected boolean isReturning;
    protected float attackProgress = 0.0f;

    public abstract void attack();

    public abstract Shape getHitbox();

    public MeleeWeapon(String name, String description, BufferedImage preview) {
        super(name, description, preview);
    }

    public void buildWeapon(int damage, double attackSpeed, int range) {
        super.buildWeapon(damage, attackSpeed, range);
    }
}
