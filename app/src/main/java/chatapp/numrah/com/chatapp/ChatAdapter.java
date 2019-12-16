package chatapp.numrah.com.chatapp;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private ArrayList<ChatMessage> chatMessages;

    public ChatAdapter(ArrayList<ChatMessage> data){
        chatMessages = data;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        CardView singleMessage;
        TextView chatMessage, chatTimestamp;
        ImageView chatStatus;
        RelativeLayout chatCompleteView;
        LinearLayout chatUltimateView;
        public ViewHolder(View myView){
            super(myView);
//            chatCompleteView = myView.findViewById(R.id.chat_recycle_view);
            chatMessage = myView.findViewById(R.id.chat_box_text);
            chatTimestamp = myView.findViewById(R.id.chat_box_timestamp);
            chatStatus = myView.findViewById(R.id.chat_message_status);
            singleMessage = myView.findViewById(R.id.chat_cardview);
            chatUltimateView = myView.findViewById(R.id.chat_main_singleitem_layout);
//            chatCompleteView.setOnClickListener(this);
            chatMessage.setOnClickListener(this);
            chatTimestamp.setOnClickListener(this);
            chatStatus.setOnClickListener(this);
            singleMessage.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){

            }
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public ArrayList<ChatMessage> getChatMessages(){
        return chatMessages;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        CardView singleMessage = viewHolder.singleMessage;
        TextView chatMessage = viewHolder.chatMessage;
        TextView chatTimestamp = viewHolder.chatTimestamp;
        ImageView chatStatus = viewHolder.chatStatus;
        RelativeLayout chatCompleteView = viewHolder.chatCompleteView;
        LinearLayout chatUltimateView = viewHolder.chatUltimateView;
        Long timeStamp = chatMessages.get(position).getTimeStamp();

        chatMessage.setText(chatMessages.get(position).getMessage());

        chatTimestamp.setText(convertTimestampToString(timeStamp));
        if(chatMessages.get(position).isMe()){
            chatUltimateView.setGravity(Gravity.END);
            int chatStatusInt = chatMessages.get(position).getMessageState();
            if(chatStatusInt == 1){
                chatStatus.setImageResource(R.drawable.one_tick);
            }else if(chatStatusInt == 2){
                chatStatus.setImageResource(R.drawable.two_tick);
            }
        }else{
            chatUltimateView.setGravity(Gravity.START);
            chatStatus.setVisibility(View.INVISIBLE);
        }

    }

    private String convertTimestampToString(long timestamp){
        Date date = new Date(timestamp);
        DateFormat formatter = new SimpleDateFormat("HH:mm");
        formatter.setTimeZone(Calendar.getInstance().getTimeZone());
        return formatter.format(date);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View chatSingle = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_single_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(chatSingle);
        return viewHolder;
    }

    public void addChat(ChatMessage msg){
        chatMessages.add(msg);
        notifyDataSetChanged();
    }

    public void updateChat(ChatMessage msg){

    }
}
