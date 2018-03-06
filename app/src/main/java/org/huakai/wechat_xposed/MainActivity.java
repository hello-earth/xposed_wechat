package org.huakai.wechat_xposed;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static de.robv.android.xposed.XposedBridge.log;


public class MainActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		String a = "{\"mode\":\"newLogin\",\"client_id\":\"1\",\"mobile\":\"15718881312\",\"phone_region\":\"0086\",\"BaseAppType\":\"android\",\"BaseAppVersion\":\"1.3.0\",\"SystemVersion\":\"5.1\",\"appIdentifier\":\"com.weimob.mdstore\",\"deviceMake\":\"Meizu\",\"deviceType\":\"PRO 5\"}";
		((TextView)findViewById(R.id.textView)).setText(getSignStr(a));
	}

	private static String getSignStr(String paramString)
	{
		StringBuilder localStringBuilder = new StringBuilder();
		localStringBuilder.append(paramString);
		localStringBuilder.append("yunjie2514572541463841s1a4d");
		return md5(localStringBuilder.toString()).toUpperCase();
	}

	public static String md5(String paramString){
		String a = "";
		try {
			a = byteToString(MessageDigest.getInstance("MD5").digest(paramString.getBytes()));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return a;
	}

	private static final String[] strDigits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

	private static String byteToArrayString(byte paramByte)
	{
		int i = paramByte;
		if (paramByte < 0) {
			i = paramByte + 256;
		}
		return strDigits[i / 16] + strDigits[(i % 16)];
	}

	private static String byteToString(byte[] paramArrayOfByte)
	{
		StringBuffer localStringBuffer = new StringBuffer();
		for (int i = 0; i < paramArrayOfByte.length; i++) {
			localStringBuffer.append(byteToArrayString(paramArrayOfByte[i]));
		}
		return localStringBuffer.toString();
	}
}
