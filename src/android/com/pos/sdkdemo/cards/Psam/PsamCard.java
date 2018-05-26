package com.pos.sdkdemo.cards.Psam;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.basewin.define.ApduResult;
import com.basewin.interfaces.OnDetectListener;
import com.basewin.services.CardBinder;
import com.basewin.services.ServiceManager;
import com.pos.sdk.card.PosCardInfo;
import com.pos.sdk.utils.PosByteArray;
import com.pos.sdk.utils.PosUtils;
import com.pos.sdkdemo.R;
import com.pos.sdkdemo.base.BaseActivity;
import com.pos.sdkdemo.utils.BCDHelper;

/**
 * M1卡 (M1 Card)
 */
public class PsamCard extends BaseActivity {

    private Button start;
    CardBinder cardService;
    private View v;

    @Override
    protected View onCreateView(LayoutInflater inflater) {
        v = inflater.inflate(R.layout.activity_card_psam, null, false);
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
                        cardService = ServiceManager.getInstence().getCard();
                        cardService.openPsamAndDetect(60 * 1000, new OnDetectListener() {
                            @Override
                            public void onSuccess(int i) {
                                LOGD("detect psam success!");
                                try {
                                    ServiceManager.getInstence().getCard().resetCard();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                showCardInfo(i);
                                PosByteArray sw = null;
                                LOGD("select DF Directory");
                                LOGD("transmit apdu 00A40000028F01");
                                // 选择存放密钥文件的DF目录
                                //select DF Directory
                                byte[] apdu = getApduBytes("00A40000028F01");
                                ApduResult apduResult = new ApduResult();
                                apduResult = cardService.transmitApduToCard(apdu);
                                showApduResult(apduResult);
                                sw = apduResult.getSw();
                                if (BCDHelper.bcdToString(sw.buffer, 0, sw.len).equals("9000")) {
                                    LOGD("transmit apdu success!");
                                }
                                else
                                    LOGD("transmit apdu failed!");
                            }

                            @Override
                            public void onError(int i, String s) {
                                showError(i, s);
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
            ServiceManager.getInstence().getCard().getCardInfo(cardtype, posCardInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOGD("attribute:" + PosUtils.bcdToString(posCardInfo.mAttribute));
        LOGD("serial number:" + PosUtils.bcdToString(posCardInfo.mSerialNum));
        LOGD("card channel:" + posCardInfo.mCardChannel);
    }

    private byte[] getApduBytes(String apdu_s) {
        LOGD("get apdu cmd " + apdu_s);
        return BCDHelper.stringToBcd(apdu_s, apdu_s.length());
    }

    private void showApduResult(ApduResult apduResult) {
        if (apduResult == null) {
            LOGD("apdu返回数据为null");
        }
        PosByteArray sw = apduResult.getSw();
        PosByteArray rep = apduResult.getRep();
        LOGD("apdu返回sw为 " + BCDHelper.bcdToString(sw.buffer, 0, sw.len));
        if (rep.len > 0) {
            LOGD("apdu返回数据为 " + BCDHelper.bcdToString(rep.buffer, 0, rep.len));
        } else {
            LOGD("无apdu返回数据");
        }

    }
}
