package xsg.book.main_inteface;

import android.annotation.SuppressLint;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.TextView;

import xsg.book.R;
import xsg.book.mine.mine;

public class detailinfo extends AppCompatActivity {
    private LocalActivityManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题拦

        setContentView(R.layout.activity_detailinfo);

initview();
        manager = new LocalActivityManager(this, false);
        manager.dispatchCreate(savedInstanceState);


    }

private  void initview()
{

    Intent intent= getIntent();
            TextView user       =   findViewById(R.id.user);
            TextView telephone  =   findViewById(R.id.tel1);
            TextView detailedad =   findViewById(R.id.ad);
            TextView bookclass  =   findViewById(R.id.bookclass);
            TextView booknum    =   findViewById(R.id.booknum);
            TextView suitage    =   findViewById(R.id.suitage);
            TextView bookname   =   findViewById(R.id.bookname);
            TextView detailbook =   findViewById(R.id.bookdetailed);





            user      .setText(intent.getStringExtra("user"));
            telephone   .setText(intent.getStringExtra("telephone"));
            detailedad   .setText(intent.getStringExtra("province")+" "+intent.getStringExtra("city")
                    +"  "+intent.getStringExtra("xian")
                    +"  "+intent.getStringExtra("detailedad"));
            bookclass   .setText(intent.getStringExtra("bookclass"));
            booknum     .setText(intent.getStringExtra("booknum"));
            suitage     .setText(intent.getStringExtra("suitage"));
            bookname    .setText(intent.getStringExtra("bookname"));
            detailbook    .setText(intent.getStringExtra("detailbook"));

}
}
