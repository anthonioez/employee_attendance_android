package com.hmkm1c.attendance.http;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class HttpUtils
{
    private static final String TAG = HttpUtils.class.getSimpleName();

    public static HttpResponse post(String postUrl, HashMap<String, String> postDataParams)
    {
        HttpResponse response = null;

        URL url;
        try
        {
            url = new URL(postUrl);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();


            response = new HttpResponse();
            response.status = conn.getResponseCode();

            if (response.status == HttpsURLConnection.HTTP_OK)
            {
                response.data = streamBytes(conn.getInputStream());
            }
            else
            {
                response.data = streamBytes(conn.getErrorStream());
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "post Exception: " + e.toString());
        }

        return response;
    }

    public static String getPostDataString(HashMap<String, String> params)
    {
        String res = "";
        try {
            StringBuilder result = new StringBuilder();
            boolean first = true;
            for(Map.Entry<String, String> entry : params.entrySet()){
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
            res = result.toString();
        }
        catch (Exception e)
        {

        }
        return res;
    }

    public static byte[] streamBytes(InputStream is) throws Exception
    {
        byte[] bytes = null;
        ByteArrayOutputStream bos = null;

        try
        {
            bos = streamBuffer(is);
            if(bos != null)
            {
                bytes = bos.toByteArray();
            }
        }
        finally
        {
            try
            {
                if (bos != null)
                    bos.close();
            }
            catch (Exception e)
            {
            }
        }

        return bytes;
    }

    public static ByteArrayOutputStream streamBuffer(InputStream is)
    {
        ByteArrayOutputStream bos = null;

        try
        {
            bos = new ByteArrayOutputStream();

            int nRead;
            byte[] data = new byte[16384*4];

            while ((nRead = is.read(data, 0, data.length)) != -1)
            {
                bos.write(data, 0, nRead);
            }

        }
        catch (IOException e)
        {
            Log.e(TAG, "streamBuffer IOException: " + e.toString());
        }
        catch (Exception e)
        {
            Log.e(TAG, "streamBuffer Exception: " + e.toString());
        }

        return bos;
    }

}
