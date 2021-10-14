package me.gardendev.simplesoups;

import me.gardendev.simplesoups.gui.KitsGUI;
import me.gardendev.simplesoups.loader.*;
import me.gardendev.simplesoups.storage.PlayerData;
import me.gardendev.simplesoups.api.Core;
import me.gardendev.simplesoups.api.Loader;
import me.gardendev.simplesoups.scoreboard.GameScoreboard;
import me.gardendev.simplesoups.storage.DataStorage;
import me.gardendev.simplesoups.storage.database.IConnection;
import me.gardendev.simplesoups.storage.database.types.mysql.MySQLConnection;
import me.gardendev.simplesoups.storage.database.types.sqlite.SQLConnection;

public class PluginCore implements Core{

    private final SimpleSoups plugin;

    private FilesLoader filesLoader;
    private ManagerLoader managerLoader;
    private HandlersLoader handlersLoader;

    private KitsGUI kitsGUI;
    private PlayerData playerData;
    private IConnection connection;
    private DataStorage storage;
    private GameScoreboard gameScoreboard;

    public PluginCore(SimpleSoups plugin) {
        this.plugin = plugin;
    }

    @Override
    public void init() {
        this.filesLoader = new FilesLoader(this);
        this.filesLoader.load();
        this.database();
        playerData = new PlayerData(this);
        initLoaders(
                this.managerLoader = new ManagerLoader(),
                this.handlersLoader = new HandlersLoader(this),
                new CommandsLoader(this),
                new ListenersLoader(this)
        );
        kitsGUI = new KitsGUI(this);
        gameScoreboard = new GameScoreboard(this);
        gameScoreboard.runTaskUpdate();
    }

    private void database() {
        this.getPlugin().getLogger().info("Loading database...");
        if (this.filesLoader.getConfig().getBoolean("database.enable")) {
            this.connection = new MySQLConnection(this);
        }else {
            this.connection = new SQLConnection(this);
        }
        this.connection.load();
        this.storage = new DataStorage(this.connection, this);
    }

    public void closeDatabase() {
        this.connection.close();
    }

    private void initLoaders(Loader...loaders){
        for (Loader loader : loaders){
            loader.load();
        }
    }

    public FilesLoader getFilesLoader() {
        return filesLoader;
    }

    public SimpleSoups getPlugin(){
        return plugin;
    }

    public ManagerLoader getManagers() {
        return managerLoader;
    }

    public KitsGUI getKitsGUI() {
        return kitsGUI;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public DataStorage getStorage() {
        return storage;
    }

    public GameScoreboard gameScoreboard(){
        return gameScoreboard;
    }

    public HandlersLoader getHandlersLoader() {
        return handlersLoader;
    }
}
