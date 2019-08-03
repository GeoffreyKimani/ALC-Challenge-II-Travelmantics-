package com.example.travelmantics;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class ListActivity extends AppCompatActivity {

	public static String TAG = ListActivity.class.toString();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.list_activity_menu, menu);
		MenuItem insertMenu = menu.findItem(R.id.insert_menu);
		if (FirebaseUtil.isAdmin == true) {
			insertMenu.setVisible(true);
		}
		else {
			insertMenu.setVisible(false);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.insert_menu:
				Intent intent = new Intent(this, DealActivity.class);
				startActivity(intent);
				return true;
			case R.id.logout:
				AuthUI.getInstance()
						.signOut(this)
						.addOnCompleteListener(new OnCompleteListener<Void>() {
							public void onComplete(@NonNull Task<Void> task) {
								Log.d(TAG, "User logged out");

								// call the firebase attach method to call the login page
								FirebaseUtil.attachListener();
							}
						});
				FirebaseUtil.detachListener();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPause() {
		super.onPause();
		FirebaseUtil.detachListener();
	}

	@Override
	protected void onResume() {
		super.onResume();
		FirebaseUtil.openFbReference("traveldeals", this);
		// create reference to recycler view
		RecyclerView travelDealsRecyclerView = findViewById(R.id.rvTravelDeals);

		// adapter
		final DealAdapter dealAdapter = new DealAdapter();
		travelDealsRecyclerView.setAdapter(dealAdapter);

		// layout manager
		LinearLayoutManager rvTravelDealsLayoutManager = new LinearLayoutManager(
				this, LinearLayoutManager.VERTICAL, false);
		travelDealsRecyclerView.setLayoutManager(rvTravelDealsLayoutManager);

		FirebaseUtil.attachListener();
	}

	public void showMenu(){
		invalidateOptionsMenu();
	}
}
