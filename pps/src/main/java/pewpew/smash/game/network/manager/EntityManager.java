package pewpew.smash.game.network.manager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import pewpew.smash.engine.entities.MovableEntity;
import pewpew.smash.engine.entities.UpdatableEntity;
import pewpew.smash.game.entities.Player;

public class EntityManager {
    private final Map<Integer, UpdatableEntity> updatableEntitiesMap;
    private final Map<Integer, MovableEntity> movableEntitiesMap;
    private final Map<Integer, Player> playerEntitiesMap;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

    public EntityManager() {
        this.updatableEntitiesMap = new ConcurrentHashMap<>();
        this.movableEntitiesMap = new ConcurrentHashMap<>();
        this.playerEntitiesMap = new ConcurrentHashMap<>();
    }

    public synchronized void addUpdatableEntity(int id, UpdatableEntity entity) {
        updatableEntitiesMap.put(id, entity);
    }

    public synchronized void addMovableEntity(int id, MovableEntity entity) {
        movableEntitiesMap.put(id, entity);
    }

    public synchronized void addPlayerEntity(int id, Player entity) {
        playerEntitiesMap.put(id, entity);
    }

    public synchronized UpdatableEntity removeUpdatableEntity(int id) {
        return updatableEntitiesMap.remove(id);
    }

    public synchronized MovableEntity removeMovableEntity(int id) {
        return movableEntitiesMap.remove(id);
    }

    public synchronized Player removePlayerEntity(int id) {
        return playerEntitiesMap.remove(id);
    }

    public synchronized UpdatableEntity getUpdatableEntity(int id) {
        return updatableEntitiesMap.get(id);
    }

    public synchronized MovableEntity getMovableEntity(int id) {
        return movableEntitiesMap.get(id);
    }

    public synchronized Player getPlayerEntity(int id) {
        return playerEntitiesMap.get(id);
    }

    public boolean containsUpdatableEntity(int id) {
        return updatableEntitiesMap.containsKey(id);
    }

    public boolean containsMovableEntity(int id) {
        return movableEntitiesMap.containsKey(id);
    }

    public boolean containsPlayerEntity(int id) {
        return playerEntitiesMap.containsKey(id);
    }

    public void clearAllEntities() {
        writeLock.lock();
        try {
            updatableEntitiesMap.clear();
            movableEntitiesMap.clear();
            playerEntitiesMap.clear();
        } finally {
            writeLock.unlock();
        }
    }

    public List<UpdatableEntity> getUpdatableEntities() {
        readLock.lock();
        try {
            return new ArrayList<>(updatableEntitiesMap.values());
        } finally {
            readLock.unlock();
        }
    }

    public List<MovableEntity> getMovableEntities() {
        readLock.lock();
        try {
            return new ArrayList<>(movableEntitiesMap.values());
        } finally {
            readLock.unlock();
        }
    }

    public List<Player> getPlayerEntities() {
        readLock.lock();
        try {
            return new ArrayList<>(playerEntitiesMap.values());
        } finally {
            readLock.unlock();
        }
    }

    public String size() {
        return "Player[" + playerEntitiesMap.size() + "] & MovableEntity[" + movableEntitiesMap.size()
                + "]  & UpdatableEntity[" + updatableEntitiesMap.size() + "]";
    }
}