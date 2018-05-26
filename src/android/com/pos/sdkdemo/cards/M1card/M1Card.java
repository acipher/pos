package com.pos.sdkdemo.cards.M1card;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

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
public class M1Card extends BaseActivity {

    private Button start;
    String keyA0 = "FFFFFF";
    int ret;
    CardBinder cardService;
    byte[] mSerialNum;
    private View v;

    @Override
    protected View onCreateView(LayoutInflater inflater) {
        v = inflater.inflate(R.layout.activity_card_m1, null, false);
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
                        LOGD("detect M1 card ...");
                        cardService =  ServiceManager.getInstence().getCard();
                        cardService.openM1AndDetect(60 * 1000, new OnDetectListener() {
                            @Override
                            public void onSuccess(int i) {
                                LOGD("detect M1 card success!");
                                showCardInfo(i);
                                byte[] secret = BCDHelper.stringToBcd(keyA0, keyA0.length());
                                ret = cardService.M1CardKeyAuth('A', 0, secret, mSerialNum);
                                // 认证成功之后读取数据
                                if (ret == 0) {
                                    LOGD("verify success，read 0 block data");
                                    PosByteArray b_data0 = new PosByteArray();
                                    ret = cardService.M1CardReadBlock(0, b_data0);
                                    LOGD("read 0 block data ret = "+ret);
                                    if (ret == 0)
                                        LOGD("0 block data " + BCDHelper.bcdToString(b_data0.buffer, 0, b_data0.buffer.length));
                                    ret = cardService.M1CardWriteBlock(0,b_data0.buffer);
                                    if (ret == 0)
                                        LOGD("write 0 block data success!");
                                    else
                                        LOGD("write 0 block data failed!");
                                }
                            }

                            @Override
                            public void onError(int i, String s) {

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
            mSerialNum = posCardInfo.mSerialNum;
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOGD("attribute:" + PosUtils.bcdToString(posCardInfo.mAttribute));
        LOGD("serial number:" + PosUtils.bcdToString(posCardInfo.mSerialNum));
        LOGD("card channel:" + posCardInfo.mCardChannel);
    }
}
