package com.pos.sdkdemo.cards.card4442;

import android.util.Log;

import com.basewin.interfaces.OnDetectListener;
import com.basewin.services.CardBinder;
import com.basewin.services.ServiceManager;
import com.basewin.utils.BCDHelper;

public class GasCardDevice {
	private final String TAG = "GasCardDevice";
	private static GasCardDevice gasCardDevice;
	private static CardBinder cardBinder;
	
	public GasCardDevice(){
		
	}
	
	public static GasCardDevice getInstance(){
		 if (gasCardDevice == null)
	            return new GasCardDevice();
	        return new GasCardDevice();
	}
	
	/**
	 * 打开
	 * @return
	 */
	public boolean open(){
		Log.d(TAG, "---open---");
		boolean flag = false;
		try {
			cardBinder = ServiceManager.getInstence().getCard();
			flag = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			flag = false;
			Log.d(TAG, "GasCardDevice open error");
			e.printStackTrace();
		}
		return flag;
	}
	
	/**
	 * 卡检测
	 * @return
	 */
	public void check(final OnDetectListener listener){
		Log.d(TAG, "---check---");
    	cardBinder.openMemoryAndDetect(5000,new OnDetectListener() {
			@Override
			public void onSuccess(int arg0) {
				// TODO Auto-generated method stub
				Log.d(TAG, "---is success---");
				try {
					ServiceManager.getInstence().getCard().resetCard();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				listener.onSuccess(arg0);
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				Log.d(TAG, "---is error---");
				listener.onError(arg0,arg1);
			}
		});

	}
	
	/**
	 * 读卡
	 * @param area
	 * @param position
	 * @param length
	 * @return
	 */
	public String readCardInfo(int area,int position,int length){
		byte[] data = cardBinder.readMemory4442(position, length,area);
		Log.d(TAG, "area is: " + area + ",position is:" + position + ",length is:" + length);
		return BCDHelper.bcdToString(data, 0, data.length);
	}
	
	/**
	 * 写卡
	 * @param password
	 * @param area
	 * @param position
	 * @param data
	 * @return
	 */
	public boolean write(String password,int area,int position,String data){
		boolean rst=false;
		if (pwdVerify(password)) {
			rst=cardBinder.writeMemory4442(position, BCDHelper.stringToBcd(data, data.length()),area);
		}
		return rst; 
	}
	
	/**
	 * 写卡
	 * @param password
	 * @param area
	 * @param position
	 * @param data
	 * @return
	 */
	public boolean write(int area,int position,String data){
		boolean rst=false;
		rst=cardBinder.writeMemory4442(position, BCDHelper.stringToBcd(data, data.length()),area);
		return rst; 
	}
	
	/**
	 * 密码验证
	 * @param password
	 * @return
	 */
	public boolean pwdVerify(String password){
		Log.d(TAG, "---pwdVerify---");
		byte[] code= BCDHelper.stringToBcd(password, password.length());
		boolean rst=false;
		try {
			rst=cardBinder.VerifyMemory4442(code);
			Log.d(TAG, "---pwdVerify---"+rst);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rst;
	}
	
	/**
	 * 密钥校验剩余次数
	 * @return
	 */
	public int pacPwdTime()
	{
		return cardBinder.pacMemory4442();
	}
	
	/**
	 * 更新密码
	 * @param passwd
	 * @return
	 */
	public boolean updatePwd(String passwd) {
		Log.d(TAG, "---updatePwd---");
		byte[] code= BCDHelper.stringToBcd(passwd, passwd.length());
		boolean rst=false;
		try {
			rst=cardBinder.updateMemory4442(code);
			Log.d(TAG, "---updatePwd---"+rst);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rst;
	}
	/**
	 * 关闭
	 * @return
	 */
	public boolean close(){
		gasCardDevice = null;
		return true;
	}
}
