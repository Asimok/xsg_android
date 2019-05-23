package xsg.book.login_regist;


import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xsg.book.R;
import xsg.book.tools.userDBHelper;

public class Login_noToken extends AppCompatActivity implements View.OnClickListener {
    EditText zhanghu, mima;
    CheckBox CK;
    Button login;
    Map<String, String> userInfo, countInfo, countlogin;
    String count = "0", logincount = "0";
    private OkHttpClient okhttpClient;
    private String zhanghu2, mima2, Token;
    private userDBHelper helper1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        initView();

    }

    private void initView() {
        helper1 = new userDBHelper(this);
        zhanghu = findViewById(R.id.zhanghao);
        mima = findViewById(R.id.mima);
        CK = findViewById(R.id.checkBox);
        login = findViewById(R.id.login);
        login.setOnClickListener(this);
        // 记住密码功能
        try {
            if (userInfo != null) {
                // 显示在界面上
                if (userInfo.get("number").equals("请输入员工号")) {
                    zhanghu.setHint("请输入员工号");
                } else {
                    zhanghu.setText(userInfo.get("number"));
                }


                if (userInfo.get("password").equals("请输入密码")) {
                    mima.setHint("请输入密码");
                } else {
                    mima.setText(userInfo.get("password"));
                }


            }
        } catch (Exception e) {
        }
    }


    public void check(View v) {
    }




    @Override
    public void onClick(View v) {

        String number = zhanghu.getText().toString().trim();
        zhanghu2 = number;
        String password = mima.getText().toString();
        mima2 = password;


        if (TextUtils.isEmpty(number)) {
            Toast.makeText(this, "请输入员工号", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (CK.isChecked()) {

            } else {
                number = "请输入员工号";
                password = "请输入密码";

               // saveDeviceInfo.saveUserInfo(this, number, password);
            }
        } catch (Exception e) {
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                sendRequest(zhanghu.getText().toString(), mima.getText().toString(), Token);
            }
        }).start();
    }

    private void sendRequest(String zhanghu1, String mima1, final String Token2) {
        Map map = new HashMap();
        map.put("zhanghu", zhanghu1);
        map.put("mima", mima1);
        // map.put("Token",Token2);

        JSONObject jsonObject = new JSONObject(map);
        String jsonString = jsonObject.toString();
//        Log.d("这将JSON对象转换为json字符串", jsonString);
        RequestBody body = RequestBody.create(null, jsonString);  //以字符串方式
        okhttpClient = new OkHttpClient();
        final Request request = new Request.Builder()

                .url("http://192.168.31.114:8080/xsg_book/LoginServlet")

                .post(body)
                .build();
        okhttp3.Call call = okhttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Login_noToken.this, "连接服务器失败！", Toast.LENGTH_SHORT).show();
                    }
                });
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {

                String res = response.body().string();//获取到传过来的字符串
                try {
                    JSONObject jsonObj = new JSONObject(res);
                    String status = jsonObj.getString("Status");

                    showRequestResult(status);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void showRequestResult(final String status) {
        runOnUiThread(new Runnable() {
            @Override
            /**
             * 实时更新，数据库信息改变时，客户端内容发生改变
             */
            public void run() {
                if (status.equals("-1")) {
                    Toast.makeText(Login_noToken.this, "密码错误!", Toast.LENGTH_SHORT).show();
                } else if (status.equals("0")) {
                    Toast.makeText(Login_noToken.this, "登录成功！", Toast.LENGTH_LONG).show();
                   // finish();
                    if (userselect()[0] == null) {
                        insertUser(zhanghu2,  zhanghu.getText().toString());
                    } else
                        userupdate(zhanghu2, mima.getText().toString());

                } else if (status.equals("-2")) {
                    Toast.makeText(Login_noToken.this, "账户名不存在！请注册", Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

    public void zhuce(View view) {
        Intent intent;
        intent = new Intent(this, Regist.class);
        startActivityForResult(intent, 0);
    }


    public void relog() {
        Intent intent;
        intent = new Intent(this, Login_noToken.class);
        startActivityForResult(intent, 0);
        finish();
    }




//    public void forgetmima(View view) {
//        Intent intent;
//        intent = new Intent(this, inputmail.class);
//        startActivityForResult(intent, 0);
//        finish();
//    }

//    public void main() {
//        Intent intent;
//        intent = new Intent(this, tab.class);
//        startActivityForResult(intent, 0);
//        finish();
//    }






    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        //非默认值
        if (newConfig.fontScale != 1) {
            getResources();
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public Resources getResources() {//还原字体大小
        Resources res = super.getResources();
        //非默认值
        if (res.getConfiguration().fontScale != 1) {
            Configuration newConfig = new Configuration();
            newConfig.setToDefaults();//设置默认
            res.updateConfiguration(newConfig, res.getDisplayMetrics());
        }
        return res;
    }



    public String[] userselect() {

        SQLiteDatabase db = helper1.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from user", null);
        String zhanghao = null;
        String name = null;
        while (cursor.moveToNext()) {
            zhanghao = cursor.getString(cursor.getColumnIndex("zhanghao"));
            name = cursor.getString(cursor.getColumnIndex("name"));
        }
        String[] user = {zhanghao, name};
        Log.d("ddd", "查出来的  账户" + user[0] + "  姓名   " + user[1]);
        db.close();
        return user;
    }
    public void userupdate(String zhanghu3, String name3) {


        //自定义更新
        SQLiteDatabase db = helper1.getWritableDatabase();
        ContentValues values = new ContentValues();
        Log.d("ddd", "更新的账户   " + zhanghu3);
        values.put("zhanghao", zhanghu3);
        values.put("name", name3);
        int i = db.update("user", values, null, null);
//        if (i == 0) {
//            Toast.makeText(this, "更新用户信息不成功", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, "更新用户信息成功", Toast.LENGTH_SHORT).show();
//        }
        db.close();
    }
    public void insertUser(String zhanghu3, String name3) {
        //自定义增加数据
        SQLiteDatabase db1 = helper1.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("zhanghao", zhanghu3);
        values.put("name", name3);
        long l = db1.insert("user", null, values);


        db1.close();
    }
}


