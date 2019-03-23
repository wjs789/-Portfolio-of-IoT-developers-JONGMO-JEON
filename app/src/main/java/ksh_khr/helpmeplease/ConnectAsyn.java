package ksh_khr.helpmeplease;

import android.os.AsyncTask;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

//TCP 소켓통신을 사용하려고 한 ConnectAsyn클래스이며, 안드로이드 스튜디오에서 제공해주는 AsynTask를 상속받아 구현했다.
public class ConnectAsyn extends AsyncTask<String, String, String> {

    public boolean STATUS_FLAG = false;     //연결됐는지 안됐는지.
    String hostname;                            //IP넘버
    Socket sock ;                               //소켓 변수선언
    PrintWriter out;                            //원래는 outputStream과 inputStream을 이용해야하는데 한번에 사용하려고 PrintWriter라는
    private BufferedWriter networkWriter;
    private int port = 3016;                    //PortNumber 3016으로 셋팅, 바꾸고 싶다면 여기 있는 포트번호와 라즈베리파이에 있는 포트번호를 겹치게 설정해주면된다.

    //디폴트 생성자
    public ConnectAsyn(){

    }
    //IP를 받아 오는 생성자
    public ConnectAsyn(String addr){
        hostname = addr;
    }


    //doinBackground는 백그라운드쓰레드와 UI쓰레드를 같이 사용하려는 것이고, AsyncTask에서 꼭 오버라이드해야하는 메소드이다.
    @Override
    protected String doInBackground(String... strings) {
        try{
            sock = new Socket(hostname, port);
            STATUS_FLAG = true;
            networkWriter = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
            out = new PrintWriter(networkWriter,true);
            out.println("success");
        }catch (SocketException e){
            e.printStackTrace();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    //닫는 메소드를 다시 만들어서 연결 유지를 하게 했다.
    public void CloseSocket()
    {
        try
        {
            if(STATUS_FLAG == true) {
                out.println("Close");
                sock.close();
                STATUS_FLAG= false;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    //up이라는 메소드로써 KeyUp,GO는 GO버튼을 누르던 손가락이 뗏을때 출력하는 값으로 UP을 의미합니다.
    //up이라는 메소드에 KeyDown,GO는 GO버튼을 누르고있다는 것으로 앞으로 가게됩니다.
    public void up(String type)
    {
        if(type.equals("UP"))
        {
            out.println("KeyUp,GO");
        }
        else if(type.equals("DOWN"))
        {
            out.println("KeyDown,GO");
        }
        else
        {

        }
    }
    public void down(String type)
    {
        if(type.equals("UP"))
        {
            out.println("KeyUp,BACK");
        }
        else if(type.equals("DOWN"))
        {
            out.println("KeyDown,BACK");
        }
        else
        {

        }
    }
    public void left(String type)
    {
        if(type.equals("UP"))
        {
            out.println("KeyUp,Left");
        }
        else if(type.equals("DOWN"))
        {
            out.println("KeyDown,Left");
        }
        else
        {

        }
    }
    public void right(String type)
    {
        if(type.equals("UP"))
        {
            out.println("KeyUp,Right");
        }
        else if(type.equals("DOWN"))
        {
            out.println("KeyDown,Right");
        }
        else
        {

        }
    }
}