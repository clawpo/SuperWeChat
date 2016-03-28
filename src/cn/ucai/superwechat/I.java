package cn.ucai.superwechat;

/**
 * Created by clawpo on 16/3/28.
 */
public class I {

    public static final String AVATAR_PATH = "//Users/clawpo/work/ucai/work/projects/SuperWeChat/superqq/";
    public static final String PAGE_ID = "pageId";
    public static final String PAGE_SIZE = "pageSize";

    public static class User {
        public static final String ID = "id";
        public static final String UID="uid";
        public static final String USER_NAME = "userName";
        public static final String NICK = "nick";
        public static final String AVATAR = "avatar";
        public static final String HEADER = "header";
        public static final String PASSWORD = "password";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String GROUPS = "groups";
        public static final String UN_READ_MSG_COUNT = "unreadMsgCount";
    }

    public static class Contact extends User {
        public static final String NAME = "name";
        public static final String MYUID = "myuid";
        public static final String CUID = "cuid";
        public static final String IS_GET_MY_LOCATION = "isGetMyLocation";
        public static final String IS_SHOW_MY_LOCATION = "isShowMyLocation";
    }

    public static class Group {
        public static final String ID = "id";
        public static final String GROUP_ID = "groupId";
        public static final String NAME = "name";
        public static final String GROUP_NAME = "groupName";
        public static final String NEW_NAME = "new_name";
        public static final String AVATAR = "avatar";
        public static final String INTRO = "intro";
        public static final String OWNER = "owner";
        public static final String IS_PUBLIC = "isPublic";
        public static final String MODIFIED_TIME = "modifiedTime";
        public static final String MEMBERS = "members";
        public static final String IS_EXAME = "isExame";
    }
    public static final String AVATAR_TYPE = "avatarType";
    public static final String KEY_REQUEST = "request";
    public static final String REQUEST_SERVERSTATUS = "server_status";
    public static final String REQUEST_REGISTER = "register";
    public static final String REQUEST_UNREGISTER = "unregister";
    public static final String REQUEST_UPLOAD_AVATAR = "upload_avatar";
    public static final String REQUEST_LOGIN = "login";
    public static final String REQUEST_DOWNLOAD_AVATAR = "download_avatar";
    public static final String DOWNLOAD_AVATAR_URL=SuperWeChatApplication.SERVER_ROOT+
            "?request="+REQUEST_DOWNLOAD_AVATAR+"&avatar=";
    public static final String REQUEST_DOWNLOAD_GROUP_AVATAR = "download_group_avatar";
    public static final String REQUEST_DOWNLOAD_CONTACTS = "download_contacts";
    public static final String REQUEST_DOWNLOAD_CONTACT_LIST = "download_contact_list";
    public static final String REQUEST_DELETE_CONTACT = "delete_contact";
    public static final String REQUEST_ADD_CONTACT = "add_contact";
    public static final String REQUEST_FIND_USER = "find_user";
    public static final String REQUEST_DOWNLOAD_CONTACT = "download_contacts";
    public static final String REQUEST_UPLOAD_LOCATION = "upload_location";
    public static final String REQUEST_DOWNLOAD_LOCATION = "download_location";
    public static final String REQUEST_CREATE_GROUP = "create_group";
    public static final String REQUEST_ADD_GROUP_MEMBER = "add_group_member";
    public static final String REQUEST_ADD_GROUP_MEMBERS = "add_group_members";
    public static final String REQUEST_UPDATE_GROUP_NAME = "update_group_name";
    public static final String REQUEST_DOWNLOAD_GROUP_MEMBERS = "download_group_members";
    public static final String REQUEST_DELETE_GROUP_MEMBER = "delete_group_member";
    public static final String REQUEST_DELETE_GROUP = "delete_group";
    public static final String REQUEST_DOWNLOAD_GROUPS = "download_groups";
    public static final String REQUEST_FIND_PUBLIC_GROUPS = "download_public_groups";
    public static final String REQUEST_FIND_GROUP = "find_group_by_group_name";
}
