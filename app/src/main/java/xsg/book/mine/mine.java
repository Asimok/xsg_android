package xsg.book.mine;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xsg.book.R;
import xsg.book.login_regist.Login_noToken;
import xsg.book.main_inteface.detailinfo;
import xsg.book.main_inteface.main_interface;
import xsg.book.tools.userDBHelper;

public class mine extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private List<requirements_info> data;
    private ListView myreq;
    private TextView user ,tel;
    private OkHttpClient okhttpClient;
    private userDBHelper helper1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine);
initview();
        search();
    }
    private  void initview(){
        helper1 = new userDBHelper(this);
        data=new ArrayList<requirements_info>();
        myreq= findViewById(R.id.myreqlv);
        user= findViewById(R.id.user);
        tel= findViewById(R.id.tel);
        myreq.setOnItemClickListener(this);
    }

    public void search() {
        data.clear();

        user.setText(userselect()[0]);
        tel.setText(userselect()[1]);

        new Thread(new Runnable() {
            @Override
            public void run() {
                sendRequest(userselect()[0],userselect()[1]);
            }
        }).start();
    }

    private void sendRequest(String user2, String tel2) {
        Map map = new HashMap();
        map.put("user", user2);
        map.put("telephone", tel2);

        JSONObject jsonObject = new JSONObject(map);
        String jsonString = jsonObject.toString();

        RequestBody body = RequestBody.create(null, jsonString);  //以字符串方式
        okhttpClient = new OkHttpClient();
        final Request request = new Request.Builder()

                .url("http://192.168.31.114:8080/xsg_book/mineServlet")

                .post(body)
                .build();
        okhttp3.Call call = okhttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mine.this, "连接服务器失败！", Toast.LENGTH_SHORT).show();
                    }
                });
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {

                String res = response.body().string();//获取到传过来的字符串
                Log.d("mine","moine res  "+res);
                try {
                    JSONArray jsonArray = new JSONArray(res);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObj = jsonArray.getJSONObject(i);
                        String user3      = jsonObj.getString("user");
                        String telephone  = jsonObj.getString("telephone");
                        String province   = jsonObj.getString("province");
                        String city       = jsonObj.getString("city");
                        String xian       = jsonObj.getString("xian");
                        String detailedad = jsonObj.getString("detailedad");
                        String bookclass  = jsonObj.getString("bookclass");
                        String booknum    = jsonObj.getString("booknum");
                        String suitage    = jsonObj.getString("suitage");
                        String bookname   = jsonObj.getString("bookname");
                         String detailedbook   = jsonObj.getString("detailbook");
                        showRequestResult(user3,telephone,province,city,xian,detailedad,bookclass,booknum,suitage,bookname,detailedbook);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void showRequestResult(final String user3,final String telephone,final String province,final String city,
                                   final String xian,final String detailedad,final String bookclass,final String booknum,
                                   final String suitage,final String bookname,final String detailedbook) {
        runOnUiThread(new Runnable() {
            @Override
            /**
             * 实时更新，数据库信息改变时，客户端内容发生改变
             */
            public void run() {

                requirements_info mapx = new requirements_info();
                mapx.setUser           (user3);
                mapx.setTelephone (telephone);
                mapx.setProvince       (province);
                mapx.setCity           (city);
                mapx.setXian           (xian);
                mapx.setDetailedad          (detailedad);
                mapx.setBookclass      (bookclass );
                mapx.setBooknum        (booknum   );
                mapx.setSuitage        (suitage   );
                mapx.setBookname       (bookname  );
                mapx.setDetailbook     (detailedbook);

                data.add(mapx);

                myreq.setAdapter(new mine.MyAdapter());
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String user          = data.get(position)         .getUser()  ;
        String telephone         = data.get(position)     .getTelephone()    ;
        String  province  = data.get(position)             .getProvince()    ;
        String city          = data.get(position)          .getCity();
        String  xian      = data.get(position)                       .getXian();
        String  detailedad= data.get(position)              .getDetailedad()      ;
        String   bookclass = data.get(position)              .getBookclass()       ;
        String   booknum  =  data.get(position)              .getBooknum()         ;
        String   suitage   = data.get(position)               .getSuitage()          ;
        String    bookname  = data.get(position)               .getBookname()          ;
        String   detailbook= data.get(position)              .getDetailbook()          ;
        Intent ssdata = new Intent(mine.this, detailinfo.class);


        ssdata.putExtra("user",  user      );
        ssdata.putExtra("telephone", telephone  );
        ssdata.putExtra("province", province   );
        ssdata.putExtra("city", city        );
        ssdata.putExtra("xian", xian       );
        ssdata.putExtra("detailedad", detailedad );
        ssdata.putExtra("bookclass", bookclass  );
        ssdata.putExtra("booknum", booknum    );
        ssdata.putExtra("suitage", suitage    );
        ssdata.putExtra("bookname", bookname   );
        ssdata.putExtra("detailbook", detailbook );


        startActivity(ssdata);
    }


    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return data.size();

        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = View.inflate(mine.this, R.layout.wty_searchroom_adp_layout, null);



            TextView detailedad = view.findViewById(R.id.ad);

            TextView detailbook = view.findViewById(R.id.bookdetailed);





           user      .setText(data.get(position).getUser());

           detailedad   .setText(data.get(position).getProvince()+" "+data.get(position).getCity()+"  "+data.get(position).getXian()
                   +"  "+data.get(position).getDetailedad());
           detailbook    .setText(data.get(position).getDetailbook());
            return view;
        }
        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }

        @Override
        public void notifyDataSetInvalidated() {
            super.notifyDataSetInvalidated();
        }
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
}
