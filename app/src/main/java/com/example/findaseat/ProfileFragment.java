package com.example.findaseat;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.annotation.Nullable;

///**
// * A simple {@link Fragment} subclass.
// * Use the {@link ProfileFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class ProfileFragment extends Fragment {
    private String username;
    private String name;
    private String USC_id;
    private String affiliation;
    private String userImage;
    private View rootView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        boolean isLoggedIn;
        if (TestUtils.isRunningTest()) {
            isLoggedIn = true;
        } else {
            isLoggedIn = MainActivity.isLoggedIn();
        }


        rootView = inflater.inflate(R.layout.my_profile, container, false);
        Button manageReservationsButton = rootView.findViewById(R.id.manage_reservation_button);
        Button logoutButton = rootView.findViewById(R.id.logout_button);

        startFireStore();

        manageReservationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ManageReservationFragment manageReservationsFragment = new ManageReservationFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, manageReservationsFragment);
                //transaction.commit();
                if (!TestUtils.isRunningTest()) {
                    transaction.commit();
                }
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create and show a confirmation dialog
                new AlertDialog.Builder(requireContext())
                        .setTitle("Confirm Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("LOGOUT", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // User confirmed the logout
//                                LoginFragment loginFragment = new LoginFragment();
//                                FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                                transaction.replace(R.id.frame_layout, loginFragment);
//                                transaction.commit();
                                LoginFragment loginFragment = new LoginFragment();
                                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                transaction.replace(R.id.frame_layout, loginFragment);
//                                if (!TestUtils.isRunningTest()) {
//                                    ((MainActivity) requireActivity()).setLoggedIn(false);
//                                    transaction.commit();
//                                }
                                MainActivity.setLoggedIn(false);
                                transaction.commit();
//                                else {
//                                    TextView testingView = rootView.findViewById(R.id.signoutSuccessMessage);
//                                    testingView.setVisibility(View.VISIBLE);
//                                }

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // User canceled the logout, do nothing
                            }
                        })
                        .show();
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    // FIND USER DOCUMENT
    private void startFireStore() {
        username = MainActivity.getUsername();

        DocumentReference docRef = db.collection("users").document(username);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "Found document");
                        name = document.getString("name");
                        USC_id = document.getString("USC_id");
                        affiliation = document.getString("affiliation");
                        userImage = document.getString("image");
//                        Toast.makeText(getActivity(), userImage, Toast.LENGTH_SHORT).show();

                        TextView nameTextView = rootView.findViewById(R.id.name_text);
                        TextView uscidTextView = rootView.findViewById(R.id.uscid_text);
                        TextView affiliationTextView = rootView.findViewById(R.id.affiliation);
                        ImageView userImageView = rootView.findViewById(R.id.profile_image);
                        int imageResource = getResources().getIdentifier(userImage, "drawable", requireContext().getPackageName());
                        //userImageView.setImageResource(R.drawable.cat1);

                        String display_uscid = "USC ID: " + USC_id;

                        nameTextView.setText(name);
                        uscidTextView.setText(display_uscid);
                        affiliationTextView.setText(affiliation);
                        userImageView.setImageResource(imageResource);


                    } else {
                        Log.d(TAG, "No such document");

                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}