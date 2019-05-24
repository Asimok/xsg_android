package xsg.book.main_inteface;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.loader.ImageLoader;

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
import xsg.book.mine.mine;
import xsg.book.mine.requirements_info;

public class main_interface extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private List<requirements_info> data;
    private ListView myreq;

    Banner banner;

    private List<String> mTitleList = new ArrayList<>();
    private List<Integer> mImgList = new ArrayList<>();

    private OkHttpClient okhttpClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_interface);

        initview();

    }
    private  void initview(){
        banner=findViewById(R.id.home_play_banner);
        data=new ArrayList<requirements_info>();
        myreq= findViewById(R.id.reqlv);
        myreq.setOnItemClickListener(this);
        search();
        BannerSet();
        Banner();
    }
    private void BannerSet() {
        mImgList.add(R.drawable.lb1);
        mImgList.add(R.drawable.lb2);
        mImgList.add(R.drawable.lb3);
        mImgList.add(R.drawable.lb4);

        mTitleList.clear();
        for (int i = 0; i < mImgList.size(); i++) {
            mTitleList.add("" );
        }
    }
    private void Banner() {

        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE); // 显示圆形指示器和标题（水平显示
//设置图片加载器
        banner.setImageLoader(new MyLoader());
//设置图片集合
        banner.setImages(mImgList);
//设置banner动画效果
        banner.setBannerAnimation(Transformer.DepthPage);
//设置标题集合（当banner样式有显示title时）
        banner.setBannerTitles(mTitleList);
//设置自动轮播，默认为true
        banner.isAutoPlay(true);
//设置轮播时间
        banner.setDelayTime(1500);
//设置指示器位置（当banner模式中有指示器时）
        banner.setIndicatorGravity(BannerConfig.CENTER);
//banner设置方法全部调用完毕时最后调用
        banner.start();
    }
    public class MyLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Glide.with(context).load(path).into(imageView);
        }

    }

    public void search() {
        data.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendRequest();
            }
        }).start();
    }

    private void sendRequest() {




        RequestBody body = RequestBody.create(null, "");  //以字符串方式
        okhttpClient = new OkHttpClient();
        final Request request = new Request.Builder()

                .url("http://192.168.31.114:8080/xsg_book/main_interfaceServlet")

                .post(body)
                .build();
        okhttp3.Call call = okhttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(main_interface.this, "连接服务器失败！", Toast.LENGTH_SHORT).show();
                    }
                });
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {

                String res = response.body().string();//获取到传过来的字符串
                Log.d("main","main res  "+res);
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

                myreq.setAdapter(new main_interface.MyAdapter());
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
        Intent ssdata = new Intent(main_interface.this,detailinfo.class);


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

            @SuppressLint("ViewHolder") View view = View.inflate(main_interface.this, R.layout.wty_searchroom_adp_layout, null);



            TextView detailedad = view.findViewById(R.id.ad);
            TextView detailbook = view.findViewById(R.id.bookdetailed);
            ImageView img= view.findViewById(R.id.img);






            detailedad   .setText(data.get(position).getProvince()+" "+data.get(position).getCity()+"  "+data.get(position).getXian()
                    +"  "+        data.get(position).getDetailedad()    );
            detailbook   .setText(data.get(position).getDetailbook());

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

}
