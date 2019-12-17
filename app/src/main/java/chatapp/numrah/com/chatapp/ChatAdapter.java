package chatapp.numrah.com.chatapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private ArrayList<ChatMessage> chatMessages;
    private Context context;
    AppLogger logger;

    public ChatAdapter(Context context,ArrayList<ChatMessage> data){
        chatMessages = data;
        this.context =context;
        logger = new AppLogger();
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

        public void setMessageBackground(Drawable background){
          singleMessage.setBackground(background);
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
//        logger.info(" Total Positions : "+getItemCount()+ " Position : "+position);
        CardView singleMessage = viewHolder.singleMessage;
        TextView chatMessage = viewHolder.chatMessage;
        TextView chatTimestamp = viewHolder.chatTimestamp;
        ImageView chatStatus = viewHolder.chatStatus;
        RelativeLayout chatCompleteView = viewHolder.chatCompleteView;
        LinearLayout chatUltimateView = viewHolder.chatUltimateView;
        Long timeStamp = chatMessages.get(position).getTimeStamp();
        chatMessage.setText(chatMessages.get(position).getMessage());
        Drawable box, boxLeftTail, boxRightTail;
//        box = context.getDrawable(R.drawable.chat_box);
//        boxLeftTail = context.getDrawable(R.drawable.zoho_tail_left_nine);
//        boxRightTail = context.getDrawable(R.drawable.zoho_tail_right_nine);
        chatTimestamp.setText(convertTimestampToString(timeStamp));
        int chatSender = chatMessages.get(position).isMe();
//        logger.info(" The chat sender is "+ chatSender);
        if(chatSender == AppConstants.me || chatSender == AppConstants.me_after_me){
            chatUltimateView.setGravity(Gravity.END);
            int chatStatusInt = chatMessages.get(position).getMessageState();
            if(chatStatusInt == 1){
                chatStatus.setImageResource(R.drawable.one_tick);
            }else if(chatStatusInt == 2){
                chatStatus.setImageResource(R.drawable.two_tick);
            }
            if(chatSender == AppConstants.me){
//                singleMessage.setBackgroundResource(R.drawable.tail_right);
            }else{
//                singleMessage.setBackgroundResource(R.drawable.chat_box);
            }
        }else if(chatSender == AppConstants.other || chatSender == AppConstants.other_after_other){
            chatUltimateView.setGravity(Gravity.START);
            chatStatus.setVisibility(View.INVISIBLE);
            if(chatSender == AppConstants.other){
//                singleMessage.setBackgroundResource(R.drawable.tail_left);
            }else{
//                singleMessage.setBackgroundResource(R.drawable.chat_box);
            }
        }else if(chatSender == AppConstants.left){
            chatUltimateView.setGravity(Gravity.CENTER);
            chatStatus.setVisibility(View.GONE);
            chatTimestamp.setVisibility(View.GONE);
            chatMessage.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
            chatMessage.setCompoundDrawablesWithIntrinsicBounds(context.getDrawable(R.drawable.ic_user_left), null, null, null );
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
        notifyItemChanged(chatMessages.size() - 1);
        notifyDataSetChanged();
    }

    public void updateChat(ChatMessage msg){

    }
}
