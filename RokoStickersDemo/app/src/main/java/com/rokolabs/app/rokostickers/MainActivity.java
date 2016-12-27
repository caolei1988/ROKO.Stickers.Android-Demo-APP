package com.rokolabs.app.rokostickers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;

import com.rokolabs.sdk.RokoMobi;
import com.rokolabs.sdk.links.ResponseVanityLink;
import com.rokolabs.sdk.links.RokoLinks;
import com.rokolabs.sdk.tools.RokoTools;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_photo);
		RokoMobi.start(this);
		String[] PERMISSIONS_APP = {
				"android.permission.READ_CONTACTS",
				"android.permission.WRITE_CONTACTS",
				"android.permission.WRITE_EXTERNAL_STORAGE",
				"android.permission.READ_PHONE_STATE"
		};
		int MY_PERMISSIONS_REQUEST = 1;

		ActivityCompat.requestPermissions(this, PERMISSIONS_APP, MY_PERMISSIONS_REQUEST);

	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.take_photo:
			startStickersActivity(1);
			break;
		case R.id.choose_photo:
			startStickersActivity(2);
			break;
		}
	}

	private void startStickersActivity(int i) {
		Intent intent = new Intent(i == 1 ? StickersActivity.ACTION_TAKE_PHOTO
				: StickersActivity.ACTION_PICK_PHOTO);
		startActivityForResult(intent, 1);

	}
}
