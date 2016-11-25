package com.xiaoooyu;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xiaoooyu.model.Collection;
import com.xiaoooyu.model.User;
import com.xiaoooyu.model.Work;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {

    static final String try1 = "2";

    static final String TB_ACCOUNT_KEY = "tbAccount";
    static final String TB_PASSWORD_KEY = "tbPassword";
    static final String WORKPATH_KEY = "workpath";
    static final String TB_PROJECT_KEY = "tbProject";
    static final String TB_FOLDER_KEY = "tbFolder";
    static final String ARCHIVE_KEY = "archive";

    static final String NOTIFY_PROJECT_KEY = "notifyPrj";
    static final String NOTIFY_MSG_KEY = "notifyMsg";

    static final String UPLOAD_KEY = "upload";

    static final String SERVER = "https://www.teambition.com";
    static final String API_SERVER = SERVER + "/api/";
    static final String FILE_SERVER = "https://striker.teambition.net/";

    static final String CLIENT_ID = "6b83ecde2891d635908c7990123e1126";
    static final String CLIENT_SECRET = "a29c72906258418c833cebb5205d2121";

    static final String DEF_TBACCOUNT = "android@teambition.com";
    static final String DEF_TBPASSWORD = "Android!23888";

    static final String ENCODING = "utf-8";

    static final String AUTH_HEADER = "Authorization";

    private static final Main SINGLETON = new Main();

    private SignIn signIn = new SignIn();
    private GetFileToken fileToken = new GetFileToken();

    private UploadFile uploadFile = new UploadFile();
    private UploadWork uploadWork = new UploadWork();
    private ZipJob zipJob = new ZipJob();
    private GetProjectChatRoom getProjectChatRoom = new GetProjectChatRoom();
    private SendChatMessage sendChatMessage = new SendChatMessage();

    private final String tbAccount = System.getProperty(TB_ACCOUNT_KEY, DEF_TBACCOUNT);
    private final String tbPassword = System.getProperty(TB_PASSWORD_KEY, DEF_TBPASSWORD);
    private final String tbProject = System.getProperty(TB_PROJECT_KEY, null);
    private final String tbFolder = System.getProperty(TB_FOLDER_KEY, null);
    private final String workPath = System.getProperty(WORKPATH_KEY, null);

    private final String notifyPrj = System.getProperty(NOTIFY_PROJECT_KEY, null);
    private final String notifyMsg = System.getProperty(NOTIFY_MSG_KEY, null);
    private final Boolean isArchive = Boolean.parseBoolean(System.getProperty(ARCHIVE_KEY, "false"));
    private final Boolean isUpload = Boolean.parseBoolean(System.getProperty(UPLOAD_KEY, "true"));

    private final List<String> uploadWorkKeys = new ArrayList<String>();

    public static Main getInstance() {
        return SINGLETON;
    }

    public static void main(String[] args) {

        if (!getInstance().checkProperties()) {
            return;
        }

        // sign in
        getInstance().signIn();

        if (getInstance().isUpload) {
            File folder = getInstance().prepareWorkFolder();
            if (folder == null) return;

            getInstance().uploadWorks(folder);
        }

        getInstance().sendNotification();
    }

    private void uploadWorks(File folder) {
        String[] files = folder.list();

        Collection collection = new Collection();
        collection.set_parentId(tbFolder);
        collection.set_projectId(tbProject);
        for (String file : files) {
            String filePath = workPath + "/" + file;
            File childFile = new File(filePath);
            if (!childFile.exists() || childFile.isDirectory() || childFile.isHidden()) {
                continue;
            }

            System.out.println(String.format("upload file: %s", file));
            Work work = uploadFile.invoke(childFile);
            if (work != null) {
                collection.setWorks(new Work[]{work});
                String uploadWorkResult = uploadWork.invoke(collection);
                uploadWorkKeys.addAll(parseUploadWorkKey(uploadWorkResult));
            }
            System.out.println(String.format("upload success: %s", file));
        }
    }

    private List<? extends String> parseUploadWorkKey(String uploadWorkResult) {
        ArrayList<String> parseIds = new ArrayList<String>();

        JsonElement jsonElement = new JsonParser().parse(uploadWorkResult);

        if (jsonElement.isJsonArray()) {
            JsonArray array = jsonElement.getAsJsonArray();
            for (int i = 0; i < array.size(); i++) {
                JsonElement childElement = array.get(i);
                if (childElement.isJsonObject()) {
                    JsonObject childObject = childElement.getAsJsonObject();
                    JsonElement idElement = childObject.get("_id");
                    if (idElement.isJsonPrimitive()) {
                        parseIds.add(idElement.getAsString());
                    }
                }

            }
        }

        return parseIds;
    }

    private void sendNotification() {
        if (notifyPrj != null && notifyMsg != null) {
            String roomId = getProjectChatRoom.invoke(notifyPrj);
            if (roomId != null) {
                String link = "";
                if (isUpload) {
                    if (uploadWorkKeys.size() == 1) {
                        link = String.format("%s/project/%s/works/%s/work/%s", SERVER, tbProject, tbFolder, uploadWorkKeys.get(0));
                    } else {
                        link = String.format("%s/project/%s/works/%s", SERVER, tbProject, tbFolder);
                    }
                }
                String msg = String.format("%s %s", notifyMsg, link);
                sendChatMessage.invoke(roomId, msg);
            }
        }
    }

    private File prepareWorkFolder() {
        File folder = new File(getInstance().workPath);
        if (!folder.exists()) {
            System.out.printf("Source folder doesn't exist. %s\r\n", folder.getAbsolutePath());
            return null;
        }

        // zip files
        if (isArchive) {
            zipJob.invoke(workPath);
        }
        return folder;
    }

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public boolean checkProperties() {
        System.out.println(String.format("upload account: %s", tbAccount));
        System.out.println(String.format("work path: %s", workPath));
        System.out.println(String.format("tb project: %s", tbProject));
        System.out.println(String.format("tb folder: %s", tbFolder));

        if (isUpload && (workPath == null || workPath.isEmpty()
                || tbProject == null
                || tbFolder == null)) {
            System.out.println("Properties are not set properly");
            return false;
        }
        return true;
    }

    public void signIn() {
        signIn.invoke(tbAccount, tbPassword);
        fileToken.invoke(User.getInstance());
    }

}
