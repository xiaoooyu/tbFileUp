package com.xiaoooyu;

import com.xiaoooyu.model.Collection;
import com.xiaoooyu.model.Work;

import java.io.File;

public class Main {

    static final String TB_ACCOUNT_KEY = "tbAccount";
    static final String TB_PASSWORD_KEY = "tbPassword";
    static final String WORKPATH_KEY = "workpath";
    static final String TB_PROJECT_KEY = "tbProject";
    static final String TB_FOLDER_KEY = "tbFolder";
    static final String ARCHIVE_KEY = "archive";

    static final String SERVER = "https://www.teambition.com/api/";
    static final String FILE_SERVER = "https://striker.teambition.net/";
    static final String CLIENT_ID = "6b83ecde2891d635908c7990123e1126";

    static final String CLIENT_SECRET = "a29c72906258418c833cebb5205d2121";
    static final String DEF_TBACCOUNT = "android@teambition.com";

    static final String DEF_TBPASSWORD = "Android!23888";
    static final String TB_ACCOUNT = System.getProperty(TB_ACCOUNT_KEY, DEF_TBACCOUNT);
    static final String TB_PASSWORD = System.getProperty(TB_PASSWORD_KEY, DEF_TBPASSWORD);
    static final String TB_PROJECT = System.getProperty(TB_PROJECT_KEY, null);

    static final String TB_FOLDER = System.getProperty(TB_FOLDER_KEY, null);

    static final String WORKPATH = System.getProperty(WORKPATH_KEY, null);
    static final Boolean IS_ARCHIVE = Boolean.parseBoolean(System.getProperty(ARCHIVE_KEY, "false"));

    static final String ACCESS_KEY_DEBUG = "aU0xJBI-xF1Ix6QjgsrZmME10mI=tMQRuT9Df45c8605a6377461c8c7ca8c10c5d0bb0d3699f04e478364decc1074a6aaea30e25ca1361368e9bdafa2c196343418259294b0d6e6a2087f30b3e35f884029c165a3658f810dd6c4e9f1b8380a296b9c720a55078b48d49c06f76203a496387e";
    static final String STRIKER_KEY_DEBUG = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiI1NzUyMTlmNDNhNjFlZjBiMzY2OTFmMTQiLCJleHAiOjE0NjU1MzczMDYsInN0b3JhZ2UiOiJzdHJpa2VyLWh6In0.e-7ktnXdb-nYbYgBYuJUNaypCndct3p4GwlWHjiMKXc";

    static final String ENCODING = "utf-8";
    static final String AUTH_HEADER = "Authorization";

    static final String tempProjectId = "57451bff014396d80f0dd7e5";
    static final String tempParentId = "5757b3f2ec24acd55ae7db20";
    static final String tempVisible = "members";
    static final String tempDownloadUrl = "https://striker.teambition.net/storage/100hf35beeee2a82f591aaa266692aa5fe92?download=DoesNotExist.txt&Signature=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJyZXNvdXJjZSI6Ii9zdG9yYWdlLzEwMGhmMzViZWVlZTJhODJmNTkxYWFhMjY2NjkyYWE1ZmU5MiIsImV4cCI6MTQ2NTUxNjgwMH0.Pz0K3bVWWWeAwnwSbCUYG9d8aCq2y3b0XthyrIHpmPY";
    static final String tempFileCategory = "";
    static final String tempFileKey = "100hf35beeee2a82f591aaa266692aa5fe92";
    static final String tempFileName = "DoesNotExist.txt";
    static final long tempFileSize = 21;
    static final String tempFileType = "txt";
    static final Integer tempImageWidth = null;
    static final Integer tempImageHeight = null;
    static final String tempSource = "strike2";
    static final String tempPreviewUrl = "";

    static final String tempCompressPath = "compress";

    private static final Main SINGLETON = new Main();
    private SignIn signIn = new SignIn();
    private UploadFile uploadFile = new UploadFile();
    private UploadWork uploadWork = new UploadWork();
    private ZipJob zipJob = new ZipJob();

    public static Main getInstance() {
        return SINGLETON;
    }

    public static void main(String[] args) {

        checkProperties();

        if (WORKPATH == null
                || WORKPATH.isEmpty()
                || TB_PROJECT == null
                || TB_FOLDER == null) {
            System.out.println("Properties are not set properly");
            return;
        }

        File folder = new File(WORKPATH);
        if (!folder.exists()) {
            System.out.printf("Source folder doesn't exist. %s\r\n", folder.getAbsolutePath());
            return;
        }

        // sign in
        getInstance().signIn.invoke();

        // zip files
        if (IS_ARCHIVE) {
            getInstance().zipJob.invoke(WORKPATH);
        }

        String[] files = folder.list();
        Collection collection = new Collection();
        collection.set_parentId(TB_FOLDER);
        collection.set_projectId(TB_PROJECT);
        for (String file : files) {
            String filePath = WORKPATH + "/" + file;
            File childFile = new File(filePath);
            if (!childFile.exists() || childFile.isDirectory() || childFile.isHidden()) {
                continue;
            }

            System.out.println(String.format("upload file: %s", file));
            Work work = getInstance().uploadFile.invoke(childFile);
            if (work != null) {
                collection.setWorks(new Work[]{work});
                getInstance().uploadWork.invoke(collection);
            }
            System.out.println(String.format("upload success: %s", file));
        }
    }

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static void checkProperties() {
        System.out.println(String.format("upload account: %s", TB_ACCOUNT));
        System.out.println(String.format("work path: %s", WORKPATH));
        System.out.println(String.format("tb project: %s", TB_PROJECT));
        System.out.println(String.format("tb folder: %s", TB_FOLDER));
    }

    private static void debug() {
        // temporal solution
//        User user = User.getInstance();
//        user.setAccessToken(ACCESS_KEY_DEBUG);
//        user.setStrikerAuth(STRIKER_KEY_DEBUG);

//        getInstance().signIn.invoke();
//        Work work = getInstance().uploadFile.invoke(new File("AndroidJD.pdf"));

//        Collection collection = new Collection();
//        collection.set_projectId(tempProjectId);
//        collection.set_parentId(tempParentId);
//        Work[] works = new Work[]{work};
//        collection.setWorks(works);
//        getInstance().uploadWork.invoke(collection);

//        getInstance().zipJob.invoke(tempCompressPath);

//        System.out.println("hello there");
    }
}