package com.hmkm1c.attendance.http;

import java.net.HttpURLConnection;

public class HttpResponse
{
    public int                     status;
    public byte[]                  data;

    public HttpResponse()
    {
        status = -1;
        data = null;
    }

    public boolean isOK()
    {
        return (status == HttpURLConnection.HTTP_OK);
    }
}
