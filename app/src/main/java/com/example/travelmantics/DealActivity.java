package com.example.travelmantics;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DealActivity extends AppCompatActivity {

	EditText txtTitle;
	EditText txtPrice;
	EditText txtDescription;
	TravelDeal deal;
	private FirebaseDatabase mFirebaseDatabase; // will have the database obj
	private DatabaseReference mDatabaseReference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mFirebaseDatabase = FirebaseUtil.sFirebaseDatabase;
		mDatabaseReference = FirebaseUtil.sDatabaseReference;

		// references to the layout fields
		txtTitle = findViewById(R.id.txtTitle);
		txtPrice = findViewById(R.id.txtPrice);
		txtDescription = findViewById(R.id.txtDescription);

		// To go to selected recyclerview item
		Intent intent = getIntent();
		TravelDeal deal = (TravelDeal) intent.getSerializableExtra("Deal");
		if (deal == null) {
			deal = new TravelDeal();
		}
		this.deal = deal;
		txtTitle.setText(deal.getTitle());
		txtDescription.setText(deal.getDescription());
		txtPrice.setText(deal.getPrice());

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.save_menu, menu);

		if (FirebaseUtil.isAdmin) {
			menu.findItem(R.id.save_menu).setVisible(true);
			menu.findItem(R.id.delete_menu).setVisible(true);
			enableEditTexts(true);
		} else {
			menu.findItem(R.id.save_menu).setVisible(false);
			menu.findItem(R.id.delete_menu).setVisible(false);
			enableEditTexts(false);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.save_menu:
				saveDeal(); // save the deal
				Toast.makeText(this, "Deal saved!", Toast.LENGTH_LONG).show(); // give user feedback
				cleanForm(); // reset the fields after saving
				backToList();
				return true;

			case R.id.delete_menu:
				deleteDeal();
				Toast.makeText(this, "Deal Deleted", Toast.LENGTH_SHORT).show();
				backToList();
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void saveDeal() {
		deal.setTitle(txtTitle.getText().toString());
		deal.setDescription(txtDescription.getText().toString());
		deal.setPrice(txtPrice.getText().toString());

		if (deal.getId() == null) {
			mDatabaseReference.push().setValue(deal); // create new deal
		} else {
			mDatabaseReference.child(deal.getId()).setValue(deal); // update existing deal
		}
	}

	private void deleteDeal() {
		if (deal == null) {
			Toast.makeText(getApplicationContext(), "Please save deal before deleting", Toast.LENGTH_LONG).show();
			return;
		}
		mDatabaseReference.child(deal.getId()).removeValue();
	}

	private void backToList() {
		Intent intent = new Intent(this, ListActivity.class);
		startActivity(intent);
	}

	private void cleanForm() {
		txtTitle.setText("");
		txtDescription.setText("");
		txtPrice.setText("");
		txtTitle.requestFocus();
	}

	private void enableEditTexts(boolean enabled) {
		txtTitle.setEnabled(enabled);
		txtDescription.setEnabled(enabled);
		txtPrice.setEnabled(enabled);
	}
}
