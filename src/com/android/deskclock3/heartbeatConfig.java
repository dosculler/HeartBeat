package com.android.deskclock3;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

public class heartbeatConfig extends Activity {
	private CheckBox checkBoxPing, checkBoxClose, checkBoxRadio, checkBoxWakelock;
	private EditText editIP, editTime , editshutdown,editpoweron;
	private Button writeTime, writeip, deviceReboot , writeshutdownTime,writepoweronTime;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.heartbeatconfig_layout);
		
		checkBoxPing = (CheckBox) findViewById(R.id.checkBoxPing);
		checkBoxClose = (CheckBox) findViewById(R.id.checkBoxClose);
		checkBoxRadio = (CheckBox) findViewById(R.id.checkBoxRadio);
		checkBoxWakelock = (CheckBox) findViewById(R.id.checkBoxWakelock);
		editIP = (EditText) findViewById(R.id.editIP);
		editTime = (EditText) findViewById(R.id.editTime);
		writeip = (Button) findViewById(R.id.writeip);
		writeTime = (Button) findViewById(R.id.writeTime);
		
		
		writeshutdownTime = (Button) findViewById(R.id.write_shutdown_Time);		
		editshutdown = (EditText) findViewById(R.id.editTimeshutdown);
		
		writepoweronTime = (Button) findViewById(R.id.write_poweron_Time);		
		editpoweron = (EditText) findViewById(R.id.editTimepoweron);
		
		
		deviceReboot = (Button) findViewById(R.id.deviceReboot);
		
		editIP.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		editTime.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		editshutdown.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		editpoweron.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		
		String strval = MySystemProperties.get("persist.sys.heartbeat.config");
		if(strval.contains("ping")){
			checkBoxPing.setChecked(true);
		}else{
			checkBoxPing.setChecked(false);
		}
		if(strval.contains("auto")){
			checkBoxClose.setChecked(true);
		}else{
			checkBoxClose.setChecked(false);
		}
		if(strval.contains("radio")){
			checkBoxRadio.setChecked(true);
			checkBoxClose.setChecked(false);
			checkBoxClose.setEnabled(false);
		}else{
			checkBoxRadio.setChecked(false);
		}
		if(strval.contains("wake")){
			checkBoxWakelock.setChecked(true);
		}else{
			checkBoxWakelock.setChecked(false);
		}
		strval = MySystemProperties.get("persist.sys.heartbeat.serviceip");
		editIP.setText(strval);
		strval = MySystemProperties.get("persist.sys.heartbeat.time");
		editTime.setText(strval);
		strval = MySystemProperties.get("persist.sys.time.off");
		editshutdown.setText(strval);
		strval = MySystemProperties.get("persist.sys.time.on");
		editpoweron.setText(strval);
		
		ListenerInit();
	}

	private void ListenerInit(){
		
		writeip.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				MySystemProperties.set("persist.sys.heartbeat.serviceip", editIP.getText().toString());
			}
		});

		writeTime.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				MySystemProperties.set("persist.sys.heartbeat.time", editTime.getText().toString());
			}
		});
		
		writeshutdownTime.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				MySystemProperties.set("persist.sys.time.off", editshutdown.getText().toString());

			}
		});		
		
		writepoweronTime.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				MySystemProperties.set("persist.sys.time.on", editpoweron.getText().toString());

			}
		});		
		
		
		deviceReboot.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				PowerManager pManager=(PowerManager) getSystemService(Context.POWER_SERVICE);  
				pManager.reboot("");
			}
		});
		checkBoxPing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked == true){
					setHeartBeatConfigStatus(getHeartBeatConfigStatus() | 0x1);
				}else{
					setHeartBeatConfigStatus(getHeartBeatConfigStatus() & (~0x1));
				}
			}
		});
		
		checkBoxClose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
						// TODO Auto-generated method stub
						if(isChecked == true){
							setHeartBeatConfigStatus(getHeartBeatConfigStatus() | 0x10);
						}else{
							setHeartBeatConfigStatus(getHeartBeatConfigStatus() & (~0x10));
						}
					}
				});
		
		checkBoxRadio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked == true){
					setHeartBeatConfigStatus(getHeartBeatConfigStatus() | 0x100);
					setHeartBeatConfigStatus(getHeartBeatConfigStatus() & (~0x10));
					checkBoxClose.setChecked(false);
					checkBoxClose.setEnabled(false);
				}else{
					setHeartBeatConfigStatus(getHeartBeatConfigStatus() & (~0x100));
					checkBoxClose.setEnabled(true);
				}
			}
		});
		checkBoxWakelock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked == true){
					setHeartBeatConfigStatus(getHeartBeatConfigStatus() | 0x1000);
				}else{
					setHeartBeatConfigStatus(getHeartBeatConfigStatus() & (~0x1000));
				}
			}
		});
	}
	
	private int getHeartBeatConfigStatus(){
		String strval = MySystemProperties.get("persist.sys.heartbeat.config");
		int intval = 0;
		if(strval.contains("ping")){
			intval |= 0x1;
		}
		if(strval.contains("auto")){
			intval |= 0x10;
		}
		if(strval.contains("radio")){
			intval |= 0x100;
		}
		if(strval.contains("wake")){
			intval |= 0x1000;
		}
		return intval;
	}
	private void setHeartBeatConfigStatus(int configval){
		String strval = "";
		if((configval & 0x1) > 0){
			strval += "ping";
		}
		if((configval & 0x10) > 0){
			strval += "|auto";
		}
		if((configval & 0x100) > 0){
			strval += "|radio";
		}
		if((configval & 0x1000) > 0){
			strval += "|wake";
		}
		MySystemProperties.set("persist.sys.heartbeat.config", strval);
	}
}
