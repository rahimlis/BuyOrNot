package io.hacksters.buyornot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class WelcomeActivity extends AppCompatActivity {

    private static final String TAG = "WelcomeActivity";
    private static final String FB_TOKEN = "FB_TOKEN";
    private  CallbackManager callbackManager;

    // change this
    private Button button;
    private Context context;
    // change this
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        FacebookSdk.sdkInitialize(getApplicationContext());

        context=this;
        button = (Button) findViewById(R.id.post);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,ActivityPostImage.class);
                startActivity(intent);
            }
        });

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "io.hacksters.buyornot",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        ignored.printStackTrace();
        } catch (NoSuchAlgorithmException ignored) {
            ignored.printStackTrace();
        }

        callbackManager = CallbackManager.Factory.create();
        final LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);

        loginButton.setReadPermissions("user_friends","user_photos");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG,loginResult.getAccessToken().getToken());
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(WelcomeActivity.this);
                sp.edit().putString(FB_TOKEN,loginResult.getAccessToken().getToken()).apply();
                Intent intent = new Intent(WelcomeActivity.this,MainActivity.class);
                startActivity(intent);;
                finish();
            }

            @Override
            public void onCancel() {
            Log.d(TAG,"CAncelled");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG,"error "+error);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,
                resultCode, data);
    }
}
