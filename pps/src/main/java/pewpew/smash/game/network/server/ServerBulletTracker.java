package pewpew.smash.game.network.server;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import pewpew.smash.game.entities.Bullet;
import pewpew.smash.game.network.packets.BulletCreatePacket;
import pewpew.smash.game.network.packets.BulletRemovePacket;

public class ServerBulletTracker {

    private ServerWrapper server;

    private static final ServerBulletTracker instance = new ServerBulletTracker();
    private final Map<Integer, Bullet> bullets = new ConcurrentHashMap<>();
    private int nextBulletId = 0;

    public static ServerBulletTracker getInstance() {
        return instance;
    }

    public void setServerReference(ServerWrapper server) {
        this.server = server;
    }

    public void addBullet(Bullet bullet) {
        int bulletId = nextBulletId++;
        bullet.setId(bulletId);
        bullets.put(bulletId, bullet);
        BulletCreatePacket packet = new BulletCreatePacket(
                bulletId,
                bullet.getX(),
                bullet.getY(),
                bullet.getPlayerOwnerID());
        server.sendToAllTCP(packet);
    }

    public void removeBullet(Bullet bullet) {
        bullets.remove(bullet.getId());
        server.sendToAllTCP(new BulletRemovePacket(bullet.getId()));
    }

    public void update(ServerWrapper server) {
        Iterator<Map.Entry<Integer, Bullet>> iterator = bullets.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Bullet> entry = iterator.next();
            Bullet bullet = entry.getValue();

            bullet.updateServer();
            if (bullet.getDistanceTraveled() > bullet.getMaxRange()) {
                server.sendToAllTCP(new BulletRemovePacket(entry.getKey()));
                iterator.remove();
            }
        }
    }

    public Collection<Bullet> getBullets() {
        return bullets.values();
    }
}
