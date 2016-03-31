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
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;

import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.bean.GroupBean;
import cn.ucai.superwechat.listener.OnSetAvatarListener;
import cn.ucai.superwechat.utils.NetUtil;
import cn.ucai.superwechat.utils.Utils;

public class NewGroupActivity extends BaseActivity {
    public static final String TAG = NewGroupActivity.class.getName();
	private EditText groupNameEditText;
	private ProgressDialog progressDialog;
	private EditText introductionEditText;
	private CheckBox checkBox;
	private CheckBox memberCheckbox;
	private LinearLayout openInviteContainer;
	static final int ACTION_CREATE_GROUP = 100;
	NewGroupActivity mContext;
	OnSetAvatarListener mOnSetAvatarListener;
	ImageView mivAvatar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_new_group);
		initView();
		setListener();
	}


	private void initView() {
		groupNameEditText = (EditText) findViewById(R.id.edit_group_name);
		introductionEditText = (EditText) findViewById(R.id.edit_group_introduction);
		checkBox = (CheckBox) findViewById(R.id.cb_public);
		memberCheckbox = (CheckBox) findViewById(R.id.cb_member_inviter);
		openInviteContainer = (LinearLayout) findViewById(R.id.ll_open_invite);
        mivAvatar = (ImageView) findViewById(R.id.iv_avatar);
	}

	private void setListener() {
	    setOnCheckchangedListener();
	    setSaveGroupClickListener();
	    setGroupIconClickListener();
    }

	private void setGroupIconClickListener() {
        findViewById(R.id.layout_group_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupName = groupNameEditText.getText().toString();
                Log.e(TAG,"groupName="+groupName);
                mOnSetAvatarListener=new OnSetAvatarListener(mContext, R.id.layout_new_group,groupName,"group_icon");
            }
        });
	}

	private void setSaveGroupClickListener() {
        findViewById(R.id.btnSaveGroup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str6 = getResources().getString(R.string.Group_name_cannot_be_empty);
                String name = groupNameEditText.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    Intent intent = new Intent(mContext, AlertDialog.class);
                    intent.putExtra("msg", str6);
                    startActivity(intent);
                } else {
                    // 进通讯录选人
                    startActivityForResult(new Intent(mContext, GroupPickContactsActivity.class).putExtra("groupName", name), ACTION_CREATE_GROUP);
                }
            }
        });
	}

	private void setOnCheckchangedListener() {
        checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked){
                openInviteContainer.setVisibility(View.INVISIBLE);
            }else{
                openInviteContainer.setVisibility(View.VISIBLE);
            }
            }
        });
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if(requestCode == ACTION_CREATE_GROUP){
            new CreateGroupTask(mContext,data).execute();
        } else {
            mOnSetAvatarListener.setAvatar(requestCode, data, mivAvatar);
        }
	}

    class CreateGroupTask extends AsyncTask<Void,Void,String>{
        Context context;
        ProgressDialog dialog;
        Intent intent;
        GroupBean group;

        public CreateGroupTask(Context context, Intent intent) {
            this.context = context;
            this.intent = intent;
            this.dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String st1 = getResources().getString(R.string.Is_to_create_a_group_chat);
            dialog.setMessage(st1);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }


        @Override
        protected String doInBackground(Void... params) {
            // 调用sdk创建群组方法
            String groupName = groupNameEditText.getText().toString().trim();
            String desc = introductionEditText.getText().toString();
            String[] members = intent.getStringArrayExtra("newmembers");
            EMGroup emGroup;
            group= NetUtil.findGroupByName(groupName);
            if(group!=null){
                return getResources().getString(R.string.Group_name_existed);
            }
            try {
                if (checkBox.isChecked()) {
                    //创建公开群，此种方式创建的群，可以自由加入
                    //创建公开群，此种方式创建的群，用户需要申请，等群主同意后才能加入此群
                    emGroup = EMGroupManager.getInstance().createPublicGroup(groupName, desc, members, true, 200);
                } else {
                    //创建不公开群
                    emGroup = EMGroupManager.getInstance().createPrivateGroup(groupName, desc, members, memberCheckbox.isChecked(), 200);
                }
                boolean isPublic=checkBox.isChecked();
                boolean isExam=!memberCheckbox.isChecked();
                String userName= SuperWeChatApplication.getInstance().getUserName();
                StringBuffer sbMemberBuffer=new StringBuffer();
                for(String member:members){
                    sbMemberBuffer.append(member).append(",");
                }
                sbMemberBuffer.append(userName);
                String groupId=emGroup.getGroupId();
                group=new GroupBean(groupId,groupName,desc,userName,isPublic,isExam,sbMemberBuffer.toString());
                boolean isSuccess=NetUtil.createGroup(group);
                if(isSuccess){
                    NetUtil.uploadAvatar(mContext, "group_icon", groupName);
                    group.setAvatar("group_icon/"+groupName+".jpg");
                }
                return getResources().getString(R.string.Create_groups_Success);
            } catch (final EaseMobException e) {
                e.printStackTrace();
            } catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            String strExisted = getResources().getString(R.string.Group_name_existed);
            String strSuccess = getResources().getString(R.string.Create_groups_Success);
            String strFailed = getResources().getString(R.string.Create_groups_Failed);
            if(strExisted.equals(result)){
                groupNameEditText.setError(strExisted);
                groupNameEditText.requestFocus();
            }else if(strSuccess.equals(result)){
                Intent intent = new Intent(mContext,GroupsActivity.class);
                intent.putExtra("group", group);
                setResult(RESULT_OK,intent);
                finish();
            }else{
                Utils.showToast(context, result, Toast.LENGTH_SHORT);
            }
        }
    }

	public void back(View view) {
		finish();
	}
}
