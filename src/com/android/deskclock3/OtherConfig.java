package com.android.deskclock3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class OtherConfig extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.otherconfig_layout);	
	}
	public void other_btn_Listener(View v){
		Intent i;
		
		switch (v.getId()) {
		case R.id.btn_startlog:
			Log.i("lizejin", "btn_startlog");
			i = new Intent("xgdlog.start");
	    	sendBroadcast(i);
	    	Toast.makeText(OtherConfig.this, getString(R.string.pos_config_logopen), 1000).show();
			break;
		case R.id.btn_stoplog:
			Log.i("lizejin", "btn_stoplog");
			i = new Intent("xgdlog.stop");
	    	sendBroadcast(i);
	    	Toast.makeText(OtherConfig.this, getString(R.string.pos_config_logclose), 1000).show();
			break;
		default:
			break;
		}
	}
	
}
