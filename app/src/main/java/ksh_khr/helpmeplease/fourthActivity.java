package ksh_khr.helpmeplease;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


//조이스틱 화면
public class fourthActivity extends AppCompatActivity {


    Button btn, bt_up, bt_down, bt_left, bt_right, btn_dis, btn_ch;
    EditText ed;
    TextView contented;
    ConnectAsyn conTread;
    String ip = null;
    private int STATUS =0;      //이것을 한 이유는 연결이 되어있으면 화면 전환을 할 수 없게 하기 때문에 연결을 끊어야한다고 알려주려는 변수.

    //ip정보를 가져오는 메소드
    class ButtonConnectListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            ip = ed.getText().toString().trim();
            startControl();
        }
    }
    //버튼 터치 리스너를 만들어서 보내는 값을 설정하는 메소드
    class BtnListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    switch (view.getId()) {
                        case R.id.btn_up:
                            //asd.up("DOWN");
                            conTread.up("DOWN");
                            contented.setText(getString(R.string.viewControl, "\nPress the btn_up"));
                            break;
                        case R.id.btn_down:
                           //asd.down("DOWN");
                            conTread.down("DOWN");
                            contented.setText(getString(R.string.viewControl, "\nPress the btn_down"));
                            break;
                        case R.id.btn_left:
                           // asd.left("DOWN");
                            conTread.left("DOWN");
                            contented.setText(getString(R.string.viewControl, "\nPress the btn_left"));
                            break;
                        case R.id.btn_right:
                           // asd.right("DOWN");
                            conTread.right("DOWN");
                            contented.setText(getString(R.string.viewControl, "\nPress the btn_right"));
                            break;
                        default:
                            break;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    switch (view.getId()) {
                        case R.id.btn_up:
                            //asd.up("UP");
                            conTread.up("UP");
                            break;
                        case R.id.btn_down:
                            //asd.down("UP");
                            conTread.down("UP");
                            break;
                        case R.id.btn_left:
                            //asd.left("UP");
                            conTread.left("UP");
                            break;
                        case R.id.btn_right:
                            //asd.right("UP");
                            conTread.right("UP");
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fourth);

        ButtonConnectListener btn_listener = new ButtonConnectListener();
        btn = (Button) findViewById(R.id.btn_con);
        ed = (EditText) findViewById(R.id.ip_num);
        bt_up = (Button) findViewById(R.id.btn_up);
        bt_down = (Button) findViewById(R.id.btn_down);
        bt_left = (Button) findViewById(R.id.btn_left);
        bt_right = (Button) findViewById(R.id.btn_right);
        contented = (TextView) findViewById(R.id.contents);
        btn_dis = (Button) findViewById(R.id.btn_disCon);

        btn_ch = (Button) findViewById(R.id.btn_return);
        btn.setOnClickListener(btn_listener);

        //연결 끊는 버튼 이벤트
        btn_dis.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onStop();
                contented.setText(getString(R.string.viewControl, "\nDisConnect"));
                finish();
            }
        });

        //다시 처음으로 돌아가는 버튼 이벤트
        btn_ch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(fourthActivity.this,MainActivity.class);
                startActivity(intent);


            }
        });

    }

    //컨트롤러 사용하려고 호출하는 메소드
    private void startControl() {
        BtnListener listener = new BtnListener();
        bt_up.setOnTouchListener(listener);
        bt_down.setOnTouchListener(listener);
        bt_left.setOnTouchListener(listener);
        bt_right.setOnTouchListener(listener);
        try {

            conTread = new ConnectAsyn(ip);
            conTread.execute();


            STATUS = 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //명시적으로 종료 메소드를 다시 작성했다
    protected void onStop() {
        super.onStop();
        if(STATUS == 1)
        conTread.CloseSocket();

    }

    //명시적으로 종료 메소드를 다시 작성했다
    @Override
    protected void onPause() {
            super.onPause();
        if(STATUS == 1)
        conTread.CloseSocket();
        }
    }