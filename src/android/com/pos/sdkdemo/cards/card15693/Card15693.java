package com.pos.sdkdemo.cards.card15693;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.basewin.define.Card15693Modes;
import com.basewin.interfaces.OnDetectListener;
import com.basewin.interfaces.OnViccTransmitListener;
import com.basewin.services.ServiceManager;
import com.pos.sdk.utils.PosByteArray;
import com.pos.sdkdemo.R;
import com.pos.sdkdemo.base.BaseActivity;

/**
 * 身份证 (IDCard)
 */
public class Card15693 extends BaseActivity {

    private Button start;
    private String pwd = "123456";
    private View v;

    @Override
    protected View onCreateView(LayoutInflater inflater) {
        v = inflater.inflate(R.layout.activity_card_15693, null, false);
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
                        ServiceManager.getInstence().getCard().openVicc(1000, new OnDetectListener() {

                            @Override
                            public void onSuccess(int arg0) {
                                // TODO Auto-generated method stub
                                LOGD("detect 15693 success!");
                                try {
                                    ServiceManager.getInstence().getCard().DetectISO15963CardAndTransmit(Card15693Modes.Lock_block, new byte[] { 1 }, new OnViccTransmitListener() {

                                        @Override
                                        public void onSuccess(PosByteArray arg0) {
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
}
