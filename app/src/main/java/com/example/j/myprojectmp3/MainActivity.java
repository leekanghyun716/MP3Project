package com.example.j.myprojectmp3;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    EditText edtName, edtNumber;
    Button btnInsert, btnInit, btnPlay, btnPause;
    SeekBar seekBar;
    MyDBHelper myDBHelper;
    ListView listView;
    ArrayList<MyData> list = new ArrayList<>();
    SQLiteDatabase sqLiteDatabase;
    MyAdapter myAdapter;
    MyData data;
    private String selectedMp3;

    private MediaPlayer mediaPlayer;
    private static final String MP3_PATH = Environment.getExternalStorageDirectory().getPath() + "/newmymusic/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("MainActivity","");
        listView = findViewById(R.id.listView);
        seekBar = findViewById(R.id.seekBar);
        btnInsert = findViewById(R.id.btnInsert);
        btnInit = findViewById(R.id.btnInit);
        btnPlay = findViewById(R.id.btnPlay);
        btnPause = findViewById(R.id.btnPause);
        edtName = findViewById(R.id.edtName);
        edtNumber = findViewById(R.id.edtNumber);

        myDBHelper = new MyDBHelper(this);
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MODE_PRIVATE);
        listAddInsert();

        myAdapter = new MyAdapter(this, R.layout.layout, list);
        listView.setAdapter(myAdapter);
        btnInsert.setOnClickListener(this);
        btnInit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SubMusic.class);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String name = list.get(position).getSingName();
                sqLiteDatabase = myDBHelper.getWritableDatabase();
                sqLiteDatabase.execSQL(
                        "DELETE FROM groupTBL WHERE gSingName = '" + list.get(position).getSingName() + "';");


                sqLiteDatabase.close();

                Toast.makeText(getApplicationContext(), name,
                        Toast.LENGTH_SHORT).show();
                listAddInsert();
                myAdapter.notifyDataSetChanged();
                return false;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedMp3 = list.get(position).getSingName();
                data=list.get(position);
                MyData myData = list.get(position);
                edtName.setText(myData.getSingerName());
                edtNumber.setText(myData.getSingName());
            }
        });
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sqLiteDatabase = myDBHelper.getWritableDatabase();
                if (edtNumber.getText().toString() != "") {
                    sqLiteDatabase.execSQL("UPDATE groupTBL SET gNumber ="
                            + (data.getNumber()+1) + " WHERE gSingName = '"
                            + edtNumber.getText().toString() + "';");
                }
                sqLiteDatabase.close();
                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(MP3_PATH + selectedMp3);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    Thread thread=new Thread(){
                        @Override
                        public void run() {
                            if (mediaPlayer==null){
                                return;
                            }

                            while (mediaPlayer.isPlaying()){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        seekBar.setMax(mediaPlayer.getDuration());
                                        seekBar.setProgress(mediaPlayer.getCurrentPosition());
                                    }
                                });//run end of runonUiThread은 화면위젯 변경은 스레트 안에서
                                SystemClock.sleep(200);
                            }//end while
                        }
                    };
                    thread.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                listAddInsert();
                myAdapter.notifyDataSetChanged();
            }
        });
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                seekBar.setProgress(0);
            }
        });
    }

    private void listAddInsert() {
        list.removeAll(list);
        sqLiteDatabase = myDBHelper.getReadableDatabase();
        Cursor cursor;
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM groupTBL;", null);

        while (cursor.moveToNext()) {
            list.add(new MyData(cursor.getString(0), cursor.getString(1),
                    cursor.getInt(2), cursor.getString(3)));
        }
        cursor.close();
        sqLiteDatabase.close();
    }

    @Override
    public void onClick(View v) {
        listAddInsert();
        myAdapter.notifyDataSetChanged();
    }

    public class MyAdapter extends BaseAdapter {
        Context context;
        Integer layout;
        ArrayList<MyData> myDataArrayList;
        LayoutInflater layoutInflater;

        public MyAdapter(Context context, Integer layout, ArrayList<MyData> myDataArrayList) {
            this.context = context;
            this.layout = layout;
            this.myDataArrayList = myDataArrayList;
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return myDataArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return myDataArrayList.get(position);
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
            TextView textView = convertView.findViewById(R.id.tvname);
            TextView textView3 = convertView.findViewById(R.id.tvSingName);
            TextView textView4 = convertView.findViewById(R.id.tvJang);
            TextView textView2 = convertView.findViewById(R.id.tvnumber);
            MyData myData = myDataArrayList.get(position);
            textView.setText(myData.getSingerName());
            textView2.setText(String.valueOf(myData.getNumber()));
            textView3.setText(myData.getSingName());
            textView4.setText(myData.getJang());
            return convertView;
        }
    }

    public class MyData {
        private String singerName;
        private String singName;
        private int number;
        private String jang;

        public MyData(String singerName, String singName, int number, String jang) {
            this.singerName = singerName;
            this.singName = singName;
            this.number = number;
            this.jang = jang;
        }

        public String getSingerName() {
            return singerName;
        }

        public void setSingerName(String singerName) {
            this.singerName = singerName;
        }

        public String getSingName() {
            return singName;
        }

        public void setSingName(String singName) {
            this.singName = singName;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getJang() {
            return jang;
        }

        public void setJang(String jang) {
            this.jang = jang;
        }
    }

}
