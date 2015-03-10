package com.codepath.apps.onefourzero.activities;

import android.content.Intent;
import android.widget.Toast;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.codepath.apps.onefourzero.R;
import com.codepath.apps.onefourzero.TwitterClient;
import com.codepath.oauth.OAuthLoginActionBarActivity;

public class LoginActivity extends OAuthLoginActionBarActivity<TwitterClient> {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public void onLoginSuccess() {
		 Intent i = new Intent(this, TimelineActivity.class);
		 startActivity(i);
	}

	@Override
	public void onLoginFailure(Exception e) {
        Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_LONG).show();
        e.printStackTrace();
	}

	public void loginToRest(View view) {
		getClient().connect();
	}
}
