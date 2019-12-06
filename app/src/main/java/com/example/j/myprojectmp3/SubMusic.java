package com.example.j.myprojectmp3;

import android.Manifest;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class SubMusic extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private ListView listView;
    private TextView tvMp3, tvTime;
    private EditText editText, edtSingName, edtName;
    private Button btnInsert, btnUpload;
    private SeekBar pbMp3;
    private MyAdapter myAdapter;
    private MediaPlayer mediaPlayer;
    private ArrayList<MyData2> list = new ArrayList<>();
    private String selectedMp3;
    MyDBHelper myDBHelper;
    private int popo;
    SQLiteDatabase sqLiteDatabase;
    private static final String MP3_PATH = Environment.getExternalStorageDirectory().getPath() + "/newmymusic/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_music);

        listView = findViewById(R.id.listView);
        editText = findViewById(R.id.editText);
        edtSingName = findViewById(R.id.editText2);
        edtName = findViewById(R.id.editText3);
        btnInsert = findViewById(R.id.button2);
        btnUpload = findViewById(R.id.button3);

        myDBHelper = new MyDBHelper(this);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MODE_PRIVATE);
        File[] files = new File(MP3_PATH).listFiles();
        for (File file : files) {
            String fileName = file.getName();
            String extendFile = fileName.substring(fileName.length() - 3);
            if (extendFile.equals("mp3")) {
                list.add(new MyData2(fileName));
            }
        }
        myAdapter = new MyAdapter(this, R.layout.layout2, list);
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(this);
        btnInsert.setOnClickListener(this);
        btnUpload.setOnClickListener(this);

    }//end onCreate

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        popo = position;
        edtSingName.setText(edtSingName.getText().toString() + list.get(position).getSingName());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button2:
                sqLiteDatabase =myDBHelper.getWritableDatabase();
                sqLiteDatabase.execSQL("INSERT INTO groupTBL VALUES ( '"
                        + edtName.getText().toString() + "' , '"
                        + edtSingName.getText().toString() +"' , '"
                        + 0 +"' , '"
                        + editText.getText().toString() + "');");
                sqLiteDatabase.close();

                Toast.makeText(getApplicationContext(), "입력됨",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.button3:
                sqLiteDatabase = myDBHelper.getWritableDatabase();
                if (edtSingName.getText().toString() != "") {
                    sqLiteDatabase.execSQL("UPDATE groupTBL SET gSingerName ='"
                            + edtName.getText().toString() + "', gJangR='"+editText.getText().toString()+"' WHERE gSingName = '"
                            + edtSingName.getText().toString() + "';");
                }
                sqLiteDatabase.close();
                break;
        }
    }


    public class MyData2 {
        private String singName;

        public MyData2(String singName) {
            this.singName = singName;
        }

        public String getSingName() {
            return singName;
        }

        public void setSingName(String singName) {
            this.singName = singName;
        }
    }

    public class MyAdapter extends BaseAdapter {
        Context context;
        Integer layout;
        ArrayList<MyData2> list2;
        LayoutInflater layoutInflater;

        public MyAdapter(Context context, Integer integer, ArrayList<MyData2> list2) {
            this.context = context;
            this.layout = integer;
            this.list2 = list2;
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return list2.size();
        }

        @Override
        public Object getItem(int position) {
            return list2.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = layoutInflater.inflate(layout, null);
            }
            MyData2 myData2 = list2.get(position);
            TextView textView = convertView.findViewById(R.id.tvtv);
            textView.setText(myData2.getSingName());
            return convertView;
        }
    }
}
