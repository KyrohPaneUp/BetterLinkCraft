package de.kyrohpaneup.betterlinkcraft.managers;

import de.kyrohpaneup.betterlinkcraft.gui.impl.speedrun.SpeedrunCompleteMenu;
import de.kyrohpaneup.betterlinkcraft.settings.Option;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.*;

public class TimerManager {

    public boolean isTimerOn = false;
    public boolean isTimerEnabled = false;
    public boolean hasTimerEnded = false;
    public long timer = 0;

    public long activePlayTimer = 0;
    public long lastActiveTime = 0;
    public boolean wasGamePaused = false;

    // private List<String> segments = new ArrayList<>();
    public Map<String, Long> rtaSegmentMap = new HashMap<>();
    public Map<String, Long> igtSegmentMap = new HashMap<>();
    private int currentSegment = 0;
    private String currentSegmentString = "Start";
    private Map<String, List<String>> speedrunMaps = new HashMap<>();

    public Map<String, List<String>> getSpeedrunMaps() {
        return speedrunMaps;
    }

    public List<String> getMaps() {
        return new ArrayList<>(speedrunMaps.keySet());
    }

    public void addMap(String map) {
        speedrunMaps.put(map, new ArrayList<>());
    }

    public void removeMap(String map) {
        speedrunMaps.remove(map);
    }

    public void renameMap(String oldMap, String newMap) {
        if (speedrunMaps.containsKey(oldMap)) {
            List<String> value = speedrunMaps.remove(oldMap);
            speedrunMaps.put(newMap, value);
        }
    }

    public void setSpeedrunMaps(Map<String, List<String>> speedrunMaps) {
        this.speedrunMaps = speedrunMaps;
    }

    public List<String> getSegments() {
        return speedrunMaps.getOrDefault(Option.SELECTED_SPEEDRUN_MAP.getStringValue(), new ArrayList<>());
    }

    public void addSegment(String segment) {
        speedrunMaps.computeIfAbsent(Option.SELECTED_SPEEDRUN_MAP.getStringValue(), k -> new ArrayList<>())
                .add(segment);
    }

    public void removeSegment(String segment) {
        speedrunMaps.computeIfPresent(Option.SELECTED_SPEEDRUN_MAP.getStringValue(),
                (key, list) -> {
                    list.remove(segment);
                    return list.isEmpty() ? null : list;
                });
    }

    public void startTimer() {
        hasTimerEnded = false;
        isTimerOn = false;
        isTimerEnabled = true;
        activePlayTimer = 0;
        lastActiveTime = 0;
        wasGamePaused = false;
        currentSegment = 0;
        currentSegmentString = "Start";
    }

