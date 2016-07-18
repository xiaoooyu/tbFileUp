package com.xiaoooyu;

import com.google.gson.Gson;
import com.xiaoooyu.model.Collection;
import com.xiaoooyu.model.User;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.xiaoooyu.Main.*;

/**
 * Created by xiaoooyu on 6/8/16.
 */
public class UploadWork {

    static final String PATH = API_SERVER + "works";

    String invoke(Collection collection) {
        String result = null;
        try {
            final HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(PATH).openConnection();

            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.addRequestProperty("Content-Type", "application/json");

            User user = User.getInstance();
            httpURLConnection.setRequestProperty(AUTH_HEADER, String.format("OAuth2 %s", user.getAccessToken()));

            String postBody = new Gson().toJson(collection, Collection.class);
            System.out.println(postBody);

            OutputStream output = httpURLConnection.getOutputStream();
            InputStream inputBody = new ByteArrayInputStream(postBody.getBytes(ENCODING));

            byte[] buff = new byte[1024];
            int read = -1;
            while ((read = inputBody.read(buff)) != -1) {
                output.write(buff, 0, read);
            }

            int responseCode = httpURLConnection.getResponseCode();
            System.out.println("response-code: " + responseCode);


            InputStream input;
            if (responseCode / 100 == 2) {
                System.out.println("add work success");
                input = httpURLConnection.getInputStream();
            } else {
                System.out.println("add work failed");
                input = httpURLConnection.getErrorStream();
            }

            result = convertStreamToString(input);
            System.out.println(result);

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            return result;
        }
    }
}
