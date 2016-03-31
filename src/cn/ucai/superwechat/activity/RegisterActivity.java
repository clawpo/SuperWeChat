/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.ucai.superwechat.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;

import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.bean.UserBean;
import cn.ucai.superwechat.listener.OnSetAvatarListener;
import cn.ucai.superwechat.utils.NetUtil;
import cn.ucai.superwechat.utils.Utils;

/**
 * 注册页
 * 
 */
public class RegisterActivity extends BaseActivity {
	public static final String TAG = RegisterActivity.class.getName();
	private EditText metUserName;
	private EditText metPassword;
	private EditText metConfirmPassword;
	private EditText metNick;
	
	private ImageView mivAvatar;

	RegisterActivity mContext;
	
	OnSetAvatarListener mOnSetAvatarListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		mContext=this;
		initView();
		setListener();
	}

	private void setListener() {
		setRegisterClickListener();
		setLoginClickListener();
		setUserAvatarClickListener();
	}

    /**
     * 设置头像的view单击事件监听
     */
    private void setUserAvatarClickListener() {
        findViewById(R.id.layout_user_avatar).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            try {
                mOnSetAvatarListener=new OnSetAvatarListener(RegisterActivity.this,R.id.layout_register,getUserName(),"user_avatar");
            } catch (Exception e) {
                e.printStackTrace();
            }
            }
        });
    }

    private void setLoginClickListener() {
        findViewById(R.id.btnLogin).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext,LoginActivity.class));
            }
        });
    }


	/**
	 * 注册
	 */
	public void setRegisterClickListener() {
        findViewById(R.id.btnRegister).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                final String username = metUserName.getText().toString().trim();
                final String nick = metNick.getText().toString().trim();
                final String pwd = metPassword.getText().toString().trim();
                String confirm_pwd = metConfirmPassword.getText().toString().trim();
                if (TextUtils.isEmpty(username)) {
                    metUserName.requestFocus();
                    metUserName.setError(mContext.getResources().getString(R.string.User_name_cannot_be_empty));
                    return;
                } else if(!username.matches("[\\w][\\w\\d_]+")){
                    metUserName.requestFocus();
                    metUserName.setError("账号只能包含英文、数字和下划线");
                    return;
                } else if (TextUtils.isEmpty(nick)) {
                    metNick.requestFocus();
                    metNick.setError(mContext.getResources().getString(R.string.Nick_name_cannot_be_empty));
                    return;
                } else if (TextUtils.isEmpty(pwd)) {
                    metPassword.requestFocus();
                    metPassword.setError(mContext.getResources().getString(R.string.Password_cannot_be_empty));
                    return;
                } else if (TextUtils.isEmpty(confirm_pwd)) {
                    metConfirmPassword.requestFocus();
                    metConfirmPassword.setError(mContext.getResources().getString(R.string.Confirm_password_cannot_be_empty));
                    return;
                } else if (!pwd.equals(confirm_pwd)) {
                    metConfirmPassword.setError(mContext.getResources().getString(R.string.Two_input_password));
                    return;
                }
                new RegisterTask(username,nick, pwd).execute();
            }
        });
	}
    private void initView() {
		metUserName = getViewById(R.id.etUserName);
		metPassword = getViewById(R.id.etPassword);
		metConfirmPassword = getViewById(R.id.etConfirmPassword);
		metNick=getViewById(R.id.etNick);
		mivAvatar=getViewById(R.id.iv_avatar);
	}

	public void back(View view) {
		finish();
	}

	public String getUserName() throws Exception{
		String userName = metUserName.getText().toString();
		if(TextUtils.isEmpty(userName)){
			metUserName.setError(getResources().getString(R.string.User_name_cannot_be_empty));
			metUserName.requestFocus();
			throw new Exception(getResources().getString(R.string.User_name_cannot_be_empty));
		}
		return userName;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i(TAG, "onActivityResult-resultCode="+resultCode);
		if(resultCode != RESULT_OK){
			return;
		}
		try {
			String userName = getUserName();
            /*
             * 设置拍照或从相册获取图片后返回的结果
             * @param requestCode:请求码
             * @param data:返回的intent
             * @param ivAvatar：显示头像的ImageView
             * @param userName：注册窗口输入的账号
             */
			mOnSetAvatarListener.setAvatar(requestCode, data, mivAvatar);
		} catch (Exception e) {
			Log.i(TAG, "onActivityResult-e="+e.getMessage());
			e.printStackTrace();
		}
	}


	/**
	 * 注册的线程类
	 */
    class RegisterTask extends AsyncTask<Void,Void,EaseMobException>{
        String userName,nick,password;
        ProgressDialog dialog;

		/**
		 * 向应用服务器注册，注册成功后，再向环信服务器注册
		 * @param userName
		 * @param nick
		 * @param password
		 */
		public RegisterTask(String userName,String nick,String password){
			super();
            this.userName = userName;
            this.nick = nick;
            this.password = password;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(mContext);
            dialog.setMessage(getResources().getString(R.string.Is_the_registered));
            dialog.show();
        }

        @Override
        protected EaseMobException doInBackground(Void... params) {
			EaseMobException errorEaseMobException = null;
            UserBean user = new UserBean(userName,nick,password);
            try {
                boolean isSuccess = NetUtil.register(user);
                if (isSuccess) {
                    isSuccess = NetUtil.uploadAvatar(mContext,"user_avatar",userName);
                    if(isSuccess) {
                        // 调用sdk注册方法
                        EMChatManager.getInstance().createAccountOnServer(userName, password);
                        // 保存用户名
                        SuperWeChatApplication.getInstance().setUserName(userName);
                        SuperWeChatApplication.getInstance().setUserBean(user);
                    }
                    errorEaseMobException = new EaseMobException(getResources().getString(R.string.Registered_successfully));
                } else {
                    errorEaseMobException = new EaseMobException(getResources().getString(R.string.Registration_failed));
                }
            } catch (EaseMobException e){
                errorEaseMobException = e;
                e.printStackTrace();
                NetUtil.unRegister(userName);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return errorEaseMobException;
        }

        @Override
        protected void onPostExecute(EaseMobException result) {
            if (!RegisterActivity.this.isFinishing())
                dialog.dismiss();
			if(result == null){
				Utils.showToast(mContext, getResources().getString(R.string.Registration_failed), 0);
			}else {
                Log.e(TAG,"result.getMessage()="+result.getMessage());
                if (result.getMessage()!=null && result.getMessage().equals(getResources().getString(R.string.Registered_successfully))) {
                    //保存用户
                    SuperWeChatApplication.getInstance().setUserName(userName);
                    Utils.showToast(mContext, getResources().getString(R.string.Registered_successfully), 0);
                    finish();
                } else {
                    int errorCode = result.getErrorCode();
                    Log.e(TAG,"result.getErrorCode()="+result.getErrorCode());
                    if (errorCode == EMError.NONETWORK_ERROR) {
                        Utils.showToast(getApplicationContext(), getResources().getString(R.string.network_anomalies), Toast.LENGTH_SHORT);
                    } else if (errorCode == EMError.USER_ALREADY_EXISTS) {
                        Utils.showToast(getApplicationContext(), getResources().getString(R.string.User_already_exists), Toast.LENGTH_SHORT);
                    } else if (errorCode == EMError.UNAUTHORIZED) {
                        Utils.showToast(getApplicationContext(), getResources().getString(R.string.registration_failed_without_permission), Toast.LENGTH_SHORT);
                    } else if (errorCode == EMError.ILLEGAL_USER_NAME) {
                        Utils.showToast(getApplicationContext(), getResources().getString(R.string.illegal_user_name), Toast.LENGTH_SHORT);
                    } else {
                        Utils.showToast(getApplicationContext(), getResources().getString(R.string.Registration_failed) + result.getMessage(), Toast.LENGTH_SHORT);
                    }
                }
            }
        }
    }

}
