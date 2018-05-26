package com.pos.sdkdemo.cards.card4442;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.basewin.interfaces.OnDetectListener;
import com.basewin.services.ServiceManager;
import com.pos.sdk.card.PosCardInfo;
import com.pos.sdk.utils.PosUtils;
import com.pos.sdkdemo.R;
import com.pos.sdkdemo.base.BaseActivity;

/**
 * 4442卡 (4442 Card)
 */
public class Card4442 extends BaseActivity {

    private Button start;
    private String pwd = "123456";
    private View v;

    @Override
    protected View onCreateView(LayoutInflater inflater) {
        v = inflater.inflate(R.layout.activity_card_4442, null, false);
        return v;
    }

    @Override
    protected void onInitView() {

        if (v != null) {
            start = (Button) v.findViewById(R.id.identify_card);
            start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        GasCardDevice.getInstance().open();
                        LOGD( getResources().getString(R.string.card_4442_pwd_left) +" "+ GasCardDevice.getInstance().pacPwdTime());
                        GasCardDevice.getInstance().check(new OnDetectListener() {

                            @Override
                            public void onSuccess(int arg0) { // TODO
                                // Auto-generated method stub
                                LOGD("detect success");
                                showCardInfo(arg0);
                                // 校验密码
                                //verify password
                                GasCardDevice.getInstance().pwdVerify(pwd);
                                // 修改密码
                                //modify password
                                if (!pwd.equals("123456")) {
                                    GasCardDevice.getInstance().updatePwd("123456");
                                    pwd = "123456"; //
                                }
                                // 校验密码
                                //verify password
                                GasCardDevice.getInstance().pwdVerify(pwd);
                                //读卡
                                //read card info
                                String valueString = GasCardDevice.getInstance().readCardInfo(0, 0, 255);
                                LOGD("value = " + valueString);
                                // 写卡
                                //write data into card
                                String dataString = "A2131091FFFF8115FFFFFFFFFFFFFFFFFFFFFFFFFFD27600000400FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";
                                GasCardDevice.getInstance().write(0, 0, dataString);
                            }

                            @Override
                            public void onError(int arg0, String arg1) { //
                                // TODO Auto-generated method stub
                                showError(arg0,arg1);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });



        }
    }


    private void showError(int i, String s) {
        try {
            LOGD("error code:" + i + " error info:" + s);
            //shutdown for card(关闭寻卡)
            ServiceManager.getInstence().getCard().removeCard();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showCardInfo(int cardtype) {
        PosCardInfo posCardInfo = new PosCardInfo();
        try {
            ServiceManager.getInstence().getCard().getCardInfo(cardtype,posCardInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOGD("attribute:" + PosUtils.bcdToString(posCardInfo.mAttribute));
        LOGD("serial number:" + PosUtils.bcdToString(posCardInfo.mSerialNum));
        LOGD("card channel:" + posCardInfo.mCardChannel);
    }
}
