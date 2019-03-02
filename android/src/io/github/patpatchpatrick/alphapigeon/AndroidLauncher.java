package io.github.patpatchpatrick.alphapigeon;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import io.github.patpatchpatrick.alphapigeon.Screens.HighScoreScreen;
import io.github.patpatchpatrick.alphapigeon.resources.DatabaseAndPreferenceManager;
import io.github.patpatchpatrick.alphapigeon.resources.MobileCallbacks;
import io.github.patpatchpatrick.alphapigeon.resources.PlayServices;

import com.badlogic.gdx.backends.android.AndroidGraphics;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;



public class AndroidLauncher extends AndroidApplication implements PlayServices, DatabaseAndPreferenceManager {

    // Google Play Services Variables

    private int RC_SIGN_IN = 1;


    // -- Leaderboard variables
    private static final int RC_LEADERBOARD_UI = 9004;
    private static final String leaderboard = "CgkIyYyG7qMKEAIQAQ";

    // Interface to send callbacks back to libgdx from mobile device
    private MobileCallbacks mobileCallbacks;

    //Database tools
    protected static ContentResolver contentResolver;

    //Google ads
    private final int SHOW_OR_LOAD_INTERSTITIAL_ADS = 2;
    private final int SHOW_BANNER_ADS = 1;
    private final int HIDE_BANNER_ADS = 0;

    //Google Billing
    private InAppBilling mInAppBilling;
    private final int PURCHASE_AD_REMOVAL = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        //Set up config for Android App
        config.useCompass = false;
        config.useImmersiveMode = true;
        useImmersiveMode(true);

        //Initialize In App Billing
        mInAppBilling = new InAppBilling(this, this);

        // Create the layout for the game/ads to share
        RelativeLayout layout = new RelativeLayout(this);

        // Set up Android for app initialization
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        // Create view for libgdx
        View gameView = initializeForView(new AlphaPigeon(this, this), config);

        // Add the libgdx view
        layout.addView(gameView);

