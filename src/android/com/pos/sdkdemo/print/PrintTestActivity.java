package com.pos.sdkdemo.print;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.basewin.aidl.OnPrinterListener;
import com.basewin.define.ConstParam;
import com.basewin.define.FontsType;
import com.basewin.define.PrinterInfo;
import com.basewin.services.PrinterBinder;
import com.basewin.services.ServiceManager;
import com.pos.sdkdemo.R;
import com.pos.sdkdemo.base.BaseActivity;
import com.pos.sdkdemo.interfaces.OnChoseListener;
import com.pos.sdkdemo.utils.TimerCountTools;
import com.pos.sdkdemo.widgets.EnterDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * Created by lyw on 2016/12/12.
 * print function:
 * print the text,one-dimension,two-dimension or picture on pager,you can input text or select a picture from you deivce
 * to print,and you can set the type of printing ,textSie,fontsType or positon.
 */

public class PrintTestActivity extends BaseActivity implements View.OnClickListener {
    private EditText et_printTextContent, et_printTextSize, et_printGray, et_lineSpace, et_printoneContent, et_printtwoContent;
    private Spinner sp_printTextSize, sp_printTextPosition, sp_fontsType, sp_printPosition1, sp_printPosition2, sp_printHeight1, sp_printSize1, sp_printSize2;
    private ImageView iv_testImage;
    private LinearLayout ll_selectprinter;
    private CheckBox cb_italic, cb_bold;
    private TextView tv_printerinfo;
    private String printTextContent, printTextSize, ePrintTextSize, printGray, printPosition, fontsType, italic, bold, lineSpace, printoneContent, printtwoContent, onePosition, twoPosition, oneHeight, oneSize, twoSize;
    private Button btn_print_demo, btn_print, btn_selectPic, btn_print_text, btn_print_one, btn_print_two, btn_print_pic, btn_print_derect;
    private final String TAG = this.getClass().getSimpleName();
    private PrinterListener printer_callback = new PrinterListener();
    JSONObject printJson = new JSONObject();
    private TimerCountTools timeTools;
    private Bitmap bm;
    private byte[] mContent;
    private static final int REQUEST_CODE_GALLERY = 100;

