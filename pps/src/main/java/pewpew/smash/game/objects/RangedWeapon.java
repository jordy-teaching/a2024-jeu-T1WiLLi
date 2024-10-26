package pewpew.smash.game.objects;

import java.awt.image.BufferedImage;

import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
public abstract class RangedWeapon extends Weapon {

    protected int ammoCapacity;
    protected int currentAmmo;
    protected double reloadSpeed;

    private double reloadTimer;

    public abstract void shoot();

    public RangedWeapon(String name, String description, BufferedImage preview) {
        super(name, description, preview);
    }

    public void buildWeapon(int damage, int range, double attackSpeed, double reloadSpeed, int ammoCapacity) {
        super.buildWeapon(damage, attackSpeed, range);
        this.reloadSpeed = reloadSpeed;
        this.ammoCapacity = ammoCapacity;
        this.currentAmmo = ammoCapacity;
    }

    // This function should be called in update()
    protected void reload() {
        reloadTimer -= 0.01;
        if (reloadSpeed <= 0) {
            reloadTimer = reloadSpeed;
            currentAmmo = ammoCapacity;
        }
    }

    protected boolean canShoot() {
        return currentAmmo > 0;
    }
}
