package chatapp.numrah.com.chatapp.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import chatapp.numrah.com.chatapp.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
    EditText text;
    ProgressBar loadingBar;
    android.support.v7.widget.Toolbar toolbar;
    AppLogger logger;
    ImageView chatSendButton;
    RecyclerView chatView;
    BroadcastReceiver chatReceiver;
    TextView username;
    ChatAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logger = new AppLogger();
        setContentView(R.layout.activity_chat);
        initViews();
    }

    private void initViews(){
        Window window = this.getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(this.getColor(R.color.colorChatPrimary));
        //Perform toolbar related actions
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //end toolbar related action
        text = findViewById(R.id.chat_box);
        text.setEnabled(false);

        loadingBar = findViewById(R.id.chat_loading_bar);
        chatSendButton = findViewById(R.id.chat_send_button);
        username = findViewById(R.id.toolbar_username);
        chatSendButton.setOnClickListener(sendChatListener);
        chatSendButton.setEnabled(false);
        loadingBar.setVisibility(View.VISIBLE);
        chatView = findViewById(R.id.chat_recycle_view);
        chatView.setVisibility(View.GONE);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setStackFromEnd(false);
        LinearLayoutManager.class.getClassLoader();
        chatView.setLayoutManager(mLayoutManager);
        ArrayList<ChatMessage> dataSet = new ArrayList<>();
        adapter = new ChatAdapter(dataSet);
        chatView.setAdapter(adapter);
        chatReceiver = new MessageBroadcastReceiver();
        IntentFilter broadcastFilter = new IntentFilter();
        broadcastFilter.addAction(AppConstants.BROADCAST_ACTION);
        this.registerReceiver(chatReceiver, broadcastFilter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(chatReceiver);
        deleteChatData();
    }

    private void deleteChatData(){
        AppUtil.getInstance().clearPreviousSessionData();
        loadingBar.setVisibility(View.VISIBLE);
        chatView.setVisibility(View.GONE);
        text.setEnabled(false);
        chatSendButton.setEnabled(false);
    }
    private void startChat(){
        loadingBar.setVisibility(View.GONE);
        chatView.setVisibility(View.VISIBLE);
        text.setEnabled(true);
        chatSendButton.setEnabled(true);
        text.requestFocus();
        showKeyboard();
        username.setText(ChatAppData.getInstance(this).getString(AppConstants.FRIEND_USER_ID));
    }

    private void storeChatdata(JSONObject message){
        try {
            Context context = this;
            String chatUsername = message.getString(AppConstants.USER_ID);
            String chatId = message.getString(AppConstants.CHAT_ID);
            ChatAppData.getInstance(context).putString(AppConstants.FRIEND_USER_ID, chatUsername);
            ChatAppData.getInstance(context).putString(AppConstants.CHAT_ID, chatId);
        }catch (Exception exp){
            logger.error("StoreChatData : error while extracting chat data from the message");
            logger.error(exp.toString());
        }
    }

    private View.OnClickListener sendChatListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String chatText = text.getText().toString();
            if(chatText.isEmpty() || chatText.equals("")){
                Toast.makeText(ChatActivity.this, "Please enter the message to send ", Toast.LENGTH_SHORT).show();
            }else{
                logger.info(" Chat Txt : "+chatText);
                String chatId = generateChatId();
                String friendId = ChatAppData.getInstance(ChatActivity.this).getString(AppConstants.FRIEND_USER_ID);
                String timeStamp = String.valueOf(System.currentTimeMillis());
                JSONObject message = new JSONObject();
                try {
                    message.put(AppConstants.SERVERMSG_TO, friendId);
                    message.put(AppConstants.SERVERMSG_CHAT_ID, chatId);
                    message.put(AppConstants.SERVERMSG_CHATMSG, chatText);
                    message.put(AppConstants.SERVERMSG_TIMESTAMP, timeStamp);
                    SocketListener.getInstance().sendMessageToServer(AppConstants.SERVERMSG_MSGTYPE_MESSAGE_SEND, message);
                    ChatMessage msg = new ChatMessage(chatId,chatText, 0,Long.valueOf(timeStamp), true);
                    addChatMessage(msg);
                    chatView.smoothScrollToPosition(chatView.getAdapter().getItemCount() - 1);
                }catch (JSONException exp){
                    logger.error(" An error occured while forming json to send message");
                    logger.error(exp.toString());
                }
                text.setText("");
            }
        }
    };

    private void showKeyboard(){
        text.requestFocus();
        text.postDelayed(new Runnable(){
                               @Override public void run(){
                                   InputMethodManager keyboard=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                   keyboard.showSoftInput(text,0);
                               }
                           },200);
    }

    private String generateChatId(){
      return AppUtil.getInstance().getAlphaNumericString(AppConstants.messageIdSize);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_skip){
            deleteChatData();
            AppUtil.getInstance().sendMatchingMessage(this);
        }
        return super.onOptionsItemSelected(item);
    }

    public class MessageBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            logger.info(" BroadcastMessage received ");

            try {
                JSONObject message = new JSONObject(intent.getStringExtra(AppConstants.BROADCAST_DATA));
                logger.info(" The message from intent is "+message.toString());
                String msgType = message.getString("MsgType");
                if(msgType.equals(AppConstants.MSG_TYPE_MATCHED)){
                    storeChatdata(message);
                    startChat();
                }else if(msgType.equals(AppConstants.MSG_TYPE_CHAT)){
                    message.put(AppConstants.isMe, false);
                    addChatMessage(message);
                    AppUtil.getInstance().sendAck(message);
                }else if(msgType.equals(AppConstants.MSG_TYPE_ACK)){
                    updateAck(msgType, message);
                }else if(msgType.equals(AppConstants.MSG_TYPE_SEEN)){
                    updateAck(msgType, message);
                }
            }catch (JSONException exp){
                logger.error(" Error while parsing data from the broadcast");
                logger.error(exp.toString());
            }
        }
    }

    private void updateAck(String msgType, JSONObject message){
        ArrayList<ChatMessage> chats = adapter.getChatMessages();
        try {
            String msgId = message.getString(AppConstants.SERVERMSG_MSGTYPE_MESSAGE_REF);
            for(ChatMessage chat : chats){
                if(chat.getChatId().equalsIgnoreCase(msgId)){
                    if(msgType.equals(AppConstants.MSG_TYPE_ACK)){
                        if(chat.getMessageState() != 2) {
                            chat.setMessageState(1);
                        }
                    }else if(msgType.equals(AppConstants.MSG_TYPE_SEEN)){
                        chat.setMessageState(2);
                    }
                }
            }
        }catch (JSONException exp){
            logger.error(" UpdateAck : Error while updating message stauts");
            logger.error(exp.toString());
        }
        adapter.notifyDataSetChanged();
    }
    private void addChatMessage(JSONObject message){
        ChatMessage msg = new ChatMessage();
        try {
            msg.setMe(message.getBoolean(AppConstants.isMe));
            msg.setTimeStamp(Long.valueOf(message.getString(AppConstants.SERVERMSG_TIMESTAMP)));
            msg.setMessage(message.getString(AppConstants.SERVERMSG_CHATMSG));
            msg.setChatId(message.getString(AppConstants.SERVERMSG_CHAT_ID));
            addChatMessage(msg);
        }catch (JSONException exp){
            logger.error(" Error while adding chat message");
            logger.error(exp.toString());
        }
    }

    private void addChatMessage(ChatMessage chatMessage){
        adapter.addChat(chatMessage);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ChatAppData.getInstance(this).putBoolean(AppConstants.ACTIVTIY_FOREGROUND, true);
        AppUtil.getInstance().handleWaitingMessages();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ChatAppData.getInstance(this).putBoolean(AppConstants.ACTIVTIY_FOREGROUND, false);
    }


}
