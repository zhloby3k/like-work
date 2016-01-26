package com.example.gbyakov.likework;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gbyakov.likework.sync.LikeWorkSyncAdapter;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.NTLMSchemeFactory;
import org.apache.http.impl.client.DefaultHttpClient;

public class LoginActivity extends AccountAuthenticatorActivity {

    public static final String AUTHTOKEN_TYPE = "like@work";

    private EditText mUserNameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    private UserLoginTask mAuthTask = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AccountManager am = AccountManager.get(LoginActivity.this);
        if (am.getAccountsByType(AUTHTOKEN_TYPE).length > 0) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        setContentView(R.layout.activity_login);

        mUserNameView = (EditText) findViewById(R.id.user_name);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUserNameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String domain = "";
        String username = mUserNameView.getText().toString();
        if (username.indexOf("\\") >= 0) {
            int indexOfSlash = username.indexOf("\\");
            domain = username.substring(0, indexOfSlash);
            username = username.substring(indexOfSlash + 1);
        }
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_password_required));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(username)) {
            mUserNameView.setError(getString(R.string.error_username_required));
            focusView = mUserNameView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            mAuthTask = new UserLoginTask(this, username, password, domain);
            mAuthTask.execute((Void) null);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUserName;
        private final String mDomain;
        private final String mPassword;
        private Integer mStatusCode;
        private Context mContext;

        UserLoginTask(Context context, String username, String password, String domain) {
            mContext    = context;
            mUserName   = username;
            mPassword   = password;
            mDomain     = domain;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

            if (cm.getActiveNetworkInfo() == null || !cm.getActiveNetworkInfo().isConnected()) {
                mStatusCode = 100;
                return false;
            }

            try
            {
                DefaultHttpClient httpclient = new DefaultHttpClient();
                httpclient.getAuthSchemes().register("ntlm", new NTLMSchemeFactory());
                httpclient.getCredentialsProvider().setCredentials(
                        new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                        new NTCredentials(mUserName, mPassword, "", mDomain));

                HttpGet httpGet = new HttpGet(getString(R.string.ws_link)+"?wsdl");
                HttpResponse response = httpclient.execute(httpGet);

                mStatusCode = response.getStatusLine().getStatusCode();

                if (mStatusCode == 200) {
                    AccountManager am = AccountManager.get(LoginActivity.this);
                    Account acc = new Account((mDomain.equals("") ? mUserName : mDomain+"\\"+mUserName),
                            LoginActivity.this.AUTHTOKEN_TYPE);
                    am.addAccountExplicitly(acc, mPassword, null);
                    LikeWorkSyncAdapter.initializeSyncAdapter(mContext);
                }

                return (mStatusCode == 200);
            }
            catch (Exception e)
            {
                mStatusCode = 300;
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;

            if (success) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                showProgress(false);
                if (mStatusCode == 401) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Неверный логин или пароль", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (mStatusCode == 100) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Проверьте подключение к интернету и попробуйте позже", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Проверьте подключение к корпоративной сети и попробуйте позже", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}
