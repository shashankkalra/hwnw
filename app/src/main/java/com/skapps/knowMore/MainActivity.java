package com.skapps.knowMore;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    static int alertCancelCount = 0;
    AlertDialog alertDialog;

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

        checkPreferencesSetOrNotAndOpenSettingsIfRequired();

		if (savedInstanceState == null) {
			addRssFragment();
		}
	}

    private void checkPreferencesSetOrNotAndOpenSettingsIfRequired() {

        Boolean mandatoryInputsNotGiven = true;

        UserProfileProvider.SharedPreferencesWrapper prefs = UserProfileProvider.getDefaultSharedPreferences(getBaseContext());
        Log.d("Preferences -- ", prefs.toString());
        //Map<String, ?> prefMap = prefs.getAll();
        String gender = (String) prefs.getString("gender",null);

        String current_state =  (String) prefs.getString("current_state",null);
        String current_city =  (String) prefs.getString("current_city",null);

        /*boolean farmerOrNot = (Boolean) prefMap.get("farmer");
        String crop = (String) prefMap.get("crop");*/

        if("Male".equalsIgnoreCase(gender) || "Female".equalsIgnoreCase(gender))
            if(current_state != null && !"".equals(current_state))
                if(current_city != null && !"".equals(current_city))
                    mandatoryInputsNotGiven = false;

        Log.d("Individual prefs - ", gender + current_state + current_city);

        prefs.edit().putBoolean(getString(R.string.pref_mandatoryInputsNotGiven), mandatoryInputsNotGiven).commit();

        if(mandatoryInputsNotGiven){

            showAlertDialog();

        }
        else {
            //this.dismissDialog(R.id.dialog);
        }
    }

    private void showAlertDialog() {

        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(R.string.app_name);
        alertDialog.setCancelable(false);
        alertDialog.setMessage("Welcome! Please provide details by clicking edit profile button below, to enable the app to help you.");
        alertDialog.setButton("Edit My Profile", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
                openSettings();
            }
        });

        alertDialog.show();
    }

    private void addRssFragment() {
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		RssFragment fragment = new RssFragment();
		transaction.add(R.id.fragment_container, fragment);
		transaction.commit();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean("fragment_added", true);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            openSettings();
            return true;
        }

        if (id == R.id.action_refresh) {
            refresh();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openSettings() {
        startActivity(new Intent(this,SettingsActivity.class));
    }

    public void refresh() {
        finish();
        startActivity(getIntent());
    }

    public void refresh(MenuItem item) {
        finish();
        startActivity(getIntent());

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(alertDialog == null || !alertDialog.isShowing()) {
            checkPreferencesSetOrNotAndOpenSettingsIfRequired();
        }

        Log.i("calling activity","Calling package is -- " + getCallingPackage());

        if(getCallingActivity() != null && getCallingActivity().getShortClassName().contains("Setting")) {
            refresh();
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        refresh();
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        refresh();
    }

    @Override
    public boolean onNavigateUpFromChild(Activity child) {

        Log.i("settings", "navigated up from settings");

        refresh();
        return super.onNavigateUpFromChild(child);
    }


}