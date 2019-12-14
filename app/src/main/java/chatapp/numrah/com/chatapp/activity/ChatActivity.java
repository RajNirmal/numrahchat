package chatapp.numrah.com.chatapp.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;
import chatapp.numrah.com.chatapp.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Executor;

public class ChatActivity extends AppCompatActivity {
    EditText text;
    View chatMainView;
    ProgressBar loadingBar;
    android.support.v7.widget.Toolbar toolbar;
    AppLogger logger;
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
        chatMainView = findViewById(R.id.chat_main_view);
        loadingBar = findViewById(R.id.chat_loading_bar);
        chatMainView.setVisibility(View.GONE);
        loadingBar.setVisibility(View.VISIBLE);
        IntentFilter broadcastFilter = new IntentFilter();
        broadcastFilter.addAction(AppConstants.BROADCAST_ACTION);
        this.registerReceiver(new messageBroadcastReceiver(), broadcastFilter);
    }

    private void startChat(){
        loadingBar.setVisibility(View.GONE);
        chatMainView.setVisibility(View.VISIBLE);
        text.setEnabled(true);
        text.requestFocus();
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
                    message.put(AppConstants.SERVERMSG_FRIENDUDID, friendId);
                    message.put(AppConstants.SERVER_ID, chatId);
                    message.put(AppConstants.SERVERMSG_CHATMSG, chatText);
                    message.put(AppConstants.SERVERMSG_TIMESTAMP, timeStamp);
                    SocketListener.getInstance().sendMessageToServer(AppConstants.SERVERMSG_MSGTYPE_MESSAGESEND, message);
                }catch (JSONException exp){
                    logger.error(" An error occured while forming json to send message");
                    logger.error(exp.toString());
                }
            }
        }
    };

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
        return super.onOptionsItemSelected(item);
    }

    public class messageBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            logger.info(" BroadcastMessage received ");

            try {
                JSONObject message = new JSONObject(intent.getStringExtra(AppConstants.BROADCAST_DATA));
                logger.info(" The message from intent is "+message.toString());
                String msgType = message.getString("MsgType");
                if(msgType.equals(AppConstants.MSG_TYPE_MATCHED)){
                    startChat();
                }else if(msgType.equals(AppConstants.MSG_TYPE_CHAT)){

                }
            }catch (JSONException exp){
                logger.error(" Error while parsing data from the broadcast");
                logger.error(exp.toString());
            }
        }
    }
}
