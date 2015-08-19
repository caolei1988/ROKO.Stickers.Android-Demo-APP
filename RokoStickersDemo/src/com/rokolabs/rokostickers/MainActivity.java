package com.rokolabs.rokostickers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo);
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
