package chatapp.numrah.com.chatapp;

public class ChatMessage {

    private String chatId;
    private String message;
    // 0 - Not sent
    // 1 - Ack - single tick
    // 2 - Seen - double tick
    private int messageState;
    private long timeStamp;
    private boolean isMe;

    public ChatMessage(){

    }

    public ChatMessage(String chatId, String msg, int msgState, long timeStamp, boolean isMe){
        this.chatId = chatId;
        message = msg;
        messageState = msgState;
        this.timeStamp = timeStamp;
        this.isMe = isMe;
    }
    public boolean isMe() {
        return isMe;
    }

    public void setMe(boolean me) {
        isMe = me;
    }



    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getMessageState() {
        return messageState;
    }

    public void setMessageState(int messageState) {
        this.messageState = messageState;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
}
