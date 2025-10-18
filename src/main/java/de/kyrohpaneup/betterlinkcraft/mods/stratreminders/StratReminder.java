package de.kyrohpaneup.betterlinkcraft.mods.stratreminders;

import de.kyrohpaneup.betterlinkcraft.utils.DrawUtils;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;
import java.util.UUID;

public class StratReminder {

    public String id;
    public String map;
    public String position;
    public String facing;
    public String setup;
    public String input;
    public String comment;
    public int x;
    public int y;
    public int z;

    public StratReminder(String id, String map, String position, String facing, String setup, String input, String comment, int x, int y, int z) {
        if (id == null || id.replace(" ", "").equals("")) {
            this.id = UUID.randomUUID().toString();
        } else {
            this.id = id;
        }
        this.map = map;
        this.position = position;
        this.facing = facing;
        this.setup = setup;
        this.input = input;
        this.comment = comment;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    private ArrayList<String> getArrayList() {
        String line1 = facing.replace(" ", "").equals("") ? position : position + " ";
        if (!facing.contains("f") && !facing.replace(" ", "").equals("")) {
            line1 = line1 + "f";
        }
        line1 = line1 + facing;

        ArrayList<String> list = new ArrayList<>();
        list.add(line1);
        String line2 = "";

        if (!setup.replace(" ", "").equals("")) {
            line2 = setup;
            if (!input.replace(" ", "").equals("")) { line2 += " "; }
        }

        if (!input.replace(" ", "").equals("")) {
            line2 = line2 + input;
        }

        if (!line2.replace(" ", "").equals("")) {
            list.add(line2);
        }

        String line3 = comment;
        list.add(line3);

        return list;
    }

    public void render(float partialTicks) {
        BlockPos blockPos = new BlockPos(this.x, this.y, this.z);
        DrawUtils.drawTextAtCoords(getArrayList(), blockPos, partialTicks);
    }

    public void updateFrom(StratReminder other) {
        this.map = other.map;
        this.position = other.position;
        this.facing = other.facing;
        this.setup = other.setup;
        this.input = other.input;
        this.comment = other.comment;
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }

    public boolean matchesQuery(String query) {
        String lcQuery = query.toLowerCase();
        return position.toLowerCase().contains(lcQuery) ||
                comment.toLowerCase().contains(lcQuery) ||
                input.toLowerCase().contains(lcQuery);
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public int getY() {
        return y;
    }

    public String getComment() {
        return comment;
    }

    public String getFacing() {
        return facing;
    }

    public String getId() {
        return id;
    }

    public String getInput() {
        return input;
    }

    public String getMap() {
        return map;
    }

    public String getPosition() {
        return position;
    }

    public String getSetup() {
        return setup;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setFacing(String facing) {
        this.facing = facing;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setSetup(String setup) {
        this.setup = setup;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setZ(int z) {
        this.z = z;
    }
}
