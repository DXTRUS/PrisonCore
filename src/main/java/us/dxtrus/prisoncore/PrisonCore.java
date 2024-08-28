@Getter
public final class PrisonCore extends JavaPlugin implements Listener {
    @Getter private static PrisonCore instance;
    private final Random random = new Random(System.currentTimeMillis());
    private final BungeeMessenger messenger = new BungeeMessenger(this);
    private Broker broker;

    @Override
    public void onEnable() {
        instance = this;

        broker = new RedisBroker(this);
        broker.connect();

        // Init all managers
        StorageManager.getInstance();
        Config.getInstance();
        Lang.getInstance();
        ServerManager.getInstance();
        LocalMineManager.getInstance();
        MineManager.getInstance();
        TransferManager.getInstance();
        HeartBeat.getInstance();

        FastInvManager.register(this);

        Stream.of(
                new CommandMine(this),
                new AdminCommand(this)
        ).forEach(BukkitCommandManager.getInstance()::registerCommand);

        Stream.of(
                new PlayerListener(this),
                new MineListener()
        ).forEach(e -> Bukkit.getPluginManager().registerEvents(e, this));

        new PAPIHook().register();
    }

    @Override
    public void onDisable() {
        broker.destroy();
        StorageManager.getInstance().shutdown();
    }
}
