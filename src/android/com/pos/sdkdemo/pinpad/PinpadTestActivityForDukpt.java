package com.pos.sdkdemo.pinpad;

import com.basewin.define.KeyType;
import com.basewin.services.ServiceManager;
import com.pos.sdk.security.PosSecurityManager;
import com.pos.sdkdemo.R;
import com.pos.sdkdemo.base.BaseActivity;
import com.pos.sdkdemo.utils.BCDHelper;
import com.pos.sdkdemo.utils.GlobalData;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class PinpadTestActivityForDukpt extends BaseActivity {

    private static final String TAG = PinpadTestActivityForDukpt.class.getName();
    private TextView tv_pinpad_result;
    private VirtualPWKeyboardView pwKeyboard;

    private int type = KeyType.PIN_KEY;
    byte[] TLKKCV = new byte[] {(byte)0x8C,(byte)0xA6,0x4D};
    String kcv = null;
    String pedleydestdata2 = "0123456789ABCDEFFEDCBA9876543210";
    String ksn = "F8765432100F0F100000";
    @Override
    protected View onCreateView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.activity_pinpad_test_dukpt, null);
    }

    @Override
    protected void onInitView() {
        setTitle("pinpad");
        initView();
    }

    private void setHint(String s) {
        Log.e("LOG:", s);
        LOGD(s);
    }

    public void loadDukptKey(View view) {
        try {
        	boolean iRet = ServiceManager.getInstence().getPinpad().loadDukptTIK(1,0,pedleydestdata2,ksn,kcv);
            if (iRet) {
                showToast("load dukpt Key Success!");
                GlobalData.getInstance().setPinpadVersion(PinpadInterfaceVersion.PINPAD_INTERFACE_DUKPT);
            } else {
                showToast("load protect Key error!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void inputonlinepin(View view) {
        String cardNumber = getCardNumber();
        if (TextUtils.isEmpty(cardNumber)) {
            setHint("cat Number  is  null !");
            return;
        }
        PWDialog pwDialog = new PWDialog(this,PinpadInterfaceVersion.PINPAD_INTERFACE_DUKPT,0,0);
        pwDialog.setListener(new PWDialog.PWListener() {
            @Override
            public void onConfirm(byte[] bytes, boolean b) {
                setHint("Password:" + BCDHelper.bcdToString(bytes));
            }

            @Override
            public void onCancel() {
                setHint("Password Cancel");
            }

            @Override
            public void onError(int i) {
                setHint("Password Error");
            }
        });
        pwDialog.showForPW(cardNumber);
    }

    private void initView() {
    }

    private String getCardNumber() {
        return "6210885200013000000";
    }

    private void showResult(String msg) {
        tv_pinpad_result.setVisibility(View.VISIBLE);
        pwKeyboard.setVisibility(View.GONE);
        tv_pinpad_result.setText(msg);
    }

    private void showToast(String msg) {
        setHint(msg);
//        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    /**
     * format pinpad ,clear all keys
     * @param view
     */
    public void format(View view)
    {
        try {
            ServiceManager.getInstence().getPinpad().format();
            LOGD("format success!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
}
