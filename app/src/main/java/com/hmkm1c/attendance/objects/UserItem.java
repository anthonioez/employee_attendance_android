package com.hmkm1c.attendance.objects;

import org.json.JSONObject;

public class UserItem
{
    public String name;
    public boolean banned;

    public UserItem()
    {
        name = "";
        banned = false;
    }

    public static UserItem copy(JSONObject json)
    {
        try
        {
            UserItem user = new UserItem();
            user.name   = json.getString("name");
            user.banned = json.getBoolean("banned");

            return user;
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
