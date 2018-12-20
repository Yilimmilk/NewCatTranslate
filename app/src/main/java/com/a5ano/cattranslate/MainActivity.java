package com.a5ano.cattranslate;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {

    private EditText editTextInput;
    private FloatingActionButton fabSend;
    private EditText editTextResult;
    private Spinner spinnerFrom;
    private Spinner spinnerTo;
    private String from = "auto";
    private String to = "auto";
    private ClipboardManager clip;
    private CardView cardView;
    private ProgressDialog progressDialog;

    String[] language = {"auto", "zh", "en", "yue", "wyw", "jp", "kor", "fra", "spa", "th", "ara", "ru", "pt", "de", "it", "el", "nl", "pl", "bul", "est", "dan", "fin", "cs", "rom"
            , "slo", "swe", "hu", "cht"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        editTextInput = (EditText) findViewById(R.id.editText_Input);
        fabSend = (FloatingActionButton) findViewById(R.id.fab);
        editTextResult = (EditText) findViewById(R.id.editText_Result);
        spinnerFrom = (Spinner) findViewById(R.id.spinner_from);
        spinnerTo = (Spinner) findViewById(R.id.spinner_to);

        fabSend.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                progressDialog = new ProgressDialog(MainActivity.this);//1.创建一个ProgressDialog的实例
                progressDialog.setTitle("请稍候...");//2.设置标题
                progressDialog.setMessage("正在获取结果...（若长时间无反应请检查网络）");//3.设置显示内容
                progressDialog.setCancelable(false);//4.设置可否用back键关闭对话框
                progressDialog.setCanceledOnTouchOutside(true);
                progressDialog.show();//5.将ProgessDialog显示出来
                final String request = editTextInput.getText().toString();
                RequestUtils requestUtils = new RequestUtils();
                if (!request.isEmpty()) {
                    try {
                        requestUtils.translate(request, from, to, new HttpCallBack() {
                            @Override
                            public void onSuccess(String result) {
                                editTextResult.setText(result);
                                progressDialog.dismiss();
                            }

                            @Override
                            public void onFailure(String exception) {
                                editTextResult.setText(exception);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Snackbar.make(fabSend, "已获取翻译结果", Snackbar.LENGTH_LONG)
                            .setAction("复制翻译结果", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ClipboardManager clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                    clip.setText(editTextResult.getText());
                                    Snackbar.make(fabSend, "已复制翻译结果", Snackbar.LENGTH_LONG)
                                            .show();
                                }
                            })
                            .setActionTextColor(Color.GREEN)
                            .setDuration(4000).show();
                } else {
                    Snackbar.make(fabSend, "请输入要翻译的内容", Snackbar.LENGTH_LONG)
                            .show();

                }
            }
        });

        spinnerFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                from = language[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                to = language[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    protected void onStart() {

        Log.i("MainActivity", "onStart");

        if (!MainActivity_NetworkUtil.isNetworkAvailable(this)) {
            showSetNetworkUI(this);
        } else {
            //Toast.makeText(this, "连接服务器...", Toast.LENGTH_SHORT).show();
        }

        super.onStart();
    }

    protected void onResume() {

        Log.i("MainActivity", "onStart");
        super.onResume();
    }

    /**
     * 打开设置网络界面
     */
    public void showSetNetworkUI(final Context context) {
        // 提示对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog show = builder.setTitle("网络检查")
                .setMessage("您好像未打开网络，是否打开设置?")
                .setCancelable(false)
                .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        Intent intent = null;
                        //判断手机系统的版本 即API大于10 就是3.0或以上版本
                        if (Build.VERSION.SDK_INT > 10) {
                            intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                        } else {
                            intent = new Intent();
                            ComponentName component = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
                            intent.setComponent(component);
                            intent.setAction("android.intent.action.VIEW");
                        }
                        context.startActivity(intent);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                }).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("关于");
            builder.setMessage("CatTranslate\n开发者:Yl\nQQ:2510355993\n版本号1.0\n\n介绍：\n一个非常简单的翻译软件，支持二十六种语言的翻译（需要联网），使用百度翻译API。\n支持AndroidWear手表端。");
            builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }

        if (id == R.id.action_update_info) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("更新日志   1.0");
            builder.setMessage("重制版，2018-12-20\nN:1192246856");
            builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }

        if (id == R.id.action_youdao) {
            Intent intent = new Intent(MainActivity.this, youdao.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

}