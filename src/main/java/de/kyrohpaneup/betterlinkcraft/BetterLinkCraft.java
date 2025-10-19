package de.kyrohpaneup.betterlinkcraft;

import de.kyrohpaneup.betterlinkcraft.chat.HideChatMessages;
import de.kyrohpaneup.betterlinkcraft.gui.GuiMenuExtension;
import de.kyrohpaneup.betterlinkcraft.managers.*;
import de.kyrohpaneup.betterlinkcraft.mods.autotext.CustomCommand;
import de.kyrohpaneup.betterlinkcraft.mods.impl.FullBright;
import de.kyrohpaneup.betterlinkcraft.mods.impl.InventoryJam;
import de.kyrohpaneup.betterlinkcraft.settings.BLCSettings;
import de.kyrohpaneup.betterlinkcraft.settings.Option;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

@Mod(modid = BetterLinkCraft.MODID, name = BetterLinkCraft.NAME, version = BetterLinkCraft.VERSION)
public class BetterLinkCraft {
    public static final String MODID = "betterlinkcraft";
    public static final String NAME = "BetterLinkCraft";
    public static final String VERSION = "1.0";

    @Mod.Instance
    public static BetterLinkCraft INSTANCE;

    private KeybindManager keybindManager;
    private ConnectionManager connectionManager;
    private PracticeManager practiceManager;
    private CommandManager commandManager;
    private ConfigManager configManager;
    private StratReminderManager srm;
    private TimerManager timerManager;
    private AutoTextManager autoTextManager;
    private CustomCommandManager customCommandManager;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        this.commandManager = new CommandManager();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        configManager = new ConfigManager();
        connectionManager = new ConnectionManager();
        practiceManager = new PracticeManager();
        keybindManager = new KeybindManager();
        timerManager = new TimerManager();
        autoTextManager = new AutoTextManager();
        customCommandManager = new CustomCommandManager();
        this.srm = new StratReminderManager(configManager);
        MinecraftForge.EVENT_BUS.register(keybindManager);
        MinecraftForge.EVENT_BUS.register(new GuiMenuExtension());
        BLCSettings.init(new File("BetterLinkCraft"));
        keybindManager.init();
        configManager.init();
        keybindManager.registerKeybindings();
        MinecraftForge.EVENT_BUS.register(new HideChatMessages());
        MinecraftForge.EVENT_BUS.register(new ServerConnector());
        MinecraftForge.EVENT_BUS.register(connectionManager);
        MinecraftForge.EVENT_BUS.register(new InventoryJam());
        MinecraftForge.EVENT_BUS.register(timerManager);
        MinecraftForge.EVENT_BUS.register(srm);
        MinecraftForge.EVENT_BUS.register(autoTextManager);
        commandManager.init();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        SheetManager.init();
        if (Option.FULLBRIGHT_ENABLED.getBooleanValue()) {
            FullBright.enable();
        }
    }

    public KeybindManager getKeybindManager() {
        return keybindManager;
    }

    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public PracticeManager getPracticeManager() {
        return practiceManager;
    }

    public ConfigManager getConfigManager() { return configManager; }

    public StratReminderManager getSrm() {
        return srm;
    }

    public TimerManager getTimerManager() {
        return timerManager;
    }

    public AutoTextManager getAutoTextManager() {
        return autoTextManager;
    }

    public CustomCommandManager getCustomCommandManager() { return customCommandManager; }
}
