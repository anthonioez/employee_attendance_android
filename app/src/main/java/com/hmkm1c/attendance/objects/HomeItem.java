package com.hmkm1c.attendance.objects;

public class HomeItem
{
    public enum Id
    {
        CHECK_IN,
        CHECK_OUT,
        SETTINGS,
        ATTENDANCE,
    }

    public Id id;
    public int icon;
    public String title;

    public HomeItem(Id id, String title, int icon)
    {
        this.id = id;
        this.title = title;
        this.icon = icon;
    }
}
