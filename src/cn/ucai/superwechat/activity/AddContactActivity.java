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
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.easemob.chat.EMContactManager;

import java.util.ArrayList;

import cn.ucai.superwechat.DemoHXSDKHelper;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.applib.controller.HXSDKHelper;
import cn.ucai.superwechat.bean.UserBean;
import cn.ucai.superwechat.data.ApiParams;
import cn.ucai.superwechat.data.GsonRequest;
import cn.ucai.superwechat.data.RequestManager;

public class AddContactActivity extends BaseActivity{
    public static final String TAG = AddContactActivity.class.getName();

	private EditText editText;
	private LinearLayout searchedUserLayout;
	private TextView nameText,mTextView;
	private Button searchBtn;
	private NetworkImageView avatar;
	private InputMethodManager inputMethodManager;
	private String toAddUsername;
	private ProgressDialog progressDialog;

    private Button mbtnAddUser;
	ImageLoader mImageLoader;
    TextView mtvSearchNoting;
    Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mContext = this;
		setContentView(R.layout.activity_add_contact);
		mTextView = (TextView) findViewById(R.id.add_list_friends);
		
		editText = (EditText) findViewById(R.id.edit_note);
		String strAdd = getResources().getString(R.string.add_friend);
		mTextView.setText(strAdd);
		String strUserName = getResources().getString(R.string.user_name);
		editText.setHint(strUserName);
		searchedUserLayout = (LinearLayout) findViewById(R.id.ll_user);
		nameText = (TextView) findViewById(R.id.name);
		searchBtn = (Button) findViewById(R.id.search);
		avatar = (NetworkImageView) findViewById(R.id.avatar);
		inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        mbtnAddUser = (Button) findViewById(R.id.indicator);
        mtvSearchNoting = (TextView) findViewById(R.id.tv_search_noting);
		mImageLoader = RequestManager.getImageLoader();
        setListener();
	}

    private void setListener(){
        searchContactListener();
        addContactListener();
    }
	
	
	/**
	 * 查找contact
	 */
	public void searchContactListener() {
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = editText.getText().toString();
                String saveText = searchBtn.getText().toString();
                if(SuperWeChatApplication.getInstance().getUserName().equals(name)){
                    String str = getString(R.string.not_add_myself);
                    startActivity(new Intent(mContext, AlertDialog.class).putExtra("msg", str));
                    return;
                }

                toAddUsername = name;
                if(TextUtils.isEmpty(toAddUsername)) {
                    String st = getResources().getString(R.string.Please_enter_a_username);
                    startActivity(new Intent(mContext, AlertDialog.class).putExtra("msg", st));
                    return;
                }
                // TODO 从服务器获取此contact,如果不存在提示不存在此用户
                try {
                    String path = new ApiParams()
                            .with(I.KEY_REQUEST, I.REQUEST_FIND_USER)
                            .with(I.User.USER_NAME, toAddUsername)
                            .getUrl(SuperWeChatApplication.SERVER_ROOT);
                    executeRequest(new GsonRequest<UserBean>(path, UserBean.class,
                            responseListener(), errorListener()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
	}

    private Response.Listener<UserBean> responseListener() {
        return new Response.Listener<UserBean>() {
            @Override
            public void onResponse(UserBean user) {
                Log.e(TAG,"user="+user);
                if(user!=null){
                    mtvSearchNoting.setVisibility(View.GONE);
                    //如果已是好友，则跳转到好友个人主页
                    ArrayList<UserBean> contactList = SuperWeChatApplication.getInstance().getContactList();
                    if(contactList.contains(user)){
                        Intent intent = new Intent();
                        intent.setClass(mContext, UserProfileActivity.class);
                        intent.putExtra("username", user.getUserName());
                        intent.putExtra("user", user);
                        mContext.startActivity(intent);
                    }else{
                        //服务器存在此用户，显示此用户和添加按钮
                        searchedUserLayout.setVisibility(View.VISIBLE);
                        nameText.setText(user.getNick()==null?user.getUserName():user.getNick());
                        avatar.setDefaultImageResId(R.drawable.default_avatar);
                        avatar.setErrorImageResId(R.drawable.default_avatar);
                        avatar.setImageUrl(I.DOWNLOAD_AVATAR_URL+user.getAvatar(),mImageLoader);
                    }
                }else{
                    searchedUserLayout.setVisibility(View.GONE);
                    mtvSearchNoting.setVisibility(View.VISIBLE);
                }
            }
        };
    }
	
	/**
	 *  添加contact
	 */
	public void addContactListener(){
        mbtnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((DemoHXSDKHelper) HXSDKHelper.getInstance()).getContactList().containsKey(nameText.getText().toString())){
                    //提示已在好友列表中，无需添加
                    if(EMContactManager.getInstance().getBlackListUsernames().contains(nameText.getText().toString())){
                        startActivity(new Intent(mContext, AlertDialog.class).putExtra("msg", "此用户已是你好友(被拉黑状态)，从黑名单列表中移出即可"));
                        return;
                    }
                    return;
                }
                progressDialog = new ProgressDialog(mContext);
                String stri = getResources().getString(R.string.Is_sending_a_request);
                progressDialog.setMessage(stri);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            //demo写死了个reason，实际应该让用户手动填入
                            String s = getResources().getString(R.string.Add_a_friend);
                            EMContactManager.getInstance().addContact(toAddUsername, s);
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    progressDialog.dismiss();
                                    String s1 = getResources().getString(R.string.send_successful);
                                    Toast.makeText(getApplicationContext(), s1, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (final Exception e) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    progressDialog.dismiss();
                                    String s2 = getResources().getString(R.string.Request_add_buddy_failure);
                                    Toast.makeText(getApplicationContext(), s2 + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();
            }
        });
	}
	
	public void back(View v) {
		finish();
	}
}
