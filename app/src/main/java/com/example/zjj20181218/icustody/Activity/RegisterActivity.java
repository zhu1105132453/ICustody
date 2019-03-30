package com.example.zjj20181218.icustody.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.zjj20181218.icustody.R;
import com.example.zjj20181218.icustody.javaBean.User;
import com.example.zjj20181218.icustody.util.DialogUtil;
import com.example.zjj20181218.icustody.util.HttpUtil;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Response;

/**
 * Created by Zjj on 2018/12/18.
 */

public class RegisterActivity extends BaseActivity {

    private UserRegisterTask mAuthTask = null;

    // UI references.
    private EditText mEmailView;
    private EditText mUsernameView;
    private EditText mPasswordView;
    private EditText mPasswordViewAgain;
    private View mProgressView;
    private View mRegisterFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mEmailView = (EditText) findViewById(R.id.email);
        mUsernameView = (EditText) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordViewAgain = (EditText) findViewById(R.id.passwordAgain);
        mPasswordViewAgain.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });
        Button mEmailRegisterButton = (Button) findViewById(R.id.email_register_button);
        mEmailRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        mRegisterFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    //注册
    private void attemptRegister() {
        if (mAuthTask != null) {
            return;
        }

        //设置输入框的错误提示为空
        mEmailView.setError(null);
        mUsernameView.setError(null);
        mPasswordView.setError(null);
        mPasswordViewAgain.setError(null);

        //获取用户输入的注册信息
        String email = mEmailView.getText().toString();
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();
        String passwordAgain = mPasswordViewAgain.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }
        //判断两个密码是否一致
        if (!password.equals(passwordAgain)) {
            mPasswordViewAgain.setError("The two passwords don't match");
            focusView = mPasswordViewAgain;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        } else if (TextUtils.isEmpty(username)){
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserRegisterTask(email, username, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        //获取系统定义的时间
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);//跟据参数控制该控件显示或隐藏
        //设置动画渐变效
        mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);//跟据参数控制该控件显示或隐藏
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
    }

    //菜单栏：登录入口
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.login:
                //跳转至登录界面
                ActivityCollector.removeActivity(RegisterActivity.this);
                break;
        }
        return true;
    }

    private class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mUsername;
        private final String mPassword;

        UserRegisterTask(String email, String username, String password) {
            mEmail = email;
            mUsername = username;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            // TODO: attempt authentication against a network service.
            // Simulate network access.
            User user = new User(mEmail, mUsername, mPassword, "register");
            //通过Gson将user对象转换为json数据格式
            Gson gson = new Gson();
            String json = gson.toJson(user);
            //OkHttp同步方式访问后台，验证登录
            String result = null;
            try {
                Response response = HttpUtil.synPostJson(getString(R.string.registerAddress), json);
                if (response != null) {
                    result = response.body().string();
                    return result.equals("success");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                //弹出提示框：注册成功
                final DialogUtil dialogUtil = new DialogUtil(RegisterActivity.this, R.style.MyMiddleDialogStyle, R.layout.dialog_success) {
                    //重写父类方法，如果触碰到dialog外区域则销毁当前活动
                    @Override
                    protected void onTouchOutside() {
                        ActivityCollector.removeActivity(RegisterActivity.this);
                    }
                };
                //设置点击Dialog外部任意区域关闭Dialog
                dialogUtil.setCanceledOnTouchOutside(true);
                dialogUtil.show();
                
            } else {
                mPasswordViewAgain.setError(getString(R.string.error_register));
                mPasswordViewAgain.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}
