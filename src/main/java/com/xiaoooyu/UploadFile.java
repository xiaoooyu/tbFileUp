package com.xiaoooyu;

import com.google.gson.Gson;
import com.xiaoooyu.model.User;
import com.xiaoooyu.model.Work;
import com.xiaoooyu.util.FileUtils;
import com.xiaoooyu.util.MimeUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.xiaoooyu.Main.AUTH_HEADER;
import static com.xiaoooyu.Main.FILE_SERVER;
import static com.xiaoooyu.Main.convertStreamToString;

/**
 * Created by xiaoooyu on 6/5/16.
 */
public class UploadFile {

    static final String PATH = FILE_SERVER + "upload";
    static final String REQUEST_METHOD = "POST";

    static final String crlf = "\r\n";
    static final String twoHyphens = "--";
    static final String boundarySeed =  "*****";
    static final String boundary = twoHyphens + boundarySeed + crlf;

    Work invoke(final File file) {
        Work work = null;

        try {
            final HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(PATH).openConnection();

            httpURLConnection.setUseCaches(false);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod(REQUEST_METHOD);
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("Cache-Control", "no-cache");
            httpURLConnection.setRequestProperty("Content-Type", String.format("multipart/form-data; boundary=%s", boundarySeed));

            User user = User.getInstance();
            httpURLConnection.setRequestProperty(AUTH_HEADER, user.getStrikerAuth());

            OutputStream output = httpURLConnection.getOutputStream();
            DataOutputStream writer = new DataOutputStream(output);

            attachFileParameter(writer, file);

            writer.writeBytes(boundary);
            writer.writeBytes(String.format("Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"%s", "attach", file.getName(), crlf));
            writer.writeBytes(String.format("Content-Type: image/*%s", crlf));
            writer.writeBytes(crlf);

            FileInputStream fileInputStream = new FileInputStream(file);
            int read = 0;
            byte[] buff = new byte[1024];
            while((read = fileInputStream.read(buff)) > 0) {
                writer.write(buff, 0, read);
                writer.flush();
            }
            writer.writeBytes(crlf);
            writer.writeBytes(boundary);

            fileInputStream.close();
            writer.close();


            int responseCode = httpURLConnection.getResponseCode();
            System.out.println("response-code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream input = httpURLConnection.getInputStream();

                String inputString = convertStreamToString(input);
                System.out.println(inputString);

                work = new Gson().fromJson(inputString, Work.class);
            }


        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return work;
    }

    private void attachFileParameter (DataOutputStream writer, File file) throws IOException {
        String extension = FileUtils.getFileExtension(file.getName());
        String mimeType = MimeUtils.guessMimeTypeFromExtension(extension);

        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }

        writer.writeBytes(boundary);
        writer.writeBytes(String.format("Content-Disposition: form-data; name=\"%s\"%s", "type", crlf));
        writer.writeBytes(crlf);
        writer.writeBytes(mimeType);
        writer.writeBytes(crlf);
        writer.flush();
    }
}
