package com.example.travelmantics;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DealAdapter extends RecyclerView.Adapter<DealAdapter.TravelDealsViewHolder> {
	ArrayList<TravelDeal> mTravelDeals;
	private FirebaseDatabase mFirebaseDatabase;
	private DatabaseReference mDatabaseReference;
	private ChildEventListener mChildEventListener; // creates a child object that listens to change on items
	public String TAG = DealAdapter.class.toString();
	private ImageView imageDeal;

	public DealAdapter() {
		mFirebaseDatabase = FirebaseUtil.sFirebaseDatabase;
		mDatabaseReference = FirebaseUtil.sDatabaseReference;

		this.mTravelDeals = FirebaseUtil.sTravelDeals; // arraylist instance

		mChildEventListener = new ChildEventListener() {
			@Override
			public void onChildAdded(DataSnapshot dataSnapshot, String s) {
				// DATASNAPSHOT; contains data from a firebase database location
				TravelDeal travelDeal = dataSnapshot.getValue(TravelDeal.class);
				Log.d("Deal", travelDeal.getTitle());
				travelDeal.setId(dataSnapshot.getKey());
				mTravelDeals.add(travelDeal);

				// notify users of new update
				notifyItemInserted(mTravelDeals.size()-1);
			}

			@Override
			public void onChildChanged(DataSnapshot dataSnapshot, String s) {

			}

			@Override
			public void onChildRemoved(DataSnapshot dataSnapshot) {

			}

			@Override
			public void onChildMoved(DataSnapshot dataSnapshot, String s) {

			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		};

		// add listener to the db reference
		mDatabaseReference.addChildEventListener(mChildEventListener);
	}
	@NonNull
	@Override
	public TravelDealsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
		Context context = viewGroup.getContext();
		View itemView = LayoutInflater.from(context)
				.inflate(R.layout.rv_row, viewGroup, false);

		return new TravelDealsViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(@NonNull TravelDealsViewHolder travelDealsViewHolder, int position) {
		TravelDeal travelDeal = mTravelDeals.get(position);
		travelDealsViewHolder.bind(travelDeal);
	}

	@Override
	public int getItemCount() {
		return mTravelDeals.size();
	}

	public class TravelDealsViewHolder extends RecyclerView.ViewHolder
	implements View.OnClickListener{

		TextView tvTitle;
		TextView tvDescription;
		TextView tvPrice;

		public TravelDealsViewHolder(@NonNull View itemView) {
			super(itemView);
			tvTitle = itemView.findViewById(R.id.tvTitle);
			tvDescription = itemView.findViewById(R.id.tvDescription);
			tvPrice = itemView.findViewById(R.id.tvPrice);
			imageDeal = itemView.findViewById(R.id.imageDeal);

			itemView.setOnClickListener(this);
		}

		public void bind(TravelDeal deal) {
			tvTitle.setText(deal.getTitle());
			tvDescription.setText(deal.getDescription());
			tvPrice.setText(deal.getPrice());
			showImage(deal.getImageUrl());
		}

		@Override
		public void onClick(View view) {
			int position = getAdapterPosition();
			Log.d(TAG, "Click " + String.valueOf(position));

			// start a new activity
			TravelDeal selectedDeal = mTravelDeals.get(position);
			Intent intent = new Intent(view.getContext(), DealActivity.class);
			intent.putExtra("Deal", selectedDeal);
			view.getContext().startActivity(intent);
		}

		private void showImage(String url) {
			if (url != null && url.isEmpty()==false) {
				Picasso.with(imageDeal.getContext())
						.load(url)
						.resize(160, 160)
						.centerCrop()
						.into(imageDeal);
			}
		}
	}
}
