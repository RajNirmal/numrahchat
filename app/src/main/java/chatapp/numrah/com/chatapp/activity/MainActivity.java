package chatapp.numrah.com.chatapp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.view.View;
import android.widget.ImageView;
import chatapp.numrah.com.chatapp.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity implements View.OnClickListener {

    View boysView, girlsView, allView, acceptButton;
    ImageView boysImage, girlsImage, allImage;
    AppLogger logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logger = new AppLogger();
        setContentView(R.layout.activity_main);
        initViews();
    }

    @Override
    protected void onStart() {
        super.onStart();
        createSocketConnection();
    }

    private void initViews(){
        logger.info("Init views");
        Context con = this;
        boysView = ((MainActivity) con).findViewById(R.id.boys_matching);
        girlsView = ((MainActivity) con).findViewById(R.id.girls_matching);
        allView = ((MainActivity) con).findViewById(R.id.any_matching);
        acceptButton = ((MainActivity) con).findViewById(R.id.start_matching_button);
        boysImage = ((MainActivity) con).findViewById(R.id.boys_matching_image);
        girlsImage = ((MainActivity) con).findViewById(R.id.girls_matching_image);
        allImage = ((MainActivity) con).findViewById(R.id.all_matching_image);
        boysView.setOnClickListener(this);
        boysImage.setOnClickListener(this);
        girlsView.setOnClickListener(this);
        girlsImage.setOnClickListener(this);
        allView.setOnClickListener(this);
        allImage.setOnClickListener(this);
        acceptButton.setOnClickListener(this);
        logger = new AppLogger();
        logger.info("Ending Init views");
    }

    private void createSocketConnection(){
        logger.info("Going to init socket connection");
        AppUtil.getInstance().initSocketConnection(getApplicationContext());
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.boys_matching:
            case R.id.boys_matching_image:
                logger.info("Boys Clicked");
                setSelected(1);
                break;

            case R.id.girls_matching:
            case R.id.girls_matching_image:
                logger.info("Girls Clicked");
                setSelected(2);
                break;

            case R.id.any_matching:
            case R.id.all_matching_image:
                logger.info("All Clicked");
                setSelected(3);
                break;

            case R.id.start_matching_button:
                logger.info("Start Matching");
                sendMatchingMessage();
                break;
        }
    }

    private void sendMatchingMessage(){
        try {
            Context context = getApplicationContext();
            int selectedMatching = ChatAppData.getInstance(context).getInt(AppConstants.MATCH_SELECTED);
            String algo = ChatAppData.getInstance(context).getString(AppConstants.ALGOS);
            logger.info(algo);
            JSONObject messageObj = new JSONObject();
            messageObj.put("algo", new JSONArray(algo).get(0));
            if(selectedMatching == 1){
                messageObj.put("gender", "m");
            }else if(selectedMatching == 2){
                messageObj.put("gender", "f");
            }

            logger.info(" The json being sent is "+ messageObj.toString());
            SocketListener.getInstance().sendMessageToServer("match", messageObj);
            changeActivity();
        }catch (JSONException exp){
            logger.error(exp.toString());
        }
    }

    private void changeActivity(){
        Intent chatIntent = new Intent(this, ChatActivity.class);
        startActivity(chatIntent);
    }

    private void setSelected(int newSelected){
        int oldSelected = ChatAppData.getInstance(getApplicationContext()).getInt(AppConstants.MATCH_SELECTED);
        logger.info(" The old selected data is "+oldSelected);
        if(oldSelected != -1){
            if(oldSelected == 1){
                changeState(boysView, false);
            }else if(oldSelected == 2){
                changeState(girlsView, false);
            }else if(oldSelected == 3){
                changeState(allView, false);
            }
        }

        if(newSelected == 1){
            changeState(boysView, true);
        }else if(newSelected == 2){
            changeState(girlsView, true);
        }else{
            changeState(allView, true);
        }
        ChatAppData.getInstance(getApplicationContext()).putInt(AppConstants.MATCH_SELECTED, newSelected);
    }

    /*
        *Sending false will de select
     */
    private void changeState(View view, boolean state){
        logger.info(" The view id is "+view.getId());
        logger.info(" The state is "+state);
        if(state){
            ((PercentRelativeLayout)view).setBackground(this.getDrawable(R.drawable.ic_polygon_match_select));
        }else{
            ((PercentRelativeLayout)view).setBackground(this.getDrawable(R.drawable.ic_polygon_match));
        }
    }
}
