package ksh_khr.helpmeplease;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

//번호저장하는 클래스
public class thirdActivity extends AppCompatActivity {

    private Button btn1, btn2, btn3, btn4, rebtn;
    private EditText edit1, edit2, edit3, edit4;
    private TextView txtv1, txtv2, txtv3, txtv4;

    /*
    //SharedPreferences를 이용할때 사용해야하는 메소드다.
    public SharedPreferences setting;
    public SharedPreferences.Editor editor;

    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        //번호 저장하는 버튼
        btn1 = (Button) findViewById(R.id.button1);
        btn2 = (Button) findViewById(R.id.button2);
        btn3 = (Button) findViewById(R.id.button3);
        btn4 = (Button) findViewById(R.id.button4);
        //돌아가는 버튼
        rebtn = (Button) findViewById(R.id.button_Return);
        //번호 치는 EditText
        edit1 = (EditText) findViewById(R.id.editText1);
        edit2 = (EditText) findViewById(R.id.editText2);
        edit3 = (EditText) findViewById(R.id.editText3);
        edit4 = (EditText) findViewById(R.id.editText4);
        //저장된 번호 보여주는 TextView
        txtv1 = (TextView) findViewById(R.id.textView1);
        txtv2 = (TextView) findViewById(R.id.textView2);
        txtv3 = (TextView) findViewById(R.id.textView3);
        txtv4 = (TextView) findViewById(R.id.textView4);


        /*
        //여기 또한 SharedPreferences를 이용할때 사용해야하는 부분.
        setting = getApplicationContext().getSharedPreferences("PhoneNumber", 0);
        editor = setting.edit();

        txtv1.setText(setting.getString("PNum_1", "Null"));
        txtv2.setText(setting.getString("PNum_2", "Null"));
        txtv3.setText(setting.getString("PNum_3", "Null"));
        txtv4.setText(setting.getString("PNum_4", "Null"));

*/


        btn1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


                /*
                //SharedPreferences를 이용할 때 사용함 아래에 메세지를 다 주석처리하면됨.
                editor.putString("PNum_1", edit1.getText().toString());
                editor.commit();
                txtv1.setText(setting.getString("PNum_1", "Null"));
                */
                PhoneNum.getInstance().setPhonenum1(edit1.getText().toString());
                txtv1.setText(PhoneNum.getInstance().getPhonenum1());

            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                /*
                //SharedPreferences를 이용할 때 사용함 아래에 메세지를 다 주석처리하면됨.
                editor.putString("PNum_2", edit2.getText().toString());
                editor.commit();
                txtv2.setText(setting.getString("PNum_2", "Null"));
                */
                PhoneNum.getInstance().setPhonenum1(edit1.getText().toString());
                txtv2.setText(PhoneNum.getInstance().getPhonenum1());
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
            /*
            //SharedPreferences를 이용할 때 사용함 아래에 메세지를 다 주석처리하면됨.
                editor.putString("PNum_3", edit3.getText().toString());
                editor.commit();
                txtv3.setText(setting.getString("PNum_3", "Null"));
                */
                PhoneNum.getInstance().setPhonenum1(edit1.getText().toString());
                txtv3.setText(PhoneNum.getInstance().getPhonenum1());
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
            /*
            //SharedPreferences를 이용할 때 사용함 아래에 메세지를 다 주석처리하면됨.
                editor.putString("PNum_4", edit4.getText().toString());
                editor.commit();
                txtv4.setText(setting.getString("PNum_4", "Null"));
                */
                PhoneNum.getInstance().setPhonenum1(edit1.getText().toString());
                txtv4.setText(PhoneNum.getInstance().getPhonenum1());
            }
        });



        rebtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
            /*
            //SharedPreferences를 이용할 때 사용함 아래에 메세지를 다 주석처리하면됨.
                editor.commit();
                */
                finish();
            }
        });

    }

}