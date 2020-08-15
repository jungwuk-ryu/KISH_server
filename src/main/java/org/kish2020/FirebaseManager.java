package org.kish2020;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.kish2020.DataBase.ExpandedDataBase;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FirebaseManager {
    public FirebaseApp firebaseApp;

    public FirebaseManager(){
        ExpandedDataBase settings = Kish2020Server.mainSettings;
        settings.put("Firebase_path_serviceAccountKey", "serviceAccountKey json파일의 경로를 입력해주세요", false);
        settings.put("Firebase_DatabaseUrl", "Firebase DB주소를 입력해주세요. ex) https://DB이름.firebaseio.com", false);

        String jsonPath = (String) settings.get("Firebase_path_serviceAccountKey");
        File file = new File(jsonPath);
        if(!file.exists()){
            MainLogger.error("Firebase의 serviceAccountKey.json 파일을 찾지 못했습니다.");
            MainLogger.error("현재 설정된 경로는 \"" + jsonPath + "\" 입니다.");
            MainLogger.error("kish2020.json 파일에서 해당 경로를 수정해주세요.");
            MainLogger.error("FirebaseManager 비활성화됨");
            return;
        }

        FileInputStream serviceAccount = null;
        try {
            serviceAccount = new FileInputStream(jsonPath);
            FirebaseOptions options = null;
            options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl((String) settings.get("Firebase_DatabaseUrl"))
                    .build();
            this.firebaseApp = FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            MainLogger.error("FirebaseManager 초기화 중 오류 발생", e);
        }
    }

    public boolean isExistUser(String uid){
        UserRecord userRecord = null;
        try {
            userRecord = FirebaseAuth.getInstance().getUser(uid);
        } catch (FirebaseAuthException ignore) {
            return false;
        }
        return true;
    }
}
