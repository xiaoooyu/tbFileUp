package com.xiaoooyu;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xiaoooyu.model.User;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.xiaoooyu.Main.*;
import static com.xiaoooyu.Main.convertStreamToString;

/**
 * Created by xiaoooyu on 11/25/16.
 */
public class GetFileToken {

    static final String PATH = API_SERVER + "users/me";

    void invoke(User user) {
        try {
            final HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(PATH).openConnection();

            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.addRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty(AUTH_HEADER, String.format("OAuth2 %s", user.getAccessToken()));

            int responseCode = httpURLConnection.getResponseCode();
            System.out.println("response-code: " + responseCode);

            if (responseCode / 100 == 2) {
                System.out.println("login success");

                InputStream input = httpURLConnection.getInputStream();

                String inputString = convertStreamToString(input);
                System.out.println(inputString);

//                JsonElement elem = new JsonParser().parse(new JsonReader(new InputStreamReader(input, ENCODING)));
                JsonElement elem = new JsonParser().parse(inputString);

                if (elem.isJsonObject()) {

                    String strikerAuth = null;

                    JsonObject userObject = elem.getAsJsonObject();
                    strikerAuth = userObject.get(User.STRIKER_AUTH_KEY).getAsString();

                    user.setStrikerAuth(strikerAuth);

                    System.out.printf("strikerAuth: %s \r\n", user.getStrikerAuth());
                    System.out.println();
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
