package com.example.travelmantics;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InsertActivity extends AppCompatActivity {

	private FirebaseDatabase mFirebaseDatabase; // will have the database obj
	private DatabaseReference mDatabaseReference;
	EditText txtTitle;
	EditText txtPrice;
	EditText txtDescription;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mFirebaseDatabase = FirebaseDatabase.getInstance(); // create an instance of the firebase database
		mDatabaseReference = mFirebaseDatabase.getReference().child("traveldeals");

		// references to the layout fields
		txtTitle = findViewById(R.id.txtTitle);
		txtPrice = findViewById(R.id.txtPrice);
		txtDescription = findViewById(R.id.txtDescription);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.save_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
			case R.id.save_menu:
				saveDeal(); // save the deal
				Toast.makeText(this, "Deal saved!", Toast.LENGTH_LONG).show(); // give user feedback
				cleanForm(); // reset the fields after saving
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void saveDeal() {
		String title = txtTitle.getText().toString();
		String price = txtPrice.getText().toString();
		String description = txtDescription.getText().toString();

		TravelDeal travelDeal = new TravelDeal(title, description, price, "");
		mDatabaseReference.push().setValue(travelDeal);
	}
	private void cleanForm() {
		txtTitle.setText("");
		txtDescription.setText("");
		txtPrice.setText("");
		txtTitle.requestFocus();
	}
}
