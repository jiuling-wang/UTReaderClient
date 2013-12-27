package com.example.testutreader;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import com.example.testutreader.utility.StoreUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.plus.PlusClient;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;


public class UserSettingActivity extends Activity implements OnClickListener,
PlusClient.ConnectionCallbacks, PlusClient.OnConnectionFailedListener {
	private final String TAG="UserSettingActivity";
	private static final int DIALOG_GET_GOOGLE_PLAY_SERVICES = 1;

    private static final int REQUEST_CODE_SIGN_IN = 1;
    private static final int REQUEST_CODE_GET_GOOGLE_PLAY_SERVICES = 2;

    private PlusClient mPlusClient;
    private SignInButton mSignInButton;
    private View mSignOutButton;
    private ConnectionResult mConnectionResult;
    private CheckBox universiBox,sportsBox,fashionBox,gamesBox,musicBox,foodBox,booksBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);

        mPlusClient = new PlusClient.Builder(this, this, this)
        		.setActions(
        		        "http://schemas.google.com/AddActivity", "http://schemas.google.com/BuyActivity")
        		        .build();
        Log.e(TAG,"mark sign in button");
        universiBox = (CheckBox)findViewById(R.id.check_university);
        sportsBox = (CheckBox)findViewById(R.id.check_sports);
        fashionBox = (CheckBox)findViewById(R.id.check_fashion);
        gamesBox =(CheckBox)findViewById(R.id.check_game_tech);
        musicBox =(CheckBox)findViewById(R.id.check_music);
        foodBox =(CheckBox)findViewById(R.id.check_food);
        booksBox = (CheckBox)findViewById(R.id.check_books);

        mSignInButton = (SignInButton) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(this);
        mSignOutButton = findViewById(R.id.sign_out_button);
        mSignOutButton.setOnClickListener(this);
        mSignOutButton.setBackgroundColor(getResources().getColor(R.color.google_sign_in_color));
        Button mSettingsButton = (Button)findViewById(R.id.save_settings);
        mSettingsButton.setOnClickListener(this);
        Button mBackButton = (Button)findViewById(R.id.settings_back);
        mBackButton.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        mPlusClient.connect();
    }

    @Override
    public void onStop() {
    	mPlusClient.disconnect();
        super.onStop();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.sign_in_button:
            	if (mConnectionResult == null) {
            		Log.e(TAG,"mark mConnectionResult = null");
            		mPlusClient.connect();
                } else {
                    try {
                        mConnectionResult.startResolutionForResult(this, REQUEST_CODE_SIGN_IN);
                    } catch (SendIntentException e) {
                        // Try connecting again.
                        mPlusClient.connect();
                    }
                }
                break;
            case R.id.sign_out_button:
                if (mPlusClient.isConnected()) {
                	
                    mPlusClient.clearDefaultAccount();
                    mPlusClient.disconnect();
                    mPlusClient.connect();
                    updateButtons(false /* isSignedIn */);
                }
                break;
            case R.id.settings_back:{
            	Intent intent = new Intent(UserSettingActivity.this, MainActivity.class);
        		startActivity(intent);
        		break;
            }
            case R.id.save_settings:
            	if (mPlusClient.isConnected() == false){
            		Toast.makeText(getApplicationContext(), "Please sign in first", Toast.LENGTH_SHORT).show();
            	}else{
            		String s = "";
            		
            		if (universiBox.isChecked()){
            			s =  s + StoreUtil.CATEGORY_NAME[0]+",";
            		}
            		if (sportsBox.isChecked()){
            			s =  s + StoreUtil.CATEGORY_NAME[1]+",";
            		}
            		if (fashionBox.isChecked()){
            			s =  s + StoreUtil.CATEGORY_NAME[2]+",";
            		}
            		if (gamesBox.isChecked()){
            			s = s + StoreUtil.CATEGORY_NAME[3]+",";
            		}
            		if (musicBox.isChecked()){
            			s = s + StoreUtil.CATEGORY_NAME[4]+",";
            		}
            		if (foodBox.isChecked()){
            			s = s + StoreUtil.CATEGORY_NAME[5]+",";
            		}
            		if (booksBox.isChecked()){
            			s = s + StoreUtil.CATEGORY_NAME[6]+",";
            		}
            		if (s.equals("")){
            			Toast.makeText(getApplicationContext(), "Please choose at least one", Toast.LENGTH_SHORT).show();
            		}else{
            			String url = "http://jiuling-utreader.appspot.com/postTopics";
                		url = url + "?topics="+s+"&username="+StoreUtil.currentUser;
                		new PostTopicsAsyncTack().execute(url);
            		}
            		//StoreUtil.TOPIC_PREFERENCE.put(mPlusClient.getAccountName(), s);
            		// post result to web
            		
            	}
           
        }
    }

    private class PostTopicsAsyncTack extends AsyncTask<String, Integer, Integer>{

		@Override
		protected Integer doInBackground(String... params) {
			HttpClient c = new DefaultHttpClient();        
            HttpPost p = new HttpPost(params[0]);        
            try {
				c.execute(p);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            return 1;
			
		}
		protected void onPostExecute(Integer res){
			if (res == 1){
				Toast.makeText(getApplicationContext(), "Thanks!", Toast.LENGTH_SHORT).show();
        		
			}
			
		}
    	
    }
    
    
    @Override
    protected Dialog onCreateDialog(int id) {
        if (id != DIALOG_GET_GOOGLE_PLAY_SERVICES) {
            return super.onCreateDialog(id);
        }

        int available = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (available == ConnectionResult.SUCCESS) {
            return null;
        }
        if (GooglePlayServicesUtil.isUserRecoverableError(available)) {
            return GooglePlayServicesUtil.getErrorDialog(
                    available, this, REQUEST_CODE_GET_GOOGLE_PLAY_SERVICES);
        }
        return new AlertDialog.Builder(this)
                .setMessage("error")
                .setCancelable(true)
                .create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SIGN_IN
                || requestCode == REQUEST_CODE_GET_GOOGLE_PLAY_SERVICES) {
            if (resultCode == RESULT_OK && !mPlusClient.isConnected()
                    && !mPlusClient.isConnecting()) {
                // This time, connect should succeed.
                mPlusClient.connect();
            }
        }
    }


    @Override
    public void onConnected(Bundle connectionHint) {
        String currentPersonName = mPlusClient.getAccountName() != null
                ? mPlusClient.getAccountName()
                : "Unknown";
        StoreUtil.currentUser = currentPersonName;
        updateButtons(true /* isSignedIn */);
       
    }

    @Override
    public void onDisconnected() {
        StoreUtil.currentUser = "Anonymous";
        mPlusClient.connect();
        updateButtons(false /* isSignedIn */);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        mConnectionResult = result;
        updateButtons(false /* isSignedIn */);
    }

    private void updateButtons(boolean isSignedIn) {
        if (isSignedIn) {
            mSignInButton.setEnabled(false);
            mSignOutButton.setEnabled(true);
        } else {
            mSignInButton.setEnabled(true);
            mSignOutButton.setEnabled(false);
        }
    }

}
