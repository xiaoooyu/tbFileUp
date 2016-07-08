package com.xiaoooyu;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xiaoooyu.model.User;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.xiaoooyu.Main.*;

/**
 * Created by xiaoooyu on 6/30/16.
 */
public class GetProjectChatRoom {
    static final String PATH = API_SERVER + "rooms/projects/%s";

    String invoke(String projectId) {
        try {
            final String path = String.format(PATH, projectId);
            final HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(path).openConnection();

            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.addRequestProperty("Content-Type", "application/json");

            User user = User.getInstance();
            httpURLConnection.setRequestProperty(AUTH_HEADER, String.format("OAuth2 %s", user.getAccessToken()));

            int responseCode = httpURLConnection.getResponseCode();
            System.out.println("response-code: " + responseCode);

            InputStream input;
            if (responseCode / 100 == 2) {
                System.out.println("get project chat room success");
                input = httpURLConnection.getInputStream();
            } else {
                System.out.println("get project chat room failed");
                input = httpURLConnection.getErrorStream();
            }

            String inputString = convertStreamToString(input);
            System.out.println(inputString);

            String roomId = null;
            JsonElement elem = new JsonParser().parse(inputString);

            if (elem.isJsonObject()) {
                JsonObject object = elem.getAsJsonObject();
                roomId = object.get("_id").getAsString();

                System.out.printf("roomId: %s \r\n", roomId);
                System.out.println();
            }

            return roomId;

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
