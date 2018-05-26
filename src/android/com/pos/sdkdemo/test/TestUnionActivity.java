package com.pos.sdkdemo.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.bouncycastle.util.encoders.Base64;

import com.basewin.aidl.OnPrinterListener;
import com.basewin.define.ConstParam;
import com.basewin.define.KeyType;
import com.basewin.interfaces.OnDetectListener;
import com.basewin.log.LogUtil;
import com.basewin.print.DataCollecter;
import com.basewin.printService.PrintCmd;
import com.basewin.printService.PrintParams;
import com.basewin.printService.PrintService;
import com.basewin.printService.PrintStack;
import com.basewin.printService.PrintService.PrintEventListener;
import com.basewin.services.ServiceManager;
import com.basewin.utils.AppTools;
import com.basewin.utils.AppUtil;
import com.basewin.utils.BCDHelper;
import com.basewin.utils.KeyTools;
import com.basewin.utils.SaveBitmap;
import com.basewin.utils.TimerCountTools;
import com.basewin.zxing.utils.QRUtil;
import com.imagealgorithmlab.barcode.a;
import com.pos.sdk.accessory.PosAccessoryManager;
import com.pos.sdk.accessory.PosAccessoryManager.EventListener;
import com.pos.sdk.printer.PosPrinter;
import com.pos.sdk.printer.PosPrinterInfo;
import com.pos.sdk.printer.PosPrinter.Parameters;
import com.pos.sdk.utils.PosByteArray;
import com.pos.sdkdemo.R;
import com.sun.corba.se.impl.ior.NewObjectKeyTemplateBase;
import com.sun.org.apache.bcel.internal.generic.NEW;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract.Contacts.Data;
import android.view.KeyEvent;
import android.view.View;


/**
 * activity for union test only
 * @author liudy
 *
 */
public class TestUnionActivity extends Activity{
	
	private PosPrinter mPosPrinter = null;
	
	private PosAccessoryManager posAccessoryManager = null;
	private PrintEventListener printEventListener = new PrintEventListener();
	public class PrintEventListener implements EventListener
	{

		@Override
		public void onError(PosAccessoryManager arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub
			LogUtil.i(getClass(), "onError "+arg1+" "+arg2);
		}

		@Override
		public void onInfo(PosAccessoryManager arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub
			LogUtil.i(getClass(), "onInfo "+arg1+" "+arg2);
		}

		@Override
		public void onTransmitRawCmdRet(PosAccessoryManager arg0, byte[] arg1) {
			// TODO Auto-generated method stub
			LogUtil.i(getClass(), "onTransmitRawCmdRet "+BCDHelper.hex2DebugHexString(arg1, arg1.length));
		}
		
	}

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		if (mPosPrinter == null)
				// 默认使用我们自己的
				mPosPrinter = PosPrinter.open(0);
		posAccessoryManager = PosAccessoryManager.getDefault();
		
	};
	
	/**
	 * for test
	 * @param view
	 * @throws IOException 
	 */
	public void test(View view) throws IOException
	{	
		Parameters params;
		try {
			params = mPosPrinter.getParameters();
			LogUtil.i(getClass(), "获取打印机params = [HeatPoint]" + params.getPrintHeatPoint()+"[gray]"+ params.getPrintGray());
			params.setPrintHeatPoint(192);
			params.setPrintGray(600);
			LogUtil.i(getClass(), "设置打印机params = [HeatPoint]" + params.getPrintHeatPoint()+"[gray]"+ params.getPrintGray());
			mPosPrinter.setParameters(params);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		LogUtil.e(getClass(), "onKeyDown [keyCode]"+keyCode);
		return super.onKeyDown(keyCode, event);
	}
}
