package com.pos.sdkdemo.pboc;

import com.basewin.define.InputPBOCInitData;
import com.basewin.define.PBOCOption;
import com.basewin.services.ServiceManager;
import com.basewin.utils.CUPParam;
import com.basewin.utils.LoadParamManage;
import com.pos.sdkdemo.R;
import com.pos.sdkdemo.base.BaseActivity;
import com.pos.sdkdemo.interfaces.OnConfirmListener;
import com.pos.sdkdemo.interfaces.OnNumKeyListener;
import com.pos.sdkdemo.pboc.pinpad.StringHelper;
import com.pos.sdkdemo.pinpad.PinpadTestActivity;
import com.pos.sdkdemo.utils.GlobalData;
import com.pos.sdkdemo.widgets.EnterDialog;
import com.pos.sdkdemo.widgets.KeyBoardView;

import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * PBOC模块(PBOC Model)
 */
public class Pboc extends BaseActivity implements View.OnClickListener {

    private LinearLayout login, onlineTrans, offlineTrans, offlineTransDetails, offlineTransBalance;
    private TextView amount;
    private FrameLayout fl_keyboard;
    private KeyBoardView keyBoardView;

    @Override
    protected View onCreateView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.activity_pboc, null, false);
    }

    @Override
    protected void onInitView() {
        fl_keyboard = (FrameLayout) findViewById(R.id.fl_keyboard);
        fl_keyboard.removeAllViews();
        keyBoardView = new KeyBoardView(this);
        keyBoardView.getKeyBoardView();
        fl_keyboard.addView(keyBoardView.getKeyBoardView(), new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        keyBoardView.setOnNumKeyListener(new KeyBoardListener());


        login = (LinearLayout) findViewById(R.id.login);
        onlineTrans = (LinearLayout) findViewById(R.id.onlineTrans);
        offlineTrans = (LinearLayout) findViewById(R.id.offlineTrans);
        offlineTransDetails = (LinearLayout) findViewById(R.id.offlineTransDetails);
        offlineTransBalance = (LinearLayout) findViewById(R.id.offlineTransBalance);
        amount = (TextView) findViewById(R.id.amount);

        //login(签到，载入参数)
        login.setOnClickListener(this);
        //Online Trans(联机交易)
        onlineTrans.setOnClickListener(this);
        //offline Trans(脱机交易)
        offlineTrans.setOnClickListener(this);
        //offline trans details(脱机交易明细)
        offlineTransDetails.setOnClickListener(this);
        //offline acount balance(脱机余额)
        offlineTransBalance.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                showProcessDialog("downloading params");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        login();
                    }
                }).start();
                break;
            case R.id.onlineTrans:
            	if (!GlobalData.getInstance().getPinkeyFlag()) {
					new EnterDialog(this).showConfirmDialog("Warning!!!", "please inject pinkey first", new OnConfirmListener() {
						
						@Override
						public void OK() {
							// TODO Auto-generated method stub
							overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
							startActivity(new Intent(Pboc.this,PinpadTestActivity.class));
						}
						
						@Override
						public void Cancel() {
							// TODO Auto-generated method stub
							
						}
					});
				}
            	else {
                    new EnterDialog(this).showConfirmDialog("trans chose", "please chose see flow or trans", "trans flow", "trans", new OnConfirmListener() {
                        @Override
                        public void OK() {
                            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                            startActivity(new Intent(Pboc.this,onlineTransFlowActivity.class));
                        }

                        @Override
                        public void Cancel() {
                            onlineTrans();
                        }
                    });
				}

                break;
            case R.id.offlineTrans:
                new EnterDialog(this).showConfirmDialog("trans chose", "please chose see flow or trans", "trans flow", "trans", new OnConfirmListener() {
                    @Override
                    public void OK() {
                        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                        startActivity(new Intent(Pboc.this,offlineTransFlowActivity.class));
                    }

                    @Override
                    public void Cancel() {
                        offlineTrans();
                    }
                });

                break;
            case R.id.offlineTransDetails:
                new EnterDialog(this).showConfirmDialog("trans chose", "please chose see flow or trans", "trans flow", "trans", new OnConfirmListener() {
                    @Override
                    public void OK() {
                        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                        startActivity(new Intent(Pboc.this,offlineDetailsFlowActivity.class));
                    }

                    @Override
                    public void Cancel() {
                        offlineTransDetails();
                    }
                });
                break;
            case R.id.offlineTransBalance:
                new EnterDialog(this).showConfirmDialog("trans chose", "please chose see flow or trans", "trans flow", "trans", new OnConfirmListener() {
                    @Override
                    public void OK() {
                        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                        startActivity(new Intent(Pboc.this,offlineBalanceFlowActivity.class));
                    }

                    @Override
                    public void Cancel() {
                        offlineTransBalance();
                    }
                });
                break;
        }
    }

    //login(签到，载入参数)
    private void login() {
        LOGD("login !");
        try {
            boolean bRet;
            LoadParamManage.getInstance().DeleteAllTerParamFile();
            for (int j = 0; j < CUPParam.aid_data.length; j++) {

                bRet = ServiceManager.getInstence().getPboc().updateAID(0, CUPParam.aid_data[j]);

                LOGD("download " +j+" aid ["+CUPParam.aid_data[j]+"]" + " bRet = " + bRet);
            }
            for (int i = 0; i < CUPParam.ca_data.length; i++) {
                bRet =
                        ServiceManager.getInstence().getPboc().updateRID(0,
                                CUPParam.ca_data[i]);
                LOGD("download " +i+" rid ["+CUPParam.ca_data[i]+"]" + " bRet = [" + bRet+"]");
            }
            GlobalData.getInstance().setLogin(true);
            dismissDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //Online Trans(联机交易)
    private void onlineTrans() {
        if (!GlobalData.getInstance().getLogin())
        {
            new EnterDialog(this).showConfirmDialog("attention!","please login first!", new OnConfirmListener() {

                @Override
                public void OK() {

                }

                @Override
                public void Cancel() {

                }
            });
        }
        else
        {
            try {
                LOGD("PBOC normal");
                Intent in = new Intent();
                //setting pboc process the amount(设置pboc流程的金额)
                in.putExtra(InputPBOCInitData.AMOUNT_FLAG, formatAmount());
                //setting pboc process support the card type(设置pboc流程寻卡类型)
                in.putExtra(InputPBOCInitData.USE_DEVICE_FLAG, InputPBOCInitData.USE_MAG_CARD | InputPBOCInitData.USE_RF_CARD | InputPBOCInitData.USE_IC_CARD);
                //set trans timeout(设置寻卡超时时间)
                in.putExtra(InputPBOCInitData.TIMEOUT, 60);
                
                //卡片不支持Q，需要设置此属性才能支持借贷记
//                in.putExtra(InputPBOCInitData.IS_QPBOC_SUPPORT_FLAG, false);
                //start pboc process[trade model,parameter settings,callback](开始流程[交易类型,设置参数,回调])
                ServiceManager.getInstence().getPboc().startTransfer(PBOCOption.ONLINE_PAY, in, new onlinePBOCListener(Pboc.this, StringHelper.changeAmout(amount.getText().toString())));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    //offline Trans(脱机交易)
    private void offlineTrans() {
        if (!GlobalData.getInstance().getLogin())
        {
            new EnterDialog(this).showConfirmDialog("attention!","please login first!", new OnConfirmListener() {

                @Override
                public void OK() {

                }

                @Override
                public void Cancel() {

                }
            });
        }
        else {
            try {
                LOGD("PBOC quick");
                Intent in = new Intent();
                //setting pboc process the amount(设置pboc流程的金额)
                in.putExtra(InputPBOCInitData.AMOUNT_FLAG, formatAmount());
                //setting pboc process support the card type(设置pboc流程所支持的卡类型)
                in.putExtra(InputPBOCInitData.USE_DEVICE_FLAG, InputPBOCInitData.USE_MAG_CARD | InputPBOCInitData.USE_RF_CARD | InputPBOCInitData.USE_IC_CARD);
                //setting pboc offline mode(设置pboc优先走脱机流程)
                in.putExtra(InputPBOCInitData.IS_SUPPERT_EC_FLAG, true);
                //start pboc process[trade model,parameter settings,callback](开始流程[交易类型,设置参数,回调])
                ServiceManager.getInstence().getPboc().startTransfer(PBOCOption.ONLINE_PAY, in, new offlinePBOCListener(Pboc.this, StringHelper.changeAmout(amount.getText().toString())));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //offline trans details(脱机交易明细)
    private void offlineTransDetails() {
        if (!GlobalData.getInstance().getLogin())
        {
            new EnterDialog(this).showConfirmDialog("attention!","please login first!", new OnConfirmListener() {

                @Override
                public void OK() {

                }

                @Override
                public void Cancel() {

                }
            });
        }
        else {
            Intent in = new Intent();
            in.putExtra(InputPBOCInitData.AMOUNT_FLAG, 1);
            in.putExtra(InputPBOCInitData.USE_DEVICE_FLAG, InputPBOCInitData.USE_RF_CARD | InputPBOCInitData.USE_IC_CARD);
            in.putExtra(InputPBOCInitData.IS_SUPPERT_EC_FLAG, true);
            try {
                ServiceManager.getInstence().getPboc().startTransfer(PBOCOption.FUN_UPCASH_QUERY_DETAIL, in, new offlineDetailsPBOCListener(Pboc.this, StringHelper.changeAmout(amount.getText().toString())));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //offline acount balance(脱机账户余额)
    private void offlineTransBalance() {
//    	GlobalData.getInstance().setLogin(true);
        if (!GlobalData.getInstance().getLogin())
        {
            new EnterDialog(this).showConfirmDialog("attention!","please login first!", new OnConfirmListener() {

                @Override
                public void OK() {

                }

                @Override
                public void Cancel() {

                }
            });
        }
        else {
            Intent in = new Intent();
            in.putExtra(InputPBOCInitData.AMOUNT_FLAG, 1);
            in.putExtra(InputPBOCInitData.USE_DEVICE_FLAG, InputPBOCInitData.USE_RF_CARD | InputPBOCInitData.USE_IC_CARD);
            in.putExtra(InputPBOCInitData.IS_SUPPERT_EC_FLAG, true);
            try {
                ServiceManager.getInstence().getPboc().startTransfer(PBOCOption.FUN_UPCASH_QUERY_BALANCE, in, new offlineBalancePBOCListener(Pboc.this, StringHelper.changeAmout(amount.getText().toString())));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * format amount
     *
     * @return
     */
    public Long formatAmount() {
        return Long.parseLong(StringHelper.changeAmout(this.amount.getText().toString()).replace(".", ""));
    }

    private class KeyBoardListener implements OnNumKeyListener {
        @Override
        public void onClick(View view) {
            StringBuilder builder = new StringBuilder();

            builder.append(amount.getText());
            switch (view.getId()) {
                case R.id.num00:
                    builder.append(00);
                case R.id.num0:
                    builder.append(0);
                    break;
                case R.id.num1:
                    builder.append(1);
                    break;
                case R.id.num2:
                    builder.append(2);
                    break;
                case R.id.num3:
                    builder.append(3);
                    break;
                case R.id.num4:
                    builder.append(4);
                    break;
                case R.id.num5:
                    builder.append(5);
                    break;
                case R.id.num6:
                    builder.append(6);
                    break;
                case R.id.num7:
                    builder.append(7);
                    break;
                case R.id.num8:
                    builder.append(8);
                    break;
                case R.id.num9:
                    builder.append(9);
                    break;
                case R.id.num_back:
                    builder = builder.delete(builder.length() - 1, builder.length());
                    break;
                default:
                    break;
            }
            amount.setText(StringHelper.changeAmout(builder.toString()));
        }

    }

    public void freshProcessDialog(final String title) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showProcessDialog(title);
            }
        });
    }

    public void dismissDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dismissProcessDialog();
            }
        });
    }
}
