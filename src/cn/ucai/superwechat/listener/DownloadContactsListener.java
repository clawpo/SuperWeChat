package cn.ucai.superwechat.listener;

import android.util.Log;

import com.android.volley.Response;

import java.util.HashMap;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.activity.BaseActivity;
import cn.ucai.superwechat.bean.ContactBean;
import cn.ucai.superwechat.data.ApiParams;
import cn.ucai.superwechat.data.GsonRequest;

/**
 * Created by clawpo on 16/3/29.
 */
public class DownloadContactsListener extends BaseActivity {

    public static final String TAG = DownloadContactsListener.class.getName();


    public  DownloadContactsListener(String userName, int pageId,int pageSize) throws Exception {

        //requestParams集合封装了向服务端发送的get请求的参数
        String url = new ApiParams()
                .with(I.KEY_REQUEST,I.REQUEST_DOWNLOAD_CONTACTS)
                .with(I.User.USER_NAME, userName)
                .with(I.PAGE_ID, pageId + "")
                .with(I.PAGE_SIZE, pageSize + "")
                .getUrl(SuperWeChatApplication.SERVER_ROOT);

//        //将URL和请求参数转换为url字符串格式
//        String url=ApiParams.getUrl(SuperWeChatApplication.SERVER_ROOT,);

        Log.e(TAG,"downloadContacts,url="+url);

        executeRequest(new GsonRequest<ContactBean[]>(url, ContactBean[].class,
                responseListener(), errorListener()));

    }

    private Response.Listener<ContactBean[]> responseListener() {
        return new Response.Listener<ContactBean[]>() {
            @Override
            public void onResponse(ContactBean[] contacts) {
                HashMap<Integer, ContactBean> map = new HashMap<Integer, ContactBean>();
                for (ContactBean contact : contacts) {
                    map.put(contact.getCuid(), contact);
                }
                Log.e(TAG,"downloadContacts,map="+map.size());
                HashMap<Integer,ContactBean> contactMap=SuperWeChatApplication.getInstance().getContacts();
                Log.e(TAG,"downloadContacts,contactMap="+contactMap.size());
                contactMap.putAll(map);
                SuperWeChatApplication.getInstance().setContacts(contactMap);
                Log.e(TAG,"downloadContacts,contactMap="+contactMap.size());
            }
        };
    }
}
