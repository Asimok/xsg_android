package xsg.book.login_regist;


import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;



import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xsg.book.R;
import xsg.book.tools.MyNotification;

public class Regist extends AppCompatActivity implements View.OnClickListener {
    EditText mima, Name, PhoneNumber, Email,  remima;
    RadioGroup Sex;
    RadioButton man, woman;
    Button zhuce;
    private OkHttpClient okhttpClient;
    private String mima1, Name1, PhoneNumber1, Email1, remima1;
    private int Sex1 = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wty_regist_layout);
        initView();

    }

    private void initView() {

        mima = findViewById(R.id.mima);
        remima = findViewById(R.id.remima);
        Name = findViewById(R.id.Name);
        Sex = findViewById(R.id.Sex);
        PhoneNumber = findViewById(R.id.PhoneNumber);
        Email = findViewById(R.id.Email);
        zhuce = findViewById(R.id.zhuce);
        man = findViewById(R.id.man);
        woman = findViewById(R.id.woman);

        zhuce.setOnClickListener(this);
        // 取出号码

        Sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                // 获取用户选中的性别
                //String sex = "";
                switch (checkedId) {
                    case R.id.man:
                        Sex1 = 1;
                        break;
                    case R.id.woman:
                        Sex1 = 0;
                        break;
                    default:
                        break;
                }

                // 消息提示.
                if (Sex1 == 1) {
                    Toast.makeText(Regist.this,
                            "选择的性别是：男", Toast.LENGTH_SHORT).show();
                } else Toast.makeText(Regist.this,
                        "选择的性别是：女", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onClick(View v) {



        mima1 = mima.getText().toString().trim();
        remima1 = remima.getText().toString().trim();
        Name1 = Name.getText().toString().trim();

        PhoneNumber1 = PhoneNumber.getText().toString().trim();
        Email1 = Email.getText().toString().trim();


        if (TextUtils.isEmpty(Name1)) {
            Toast.makeText(this, "请输入昵称", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Sex1 == 3) {
            Toast.makeText(this, "请选择性别", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(PhoneNumber1)) {
            Toast.makeText(this, "请输入电话号码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(Email1)) {
            Toast.makeText(this, "请输入邮箱", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(mima1)) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!remima1.equals(mima1)) {
            Toast.makeText(this, "两次输入密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }


        new Thread(new Runnable() {
            @Override
            public void run() {
//                sendRequest(zhanghu.getText().toString(),mima.getText().toString(),
//                          Name.getText().toString()),Sex.getText().toString(),PhoneNumber.getText().toString(),Email.getText().toString(),Ministry.getText().toString());
//            }
                sendRequest( mima1, Name1, Sex1, PhoneNumber1, Email1);
            }
        }).start();
    }

    private void sendRequest( String mima1, String Name1, int Sex1, String PhoneNumber1, String Email1) {
        Map map = new HashMap();

        map.put("mima", mima1);
        map.put("Name", Name1);
        map.put("Sex", Sex1);
        map.put("PhoneNumber", PhoneNumber1);
        map.put("Email", Email1);


        JSONObject jsonObject = new JSONObject(map);
        String jsonString = jsonObject.toString();

        RequestBody body = RequestBody.create(null, jsonString);//以字符串方式
        okhttpClient = new OkHttpClient();
        final Request request = new Request.Builder()

                .url("http://192.168.31.114:8080/xsg_book/RegistServlet")

                .post(body)
                .build();
        Call call = okhttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Regist.this, "连接服务器失败！", Toast.LENGTH_SHORT).show();
                    }
                });
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String res = response.body().string();//获取到传过来的字符串
                try {
                    JSONObject jsonObj = new JSONObject(res);
                    String status = jsonObj.getString("status");

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
                if (status.equals("0")) {
                    MyNotification notify = new MyNotification(getApplicationContext());
                    notify.MyNotification("智能会议室", "注册成功", R.drawable.iconsmall, "regist", "注册", 10, "注册");
                    Toast.makeText(Regist.this, "注册成功！", Toast.LENGTH_SHORT).show();
                    login();
                    finish();

                } else if (status.equals("-1")) {
                    Toast.makeText(Regist.this, "信息不存在，注册失败！", Toast.LENGTH_SHORT).show();
                } else if (status.equals("-4")) {
                    Toast.makeText(Regist.this, "该员工号已注册！请重新输入！", Toast.LENGTH_SHORT).show();
                } else if (status.equals("-3")) {
                    Toast.makeText(Regist.this, "您不是该公司员工！", Toast.LENGTH_SHORT).show();
                } else if (status.equals("-2")) {
                    Toast.makeText(Regist.this, "账户名非法！请重新登录", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void login() {
        Intent intent;
        intent = new Intent(this, Login_noToken.class);
        startActivityForResult(intent, 0);
        this.finish();
    }

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
}

