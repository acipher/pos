package com.pos.sdkdemo.cards.readcard;

import android.os.Handler;
import android.os.Message;
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
 * 读卡模块 (CardReader)
 */
public class MixCardDetect extends BaseActivity {

    private Button start;
    private View v;
    private static final int SEARCHPSAM = 1; // 寻PSAM卡
    private static final int APDUWITHPSAM = 2; // APDU交互PSAM卡
    private static final int SEARCHM1 = 3; // 寻M1卡
    private static final int READCARDNOINM1 = 4; // 读取22块M1卡内卡号数据
    private static final int APDUWITHPSAM2 = 5; // 解密卡号apdu交互

    private byte[] data0 = null; // M1卡第0块数据
    private byte[] data22 = null; // M1卡第22块数据
    private String keyA0 = "FFFFFFFFFFFF"; // 第0块认证密钥
    private byte[] keyA = new byte[6]; // M1卡22块KeyA
    private byte[] keyB = new byte[6]; // M1卡22块KeyB
    private byte[] apdu = null; // apdu命令
    private ApduResult apduResult = new ApduResult();

    private CardBinder cardService = null;
    private PosCardInfo cardInfo = new PosCardInfo();
    private int ret = 0;

    Handler handler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SEARCHPSAM:
                    LOGD("detect PSAM card");
                    cardService.selectPsam();
                    cardService.openPsamAndDetectNotCloseOtherCard(10 * 1000, new OnDetectListener() {

                        @Override
                        public void onSuccess(int cardtype) {
                            LOGD("detect  PSAM success!");
                            cardService.selectPsam();
                            cardService.resetCard();
                            handler2.sendEmptyMessage(APDUWITHPSAM);
                        }

                        @Override
                        public void onError(int errorcode, String msg) {
                            // TODO Auto-generated method stub
                            LOGD("detect PSAM card failed:" + errorcode + ":" + msg);
                        }
                    });
                    break;
                case APDUWITHPSAM:
                    // TODO Auto-generated method stub
                    PosByteArray sw = null;
                    LOGD("select DF directory");
                    LOGD("transmit apdu 00A40000028F01");
                    // 选择存放密钥文件的DF目录
                    apdu = getApduBytes("00A40000028F01");
                    apduResult = cardService.transmitApduToCard(apdu);
                    showApduResult(apduResult);
                    sw = apduResult.getSw();
                    if (BCDHelper.bcdToString(sw.buffer, 0, sw.len).equals("9000")) {
                        String data0_8 = BCDHelper.bcdToString(data0, 0, 8);
                        // 密钥1初始化
                        LOGD("key1 init");
                        LOGD("trnasmit apdu 801A280108" + data0_8);
                        apdu = getApduBytes("801A280108" + data0_8);
                        apduResult = cardService.transmitApduToCard(apdu);
                        showApduResult(apduResult);
                        sw = apduResult.getSw();
                        if (BCDHelper.bcdToString(sw.buffer, 0, sw.len).equals("9000")) {
                            // 计算keyA
                            LOGD("calc keyA");
                            LOGD("transmit apdu 80FA000008" + data0_8);
                            apdu = getApduBytes("80FA000008" + data0_8);
                            apduResult = cardService.transmitApduToCard(apdu);
                            showApduResult(apduResult);
                            sw = apduResult.getSw();
                            if (BCDHelper.bcdToString(sw.buffer, 0, sw.len).equals("9000")) {
                                // 获取keyA
                                System.arraycopy(apduResult.getRep().buffer, 0, keyA, 0, 6);
                                // 密钥2初始化
                                LOGD("key2 init");
                                LOGD("transmit apdu 801A280208" + data0_8);
                                apdu = getApduBytes("801A280208" + data0_8);
                                apduResult = cardService.transmitApduToCard(apdu);
                                showApduResult(apduResult);
                                sw = apduResult.getSw();
                                if (BCDHelper.bcdToString(sw.buffer, 0, sw.len).equals("9000")) {
                                    // 计算keyB
                                    LOGD("calc keyB");
                                    LOGD("transmit apdu 80FA000008" + data0_8);
                                    apdu = getApduBytes("80FA000008" + data0_8);
                                    apduResult = cardService.transmitApduToCard(apdu);
                                    showApduResult(apduResult);
                                    sw = apduResult.getSw();
                                    if (BCDHelper.bcdToString(sw.buffer, 0, sw.len).equals("9000")) {
                                        // 获取keyB
                                        System.arraycopy(apduResult.getRep().buffer, 0, keyB, 0, 6);
                                        // 使用keyA获取M1卡22块内容
                                        handler2.sendEmptyMessage(READCARDNOINM1);
                                    }

                                }

                            }

                        }
                    }

