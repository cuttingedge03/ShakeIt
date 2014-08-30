package com.android.internal.telephony;
import android.os.Bundle;
import java.util.List;
interface ITelephony {

 boolean endCall();

 void answerRingingCall();


}/*TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
Class c = Class.forName(tm.getClass().getName());
Method m = c.getDeclaredMethod("getITelephony");
m.setAccessible(true);
ITelephony telephonyService;
telephonyService = (ITelephony)m.invoke(tm);

// Silence the ringer
telephonyService.silenceRinger();
// answer the call!

telephonyService.answerRingingCall();

For Ending call Automatically use

telephonyService.endCall();*/