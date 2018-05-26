package com.pos.sdkdemo.cards.IDcard;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.basewin.interfaces.OnApduCmdListener;
import com.basewin.interfaces.OnDetectListener;
import com.basewin.services.ServiceManager;
import com.pos.sdk.card.PosCardInfo;
import com.pos.sdk.utils.PosByteArray;
import com.pos.sdk.utils.PosUtils;
import com.pos.sdkdemo.R;
import com.pos.sdkdemo.base.BaseActivity;

/**
 * 身份证 (IDCard)
 */
public class IdCard extends BaseActivity {

    private Button start;
    private String pwd = "123456";
    private View v;

    @Override
    protected View onCreateView(LayoutInflater inflater) {
        v = inflater.inflate(R.layout.activity_card_idcard, null, false);
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
                        ServiceManager.getInstence().getCard().openIDCardAndDetect(60 * 1000, new OnDetectListener() {

                            @Override
                            public void onSuccess(int arg0) {
                                // TODO Auto-generated method stub
                                LOGD("detect id card success!");
                                try {
                                    showCardInfo(arg0);
                                    // APDU交互
                                    byte[] cmd = new byte[8];
                                    cmd[0] = 0x64;
                                    cmd[1] = 0x05;
                                    cmd[2] = 0x00;
                                    cmd[3] = 0x36;
                                    cmd[4] = 0x00;
                                    cmd[5] = 0x00;
                                    cmd[6] = 0x08;
                                    ServiceManager.getInstence().getCard().transmitApduToIdCard(cmd, new OnApduCmdListener() {

                                        @Override
                                        public void onSuccess(PosByteArray arg0, byte[] arg1) {
                                            // TODO Auto-generated method stub

                                        }

                                        @Override
                                        public void onError() {
                                            // TODO Auto-generated method stub

                                        }
                                    });
                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(int arg0, String arg1) {
                                // TODO Auto-generated method stub
                                LOGD("detect id card failed!");
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
