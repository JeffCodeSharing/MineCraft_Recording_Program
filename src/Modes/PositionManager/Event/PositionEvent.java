package Modes.PositionManager.Event;

import com.alibaba.fastjson.JSONObject;

public class PositionEvent {
    private String x;
    private String y;
    private String z;
    private String note;
    private String color;

    public PositionEvent(
            String x, String y, String z, String note, String color
    ) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.note = note;
        this.color = color;
    }

    public PositionEvent(JSONObject jsonData) {
        this(
                jsonData.getString("x"), jsonData.getString("y"), jsonData.getString("z"),
                jsonData.getString("note"), jsonData.getString("color")
        );
    }

    public String getX() {
        return x;
    }

    public String getY() {
        return y;
    }

    public String getZ() {
        return z;
    }

    public String getNote() {
        return note;
    }

    public String getColor() {
        return color;
    }

    public void setX(String x) {
        this.x = x;
    }

    public void setY(String y) {
        this.y = y;
    }

    public void setZ(String z) {
        this.z = z;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setAll(String x, String y, String z, String note, String color) {
        setX(x);
        setY(y);
        setZ(z);
        setNote(note);
        setColor(color);
    }
}
