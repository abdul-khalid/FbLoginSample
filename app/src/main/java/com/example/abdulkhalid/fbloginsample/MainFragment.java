package com.example.abdulkhalid.fbloginsample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.util.Arrays;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment {

    private CallbackManager mCallbackManager;
    private TextView mtextDetial;
    private AccessToken accessToken;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private String token;

    public MainFragment() {
    }

    private FacebookCallback<LoginResult> mCallBack = new FacebookCallback<LoginResult>() {

        @Override
        public void onSuccess(LoginResult loginResult) {

            accessToken = loginResult.getAccessToken();
            token = accessToken.getToken();
            Toast.makeText(getActivity(), "Accesstoken = " + token, Toast.LENGTH_SHORT).show();
            Profile profile = Profile.getCurrentProfile();
            DisplayWelcomeMessage(profile, token);

            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {
                            // Application code
                            Log.v("LoginActivity", response.toString());
                            Toast.makeText(getActivity(), "response: " + response.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,gender, birthday,likes");
            request.setParameters(parameters);
            request.executeAsync();


        }

        @Override
        public void onCancel() {
            Toast.makeText(getActivity(), "Error on cancel", Toast.LENGTH_SHORT).show();
            Log.v("LoginActivity", "cancel");
        }


        @Override
        public void onError(FacebookException e) {
            Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
            Log.v("LoginActivity", e.getCause().toString());
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {

            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                DisplayWelcomeMessage(newProfile, token);
            }
        };

        accessTokenTracker.startTracking();
        profileTracker.startTracking();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button);
        mtextDetial = (TextView) view.findViewById(R.id.textView);

        loginButton.setReadPermissions(Arrays.asList("user_friends, email, user_likes, user_birthday"));
        loginButton.setFragment(this);

        loginButton.registerCallback(mCallbackManager, mCallBack);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        Profile profile = Profile.getCurrentProfile();
        //token = accessToken.getToken();
        DisplayWelcomeMessage(profile, token);

    }

    private void DisplayWelcomeMessage(Profile profile, String token) {

        if (profile != null) {
            mtextDetial.setText("Welcome " + profile.getName() + "\n accesstoken=" + token);
        } else {
            mtextDetial.setText("Profile is null");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }
}
