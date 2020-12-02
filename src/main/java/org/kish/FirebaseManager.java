package org.kish;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.messaging.*;
import org.kish.dataBase.DataBase;
import org.kish.dataBase.ExpandedDataBase;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class FirebaseManager {
    public FirebaseApp firebaseApp;
    public DataBase<HashSet<String>> notificationUser;

    public boolean isReady = false;

    public FirebaseManager(){
        ExpandedDataBase settings = KishServer.CONFIG;
        this.notificationUser = new DataBase<>("db/notificationUser.json");
        for(String key : this.notificationUser.keySet()){
            this.notificationUser.put(key, new HashSet<>(notificationUser.get(key)));
        }

        settings.put("Firebase_operator_messaging_tokens", new ArrayList<>(), false);
        settings.put("Firebase_path_serviceAccountKey", "serviceAccountKey json파일의 경로를 입력해주세요", false);
        settings.put("Firebase_DatabaseUrl", "Firebase DB주소를 입력해주세요. ex) https://DB이름.firebaseio.com", false);

        Object admins = settings.get("Firebase_operator_messaging_tokens");
        if(admins instanceof ArrayList){
            this.notificationUser.put("operators", new HashSet<>((ArrayList) admins));
        }else{
            this.notificationUser.put("operators", (HashSet<String>) admins);
        }

        String jsonPath = (String) settings.get("Firebase_path_serviceAccountKey");
        File file = new File(jsonPath);
        if(!file.exists()){
            MainLogger.error("Firebase의 serviceAccountKey.json 파일을 찾지 못했습니다.");
            MainLogger.error("현재 설정된 경로는 \"" + jsonPath + "\" 입니다.");
            MainLogger.error("kish2020.json 파일에서 해당 경로를 수정해주세요.");
            MainLogger.error("FirebaseManager 비활성화됨");
            return;
        }

        FileInputStream serviceAccount;
        try {
            serviceAccount = new FileInputStream(jsonPath);
            FirebaseOptions options;
            options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl((String) settings.get("Firebase_DatabaseUrl"))
                    .build();
            this.firebaseApp = FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            MainLogger.error("FirebaseManager 초기화 중 오류 발생", e);
        }
        this.isReady = true;
    }

    public boolean isExistUser(String uid){
        if(this.firebaseApp == null) return false;
        try {
            FirebaseAuth.getInstance().getUser(uid);
        } catch (FirebaseAuthException ignore) {
            return false;
        }
        return true;
    }

    public void sendFCMToAdmin(String title, String content, Map<String, String> data){
        this.sendFCM("operators", title, content, data);
    }

    public void sendFCM(String topic, String title, String content, Map<String, String> data) {
        if(!isReady) {
            MainLogger.warn("Firebase is not ready.");
            return;
        }
        Thread thread = new Thread(() -> {
            FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance();
            HashSet<String> userSet = this.notificationUser.get(topic);
            ArrayList<String> users = new ArrayList<>(userSet);
            if (users.size() < 1) return;

            MulticastMessage message = MulticastMessage.builder()
                    .setAndroidConfig(AndroidConfig.builder()
                            .setTtl(3600 * 1000)
                            .setPriority(AndroidConfig.Priority.NORMAL)
                            .setNotification(AndroidNotification.builder()
                                    .setColor("#344aba")
                                    .build())
                            .build())
                    .setNotification(new Notification(title, content))
                    .putAllData(data)
                    .addAllTokens(users)
                    .build();

            try {
                BatchResponse response = firebaseMessaging.sendMulticast(message);
                if (response.getFailureCount() > 0) {
                    List<SendResponse> responses = response.getResponses();
                    for (int i = 0; i < responses.size(); i++) {
                        if (!responses.get(i).isSuccessful()) {
                            if (responses.get(i).getException().getErrorCode().equals("invalid-argument"))
                                userSet.remove(users.get(i));
                        }
                    }
                }
            } catch (FirebaseMessagingException e) {
                MainLogger.error("", e);
            }
            this.notificationUser.put(topic, userSet);
        });
        thread.start();
    }

    public void addNotificationUser(String topic, String userToken){
        HashSet<String> map = this.notificationUser.getOrDefault(topic, new HashSet<>());
        map.add(userToken);
        this.notificationUser.put(topic, map);
    }

    public void removeNotificationUser(String topic, String userToken){
        HashSet<String> map = this.notificationUser.getOrDefault(topic, new HashSet<>());
        map.remove(userToken);
        this.notificationUser.put(topic, map);
    }

    public boolean isNotificationUser(String topic, String userToken){
        if(!this.notificationUser.containsKey(topic)) return false;
        HashSet<String> map = this.notificationUser.get(topic);
        return map.contains(userToken);
    }
}
