package com.example.travelmantics;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import static com.example.travelmantics.FirebaseUtil.mStorageRef;

public class DealActivity extends AppCompatActivity {

	private static final int PICTURE_RESULT = 42;
	private static final String TAG = DealActivity.class.toString();
	EditText txtTitle;
	EditText txtPrice;
	EditText txtDescription;
	ImageView mImageView;
	TravelDeal deal;
	private FirebaseDatabase mFirebaseDatabase; // will have the database obj
	private DatabaseReference mDatabaseReference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_deal);

		mFirebaseDatabase = FirebaseUtil.sFirebaseDatabase;
		mDatabaseReference = FirebaseUtil.sDatabaseReference;

		// references to the layout fields
		txtTitle = findViewById(R.id.txtTitle);
		txtPrice = findViewById(R.id.txtPrice);
		txtDescription = findViewById(R.id.txtDescription);
		mImageView = findViewById(R.id.imageUpload);

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
		showImage(deal.getImageUrl());
		Button btnImage = findViewById(R.id.btnImage);
		btnImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("image/jpeg");
				intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
				startActivityForResult(intent.createChooser(intent,
						"Insert Picture"), PICTURE_RESULT);
			}
		});

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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode ==PICTURE_RESULT && resultCode == RESULT_OK){

			Uri imageUri = data.getData();
			final StorageReference ref = mStorageRef.child(imageUri.getLastPathSegment());

			ref.putFile(imageUri)
					.addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
						@Override
						public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
							ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
								@Override
								public void onComplete(@NonNull Task<Uri> task) {
									String downloadUrl = task.getResult().toString();
									deal.setImageUrl(downloadUrl);
									deal.setImageName(taskSnapshot.getStorage().getName());
									showImage(downloadUrl);

									Log.d(TAG, "Url for image is: " + downloadUrl +
											"\nAnd name is: " + taskSnapshot.getStorage().getName());
								}
							});
						}
					});
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
		Log.d("image name", deal.getImageName());
		if(deal.getImageName() != null && deal.getImageName().isEmpty() == false) {
			StorageReference picRef = FirebaseUtil.mStorage.getReference().child(deal.getImageName());
			picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
				@Override
				public void onSuccess(Void aVoid) {
					Log.d("Delete Image", "Image Successfully Deleted");
				}
			}).addOnFailureListener(new OnFailureListener() {
				@Override
				public void onFailure(@NonNull Exception e) {
					Log.d("Delete Image", e.getMessage());
				}
			});
		}
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

	private void showImage(String url){
		if(url != null && url.isEmpty() == false){
			int width = Resources.getSystem().getDisplayMetrics().widthPixels;
			Picasso.with(this)
					.load(url)
					.resize(width, (width*2/3))
					.centerCrop()
					.into(mImageView);
		}
	}
}
