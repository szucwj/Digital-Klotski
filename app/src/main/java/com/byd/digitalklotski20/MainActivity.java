package com.byd.digitalklotski20;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener {

    private LinearLayout frame;
    private Button restart_btn;
    private Button next_btn;
    private Button last_btn;
    private Chronometer timer;
    private TextView textView_count;
    private TextView textView_order;

    private int count = 0;
    private static int order_n = 3;
    private int array[];
    private boolean isStart = false;

    private SoundPool soundPool;
    private int change_sound;
    private int finish_sound;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initSound();

        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();

        for (int i = 1; i <= order_n; i++) {
            for (int j = 1; j <= order_n; j++) {
                TextView block = (TextView) findViewById(i * 10 + j);
                block.setOnClickListener(this);
            }
        }

        restart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                finish();
            }
        });
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (order_n >= 10) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setMessage("嗯...再大就放不下了，要不就这样吧。\n\n恭喜你通关了")
                            .create().show();
                } else {
                    order_n++;
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                    finish();
                }
            }
        });
        last_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (order_n <= 2) {
                    new AlertDialog.Builder(MainActivity.this).setMessage("还想玩一阶的？过分了吧！往后走").create().show();
                } else {
                    order_n--;
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                    finish();
                }
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initSound(){
        soundPool = new SoundPool.Builder().build();
        change_sound = soundPool.load(MainActivity.this,R.raw.change_sound,1);
        finish_sound = soundPool.load(MainActivity.this,R.raw.finish_sound,1);
    }

    private void playChangeSound() {
        soundPool.play(
                change_sound,
                0.3f,   //左耳道音量【0~1】
                0.3f,   //右耳道音量【0~1】
                1,     //播放优先级【0表示最低优先级】
                0,     //循环模式【0表示循环一次，-1表示一直循环，其他表示数字+1表示当前数字对应的循环次数】
                1     //播放速度【1是正常，范围从0~2】
        );
    }

    private void playFinishSound() {
        soundPool.play(
                finish_sound,
                0.5f,   //左耳道音量【0~1】
                0.5f,   //右耳道音量【0~1】
                1,     //播放优先级【0表示最低优先级】
                0,     //循环模式【0表示循环一次，-1表示一直循环，其他表示数字+1表示当前数字对应的循环次数】
                1     //播放速度【1是正常，范围从0~2】
        );
    }

    public void initView() {
        //随机生成n个数字，用以初始的排列
        int array_len = order_n * order_n - 1;
        array = new int[array_len + 1];
        while (true) {
            int reverse_order_num = 0;  //逆序数，逆序数为偶数的序列才能被还原

            for (int i = 0; i < array_len; i++) {
                int num = (int) (Math.random() * array_len) + 1;
                boolean flag = true;
                for (int j = 0; j < i; j++) {
                    if (num == array[j]) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    array[i] = num;
                } else {
                    i--;
                }
            }

            for (int i = 1; i < array_len; i++) {
                for (int j = 0; j < i; j++) {
                    if (array[j] > array[i]) {
                        reverse_order_num++;
                    }
                }
            }
            if (reverse_order_num % 2 == 0) {
                break;
            }
        }
        array[array_len] = 0;

        frame = (LinearLayout) findViewById(R.id.frame);
        restart_btn = (Button) findViewById(R.id.restart);
        next_btn = (Button) findViewById(R.id.next);
        last_btn = (Button) findViewById(R.id.last);
        textView_count = (TextView) findViewById(R.id.count);
        textView_order = (TextView) findViewById(R.id.order);
        timer = (Chronometer) findViewById(R.id.timer);

        //获取手机宽度
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        //设置框架宽高
        LinearLayout.LayoutParams frameLayoutParams = (LinearLayout.LayoutParams) frame.getLayoutParams();
        frameLayoutParams.width = (int) (width * 0.8);
        frameLayoutParams.height = (int) (width * 0.8);
        frame.setLayoutParams(frameLayoutParams);

        textView_order.setText(order_n + " x " + order_n);

        int index = 0;
        for (int i = 1; i <= order_n; i++) {
            LinearLayout linearLayout = new LinearLayout(MainActivity.this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f);
            linearLayout.setLayoutParams(params);

            //       linearLayout.setId(i * 1000);
            frame.addView(linearLayout);

            for (int j = 1; j <= order_n; j++) {
                TextView textView = new TextView(MainActivity.this);
                LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
                tvParams.setMargins(1, 1, 1, 1);
                textView.setLayoutParams(tvParams);

                if (i == order_n && j == order_n) {
                    textView.setText("");
                } else {
                    textView.setText(String.valueOf(array[index++]));
                    textView.setBackgroundColor(Color.parseColor("#C5C1AA"));
                }
                textView.setId(i * 10 + j);
                textView.setGravity(Gravity.CENTER);
                //   textView.setTextSize(30);
                linearLayout.addView(textView);
            }

        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View view) {

        int a[] = {-1, -10, 1, 10};
        TextView tv_this = null;
        tv_this = (TextView) findViewById(view.getId());

        for (int i = 0; i < a.length; i++) {
            TextView textView = null;
            textView = (TextView) findViewById(view.getId() + a[i]);
            if (textView != null) {
                if ("".equals(textView.getText().toString())) {
                    playChangeSound();

                    count++;
                    textView_count.setText("" + count);
                    textView.setText(tv_this.getText());
                    textView.setBackgroundColor(Color.parseColor("#C5C1AA"));
                    tv_this.setText("");
                    tv_this.setBackgroundColor(Color.parseColor("#8B4500"));
                    break;
                }
            }
        }

        if (isDone()) {
            playFinishSound();
            Toast.makeText(MainActivity.this, "恭喜通过本关", Toast.LENGTH_SHORT).show();
        }
    }


    protected boolean isDone() {
        TextView tv;
        int val = 1;
        for (int i = 1; i <= order_n; i++) {
            for (int j = 1; j <= order_n; j++) {
                if (!(i == order_n && j == order_n)) {
                    tv = (TextView) findViewById(i * 10 + j);
                    if (!String.valueOf(val++).equals(tv.getText().toString())) {
                        return false;
                    }
                }
            }
        }
        timer.stop();
        for (int i = 1; i <= order_n; i++) {
            for (int j = 1; j <= order_n; j++) {
                tv = (TextView) findViewById(i * 10 + j);
                tv.setOnClickListener(null);
            }
        }
        return true;
    }
}