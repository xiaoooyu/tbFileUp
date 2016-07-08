package com.xiaoooyu;

import com.xiaoooyu.model.User;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.xiaoooyu.Main.*;

/**
 * Created by xiaoooyu on 6/30/16.
 */
public class SendChatMessage {

    static final String PATH = API_SERVER + "rooms/%s/activities";

    void invoke(String roomId, String message) {
        try {
            final String path = String.format(PATH, roomId);
            final HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(path).openConnection();

            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.addRequestProperty("Content-Type", "application/json");

            User user = User.getInstance();
            httpURLConnection.setRequestProperty(AUTH_HEADER, String.format("OAuth2 %s", user.getAccessToken()));

            String postBody = "{\"content\": \"" + message + "\"}";
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
                System.out.println("post chat message success");
                input = httpURLConnection.getInputStream();
            } else {
                System.out.println("post chat message failed");
                input = httpURLConnection.getErrorStream();
            }

            String inputString = convertStreamToString(input);
            System.out.println(inputString);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
