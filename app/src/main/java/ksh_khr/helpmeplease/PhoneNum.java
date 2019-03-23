package ksh_khr.helpmeplease;

//싱글톤 패턴으로 클래스를 생성하여 전역 클래스를 생성함.
//SharedPreferences 를 이용할 때 보다 더 빠르지만 어플이 종료되면 다시 번호를 저장해야한다.
public class PhoneNum {
    private String Phonenum1=null;
    private String Phonenum2=null;
    private String Phonenum3=null;
    private String Phonenum4=null;


    public String getPhonenum1() {
        return Phonenum1;
    }

    public void setPhonenum1(String phonenum1) {
        Phonenum1 = phonenum1;
    }

    public String getPhonenum2() {
        return Phonenum2;
    }

    public void setPhonenum2(String phonenum2) {
        Phonenum2 = phonenum2;
    }

    public String getPhonenum3() {
        return Phonenum3;
    }

    public void setPhonenum3(String phonenum3) {
        Phonenum3 = phonenum3;
    }

    public String getPhonenum4() {
        return Phonenum4;
    }

    public void setPhonenum4(String phonenum4) {
        Phonenum4 = phonenum4;
    }
    private static PhoneNum instance = null;
    public static synchronized PhoneNum getInstance(){
        if(null==instance){
            instance = new PhoneNum();
        }
        return instance;
    }
}
