package xsg.book.mainFunctions;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectChangeListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xsg.book.MainActivity;
import xsg.book.R;
import xsg.book.bean.JsonBean;
import xsg.book.login_regist.Login_noToken;
import xsg.book.login_regist.Regist;

public class releaseRequirements extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private List<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    private Thread thread;
    private static final int MSG_LOAD_DATA = 0x0001;
    private static final int MSG_LOAD_SUCCESS = 0x0002;
    private static final int MSG_LOAD_FAILED = 0x0003;
    private OkHttpClient okhttpClient;
TextView province,city,xian,changead;
Spinner bookclass,suitage;
EditText booknum,detailad,detailbook,user,telephone,bookname;
private String strbookclass,strsuitage;
    private static boolean isLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_requirements);
        initView();
        mHandler.sendEmptyMessage(MSG_LOAD_DATA);
    }


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOAD_DATA:
                    if (thread == null) {//如果已创建就不再重新创建子线程了

                        Toast.makeText(releaseRequirements.this, "Begin Parse Data", Toast.LENGTH_SHORT).show();
                        thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // 子线程中解析省市区数据
                                initJsonData();
                            }
                        });
                        thread.start();
                    }
                    break;

                case MSG_LOAD_SUCCESS:
                    Toast.makeText(releaseRequirements.this, "Parse Succeed", Toast.LENGTH_SHORT).show();
                    isLoaded = true;
                    break;

                case MSG_LOAD_FAILED:
                    Toast.makeText(releaseRequirements.this, "Parse Failed", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void initView() {

        province=findViewById(R.id.sheng);
        city=findViewById(R.id.shi);
        xian=findViewById(R.id.xian);
        changead=findViewById(R.id.changead);
        bookclass=findViewById(R.id.bookclass);
        booknum=findViewById(R.id.booknum);
        suitage=findViewById(R.id.suitage);
        detailad=findViewById(R.id.detailad);
        detailbook=findViewById(R.id.detailbook);
        user=findViewById(R.id.user);
        bookname=findViewById(R.id.bookname);
        telephone=findViewById(R.id.telephone);
        bookclass.setOnItemSelectedListener(releaseRequirements.this);
        suitage.setOnItemSelectedListener(this);
    }




    private void showPickerView() {// 弹出选择器

        OptionsPickerView pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String opt1tx = options1Items.size() > 0 ?
                        options1Items.get(options1).getPickerViewText() : "";

                String opt2tx = options2Items.size() > 0
                        && options2Items.get(options1).size() > 0 ?
                        options2Items.get(options1).get(options2) : "";

                String opt3tx = options2Items.size() > 0
                        && options3Items.get(options1).size() > 0
                        && options3Items.get(options1).get(options2).size() > 0 ?
                        options3Items.get(options1).get(options2).get(options3) : "";

                String tx = opt1tx + opt2tx + opt3tx;
                //TODO 这里有省市县
                province.setText(opt1tx);
                city.setText(opt2tx);
                xian.setText(opt3tx);
                changead.setText("  修改");
                Toast.makeText(releaseRequirements.this, tx, Toast.LENGTH_SHORT).show();
            }
        })

                .setTitleText("城市选择")
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(20)
                .build();

        /*pvOptions.setPicker(options1Items);//一级选择器
        pvOptions.setPicker(options1Items, options2Items);//二级选择器*/
        pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
        pvOptions.show();
    }

    private void initJsonData() {//解析数据

        /**
         * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
         * 关键逻辑在于循环体
         *
         * */
        String JsonData = new GetJsonDataUtil().getJson(this, "province.json");//获取assets目录下的json文件数据

        ArrayList<JsonBean> jsonBean = parseData(JsonData);//用Gson 转成实体

        /**
         * 添加省份数据
         *
         * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */
        options1Items = jsonBean;

        for (int i = 0; i < jsonBean.size(); i++) {//遍历省份
            ArrayList<String> cityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

            for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {//遍历该省份的所有城市
                String cityName = jsonBean.get(i).getCityList().get(c).getName();
                cityList.add(cityName);//添加城市
                ArrayList<String> city_AreaList = new ArrayList<>();//该城市的所有地区列表

                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                /*if (jsonBean.get(i).getCityList().get(c).getArea() == null
                        || jsonBean.get(i).getCityList().get(c).getArea().size() == 0) {
                    city_AreaList.add("");
                } else {
                    city_AreaList.addAll(jsonBean.get(i).getCityList().get(c).getArea());
                }*/
                city_AreaList.addAll(jsonBean.get(i).getCityList().get(c).getArea());
                province_AreaList.add(city_AreaList);//添加该省所有地区数据
            }

            /**
             * 添加城市数据
             */
            options2Items.add(cityList);

            /**
             * 添加地区数据
             */
            options3Items.add(province_AreaList);
        }

        mHandler.sendEmptyMessage(MSG_LOAD_SUCCESS);

    }


    public ArrayList<JsonBean> parseData(String result) {//Gson 解析
        ArrayList<JsonBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                JsonBean entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(MSG_LOAD_FAILED);
        }
        return detail;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void selectad(View view) {
        if (isLoaded) {
            showPickerView();
        } else {
            Toast.makeText(releaseRequirements.this, "Please waiting until the data is parsed", Toast.LENGTH_SHORT).show();
    }
    }

    public void publish(View view) {
        final String  struser =user.getText().toString().trim();
        final String  strtelephone = telephone.getText().toString().trim();
        final String  strprovince = province.getText().toString().trim();
        final String  strcity = city.getText().toString().trim();
        final String  strxian =xian.getText().toString().trim();
        final String  strdetailedad = detailad.getText().toString().trim();

        final String  strbooknum = booknum.getText().toString().trim();

        final String  strbookname = bookname.getText().toString().trim();
        final String  strdetailbook = detailbook.getText().toString().trim();
        if ((strprovince+strcity+strxian).equals("点击选择所属地区")) {
            Toast.makeText(this, "请选择地区", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(strdetailedad)) {
            Toast.makeText(this, "请填写详细地址", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(struser)) {
            Toast.makeText(this, "请填写姓名", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(strtelephone)) {
            Toast.makeText(this, "请填写联系方式", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(strbooknum)) {
            Toast.makeText(this, "请填写书籍数量", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(strbookname)) {
            Toast.makeText(this, "请选填写书籍名称", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(strdetailbook)) {
            Toast.makeText(this, "请书籍填写详细信息", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(strbookclass)) {
            Toast.makeText(this, "请选择书籍类别", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(strsuitage)) {
            Toast.makeText(this, "请选择年龄段", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendRequest(  struser,    strtelephone,    strprovince,
                        strcity,    strxian,    strdetailedad,     strbookclass,
                        strbooknum,    strsuitage,    strbookname,   strdetailbook);
            }
        }).start();
    }

    private void sendRequest( String user, String telephone, String province,
                              String city, String xian, String detailedad, String bookclass,
                              String booknum, String suitage, String bookname,String detailbook) {
        Map map = new HashMap();

        map.put("user", user);
        map.put("telephone", telephone);
        map.put("province", province);
        map.put("city", city);
        map.put("xian", xian);
        map.put("detailedad", detailedad);
        map.put("bookclass", bookclass);
        map.put("booknum", booknum);
        map.put("suitage", suitage);
        map.put("bookname", bookname);
        map.put("detailbook", detailbook);



        JSONObject jsonObject = new JSONObject(map);
        String jsonString = jsonObject.toString();

        RequestBody body = RequestBody.create(null, jsonString);//以字符串方式
        okhttpClient = new OkHttpClient();
        final Request request = new Request.Builder()

                .url("http://192.168.31.114:8080/xsg_book/releaseRequirements")

                .post(body)
                .build();
        Call call = okhttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(releaseRequirements.this, "连接服务器失败！", Toast.LENGTH_SHORT).show();
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
                if (status.equals("-1")) {
                    Toast.makeText(releaseRequirements.this, "发布失败!", Toast.LENGTH_SHORT).show();
                } else if (status.equals("0")) {
                    Toast.makeText(releaseRequirements.this, "发布成功！", Toast.LENGTH_LONG).show();
                    // finish();
                }

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String content = parent.getItemAtPosition(position).toString();
        switch (parent.getId()) {
            case R.id.bookclass:
                strbookclass=content;

                break;

            case R.id.suitage:
                strsuitage=content;

                break;

            default:
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
