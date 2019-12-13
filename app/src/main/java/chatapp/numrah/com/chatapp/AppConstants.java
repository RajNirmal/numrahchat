package chatapp.numrah.com.chatapp;

public class AppConstants {
    //Constants for database
    public static final String MATCH_SELECTED = "MATCH_SELECTED";
    public static final String SESSION_ID = "sessionId";
    public static final String SERVER_ID = "serverId";
    public static final String IS_SESSION_READY = "isSessionReady";
    public static final String IS_PREMIUM = "premium";
    public static final String ALGOS = "algos";

    //Constants for sockets
    public static int uuidSize = 10;

    //message types constants
    public static final String MSG_TYPE_SESSION = "session";
    public static final String MSG_TYPE_STATUS = "status";
    public static final String MSG_TYPE_LEAVE = "leave";
    public static final String MSG_TYPE_MATCHED = "matched";
    public static final String MSG_TYPE_SYNC = "sync";

}
