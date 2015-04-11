package com.lenddo.widget.address.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by joseph on 12/24/14.
 */
public class WebserviceClient {

    /**
     * Downloads data to buffer
     *
     * @param url Data location
     * @return downloaded data or null if error occurred
     */
    public static byte[] download(String url)
    {
        InputStream is = null;
        ByteArrayOutputStream os = null;

        try {

            final URL u = new URL(url);
            final URLConnection connection = u.openConnection();
            connection.connect();

            is = connection.getInputStream();
            os = new ByteArrayOutputStream();

            final byte[] buffer = new byte[5120];
            int read;

            while (true) {
                read = is.read(buffer, 0, buffer.length);
                if (read == -1)
                    break;
                os.write(buffer, 0, read);
            }

            return os.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null)
                try {
                    is.close();
                } catch (IOException ignored) {
                }
            if (os != null)
                try {
                    os.close();
                } catch (IOException ignored) {
                }
        }

        return null;
    }
}