        // Add the AdMob view
        RelativeLayout.LayoutParams adParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        adParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        adParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);


        // Hook it all up
        setContentView(layout);

        // Get the content resolver for database
        contentResolver = getContentResolver();

    }

    protected Handler handler = new Handler() {

        //Handler to handle enabling and disabling ads on the UI thread
        @Override
        public void handleMessage(Message msg) {

        }
    };


    private void signInSilently() {

    }

    private void startSignInIntent() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }


    @Override
    protected void onResume() {
        super.onResume();
        //When the app resumes, sign back in
        if (!isSignedIn()) {
            signInSilently();
        }

        //Callback to libgdx game to let it know that the android app has resumed
        //The game will resize the screen appropriately, if this callback isn't used then there are
        //issues with the game screen scaling incorrectly and being stretched
        if (mobileCallbacks != null) {
            mobileCallbacks.appResumed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //gameHelper.onStart(this); // You will be logged in to google play services as soon as you open app , i,e on start
    }

    @Override
    public void signIn() {
        signInSilently();
    }

    @Override
    public void signOut() {


    }

    @Override
    public void submitScore(int highScore, String user) {


        //Submits a high score for a specific user
        //Scores are submitted using the Dreamlo online leaderboard database using an HTTP GET Request

        //Build the url get request string based on user and score
        StringBuilder urlScoreReq = new StringBuilder("http://dreamlo.com/lb/XtrQXD_4BUGkPBmdz2WSUg3OwYKXHfZUqIcuUscCsXUw/add/");
        urlScoreReq.append(user);
        urlScoreReq.append("/");
        urlScoreReq.append(highScore);
        urlScoreReq.append("/");
        String urlString = urlScoreReq.toString();

        URL url = null;

        //CREATE THE URL TO SUBMIT SCORE TO DREAMLO
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            Log.e("URL", "Error in creating URL");
        }

        InputStream stream = null;
        HttpURLConnection connection = null;
        String result = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            // Timeout for reading InputStream arbitrarily set to 3000ms.
            connection.setReadTimeout(3000);
            // Timeout for connection.connect() arbitrarily set to 3000ms.
            connection.setConnectTimeout(3000);
            // For this use case, set HTTP method to GET.
            connection.setRequestMethod("GET");
            // Already true by default but setting just in case; needs to be true since this request
            // is carrying an input (response) body.
            connection.setDoInput(true);
            // Open communications link (network traffic occurs here).
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                Log.e("URLConnectionExc", "Error Code: " + responseCode);
                throw new IOException("HTTP error code: " + responseCode);
            }
            // Retrieve the response body as an InputStream.
            stream = connection.getInputStream();
            if (stream != null) {
                InputStreamReader isr = new InputStreamReader(stream);
                // Use InputStreamReader to get response from network
                int data = isr.read();
                while (data != -1){
                    char current = (char) data;
                    data = isr.read();
                    Log.d("ScoreGET", "" + current);

                }

                stream.close();

            }
        } catch (Exception e) {
            Log.d("ConnectException", "" + e);

        } finally {
            // Disconnect HTTP connection.

            if (connection != null) {
                connection.disconnect();
            }
        }


    }

    @Override
    public void showLeaderboard() {


    }

    @Override
    public void getPlayerCenteredScores() {

    }

    @Override
    public void getTopScores(int scoreType) {

    }

    @Override
    public boolean isSignedIn() {

        return false;
    }

    @Override
    public void showBannerAds(boolean show) {
        //Send message to handler to show or hide banner ads
        handler.sendEmptyMessage(show ? SHOW_BANNER_ADS : HIDE_BANNER_ADS);
    }

    @Override
    public void showOrLoadInterstitialAd() {
        //Send message to handler to show or load interstitial ads
        handler.sendEmptyMessage(SHOW_OR_LOAD_INTERSTITIAL_ADS);
    }

    @Override
    public void purchaseAdRemoval() {
        //Send message to handler to purchase ad removal
        handler.sendEmptyMessage(PURCHASE_AD_REMOVAL);
    }

    @Override
    public void setMobileCallbacks(MobileCallbacks mobileCallbacks) {
        this.mobileCallbacks = mobileCallbacks;
    }

    @Override
    protected void onStop() {
        super.onStop();
        //gameHelper.onStop();
    }


    @Override
    public void insert(float highScore, float lastScore) {

        DatabaseHandler.insert(this, highScore, lastScore);

    }

    @Override
    public void queryHighScores() {

        DatabaseHandler.query(mobileCallbacks);
    }

    @Override
    public float getHighScore() {
        return DatabaseHandler.getHighScore(this);
    }

    @Override
    public float getTotalNumGames() {
        return DatabaseHandler.getTotalNumberOfGames(this);
    }

    private void handleException(Exception e, String details) {
        int status = 0;


        String message = getString(R.string.status_exception_error, details, status, e);

        new AlertDialog.Builder(this)
                .setMessage(message)
                .setNeutralButton(android.R.string.ok, null)
                .show();
    }

    private void hideSystemUi() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }


    @Override
    public void toggleMusicOnOff(Boolean isOn) {

        // Update the settings preference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String musicSettingPreference = getResources().getString(R.string.music_setting_pref);
        sharedPreferences.edit().putBoolean(musicSettingPreference, isOn).commit();

    }

    @Override
    public void toggleGameSoundsOnOff(Boolean isOn) {

        // Update the settings preference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String gameSoundsSettingPreference = getResources().getString(R.string.game_sounds_setting_pref);
        sharedPreferences.edit().putBoolean(gameSoundsSettingPreference, isOn).commit();

    }

    @Override
    public void toggleTouchControlsOnOff(Boolean isOn) {

        // Update the settings preference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String touchSettingPreference = getResources().getString(R.string.touch_setting_pref);
        sharedPreferences.edit().putBoolean(touchSettingPreference, isOn).commit();

    }

    @Override
    public void toggleAccelButtonOnOff(Boolean isOn) {

        // Update the settings preference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String accelSettingPreference = getResources().getString(R.string.accel_setting_pref);
        sharedPreferences.edit().putBoolean(accelSettingPreference, isOn).commit();

    }

    @Override
    public boolean isMusicOn() {
        //Return settings preference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String musicPreference = getResources().getString(R.string.music_setting_pref);
        return sharedPreferences.getBoolean(musicPreference, true);
    }

    @Override
    public boolean isGameSoundsOn() {
        //Return settings preference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String gameSoundsPreference = getResources().getString(R.string.game_sounds_setting_pref);
        return sharedPreferences.getBoolean(gameSoundsPreference, true);
    }

    @Override
    public boolean isTouchControlsOn() {
        //Return settings preference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String touchPreference = getResources().getString(R.string.touch_setting_pref);
        return sharedPreferences.getBoolean(touchPreference, true);
    }

    @Override
    public boolean isAccelButtonOn() {
        //Return settings preference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String accelPreference = getResources().getString(R.string.accel_setting_pref);
        return sharedPreferences.getBoolean(accelPreference, true);
    }

    @Override
    public void toggleMusicVolumeSlider(float value) {

        // Update the settings preference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String musicVolumeSettingPreference = getResources().getString(R.string.music_volume_setting_pref);
        sharedPreferences.edit().putFloat(musicVolumeSettingPreference, value).commit();

    }

    @Override
    public void toggleGameVolumeSlider(float value) {

        // Update the settings preference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String gameVolumeSettingPreference = getResources().getString(R.string.game_volume_setting_pref);
        sharedPreferences.edit().putFloat(gameVolumeSettingPreference, value).commit();

    }

    @Override
    public void toggleTouchSensitivity(float value) {

        // Update the settings preference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String touchSensitivitySettingPreference = getResources().getString(R.string.touch_sensitivity_setting_pref);
        sharedPreferences.edit().putFloat(touchSensitivitySettingPreference, value).commit();

    }

    @Override
    public void toggleAccelSensitivity(float value) {

        // Update the settings preference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String accelSensitivitySettingPreference = getResources().getString(R.string.accel_sensitivity_setting_pref);
        sharedPreferences.edit().putFloat(accelSensitivitySettingPreference, value).commit();

    }

    @Override
    public void toggleFullScreenMode(boolean isOn) {

        // Update the settings preference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String fullScreenSettingPreference = getResources().getString(R.string.full_screen_setting_pref);
        sharedPreferences.edit().putBoolean(fullScreenSettingPreference, isOn).commit();

    }

    @Override
    public float getMusicVolumeSliderValue() {
        //Return settings preference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String musicVolPreference = getResources().getString(R.string.music_volume_setting_pref);
        return sharedPreferences.getFloat(musicVolPreference, 0.5f);
    }

    @Override
    public float getGameVolumeSliderValue() {
        //Return settings preference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String gameVolPreference = getResources().getString(R.string.game_volume_setting_pref);
        return sharedPreferences.getFloat(gameVolPreference, 0.5f);
    }

    @Override
    public float getTouchSensitivitySliderValue() {
        //Return settings preference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String touchSensitivityPreference = getResources().getString(R.string.touch_sensitivity_setting_pref);
        return sharedPreferences.getFloat(touchSensitivityPreference, 0.5f);
    }

    @Override
    public float getAccelSensitivitySliderValue() {
        //Return settings preference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String accelSensitivityPreference = getResources().getString(R.string.accel_sensitivity_setting_pref);
        return sharedPreferences.getFloat(accelSensitivityPreference, 0.5f);
    }

    @Override
    public boolean isFullScreenModeOn() {
        //Return settings preference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String fullScreenPreference = getResources().getString(R.string.full_screen_setting_pref);
        return sharedPreferences.getBoolean(fullScreenPreference, false);
    }

    @Override
    public boolean getAdRemovalPurchasedValue() {
        return mInAppBilling.isAdRemovalPurchased();
    }
}
