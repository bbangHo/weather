package org.pknu.weather.service.sender;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FcmNotificationSender implements NotificationSender {

    @Value("${fcm.service-account}")
    private String serviceAccountKeyContent;

    @Value("${fcm.project-id}")
    private String projectId;

    @PostConstruct
    public void initialize() throws IOException {

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(new ByteArrayInputStream(serviceAccountKeyContent.getBytes())))
                .setProjectId(projectId)
                .build();

        FirebaseApp.initializeApp(options);
    }

    @Override
    public void send(NotificationMessage message) {

        if (!(message instanceof FcmMessage fcmMessage)) {
            log.warn("푸시 발송 실패: FcmSender는 FcmMessage만 지원. 입력 타입: {}",
                    message != null ? message.getClass().getName() : "null");
            throw new IllegalArgumentException("FcmSender는 FcmMessage만을 지원합니다.");
        }

        try {
            FirebaseMessaging.getInstance().send(buildFcmMessage(fcmMessage));
            log.debug("FCM 메시지 전송 성공");
        } catch (FirebaseMessagingException e) {
            log.warn("푸시 전송 실패: {}", e.getMessage());
        }
    }

    private Message buildFcmMessage(FcmMessage message) {
        return Message.builder()
                .setToken(message.getFcmToken())
                .setNotification(Notification.builder()
                        .setTitle(message.getAlarmTitle())
                        .setBody(message.getAlarmMessage())
                        .build())
                .setAndroidConfig(AndroidConfig.builder()
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .build())
                .setApnsConfig(ApnsConfig.builder()
                        .putHeader("apns-priority", "10")
                        .setAps(Aps.builder()
                                .setBadge(1)
                                .setSound("default")
                                .build())
                        .build())
                .build();
    }
}
