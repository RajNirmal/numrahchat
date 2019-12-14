package chatapp.numrah.com.chatapp;

public class AppConstants {
    //Constants for database
    public static final String MATCH_SELECTED = "MATCH_SELECTED";
    public static final String SESSION_ID = "sessionId";
    public static final String SERVER_ID = "serverId";
    public static final String IS_SESSION_READY = "isSessionReady";
    public static final String IS_PREMIUM = "premium";
    public static final String ALGOS = "algos";
    public static final String CHAT_ID = "chatId";
    public static final String USER_ID = "udid";
    public static final String FRIEND_USER_ID = "friendudid";

    //Constants for sockets
    public static int uuidSize = 10;
    public static int messageIdSize = 6;

    //message types constants
    public static final String MSG_TYPE_SESSION = "session";
    public static final String MSG_TYPE_STATUS = "status";
    public static final String MSG_TYPE_LEAVE = "leave";
    public static final String MSG_TYPE_MATCHED = "matched";
    public static final String MSG_TYPE_SYNC = "sync";
    public static final String MSG_TYPE_CHAT = "chat";

    //Broadcast intent
    public static final String BROADCAST_ACTION = "numrah.com.chatapp.message.broadcast";
    public static final String BROADCAST_DATA = "message";

    //Sending message constants
    public static final String SERVERMSG_TO = "to";
    public static final String SERVERMSG_FRIENDUDID = "id";
    public static final String SERVERMSG_CHATMSG = "content";
    public static final String SERVERMSG_TIMESTAMP = "ts";
    public static final String SERVERMSG_ALGO = "algo";
    public static final String SERVERMSG_GENDER = "gender";
    public static final String SERVERMSG_MSGTYPE_MATCH = "match";
    public static final String SERVERMSG_MSGTYPE_MESSAGESEND = "send";

}
