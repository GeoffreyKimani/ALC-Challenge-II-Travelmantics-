package com.example.travelmantics;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseUtil {
	public static FirebaseDatabase sFirebaseDatabase;
	public static DatabaseReference sDatabaseReference;
	public static ArrayList<TravelDeal> sTravelDeals;
	public static FirebaseAuth sFirebaseAuth;
	public static FirebaseAuth.AuthStateListener sAuthStateListener;
	private static FirebaseUtil sFirebaseUtil;
	private static ListActivity caller;
	private static final int RC_SIGN_IN = 123;
	public static Boolean isAdmin;
	public static String TAG = FirebaseUtil.class.toString();

	// helps to avoid this class from being instantiated from outside of this class.
	private FirebaseUtil() {
	}

	/**
	 * generic static method that opens a reference of the child, passed as a param.
	 * if the method has already been called, it will do nothing but return itself
	 * otherwise it will create a single instance of itself, get an instance of firebase db and
	 * create an new empty array list of travel deals.
	 * ln 28. opens a path that was passed as a parameter.
	 **/
	public static void openFbReference(String ref, final ListActivity callerActivity) {
		if (sFirebaseUtil == null) {
			sFirebaseUtil = new FirebaseUtil();
			sFirebaseDatabase = FirebaseDatabase.getInstance();
			sFirebaseAuth = FirebaseAuth.getInstance();
			caller = callerActivity;
			sAuthStateListener = new FirebaseAuth.AuthStateListener() {
				@Override
				public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
					if(firebaseAuth.getCurrentUser() == null){
						FirebaseUtil.signIn();
						Toast.makeText(callerActivity.getBaseContext(),
								"Welcome to our App!", Toast.LENGTH_LONG).show();
					}
					else {
						String userId = firebaseAuth.getUid();
						checkAdmin(userId);
					}
				}
			};
		}
		sTravelDeals = new ArrayList<TravelDeal>();
		sDatabaseReference = sFirebaseDatabase.getReference().child(ref);
	}

	private static void checkAdmin(String userId) {
		FirebaseUtil.isAdmin = false;
		DatabaseReference ref = sFirebaseDatabase.getReference().child("administrators").child(userId);

		ChildEventListener listener = new ChildEventListener() {
			@Override
			public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
				FirebaseUtil.isAdmin = true;
				caller.showMenu();
				Log.d(TAG, "User is admin");
			}

			@Override
			public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

			}

			@Override
			public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

			}

			@Override
			public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {

			}
		};
		ref.addChildEventListener(listener);
	}

	private static void signIn(){
		// Choose authentication providers
		List<AuthUI.IdpConfig> providers = Arrays.asList(
				new AuthUI.IdpConfig.EmailBuilder().build(),
				new AuthUI.IdpConfig.GoogleBuilder().build());

		// TODO: Fix logIn activity with google (error: 12500)
		// since we can't call an activity from outside we se a caller activity to start the intent
		// Create and launch sign-in intent
		caller.startActivityForResult(
				AuthUI.getInstance()
						.createSignInIntentBuilder()
						.setAvailableProviders(providers)
						.build(),
				RC_SIGN_IN);
	}
	/**
	 * methods to attach and detach the auth listener
	 */
	public static void attachListener() {
		sFirebaseAuth.addAuthStateListener(sAuthStateListener);
	}

	public static void detachListener() {
		sFirebaseAuth.removeAuthStateListener(sAuthStateListener);
	}

}
