package pewpew.smash.game.network.server;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.esotericsoftware.kryonet.Connection;

import pewpew.smash.engine.GameTime;
import pewpew.smash.game.entities.Player;
import pewpew.smash.game.network.Handler;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.DirectionPacket;
import pewpew.smash.game.network.packets.PlayerJoinedPacket;
import pewpew.smash.game.network.packets.PlayerLeftPacket;
import pewpew.smash.game.network.packets.PlayerUsernamePacket;

public class ServerHandler extends Handler implements Runnable {

    private ExecutorService executor;
    private ServerWrapper server;
    private EntityManager entityManager;
    private EntityUpdater entityUpdater;
    private ServerWorldManager worldManager;

    private GameTime gameTime;

    public ServerHandler(int port) {
        this.server = new ServerWrapper(port, port);
        this.executor = Executors.newSingleThreadExecutor();
        this.entityManager = new EntityManager();
        this.entityUpdater = new EntityUpdater(entityManager);
        this.worldManager = new ServerWorldManager();
        this.worldManager.displayWorld();
        this.gameTime = GameTime.getServerInstance();
        registersClasses(this.server.getKryo());
    }

    @Override
    public void start() throws IOException {
        this.server.addListener(bindListener());
        this.server.start();
        this.executor.execute(this);
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            if (gameTime.shouldUpdate()) {
                update(gameTime.getDeltaTime());
                sendStateUpdate();
            }
        }
    }

    @Override
    protected void handlePacket(Connection connection, Object packet) {
        if (packet instanceof PlayerUsernamePacket) {
            PlayerUsernamePacket usernamePacket = (PlayerUsernamePacket) packet;
            this.entityManager.getPlayerEntity(connection.getID()).setUsername(usernamePacket.getUsername());
            PlayerJoinedPacket joinedPacket = new PlayerJoinedPacket(connection.getID(), usernamePacket.getUsername());
            this.server.sendToAllExceptTCP(connection.getID(), joinedPacket);
        } else if (packet instanceof DirectionPacket) {
            DirectionPacket directionPacket = (DirectionPacket) packet;
            Player player = this.entityManager.getPlayerEntity(connection.getID());
            if (player != null) {
                player.setDirection(directionPacket.getDirection());
                player.setRotation(directionPacket.getRotation());
            }
        }
    }

    @Override
    protected void onConnect(Connection connection) {
        Player player = new Player(connection.getID());
        player.teleport(100, 100);
        player.setRotation(0);

        this.entityManager.getPlayerEntities().forEach(existingPlayer -> {
            PlayerJoinedPacket existingPlayerPacket = new PlayerJoinedPacket(
                    existingPlayer.getId(),
                    existingPlayer.getUsername());
            this.server.sendToTCP(connection.getID(), existingPlayerPacket);
        });
        this.entityManager.addPlayerEntity(player.getId(), player);
        this.worldManager.sendWorldDataToClient(server, connection.getID());
        this.entityUpdater.sendAllPlayerPositions(server, connection.getID());
    }

    @Override
    protected void onDisconnect(Connection connection) {
        this.entityManager.removePlayerEntity(connection.getID());
        this.server.sendToAllTCP(new PlayerLeftPacket(connection.getID()));
    }

    @Override
    public void stop() {
        try {
            this.server.stop();
            this.executor.shutdown();

            if (!this.executor.awaitTermination(30, TimeUnit.SECONDS)) {
                this.executor.shutdownNow();
                if (!this.executor.awaitTermination(30, TimeUnit.SECONDS)) {
                    System.err.println("Thread did not terminate");
                }
            }
        } catch (InterruptedException e) {
            this.executor.shutdownNow();
            Thread.currentThread().interrupt();
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void update(double deltaTime) {
        this.entityManager.getPlayerEntities().forEach(Player::updateServer);
    }

    // Do other state update, such as hp, collision, bullet, ammo, inventory , etc.
    private void sendStateUpdate() {
        sendPlayerPos();
    }

    private void sendPlayerPos() {
        this.entityUpdater.sendPlayerPositions(this.server);
    }
}