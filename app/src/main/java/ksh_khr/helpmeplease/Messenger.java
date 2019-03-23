package ksh_khr.helpmeplease;

import android.content.Context;
import android.telephony.SmsManager;
import android.widget.Toast;

//메세지보내는 클래스를 만들었다.
public class Messenger {
    private Context mContext;

    public Messenger(Context mContext) {
        this.mContext = mContext;
    }

    public void sendMessageTo(String phoneNum, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNum, null, message, null, null);

        Toast.makeText(mContext, "전송 완료", Toast.LENGTH_SHORT).show();
    }
}