    @Override
    protected View onCreateView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.activity_print, null);
    }

    @Override
    protected void onInitView() {
        initView();
    }

    private void initView() {
        iv_testImage = (ImageView) findViewById(R.id.iv_testImage);
        tv_printerinfo = (TextView) findViewById(R.id.tv_printerinfo);
        ll_selectprinter = (LinearLayout) findViewById(R.id.select_printer);
        et_printoneContent = (EditText) findViewById(R.id.et_printoneContent);
        et_printtwoContent = (EditText) findViewById(R.id.et_printtwoContent);
        btn_selectPic = (Button) findViewById(R.id.btn_selectPic);
        btn_selectPic.setOnClickListener(this);
        btn_print_text = (Button) findViewById(R.id.btn_print_text);
        btn_print_one = (Button) findViewById(R.id.btn_print_onedimension);
        btn_print_two = (Button) findViewById(R.id.btn_print_twodimension);
        btn_print_pic = (Button) findViewById(R.id.btn_print_pic);
        btn_print_demo = (Button) findViewById(R.id.btn_print_demo);
        btn_print_derect = (Button) findViewById(R.id.btn_print_derect);
        btn_print_text.setOnClickListener(this);
        btn_print_one.setOnClickListener(this);
        btn_print_pic.setOnClickListener(this);
        btn_print_two.setOnClickListener(this);
        btn_print_demo.setOnClickListener(this);
        btn_print_derect.setOnClickListener(this);
        ll_selectprinter.setOnClickListener(this);

        et_printTextContent = (EditText) findViewById(R.id.et_printTextContent);
        optimizSoftKeyBoard(et_printTextContent);
        et_printTextSize = (EditText) findViewById(R.id.et_printTextSize);
        et_printGray = (EditText) findViewById(R.id.et_printGray);
        et_lineSpace = (EditText) findViewById(R.id.et_lineSpace);
        btn_print = (Button) findViewById(R.id.btn_print);
        btn_print.setOnClickListener(this);

        onePosition = "center";
        //select the printing position of the one-dimesion,default position is centet
        sp_printPosition1 = (Spinner) findViewById(R.id.sp_printPosition1);
        sp_printPosition1.setSelection(1);
        sp_printPosition1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        onePosition = "left";
                        break;
                    case 1:
                        onePosition = "center";
                        break;
                    case 2:
                        onePosition = "right";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                onePosition = "center";
            }
        });
        //select the printing height of the one-dimesion ,default height is 3.
        sp_printHeight1 = (Spinner) findViewById(R.id.sp_printHeight1);
        sp_printHeight1.setSelection(2);
        sp_printHeight1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        oneHeight = "1";
                        break;
                    case 1:
                        oneHeight = "2";
                        break;
                    case 2:
                        oneHeight = "3";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                oneHeight = "2";
            }
        });
        //select the printing size of the one-dimesion , default value is 3.
        sp_printSize1 = (Spinner) findViewById(R.id.sp_printSize1);
        sp_printSize1.setSelection(2);
        sp_printSize1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        oneSize = "1";
                        break;
                    case 1:
                        oneSize = "2";
                        break;
                    case 2:
                        oneSize = "3";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                oneSize = "2";
            }
        });
      
        //select the printing position of the two-dimesion,default position is right
        sp_printPosition2 = (Spinner) findViewById(R.id.sp_printPosition2);
        sp_printPosition2.setSelection(2);
        sp_printPosition2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        twoPosition = "left";
                        break;
                    case 1:
                        twoPosition = "center";
                        break;
                    case 2:
                        twoPosition = "right";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                twoPosition = "center";
            }
        });
        //select the
        sp_printSize2 = (Spinner) findViewById(R.id.sp_printSize2);
        sp_printSize2.setSelection(7);
        sp_printSize2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        twoSize = "1";
                        break;
                    case 1:
                        twoSize = "2";
                        break;
                    case 2:
                        twoSize = "3";
                        break;
                    case 3:
                        twoSize = "4";
                        break;
                    case 4:
                        twoSize = "5";
                        break;
                    case 5:
                        twoSize = "6";
                        break;
                    case 6:
                        twoSize = "7";
                        break;
                    case 7:
                        twoSize = "8";
                        break;
                    default:
                        twoSize = "8";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        printPosition = "center";
        italic = "0";
        bold = "0";
        //Select Italic  property
        cb_italic = (CheckBox) findViewById(R.id.cb_italic);
        cb_italic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    italic = "1";
                } else {
                    italic = "0";
                }
            }
        });
        //Select Bold  property
        cb_bold = (CheckBox) findViewById(R.id.cb_bold);
        cb_bold.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    bold = "1";
                } else {
                    bold = "0";
                }
            }
        });
        //Set TextSize  property
        sp_printTextSize = (Spinner) findViewById(R.id.sp_printTextSize);
        sp_printTextSize.setSelection(2);
        sp_printTextSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                et_printTextSize.setText("");//if select a  value ,make the edit_input blank
                switch (position) {
                    case 0:
                        printTextSize = "1";//small
                        break;
                    case 1:
                        printTextSize = "2";//middle
                        break;
                    case 2:
                        printTextSize = "3";//big
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if (ePrintTextSize.length() != 0) {
                    printTextSize = ePrintTextSize;
                } else {
                    printTextSize = "2";
                }
            }
        });
        //Set TextPosition  property
        sp_printTextPosition = (Spinner) findViewById(R.id.sp_printTextPosition);
        sp_printTextPosition.setSelection(2);
        sp_printTextPosition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        printPosition = "left";
                        break;
                    case 1:
                        printPosition = "center";
                        break;
                    case 2:
                        printPosition = "right";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                printPosition = "center";//default position is center
            }
        });
        //Set fontsType  property
        sp_fontsType = (Spinner) findViewById(R.id.sp_fontsType);
        sp_fontsType.setSelection(0);
        sp_fontsType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        try {
                            fontsType = "";
                            ServiceManager.getInstence().getPrinter().setPrintFont(FontsType.simsun);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 1:
                        try {
                            ServiceManager.getInstence().getPrinter().setPrintFontByAsserts("songti.ttf");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        fontsType = "";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //fresh printer info
        try {
            tv_printerinfo.setText("printer info fresh after select printer,make usre your system version is new or demo will crash!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("NewApi")
	private void getValue() {
        printTextContent = et_printTextContent.getText().toString().trim();
        if (printTextContent.length() == 0) {
            printTextContent = "ABC";//default content is "ABC"
        }
        ePrintTextSize = et_printTextSize.getText().toString().trim();
        if (!ePrintTextSize.isEmpty()) {
            printTextSize = ePrintTextSize;// when editInput is not blank ,using the value to set the textSize
        }
        printGray = et_printGray.getText().toString().trim();
        if (printGray.length() == 0) {
            printGray = "2000";//the "printGray" default value is 2000
        }
        lineSpace = et_lineSpace.getText().toString().trim();
        if (lineSpace.length() == 0) {
            lineSpace = "2";
        }
        // validate
        printoneContent = et_printoneContent.getText().toString().trim();
        if (printoneContent.isEmpty()) {
            printoneContent = "12345678";
        }
        printtwoContent = et_printtwoContent.getText().toString().trim();
        if (printtwoContent.isEmpty()) {
            printtwoContent = "111111111";
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_print:
                printAll();
                break;
            case R.id.btn_selectPic:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
                break;
            case R.id.btn_print_text:
                printText();
                break;
            case R.id.btn_print_onedimension:
                printOne();
                break;
            case R.id.btn_print_twodimension:
                printTwo();
                break;
            case R.id.btn_print_pic:
                printPic();
                break;
            case R.id.btn_print_demo:
                printDemo();
                break;
            case R.id.btn_print_derect:
                printDerect();
                break;
            case R.id.select_printer:
                selectPrinter();
                break;

        }
    }

    /**
     * print all content
     */
    public void printAll() {
        getValue();
        LOGD("Print all");
        LOGD("TextContent:" + printTextContent + "  size:" + printTextSize + "  position:" + printPosition + "  bold:" + bold + "  italic:" + italic);
        JSONArray printTest = new JSONArray();
        // add text printer
        JSONObject json1 = new JSONObject();
        try {
        	
            // add picture
            JSONObject json11 = new JSONObject();
            json11.put("content-type", "jpg");
            json11.put("position", "center");
            
            // add picture 2
            JSONObject json12 = new JSONObject();
            json11.put("content-type", "jpg");
            json11.put("position", "center");
            
            
            // Add text printing
            json1.put("content-type", "txt");
            json1.put("content", printTextContent);
            json1.put("size", printTextSize);
            json1.put("position", printPosition);
            json1.put("offset", "0");
            json1.put("bold", bold);
            json1.put("italic", italic);
            json1.put("height", "-1");

            // Add one dimensional code printing
            JSONObject json3 = new JSONObject();
            json3.put("content-type", "one-dimension");
            json3.put("content", printoneContent);
            json3.put("size", oneSize);
            json3.put("position", onePosition);
            json3.put("height", oneHeight);
            
            // Add two dimensional code printing
            JSONObject json2 = new JSONObject();
            json2.put("content-type", "two-dimension");
            json2.put("content", printtwoContent);
            json2.put("size", twoSize);
            json2.put("position", twoPosition);


            timeTools = new TimerCountTools();
            timeTools.start();
            ServiceManager.getInstence().getPrinter().setPrintGray(Integer.valueOf(printGray));//set Gray
            ServiceManager.getInstence().getPrinter().setLineSpace(Integer.valueOf(lineSpace));//set lineSpace

            printTest.put(json11);
            printTest.put(json12);
            printTest.put(json1);
            printTest.put(json3);
            printTest.put(json2);

            printJson.put("spos", printTest);
            // 设置底部空3行
            // Set at the bottom of the empty 3 rows
            ServiceManager.getInstence().getPrinter().printBottomFeedLine(3);
            Bitmap qr = BitmapFactory.decodeResource(getResources(), R.drawable.test);
            Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.print_title);
            Bitmap[] bitmaps = null;
            if (bm == null) {// if  you has not choosen  a picture ,we will print the default picture.
                bitmaps = new Bitmap[]{qr.createScaledBitmap(qr, 240, 240, true),logo};
            } else {
                bitmaps = new Bitmap[]{qr.createScaledBitmap(bm, 240, bm.getHeight(), true),logo};
            }
            ServiceManager.getInstence().getPrinter().print(printJson.toString(), bitmaps, printer_callback);
            LOGD("Print success");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * print text content
     */
    public void printText() {
        getValue();
        LOGD("Print text");
        JSONArray printTest = new JSONArray();
        LOGD("content:" + printTextContent + "  size:" + printTextSize + "  position:" + printPosition + "  bold:" + bold + "  italic:" + italic);
        // add text printer
        JSONObject json1 = new JSONObject();
        try {
            // Add text printing
            json1.put("content-type", "txt");
            json1.put("content", printTextContent);
            json1.put("size", printTextSize);
            json1.put("position", printPosition);
            json1.put("offset", "0");
            json1.put("bold", bold);
            json1.put("italic", italic);
            json1.put("height", "-1");


            timeTools = new TimerCountTools();
            timeTools.start();
            ServiceManager.getInstence().getPrinter().setPrintGray(Integer.valueOf(printGray));//set Gray
            ServiceManager.getInstence().getPrinter().setLineSpace(Integer.valueOf(lineSpace));//set lineSpace


            printTest.put(json1);
            printJson.put("spos", printTest);
            // 设置底部空3行
            // Set at the bottom of the empty 3 rows
//            ServiceManager.getInstence().getPrinter().printBottomFeedLine(3);
            ServiceManager.getInstence().getPrinter().print(printJson.toString(), null, printer_callback);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * print oneDimension content
     */
    public void printOne() {
        getValue();
        LOGD("Print one-dimension");
        JSONArray printTest = new JSONArray();
        LOGD("oneDimension-content:" + printoneContent + "  size:" + oneSize + "  position:" + onePosition + " height:" + oneHeight);
        // add text printer
        try {

            // Add one dimensional code printing
            JSONObject json3 = new JSONObject();
            json3.put("content-type", "one-dimension");
            json3.put("content", printoneContent);
            json3.put("size", oneSize);
            json3.put("position", onePosition);
            json3.put("height", oneHeight);


            timeTools = new TimerCountTools();
            timeTools.start();
            ServiceManager.getInstence().getPrinter().setPrintGray(Integer.valueOf(printGray));//set Gray
            ServiceManager.getInstence().getPrinter().setLineSpace(Integer.valueOf(lineSpace));//set lineSpace


            printTest.put(json3);

            printJson.put("spos", printTest);
            // 设置底部空3行
            // Set at the bottom of the empty 3 rows
//            ServiceManager.getInstence().getPrinter().printBottomFeedLine(3);

            ServiceManager.getInstence().getPrinter().print(printJson.toString(), null, printer_callback);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * print twoDimension content
     */
    public void printTwo() {
        getValue();
        LOGD("Print two-dimension");
        JSONArray printTest = new JSONArray();
        LOGD("twoDimension-content:" + printtwoContent + "  size:" + twoSize + "  position:" + twoPosition);
        // add text printer
        try {

            // Add two dimensional code printing
            JSONObject json2 = new JSONObject();
            json2.put("content-type", "two-dimension");
            json2.put("content", printtwoContent);
            json2.put("size", twoSize);
            json2.put("position", twoPosition);


            timeTools = new TimerCountTools();
            timeTools.start();
            ServiceManager.getInstence().getPrinter().setPrintGray(Integer.valueOf(printGray));//set Gray
            ServiceManager.getInstence().getPrinter().setLineSpace(Integer.valueOf(lineSpace));//set lineSpace

            printTest.put(json2);
            printJson.put("spos", printTest);
            // 设置底部空3行
            // Set at the bottom of the empty 3 rows
//            ServiceManager.getInstence().getPrinter().printBottomFeedLine(3);
            ServiceManager.getInstence().getPrinter().print(printJson.toString(), null, printer_callback);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * print picture content
     */
    public void printPic() {
        getValue();
        LOGD("Print picture");
        JSONArray printTest = new JSONArray();
        // add text printer
        try {

            // add picture
            JSONObject json11 = new JSONObject();
            json11.put("content-type", "jpg");
            json11.put("position", "center");


            timeTools = new TimerCountTools();
            timeTools.start();
            ServiceManager.getInstence().getPrinter().setPrintGray(Integer.valueOf(printGray));//set Gray
            ServiceManager.getInstence().getPrinter().setLineSpace(Integer.valueOf(lineSpace));//set lineSpace
            printTest.put(json11);

            printJson.put("spos", printTest);
            // 设置底部空3行
            // Set at the bottom of the empty 3 rows
//            ServiceManager.getInstence().getPrinter().printBottomFeedLine(3);
//            Bitmap qr = BitmapFactory.decodeResource(getResources(), R.drawable.printlogo3);
            Bitmap qr = BitmapFactory.decodeStream(getAssets().open("sign.bmp"));
            Bitmap[] bitmaps = null;
            if (bm == null) {// if  you has not choosen  a picture ,we will print the default picture.
//                bitmaps = new Bitmap[]{qr.createScaledBitmap(qr, 240, 240, true)};
                bitmaps = new Bitmap[]{qr};
            } else {
//                bitmaps = new Bitmap[]{qr.createScaledBitmap(bm, 240, bm.getHeight(), true)};
                bitmaps = new Bitmap[]{qr};
            }
            ServiceManager.getInstence().getPrinter().setPrintGray(1000);
            ServiceManager.getInstence().getPrinter().print(printJson.toString(), bitmaps, printer_callback);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private JSONObject getPrintObject(String test) {
        JSONObject json = new JSONObject();
        try {
            json.put("content-type", "txt");
            json.put("content", test);
            json.put("size", "2");
            json.put("position", "left");
            json.put("offset", "0");
            json.put("bold", "0");
            json.put("italic", "0");
            json.put("height", "-1");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return json;
    }

    private JSONObject getPrintObject(String test, String size) {
        JSONObject json = new JSONObject();
        try {
            json.put("content-type", "txt");
            json.put("content", test);
            json.put("size", size);
            json.put("position", "left");
            json.put("offset", "0");
            json.put("bold", "0");
            json.put("italic", "0");
            json.put("height", "-1");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return json;
    }

    /**
     * print text content
     */
    public void printDemo() {
        try {
            LOGD("print Demo");
            // 組打印json字符串
            JSONArray printTest = new JSONArray();
            // 添加文本打印,正常
            timeTools = new TimerCountTools();
            timeTools.start();
            ServiceManager.getInstence().getPrinter().setPrintFont(FontsType.simsun);
//            ServiceManager.getInstence().getPrinter().setPrintFontByAsserts("songti.ttf");
            printTest.put(getPrintObject("------------------------------------------------"));
            printTest.put(getPrintObject("商品                    单价      数量      价格"));
            printTest.put(getPrintObject("-------------------热菜(42.00)------------------\n"));
            printTest.put(getPrintObject("\n1.意式肉酱面/直面       27.00     1/份      27.00"));
            printTest.put(getPrintObject("2.凤尾                  15.00     1/份      15.00"));
            printTest.put(getPrintObject("------------------------------------------------"));
            printTest.put(getPrintObject("合计                                 2      42.00"));
            printTest.put(getPrintObject("------------------------------------------------"));
            printTest.put(getPrintObject("手动折扣", "3"));
            printTest.put(getPrintObject("    [服务]                                  12.00"));
            printTest.put(getPrintObject("    [包间]                                   1.05"));
            printTest.put(getPrintObject("*注:[活动]后面的数字,为参加活动的商品编号"));
            printTest.put(getPrintObject("参与折扣统计", "3"));
            printTest.put(getPrintObject("iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii"));
            printTest.put(getPrintObject("mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"));
            printJson.put("spos", printTest);
            // 设置底部空3行
            printJson.put("spos", printTest);
            // 设置底部空3行
            // Set at the bottom of the empty 3 rows
            ServiceManager.getInstence().getPrinter().printBottomFeedLine(3);
            Bitmap qr = BitmapFactory.decodeResource(getResources(), R.drawable.test);

            Bitmap[] bitmaps = new Bitmap[]{qr.createScaledBitmap(qr, 240, 240, true)};
            ServiceManager.getInstence().getPrinter().print(printJson.toString(), null, printer_callback);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * print by derect interface
     */
    public void printDerect() {
        try {
            timeTools = new TimerCountTools();
            timeTools.start();
            //设置灰度值，一般2000不需要动
            //set gray
            ServiceManager.getInstence().getPrinter().setPrintGray(2000);
            //设置字库为宋体
            //set fontname
//            ServiceManager.getInstence().getPrinter().setPrintFont(FontsType.simsun);
            ServiceManager.getInstence().getPrinter().setPrintFontByAsserts("songti.ttf");
            //清空缓存
            //clear cashe
            ServiceManager.getInstence().getPrinter().cleanCache();
            //获取打印机参数，方便后续设置
            //get params of printer
            com.pos.sdk.printer.PosPrinter.Parameters params =
                    ServiceManager.getInstence().getPrinter().getParameters();
            //设置字体大小
            //set font size
            params.setFontSize(30);
            //设置字体靠左
            //set font align left
            params.setPrintAlign(0);
            //设置字体居中
            //set font align middle
            params.setPrintAlign(1);
            //设置字体靠右
            //set font align right
            params.setPrintAlign(2);
            //加粗斜体
            //Bold and itilc
            params.setFontEffet(3);
            //加粗
            //Bold
            params.setFontEffet(1);
            //斜体
            //italic
            params.setFontEffet(2);
            //正常
            //normal
            params.setFontEffet(0);
            //linespace
            params.setLineSpace(5);
            //设置打印机参数
            //set params of printer
            ServiceManager.getInstence().getPrinter().setParameters(params);
            //增加打印文本
            //add text to print cache
            ServiceManager.getInstence().getPrinter().addText("调用细颗粒直接打印");
            //增加打印文本
            //add text to print cache
            ServiceManager.getInstence().getPrinter().addText("\nprint with derect interfaces");
            //增加打印图片
            //add bmp to print cache
            ServiceManager.getInstence().getPrinter().addBitMap(ConstParam.SD_Path + "ywm.bmp");
            //增加打印图片
            //add bmp to print cache
            ServiceManager.getInstence().getPrinter().addBitMap(ConstParam.SD_Path + "ewm.bmp");
            //开始打印
            //begin to print
            ServiceManager.getInstence().getPrinter().beginPrint(printer_callback);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void selectPrinter() {
        LOGD("get printer info....");
        try {
            int printerNum = ServiceManager.getInstence().getPrinter().getPrinterNum();
            if (printerNum > 0) {
                final List<PrinterInfo> infos = ServiceManager.getInstence().getPrinter().getPrinterInfo();
                String[] printerName = new String[infos.size()];
                for (int i = 0; i < infos.size(); i++)
                    printerName[i] = infos.get(i).getName();
                new EnterDialog(PrintTestActivity.this).showListChoseDialog("please chose a printer", printerName, new OnChoseListener() {
                    @Override
                    public void Chose(int i) {
                        LOGD("select " + i + " printer");
                        try {
                            ServiceManager.getInstence().getPrinter().selectPosPrinter(infos.get(i).getId());
                            tv_printerinfo.setText(infos.get(i).toNormalString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        ContentResolver resolver = getContentResolver();
        if (requestCode == REQUEST_CODE_GALLERY) {
            try {
                //获得图片的uri
                Uri originalUri = data.getData();
                LOGD("get the uri of the picture");
                //将图片内容解析成字节数组
                mContent = PicUtils.getBytesFromInputStream(resolver.openInputStream(Uri.parse(originalUri.toString())), 3500000);
                LOGD("Parse the image into an array of bytes ");
                //Converts a byte array to a bitmap object that can be called by a ImageView (将字节数组转换为ImageView可调用的Bitmap对象)
                bm = PicUtils.getPicFromBytes(mContent, null);
                Log.d(TAG, "Converts an image content resolution to an array of ImageView ");
                if (bm != null) {
                    //he bitmap object is cut, the width of 240, height according to the geometric proportion zoom (将bitmap对象进行裁剪，宽度为240,高度按等比比例缩放)
                    bm = PicUtils.zoomImage(bm, 384);
                    bm = PicUtils.switchColor(bm);//将彩色图片转换成黑白图片
                    //show image on the activity
                    iv_testImage.setImageBitmap(bm);
                } else {
                    Toast.makeText(this, "This picture is not available. Please choose another one. ", Toast.LENGTH_SHORT).show();
                }


            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

    }


    class PrinterListener implements OnPrinterListener {
        private final String TAG = "Print";

        @Override
        public void onStart() {
            // TODO 打印开始
            // Print start
            LOGD("start print");
        }

        @Override
        public void onFinish() {
            // TODO 打印结束
            // End of the print
            LOGD("pint success");
            timeTools.stop();
            LOGD("time cost：" + timeTools.getProcessTime());
        }

        @Override
        public void onError(int errorCode, String detail) {
            // TODO 打印出错
            // print error
            LOGD("print error" + " errorcode = " + errorCode + " detail = " + detail);
            if (errorCode == PrinterBinder.PRINTER_ERROR_NO_PAPER) {
                Toast.makeText(PrintTestActivity.this, "paper runs out during printing", Toast.LENGTH_SHORT).show();
            }
            if (errorCode == PrinterBinder.PRINTER_ERROR_OVER_HEAT) {
                Toast.makeText(PrintTestActivity.this, "over heat during printing", Toast.LENGTH_SHORT).show();
            }
            if (errorCode == PrinterBinder.PRINTER_ERROR_OTHER) {
                Toast.makeText(PrintTestActivity.this, "other error happen during printing", Toast.LENGTH_SHORT).show();
            }


        }
    }

    ;


}
