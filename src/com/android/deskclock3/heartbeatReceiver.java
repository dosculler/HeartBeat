package com.android.deskclock3;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.PowerManager;
import android.util.Log;

public class heartbeatReceiver extends BroadcastReceiver {
	private static int count;
	private static String printLogName;
	private static String ipAddr;
	private static int RepeatTime;
	private final String TAG = "heartbeat";
	private static boolean mNetWorkFlag = false;
	private static AlarmManager am;
	private static Intent alarmIntent;
	private static PendingIntent sender;
	private static String configStatus;
	
	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) { 
	
			ipAddr = MySystemProperties.get("persist.sys.heartbeat.serviceip");
			
			try {
				RepeatTime = Integer.parseInt(MySystemProperties.get("persist.sys.heartbeat.time"));
			} catch (NumberFormatException e) {
				Log.i(TAG, "persist.sys.heartbeat.time err!");
				return;
			}
			
			configStatus = MySystemProperties.get("persist.sys.heartbeat.config");
			
			if(ipAddr == null){
				Log.i(TAG, "ipAddr err!");
				return;
			}
			if(RepeatTime == 0){
				Log.i(TAG, "heartbeat close!");
				return;
			}
			if(RepeatTime < 10){
				RepeatTime = 10;
			}	

			Log.i(TAG, "heartbeat start, ip="+ipAddr + ", RepeatTime=" + RepeatTime);			
			am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
			alarmIntent = new Intent("com.android.deskclock.ALARM_DDALERT");
			sender = PendingIntent.getBroadcast(
	        		context, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
			if(configStatus.contains("auto")){
				if(isNetworkAvailable(context) == true){
					am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), RepeatTime*1000, sender);
					mNetWorkFlag = true;
				}else{
					am.cancel(sender);
					mNetWorkFlag = false;
				}
			}else{
				am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), RepeatTime*1000, sender);
			}
	
		}
		if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) { //
			if(am == null){
				return;
			}
			if(configStatus.contains("auto")){
				if(isNetworkAvailable(context) == true){	
					if(mNetWorkFlag == false){
						Log.i(TAG, "Network connected");
						am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), RepeatTime*1000, sender);
						mNetWorkFlag = true;
					}
				}else{				
					if(mNetWorkFlag == true){
						Log.i(TAG, "Network disconnection");
						am.cancel(sender);
						mNetWorkFlag = false;
					}
				}
			}
		}
		else if(intent.getAction().equals("com.android.deskclock.ALARM_DDALERT")){
			if(am == null){
				return;
			}
			
			if(configStatus.contains("wake")){
				PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
	            PowerManager.WakeLock mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP|PowerManager.ON_AFTER_RELEASE, "TAG");
	            mWakeLock.acquire();
	            mWakeLock.release();
			}
 //			KeyguardManager keyguardManager = (KeyguardManager)context.getSystemService(Context.KEYGUARD_SERVICE);
 //            KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("");
 //            keyguardLock.disableKeyguard();
			
			Date curDate = null;
			SimpleDateFormat formatter = null;
			MyFile myfile = new MyFile();
			String path = android.os.Environment.getExternalStorageDirectory().getPath();	
			myfile.creatPath(path + "/pinglog");	
			myfile.creatPath(path + "/radiolog");
			
			if(count == 0){		
				File[] fileList = null;
				fileList = new File(path + "/pinglog").listFiles();
				if(fileList.length > 2){
					int mini = 0;
					for (int i = 1; i < fileList.length; i++) {
						if(fileList[mini].getName().compareTo(fileList[i].getName())>0){
							mini = i;
						}						
					}	
					fileList[mini].delete();
				}
				formatter = new SimpleDateFormat("yyyyMMdd-HHmmss");
				curDate = new Date(System.currentTimeMillis());
				printLogName = path + "/pinglog/" + "ping-" + formatter.format(curDate) + ".txt";
				
			}
			myfile.creatTxtFile(printLogName); 
			formatter = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
			curDate = new Date(System.currentTimeMillis());
			
	        count++;
	        if(count > 150){
	        	count = 0;
	        }
			if(isNetworkAvailable(context) == true && configStatus.contains("ping")){	
		        Process process = null;  
		        String pingLine = "";
		        try {  
		            process = Runtime.getRuntime().exec("/system/bin/ping -c 1 "+ipAddr);    
		            InputStream input = process.getInputStream();  
		            BufferedReader in = new BufferedReader(new InputStreamReader(input));  
		            StringBuffer buffer = new StringBuffer();  
	
		            if(in.readLine() == null){
		            	Log.i(TAG, formatter.format(curDate) + "unknown host " +  ipAddr);
		        		myfile.AppendToFile(printLogName, formatter.format(curDate) + ": unknown host " +  ipAddr + "\r\n");
		            }
		            while ((pingLine = in.readLine()) != null ){ 
		            	if(pingLine.contains("packet loss")){
		            		Log.i(TAG, formatter.format(curDate) + "[" + ipAddr + "]" +pingLine);
		            		myfile.AppendToFile(printLogName, formatter.format(curDate) + "[" + ipAddr + "]" +pingLine+ "\r\n");
		            		if(pingLine.contains("100%") && configStatus.contains("radio")){
		            			SimpleDateFormat formatter2 = new SimpleDateFormat("yyyyMMdd-HHmmss");
		        				Date curDate2 = new Date(System.currentTimeMillis());
		        				String radioLogName = path + "/radiolog/" + "radio-" + formatter2.format(curDate2) + "-loss.txt";
		        				Runtime.getRuntime().exec("logcat -b radio -d -f" + radioLogName);
		            		}
		            	}
		            }  
		            if(configStatus.contains("radio")){
		            	Runtime.getRuntime().exec("logcat -b radio -c");
		            }
		        } catch (IOException e) {  
		            e.printStackTrace();  
		        } 
			}else{
				Log.i(TAG, "NetworkAvailable false"); 
				myfile.AppendToFile(printLogName, formatter.format(curDate) + "NetworkAvailable false"+ "\r\n");
				
				if(configStatus.contains("radio")){
					SimpleDateFormat formatter2 = new SimpleDateFormat("yyyyMMdd-HHmmss");
					Date curDate2 = new Date(System.currentTimeMillis());
					String radioLogName = path + "/radiolog/" + "radio-" + formatter2.format(curDate2) + "-disconnect.txt";
					try {
						Runtime.getRuntime().exec("logcat -b radio -d -f" + radioLogName);
						Runtime.getRuntime().exec("logcat -b radio -c");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
				}
			}
		}
	}

	
    /**
     *
     * 
     * @param context
     * @return
     */
    
    public boolean isNetworkAvailable(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        
        if (connectivityManager == null)
        {
            return false;
        }
        else
        {
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            
            if (networkInfo != null && networkInfo.length > 0)
            {
                for (int i = 0; i < networkInfo.length; i++)
                {
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
} 
