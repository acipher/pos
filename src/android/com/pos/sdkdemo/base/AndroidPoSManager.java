package com.pos.sdkdemo.base;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import android.app.Application;

import com.basewin.database.DataBaseManager;
import com.basewin.services.ServiceManager;
import com.pos.sdkdemo.utils.GlobalData;
public class AndroidPoSManager extends CordovaPlugin {
	private static final String TAG = "PoSApplication";
    private static AndroidPoSManager instance;

    public static AndroidPoSManager getInstance() {
        return instance;
    }
    //@Override
    //public void onCreate() {
    //    super.onCreate();
    //    instance = this;
    //    /**
    //     * init Device Server
    //     */
	//	ServiceManager.getInstence().init(getApplicationContext());
    //    /**
    //     * init database
    //     */
    //    DataBaseManager.getInstance().init(getApplicationContext());
    //    /**
    //     * init the GlobalData cashe
    //     */
    //    GlobalData.getInstance().init(this);
    //}
	
	
	
	@Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.onCreate();
        instance = this;
        /**
         * init Device Server
         */
		ServiceManager.getInstence().init(getApplicationContext());
        /**
         * init database
         */
        DataBaseManager.getInstance().init(getApplicationContext());
        /**
         * init the GlobalData cashe
         */
        GlobalData.getInstance().init(this);
    }

    @Override
    public void onDestroy() {
        //mSensorManager.unregisterListener(listener);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
       return true;  // Returning false results in a "MethodNotFound" error.
    }
}
