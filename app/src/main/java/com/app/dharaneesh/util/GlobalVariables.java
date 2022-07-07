package com.app.dharaneesh.util;



import java.util.Map;

public class GlobalVariables {

    private static Map<String, Integer> messagesNotificationMap;


    public static Map<String, Integer> getMessagesNotificationMap() {
        return messagesNotificationMap;
    }

    public static void setMessagesNotificationMap(Map<String, Integer> messagesNotificationMap) {
        GlobalVariables.messagesNotificationMap = messagesNotificationMap;
    }

}
