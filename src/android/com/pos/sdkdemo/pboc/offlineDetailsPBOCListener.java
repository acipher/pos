package com.pos.sdkdemo.pboc;

import java.util.List;

import com.basewin.aidl.OnPBOCListener;
import com.basewin.define.OutputOfflineRecord;
import com.basewin.services.ServiceManager;
import com.basewin.utils.StringListConvert;
import com.pos.sdkdemo.interfaces.OnChoseListener;
import com.pos.sdkdemo.widgets.EnterDialog;

import android.content.Intent;
import android.os.Looper;
import android.os.RemoteException;

/**
 * PBOC监听过程(PBOC listener process)
 */
public class offlineDetailsPBOCListener implements OnPBOCListener {


    private Pboc ba;                    //context(上下文)

    public offlineDetailsPBOCListener(Pboc ba, String amt) {
        this.ba = ba;
    }


    @Override
    public void onStartPBOC() throws RemoteException {
        //PBOC process the start(PBOC流程开始)
        ba.LOGD("PBOC Start");
        ba.freshProcessDialog("start pboc...");
    }

    @Override
    public void onRequestAmount() {
        //if you don't set the amount before,can be in this setting(如果之前没有设置金额，可以再次设置)
        ba.LOGD("PBOC Setting amount");
    }

    @Override
    public void onSelectApplication(List<String> applicationList) {
        //selection card application(在此选着卡应用)
        ba.LOGD("PBOC Select Application");
        ba.LOGD("PBOC Select Application");
        ba.dismissDialog();
        Looper.prepare();
        new EnterDialog(ba).showListChoseDialog("please chose application!", StringListConvert.ListToStringArray(applicationList), new OnChoseListener() {
            @Override
            public void Chose(int i) {
                ba.LOGD("chose :"+i);
                try {
                    ServiceManager.getInstence().getPboc().selectApplication(i+1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Looper.loop();
    }

    @Override
    public void onFindingCard(int cardType, Intent data) {
        //find and identify the card as well as read relevant data(寻卡和选卡，然后读取相关数据)
        ba.LOGD("PBOC Finding Choose card");
        ba.freshProcessDialog("finding card...");
    }

    @Override
    public void onRequestInputPIN(boolean isOnlinePin, int retryTimes) throws RemoteException {
        // Need a password,At this point you need to call password pinpad(底层返回需要设置密码，这个时候需要调用pinpad模块进行密码输入，只有IC PBOC流程)
        ba.LOGD("PBOC Request input PIN");
    }

    @Override
    public void onConfirmCardInfo(Intent info) {
        //may need to confirm the IC card information display interface(确认IC卡卡号信息的时候，可能需要进行界面显示，此处略过，最后确认完了调用confirmCardInfo()即可)
        ba.LOGD("PBOC Confirm Card Info");

    }

    @Override
    public void onConfirmCertInfo(String certType, String certInfo) throws RemoteException {
        //confirm the identity(确认身份信息)
        ba.LOGD("PBOC Confirm credentials info");
    }

    @Override
    public void onAARequestOnlineProcess(Intent actionAnalysisData) throws RemoteException {
        //online trading(联机交易)
        ba.LOGD("PBOC the Online trade process");
    }

    @Override
    public void onTransactionResult(int result, Intent data) throws RemoteException {
        //Transaction result(交易结果)
        ba.LOGD("PBOC the Transaction result");
    }

    @Override
    public void onReadECBalance(Intent ecBalance) throws RemoteException {
        //online trading the balance,but temporarily didn't use him(在线余额，暂时没有使用)
        ba.LOGD("PBOC EC balance");
    }

    @Override
    public void onReadCardOfflineRecord(Intent contents) throws RemoteException {
        //offline trading the balance,but temporarily didn't use him(离线余额，暂时没有使用)
        ba.LOGD("PBOC Transaction record");
        ba.dismissDialog();
        OutputOfflineRecord list = new OutputOfflineRecord(contents);
        String [] record = new String[list.getRecordSize()];
        for (int i = 0; i < list.getRecordSize(); i++) {
            ba.LOGD("Record " + (i + 1) + ":" + list.getRecord(i));
            record[i] = list.getRecord(i);
        }
        Looper.prepare();
        new EnterDialog(ba).showListDialog("record list",record);
        Looper.loop();
    }

    @Override
    public void onError(Intent result) throws RemoteException {
        //PBOC process to error(流程出错)
        ba.LOGD("PBOC Error");
        ba.dismissDialog();
    }
}