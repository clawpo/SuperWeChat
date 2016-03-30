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

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.umeng.analytics.MobclickAgent;

import cn.ucai.superwechat.applib.controller.HXSDKHelper;
import cn.ucai.superwechat.data.RequestManager;

public class BaseActivity extends FragmentActivity {

    protected Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // onresume时，取消notification显示
        HXSDKHelper.getInstance().getNotifier().reset();
        
        // umeng
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // umeng
        MobclickAgent.onPause(this);
    }


    /**
     * 返回
     * 
     * @param view
     */
    public void back(View view) {
        finish();
    }

    public <T> T getViewById(int id){
        return (T)findViewById(id);
    }

    @Override
    public void onStop() {
        super.onStop();
        RequestManager.cancelAll(this);
    }

    public void executeRequest(Request<?> request) {
        RequestManager.addRequest(request, this);
    }

    public Response.ErrorListener errorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
    }
}
