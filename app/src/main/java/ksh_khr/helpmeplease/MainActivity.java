package ksh_khr.helpmeplease;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    private TextView qq;    //아두이노에서 블루투스로 오는 값을 TextView에 보여주려고 하는 변수
    private Button btnRegister;         //전화번호 저장하는 페이지로 넘어가는 버튼 변수
    private Button btnShowLocation;     //위치 보여주는 버튼 변수
    private TextView txtLat;            //위도를 보여줄 TextView
    private TextView txtLon;            //경도를 보여줄 TextView
    private ImageButton smsSender;      //문자보내는 이미지버튼
    private GpsInfo gps;                //GpsInfo 클래스를 이용해 위도 경도를 구할 수 있도록 생성한 변수
    private Messenger ms;               //문자를 보내기 위한 클래스 변수
    private Button abc;                 //번호 저장확인 버튼
    private Button btnControl;         //원격조종하는 페이지로 넘어가는 버튼


    /*
    만약 SharedPrefernecs를 사용하려고 하실경우 필요한 변수들입니다.
    private SharedPreferences set;
    private String PhoneNum1,PhoneNum2,PhoneNum3,PhoneNum4;
    */


    private final String TAG = "Activity";      //로그 테스트 할때 사용하는 TAG
    // 사용자 정의 함수로 블루투스 활성 상태의 변경 결과를 App으로 알려줄때 식별자로 사용됨 (0보다 커야함)
    static final int REQUEST_ENABLE_BT = 10;
    //블루투스로 페어링된 기기 갯수
    int mPariedDeviceCount = 0;
    //페어링된 기기 보여주는 변수
    Set<BluetoothDevice> mDevices;
    // 폰의 블루투스 모듈을 사용하기 위한 오브젝트.
    BluetoothAdapter mBluetoothAdapter;
    /**
     BluetoothDevice 로 기기의 장치정보를 알아낼 수 있는 자세한 메소드 및 상태값을 알아낼 수 있다.
     연결하고자 하는 다른 블루투스 기기의 이름, 주소, 연결 상태 등의 정보를 조회할 수 있는 클래스.
     현재 기기가 아닌 다른 블루투스 기기와의 연결 및 정보를 알아낼 때 사용.
     */
    BluetoothDevice mRemoteDevie;
    // 스마트폰과 페어링 된 디바이스간 통신 채널에 대응 하는 BluetoothSocket
    BluetoothSocket mSocket = null;

    //소켓통신을 이용해 값전달을 위한 변수
    OutputStream mOutputStream = null;
    InputStream mInputStream = null;


    boolean stopWorker;     //일을 중단하는지에 대한 변수
    boolean is_connected = false;   //연결 되었는지에 대한 불린값


    Thread mWorkerThread = null;    //쓰레드 생성
    byte[] readBuffer;              //Byte[] 배열 생성
    int readBufferPosition;         //읽어들어온 버퍼 위치를 지정하는 것


    double latitude =0;             //위도변수
    double longitude = 0;           //경도변수


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        qq=(TextView) findViewById(R.id.txt_Arduino);
        btnControl = (Button)findViewById(R.id.btn_Control);
        btnRegister = (Button) findViewById(R.id.btn_Cloud);
        btnShowLocation = (Button) findViewById(R.id.btn_Location);
        txtLat = (TextView) findViewById(R.id.textlatitude);
        txtLon = (TextView) findViewById(R.id.textLongitude);
        smsSender = (ImageButton) findViewById(R.id.SMSsned);
        abc = (Button) findViewById(R.id.button7);



   /*
   //Preferences를 사용할 때 필요한 변수들
   //번호를 셋팅하는 메소드로 현재 PhoneNum1~4까지가 MainActivity에 존재하기 때문에 값을 셋팅해줌

        set = getSharedPreferences("PhoneNumber", 0);

        PhoneNum1 = set.getString("PNum_1","Null");
        PhoneNum2 = set.getString("PNum_2","Null");
        PhoneNum3 = set.getString("PNum_3","Null");
        PhoneNum4 = set.getString("PNum_4","Null");
        */


        //위치정보 등록
        btnShowLocation.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                gps = new GpsInfo(MainActivity.this);
                // GPS 사용유무 가져오기
                if (gps.isGetLocation()) {

                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();

                    txtLat.setText(String.valueOf(latitude));
                    txtLon.setText(String.valueOf(longitude));

                    Toast.makeText(
                            getApplicationContext(),
                            "당신의 위치 - \n위도: " + latitude + "\n경도: " + longitude,
                            Toast.LENGTH_LONG).show();
                } else {
                    // GPS 를 사용할수 없으므로
                    gps.showSettingsAlert();
                }
            }

        });


        //번호저장 화면으로 넘기기
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Thi = new Intent(getApplicationContext(), thirdActivity.class);
                startActivity(Thi);
            }
        });

        //메세지 보내기
        smsSender.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                ms = new Messenger(MainActivity.this);
                Log.d(TAG, "ms.Messenger");
                CheckPhoneNum();
            }
        });


        //번호 저장확인 버튼
        abc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplication(),PhoneNum.getInstance().getPhonenum1() + "\n" +PhoneNum.getInstance().getPhonenum2() + "\n" +PhoneNum.getInstance().getPhonenum3() + "\n" +PhoneNum.getInstance().getPhonenum4() ,Toast.LENGTH_SHORT).show();
          //      Toast.makeText(getApplication(),PhoneNum1.toString() + "\n" +PhoneNum2.toString() + "\n" +PhoneNum3.toString() + "\n" +PhoneNum4.toString() ,Toast.LENGTH_SHORT).show();
            }
        });

        //원격조종 하는 화면 넘기기
        btnControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Forth = new Intent(getApplicationContext(), fourthActivity.class);
                startActivity(Forth);


            }
        });

        //블루투스 연결을 하지 않으면 꺼지게끔 셋팅하는 checkBluetooth메소드
        checkBluetooth();
    }


    // 블루투스 장치의 이름이 주어졌을때 해당 블루투스 장치 객체를 페어링 된 장치 목록에서 찾아내는 코드.
    BluetoothDevice getDeviceFromBondedList(String name) {
        // BluetoothDevice : 페어링 된 기기 목록을 얻어옴.
        BluetoothDevice selectedDevice = null;
        // getBondedDevices 함수가 반환하는 페어링 된 기기 목록은 Set 형식이며,
        // Set 형식에서는 n 번째 원소를 얻어오는 방법이 없으므로 주어진 이름과 비교해서 찾는다.
        for(BluetoothDevice deivce : mDevices) {
            // getName() : 단말기의 Bluetooth Adapter 이름을 반환
            if(name.equals(deivce.getName())) {
                selectedDevice = deivce;
                break;
            }
        }
        return selectedDevice;
    }




    //  connectToSelectedDevice() : 원격 장치와 연결하는 과정을 나타냄.
    //        실제 데이터 송수신을 위해서는 소켓으로부터 입출력 스트림을 얻고 입출력 스트림을 이용하여 이루어 진다.
    void connectToSelectedDevice(String selectedDeviceName) {
        // BluetoothDevice 원격 블루투스 기기를 나타냄.
        mRemoteDevie = getDeviceFromBondedList(selectedDeviceName);
        // java.util.UUID.fromString : 자바에서 중복되지 않는 Unique 키 생성.
        UUID uuid = java.util.UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        try {
            // 소켓 생성, RFCOMM 채널을 통한 연결.
            // createRfcommSocketToServiceRecord(uuid) : 이 함수를 사용하여 원격 블루투스 장치와 통신할 수 있는 소켓을 생성함.
            // 이 메소드가 성공하면 스마트폰과 페어링 된 디바이스간 통신 채널에 대응하는 BluetoothSocket 오브젝트를 리턴함.
            mSocket = mRemoteDevie.createRfcommSocketToServiceRecord(uuid);
            mSocket.connect(); // 소켓이 생성 되면 connect() 함수를 호출함으로써 두기기의 연결은 완료된다.

            // 데이터 송수신을 위한 스트림 얻기.
            // BluetoothSocket 오브젝트는 두개의 Stream을 제공한다.
            // 1. 데이터를 보내기 위한 OutputStrem
            // 2. 데이터를 받기 위한 InputStream
            mOutputStream = mSocket.getOutputStream();
            mInputStream = mSocket.getInputStream();

            is_connected =true;     //연결변수를 true로 변경해서 연결됐다는 것을 사용함

            // 데이터 수신 준비.
            beginListenForData();

        }catch(Exception e) { // 블루투스 연결 중 오류 발생
            Toast.makeText(getApplicationContext(), "블루투스 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            finish();  // App 종료
        }
    }





    // 블루투스로부터 들어오는 데이터 처리 하는 부분
    void beginListenForData()
    {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        Log.d(TAG,"BeginListenForData()");

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        // 데이터가 들어오는지 확인하는 Thread를 생성
        mWorkerThread = new Thread(new Runnable()
        {
            public void run()
            {
                Log.d(TAG,"mWorkerThread = new Thread()");
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {

                    if(!is_connected)
                    {
                        continue;
                    }

                    try
                    {
                        int bytesAvailable = mInputStream.available();
                        if(bytesAvailable > 0)
                        {
                            Log.d(TAG,"if(bytesAvailable>0");
                            byte[] packetBytes = new byte[bytesAvailable];
                            mInputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                if(b == delimiter)
                                {
                                    Log.d(TAG,"if(b==delimiter)");
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "EUC-KR");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable()
                                    {
                                        public void run()
                                        {
                                            String tmp2 = (String)data;
                                            qq.setText(tmp2);
                                        if(tmp2.equals("c")){
                                            //블루투스에서 들어오는 값이 "c"라면 CheckGPS, CheckPhoneNum 메소드를 사용하여 자동적으로 메세지 보내는 것이다.
                                            CheckGPS();
                                            CheckPhoneNum();
                                        }

                                        }
                                    });
                                }
                                else
                                {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex)
                    {
                        stopWorker = true;
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) { }
                }
            }
        });
        mWorkerThread.start();
    }

    // 블루투스 지원하며 활성 상태인 경우.
    void selectDevice() {
        // 블루투스 디바이스는 연결해서 사용하기 전에 먼저 페어링 되어야만 한다
        // getBondedDevices() : 페어링된 장치 목록 얻어오는 함수.
        mDevices = mBluetoothAdapter.getBondedDevices();
        mPariedDeviceCount = mDevices.size();

        if(mPariedDeviceCount == 0 ) { // 페어링된 장치가 없는 경우.
            Toast.makeText(getApplicationContext(), "페어링된 장치가 없습니다.", Toast.LENGTH_LONG).show();
            finish(); // App 종료.
        }
        // 페어링된 장치가 있는 경우.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("블루투스 장치 선택");

        // 각 디바이스는 이름과(서로 다른) 주소를 가진다. 페어링 된 디바이스들을 표시한다.
        List<String> listItems = new ArrayList<String>();
        for(BluetoothDevice device : mDevices) {
            // device.getName() : 단말기의 Bluetooth Adapter 이름을 반환.
            listItems.add(device.getName());
        }
        listItems.add("취소");  // 취소 항목 추가.


        // CharSequence : 변경 가능한 문자열.
        // toArray : List형태로 넘어온것 배열로 바꿔서 처리하기 위한 toArray() 함수.
        final CharSequence[] items = listItems.toArray(new CharSequence[listItems.size()]);
        // toArray 함수를 이용해서 size만큼 배열이 생성 되었다.
        listItems.toArray(new CharSequence[listItems.size()]);

        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                // TODO Auto-generated method stub
                if(item == mPariedDeviceCount) { // 연결할 장치를 선택하지 않고 '취소' 를 누른 경우.
                    Toast.makeText(getApplicationContext(), "연결할 장치를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
                    finish();
                }
                else { // 연결할 장치를 선택한 경우, 선택한 장치와 연결을 시도함.
                    connectToSelectedDevice(items[item].toString());
                }
            }

        });

        builder.setCancelable(false);  // 뒤로 가기 버튼 사용 금지.
        AlertDialog alert = builder.create();
        alert.show();
    }


    void checkBluetooth() {
        /**
         * getDefaultAdapter() : 만일 폰에 블루투스 모듈이 없으면 null 을 리턴한다.
         이경우 Toast를 사용해 에러메시지를 표시하고 앱을 종료한다.
         */
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null ) {  // 블루투스 미지원
            Toast.makeText(getApplicationContext(), "기기가 블루투스를 지원하지 않습니다.", Toast.LENGTH_LONG).show();
            finish();  // 앱종료
        }
        else { // 블루투스 지원
            /** isEnable() : 블루투스 모듈이 활성화 되었는지 확인.
             *               true : 지원 ,  false : 미지원
             */
            if(!mBluetoothAdapter.isEnabled()) { // 블루투스 지원하며 비활성 상태인 경우.
                Toast.makeText(getApplicationContext(), "현재 블루투스가 비활성 상태입니다.", Toast.LENGTH_LONG).show();
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                // REQUEST_ENABLE_BT : 블루투스 활성 상태의 변경 결과를 App 으로 알려줄 때 식별자로 사용(0이상)
                /**
                 startActivityForResult 함수 호출후 다이얼로그가 나타남
                 "예" 를 선택하면 시스템의 블루투스 장치를 활성화 시키고
                 "아니오" 를 선택하면 비활성화 상태를 유지 한다.
                 선택 결과는 onActivityResult 콜백 함수에서 확인할 수 있다.
                 */
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            else // 블루투스 지원하며 활성 상태인 경우.
                selectDevice();
        }
    }



    // onDestroy() : 어플이 종료될때 호출 되는 함수.
    //               블루투스 연결이 필요하지 않는 경우 입출력 스트림 소켓을 닫아줌.
    @Override
    protected void onDestroy() {
        try{
            Toast.makeText(getApplicationContext(),"onDestroy",Toast.LENGTH_SHORT).show();
            mWorkerThread.interrupt(); // 데이터 수신 쓰레드 종료
            mInputStream.close();
            mSocket.close();
        }catch(Exception e){}
        super.onDestroy();
    }


    // onActivityResult : 사용자의 선택결과 확인 (아니오, 예)
    // RESULT_OK: 블루투스가 활성화 상태로 변경된 경우. "예"
    // RESULT_CANCELED : 오류나 사용자의 "아니오" 선택으로 비활성 상태로 남아 있는 경우  RESULT_CANCELED

    /**
     사용자가 request를 허가(또는 거부)하면 안드로이드 앱의 onActivityResult 메소도를 호출해서 request의 허가/거부를 확인할수 있다.
     첫번째 requestCode : startActivityForResult 에서 사용했던 요청 코드. REQUEST_ENABLE_BT 값
     두번째 resultCode  : 종료된 액티비티가 setReuslt로 지정한 결과 코드. RESULT_OK, RESULT_CANCELED 값중 하나가 들어감.
     세번째 data        : 종료된 액티비티가 인테트를 첨부했을 경우, 그 인텐트가 들어있고 첨부하지 않으면 null
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // startActivityForResult 를 여러번 사용할 땐 이런 식으로 switch 문을 사용하여 어떤 요청인지 구분하여 사용함.
        switch(requestCode) {
            case REQUEST_ENABLE_BT:
                if(resultCode == RESULT_OK) { // 블루투스 활성화 상태
                    selectDevice();
                }
                else if(resultCode == RESULT_CANCELED) { // 블루투스 비활성화 상태 (종료)
                    Toast.makeText(getApplicationContext(), "블루투수를 사용할 수 없어 프로그램을 종료합니다", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //자동적으로 GPS를 찾기위해 만든 메소드
    public void CheckGPS() {
        gps = new GpsInfo(MainActivity.this);
        // GPS 사용유무 가져오기
        if (gps.isGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();


        } else {
                // GPS 를 사용할수 없으므로
                gps.showSettingsAlert();
            }
        }

    //번호가 저장되어있는지 확인하고 있으면 문자를 보내고 없으면 없다고 토스트 메세지를 보낸다.
    public void CheckPhoneNum() {
        Messenger as = new Messenger(MainActivity.this);

        /*
        //여기는 Preferences를 이용할때 사용하는 부분으로 만약 사용할 경우 아래 부분을 주석처리해야한다.
            if (!PhoneNum1.equals("Null")) {
                ms.sendMessageTo(PhoneNum1, "사고발생 - 구조요청바랍니다. 현재 위치는 위도:" + latitude + "경도:" + longitude + "입니다.");
            } else {
                Toast.makeText(getApplicationContext(), "PhoneNum1에 저장된 번호가 없습니다. ", Toast.LENGTH_SHORT).show();
            }
            if (!PhoneNum2.equals("Null")) {
                ms.sendMessageTo(PhoneNum2, "사고발생 - 구조요청바랍니다. 현재 위치는 위도:" + latitude + "경도:" + longitude + "입니다.");
            } else {
                Toast.makeText(getApplicationContext(), "PhoneNum2에 저장된 번호가 없습니다. ", Toast.LENGTH_SHORT).show();
            }
            if (!PhoneNum3.equals("Null")) {
                ms.sendMessageTo(PhoneNum3, "사고발생 - 구조요청바랍니다. 현재 위치는 위도:" + latitude + "경도:" + longitude + "입니다.");
            } else {
                Toast.makeText(getApplicationContext(), "PhoneNum3에 저장된 번호가 없습니다. ", Toast.LENGTH_SHORT).show();
            }
            if (!PhoneNum4.equals("Null")) {
                ms.sendMessageTo(PhoneNum4, "사고발생 - 구조요청바랍니다. 현재 위치는 위도:" + latitude + "경도:" + longitude + "입니다.");
            } else {
                Toast.makeText(getApplicationContext(), "PhoneNum4에 저장된 번호가 없습니다. ", Toast.LENGTH_SHORT).show();
            }


    */


        if (PhoneNum.getInstance().getPhonenum1() == null) {
            Toast.makeText(getApplicationContext(), " PhoneNum1에 저장된번호가 없습니다.", Toast.LENGTH_SHORT).show();
        } else {
            as.sendMessageTo(PhoneNum.getInstance().getPhonenum1(), "사고발생 - 구조요청바랍니다. 현재 위치는 위도:" + latitude + "경도:" + longitude + "입니다.");
        }
        if (PhoneNum.getInstance().getPhonenum2() == null) {
            Toast.makeText(getApplicationContext(), "PhoneNum2에 저장된번호가 없습니다.", Toast.LENGTH_SHORT).show();
        } else {
            as.sendMessageTo(PhoneNum.getInstance().getPhonenum2(), "사고발생 - 구조요청바랍니다. 현재 위치는 위도:" + latitude + "경도:" + longitude + "입니다.");
        }
        if (PhoneNum.getInstance().getPhonenum3() == null) {
            Toast.makeText(getApplicationContext(), "PhoneNum3에 저장된번호가 없습니다.", Toast.LENGTH_SHORT).show();
        } else {
            as.sendMessageTo(PhoneNum.getInstance().getPhonenum3(), "사고발생 - 구조요청바랍니다. 현재 위치는 위도:" + latitude + "경도:" + longitude + "입니다.");
        }
        if (PhoneNum.getInstance().getPhonenum4() == null) {
            Toast.makeText(getApplicationContext(), "PhoneNum4에 저장된번호가 없습니다.", Toast.LENGTH_SHORT).show();
        } else {
            as.sendMessageTo(PhoneNum.getInstance().getPhonenum4(), "사고발생 - 구조요청바랍니다. 현재 위치는 위도:" + latitude + "경도:" + longitude + "입니다.");
        }

    }
    }


