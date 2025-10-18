package de.kyrohpaneup.betterlinkcraft.managers;
import de.kyrohpaneup.betterlinkcraft.mods.stratreminders.StratReminder;
import de.kyrohpaneup.betterlinkcraft.settings.Option;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;
import java.util.stream.Collectors;

public class StratReminderManager {
    private final ConfigManager configManager;
    public static boolean showReminders = false;

    public StratReminderManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public void addReminder(StratReminder reminder) {
        configManager.saveReminder(reminder);
    }

    public void removeReminder(String reminderId) {
        configManager.deleteReminder(reminderId);
    }

    // --------------- Requests ---------------
    public List<StratReminder> getCurrentMapReminders() {
        return configManager.getRemindersByMap(Option.SELECTED_SR_MAP.getStringValue());
    }

    public List<StratReminder> getAllRemindersList() {
        List<StratReminder> allReminders = new ArrayList<>();
        configManager.getAllRemindersMap().values().forEach(allReminders::addAll);
        return allReminders;
    }

    public List<Map<String, String>> getAllRemindersMap() {
        List<StratReminder> allReminders = getCurrentMapReminders();
        List<Map<String, String>> list = new ArrayList<>();
        for (StratReminder reminder : allReminders) {
            list.add(toSimpleMap(reminder));
        }
        return list;
    }

    public List<String> getAllMaps() {
        List<StratReminder> reminders = getAllRemindersList();
        return reminders.stream()
                .map(StratReminder::getMap)
                .filter(Objects::nonNull).distinct().collect(Collectors.toList());
    }

    // --------------- Rendering ---------------
    @SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent event) {
        if (showReminders) {
            getCurrentMapReminders().forEach(reminder ->
                    reminder.render(event.partialTicks)
            );
        }
    }

    public Map<String, String> toSimpleMap(StratReminder reminder) {
        Map<String, String> map = new HashMap<>();
        map.put("id", reminder.getId());
        map.put("map id", reminder.map.toLowerCase());
        map.put("position", reminder.position);
        map.put("facing", reminder.facing);
        map.put("setup", reminder.setup);
        map.put("strat", reminder.input);
        map.put("comment", reminder.comment);
        map.put("x", String.valueOf(reminder.x));
        map.put("y", String.valueOf(reminder.y));
        map.put("z", String.valueOf(reminder.z));
        return map;
    }
}