    public void stopTimer() {
        isTimerOn = false;
        isTimerEnabled = false;
        hasTimerEnded = true;
        timer = System.currentTimeMillis() - timer;

        updateActivePlayTime();

        ChatManager.sendMessageWithPrefix("\u00A7aYou completed the Speedrun. The results will be shown in 5 seconds.");

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Minecraft.getMinecraft().displayGuiScreen(new SpeedrunCompleteMenu(null));
            }
        }, 5000);
    }

    public void nextSegment() {
        List<String> segments = getSegments();
        if (segments.size() >= currentSegment + 1) {
            String segment = segments.get(currentSegment);
            rtaSegmentMap.put(segment, (System.currentTimeMillis() - timer));
            igtSegmentMap.put(segment, activePlayTimer);
            currentSegment++;
            currentSegmentString = segment;
            ChatManager.sendMessageWithPrefix("\u00A77You reached " + segment + " in " + formatTime(System.currentTimeMillis() - timer));
        } else {
            rtaSegmentMap.put("End", (System.currentTimeMillis() - timer));
            igtSegmentMap.put("End", activePlayTimer);
            currentSegmentString = "End";
            stopTimer();
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            Minecraft mc = Minecraft.getMinecraft();

            GuiScreen screen = mc.currentScreen;
            boolean isGamePaused = screen != null && screen.doesGuiPauseGame() || mc.thePlayer == null;

            if (isTimerEnabled && isTimerOn) {
                if (!isGamePaused) {
                    if (wasGamePaused || lastActiveTime == 0) {
                        lastActiveTime = System.currentTimeMillis();
                        wasGamePaused = false;
                    } else {
                        long currentTime = System.currentTimeMillis();
                        activePlayTimer += (currentTime - lastActiveTime);
                        lastActiveTime = currentTime;
                    }
                } else {
                    wasGamePaused = true;
                }
            }
        }
    }

    @SubscribeEvent
    public void onMove(LivingEvent.LivingUpdateEvent event) {
        if (event.entity instanceof EntityPlayerSP) {
            EntityPlayerSP player = (EntityPlayerSP) event.entity;
            if (player.posX == player.prevPosX && player.posZ == player.prevPosZ) {
                return;
            }
            if (isTimerEnabled && !isTimerOn) {
                isTimerOn = true;
                timer = System.currentTimeMillis();
                lastActiveTime = System.currentTimeMillis();
            }
        }
    }



    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
        if (!Option.TIMER_ENABLED.getBooleanValue()) return;

        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution sr = new ScaledResolution(mc);
        float scale = 1.4F;

        String rtaTime = getRTATime();
        String igtTime = getIGTTime();

        String rtaString = "RTA: " + rtaTime;
        String igtString = "IGT: " + igtTime;
        String headerString = "Current Section";

        int rtaWidth = mc.fontRendererObj.getStringWidth(rtaString);
        int igtWidth = mc.fontRendererObj.getStringWidth(igtString);
        int headerWidth = mc.fontRendererObj.getStringWidth(headerString);
        int sectionWidth = mc.fontRendererObj.getStringWidth(currentSegmentString);

        int rightMargin = 15;
        int rtaX = sr.getScaledWidth() - (int)(rtaWidth * scale) - rightMargin;
        int igtX = sr.getScaledWidth() - (int)(igtWidth * scale) - rightMargin;
        int headerX = sr.getScaledWidth() - (int) (headerWidth * scale) - rightMargin;
        int sectionX = sr.getScaledWidth() - (int) (sectionWidth * scale) - rightMargin;
        int yPosRTA = 10;
        int yPosIGT = 30;
        int yPosHeader = 55;
        int yPosSection = 70;

        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, scale);

        int scaledRtaX = (int) (rtaX / scale);
        int scaledIgtX = (int) (igtX / scale);
        int scaledHeaderX = (int) (headerX / scale);
        int scaledSectionX = (int) (sectionX / scale);

        mc.fontRendererObj.drawString(rtaString, scaledRtaX, (int)(yPosRTA / scale), 0x55FFFF, true);
        mc.fontRendererObj.drawString(igtString, scaledIgtX, (int)(yPosIGT / scale), 0xFFFF55, true);

        mc.fontRendererObj.drawString(headerString, scaledHeaderX, (int) (yPosHeader / scale), 0xFFFE55, true);
        mc.fontRendererObj.drawString(currentSegmentString, scaledSectionX, (int) (yPosSection / scale), 0xFFFFFF, true);

        GlStateManager.popMatrix();
    }

    private String getRTATime() {
        if (hasTimerEnded) return formatTime(timer);
        if (isTimerOn) return formatTime(System.currentTimeMillis() - timer);
        return isTimerEnabled ? "Move to start!" : "N/A";
    }

    private String getIGTTime() {
        if (hasTimerEnded) return formatTime(activePlayTimer);
        if (isTimerOn) return formatTime(activePlayTimer);
        return isTimerEnabled ? "Move to start!" : "N/A";
    }

    private void updateActivePlayTime() {
        if (isTimerEnabled && isTimerOn && !wasGamePaused && lastActiveTime != 0) {
            long currentTime = System.currentTimeMillis();
            activePlayTimer += (currentTime - lastActiveTime);
            lastActiveTime = currentTime;
        }
    }

    public String formatTime(long milliseconds) {
        long seconds = (milliseconds / 1000) % 60;
        long minutes = (milliseconds / (1000 * 60)) % 60;
        long hours = milliseconds / (1000 * 60 * 60);
        long ms = milliseconds % 1000;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, ms);
        } else {
            return String.format("%02d:%02d.%03d", minutes, seconds, ms);
        }
    }
}