package com.xiaoooyu;

import com.google.gson.*;
import com.xiaoooyu.model.User;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.xiaoooyu.Main.*;

public class SignIn {

    static final String PATH = SERVER + "oauth2/token";

    void invoke() {
        try {
            final HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(PATH).openConnection();

            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.addRequestProperty("Content-Type", "application/json");

            String postBody = "{\"client_id\": \"" + CLIENT_ID + "\", " +
                    "\"client_secret\": \"" + CLIENT_SECRET + "\", " +
                    "\"email\": \"" + TB_ACCOUNT + "\", " +
                    "\"password\": \"" + TB_PASSWORD + "\"}";
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

            if (responseCode / 100 == 2) {
                System.out.println("login success");

                InputStream input = httpURLConnection.getInputStream();

                String inputString = convertStreamToString(input);
                System.out.println(inputString);

//                JsonElement elem = new JsonParser().parse(new JsonReader(new InputStreamReader(input, ENCODING)));
                JsonElement elem = new JsonParser().parse(inputString);

                if (elem.isJsonObject()) {
                    User user = User.getInstance();
                    String accessToken = null;
                    String strikerAuth = null;

                    JsonObject object = elem.getAsJsonObject();
                    accessToken = object.get(User.ACCESS_TOKEN_KEY).getAsString();

                    JsonObject userObject = object.get(User.USER_ELEM_KEY).getAsJsonObject();
                    strikerAuth = userObject.get(User.STRIKER_AUTH_KEY).getAsString();

                    user.setAccessToken(accessToken);
                    user.setStrikerAuth(strikerAuth);

                    System.out.printf("accessToken: %s \r\n", user.getAccessToken());
                    System.out.printf("strikerAuth: %s \r\n", user.getStrikerAuth());
                    System.out.println();
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}