                    break;
                case SEARCHM1:
                    cardService.openM1AndDetect(10*1000, new OnDetectListener() {

                        @Override
                        public void onError(int arg0, String arg1) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onSuccess(int arg0) {
                            // TODO Auto-generated method stub
                            LOGD("detect M1 card success!");
                            // 寻找M1卡成功
                            cardService.getCardInfo(arg0, cardInfo);
                            LOGD("verofy 0 block");
                            byte[] secret = BCDHelper.stringToBcd(keyA0, keyA0.length());
                            ret = cardService.M1CardKeyAuth('A', 0, secret, cardInfo.mSerialNum);
                            // 认证成功之后读取数据
                            if (ret == 0) {
                                LOGD("verify success，read 0 block data");
                                PosByteArray b_data0 = new PosByteArray();
                                cardService.M1CardReadBlock(0, b_data0);
                                data0 = b_data0.buffer;
                                LOGD("0 block data " + BCDHelper.bcdToString(data0, 0, data0.length));
                                if (data0.length > 8) {
                                    // 寻PSAM卡
                                    handler2.sendEmptyMessage(SEARCHPSAM);
                                } else {
                                    LOGD("0 block data less than 8 byte，please fix problem！");
                                }

                            }
                        }
                    });
                    break;
                case READCARDNOINM1:
                    cardService.selectPicc();
                    cardService.openM1AndDetectNotCloseOtherCard(10*1000, new OnDetectListener() {

                        @Override
                        public void onError(int arg0, String arg1) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onSuccess(int arg0) {
                            LOGD("verify 0 block");
                            byte[] secret = new byte[]{(byte) 0xFF,(byte) 0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF};
                            ret = cardService.M1CardKeyAuth('A', 0, secret, cardInfo.mSerialNum);
                            // 认证成功之后读取数据
                            if (ret == 0) {
                                LOGD("verify success!");
                            }
                            else
                            {
                                LOGD("verify 0 block  "+ret);
                            }


                            LOGD("verify 22 block");
                            secret = keyA;
                            LOGD("keyA为"+BCDHelper.bcdToString(keyA, 0, keyA.length));
                            ret = cardService.M1CardKeyAuth('A', 22, secret, cardInfo.mSerialNum);
                            // 认证成功之后读取数据
                            if (ret == 0) {
                                LOGD("verify success，read 22 block data");
                                PosByteArray b_data22 = new PosByteArray();
                                cardService.M1CardReadBlock(22, b_data22);
                                data22 = b_data22.buffer;
                                LOGD("22 block data " + BCDHelper.bcdToString(data22, 0, data22.length));
                                handler2.sendEmptyMessage(APDUWITHPSAM2);
                            }
                            else
                            {
                                LOGD("verify 22 block ret "+ret);
                            }
                        }
                    });


                    break;
                case APDUWITHPSAM2:
                    cardService.selectPsam();
                    String data0_8 = BCDHelper.bcdToString(data0, 0, 8);
                    // 解密密钥初始化
                    //init key
                    LOGD("init key");
                    LOGD("transmit apdu 801A2C0108" + data0_8);
                    apdu = getApduBytes("801A2C0108" + data0_8);
                    apduResult = cardService.transmitApduToCard(apdu);
                    showApduResult(apduResult);
                    sw = apduResult.getSw();
                    if (BCDHelper.bcdToString(sw.buffer, 0, sw.len).equals("9000")) {
                        LOGD("calc cardno");
                        LOGD("transmit apdu 80FA000010" + BCDHelper.bcdToString(data22, 0, 16));
                        apdu = getApduBytes("80FA000010" + BCDHelper.bcdToString(data22, 0, 16));
                        apduResult = cardService.transmitApduToCard(apdu);
                        showApduResult(apduResult);
                        sw = apduResult.getSw();
                        if (BCDHelper.bcdToString(sw.buffer, 0, sw.len).equals("9000")) {
                            PosByteArray rep = apduResult.getRep();
                            LOGD("cardno:" + BCDHelper.bcdToString(rep.buffer, 0, rep.len));
                        }
                    }
                    break;
            }
        };
    };

    @Override
    protected View onCreateView(LayoutInflater inflater) {
        v = inflater.inflate(R.layout.activity_card_mix, null, false);
        return v;
    }

    @Override
    protected void onInitView() {
        try {
            cardService = ServiceManager.getInstence().getCard();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (v != null) {
            start = (Button) v.findViewById(R.id.identify_card);
            start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 寻M1卡
                    //detect m1 card
                    LOGD("detect M1 card first");
                    handler2.sendEmptyMessage(SEARCHM1);
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
            LOGD("apdu rep data is null");
        }
        PosByteArray sw = apduResult.getSw();
        PosByteArray rep = apduResult.getRep();
        LOGD("apdu->sw " + BCDHelper.bcdToString(sw.buffer, 0, sw.len));
        if (rep.len > 0) {
            LOGD("apdu data " + BCDHelper.bcdToString(rep.buffer, 0, rep.len));
        } else {
            LOGD("apdu data null");
        }

    }
}
