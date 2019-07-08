package top.omooo.customlint;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ImageView mIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mIv = findViewById(R.id.iv);
        mIv.setImageResource(R.drawable.ic_launcher_background);
    }

    /**
     * Detector Test
     */
    private void testLogUsage() {
        //Android 自带的 ToastDetector
        Toast.makeText(this, "", Toast.LENGTH_SHORT);

        System.out.println("Omooo");

//        Log.i(TAG, "啊啊啊啊，我被发现了！");
        Log.println(1, TAG, "嘿，我没事～");
        top.omooo.customlint.test.Log.i("我也没事～");

        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();
    }
}
