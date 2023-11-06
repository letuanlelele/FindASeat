package com.example.findaseat;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class LoginFragment extends Fragment {

    private EditText uscidEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference users = db.collection("users");
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.login_fragment, container, false);
        uscidEditText = rootView.findViewById(R.id.uscidEditText);
        passwordEditText = rootView.findViewById(R.id.passwordEditText);
        loginButton = rootView.findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle login button click here
                // Perform login logic here
                String uscid = uscidEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                startFireStore(uscid, password);


                // Check login credentials, and if login is successful
//                if (true) {
//                    // Navigate to the ProfileFragment or any other fragment
//                    ((MainActivity) requireActivity()).setLoggedIn(true);
//
//                    ProfileFragment profileFragment = new ProfileFragment();
//                    FragmentManager fragmentManager = getParentFragmentManager();
//                    fragmentManager.beginTransaction()
//                            .replace(R.id.frame_layout, profileFragment)
//                            .commit();
//                }
//                else{
//                    TextView errorMessageTextView = rootView.findViewById(R.id.errorMessage);
//                    errorMessageTextView.setVisibility(View.VISIBLE);
//                }
            }
        });

        return rootView;
    }

    // Example login logic (replace with your actual login logic)
    private void startFireStore(String uscid, String password) {
        if (uscid.isEmpty() || password.isEmpty()) {
            // Handle empty fields, show an error message or toast
            //Toast.makeText(getActivity(), "Please enter USC ID and password", Toast.LENGTH_SHORT).show();
            TextView errorMessageTextView = rootView.findViewById(R.id.errorMessage);
            errorMessageTextView.setVisibility(View.VISIBLE);
        } else {
            DocumentReference docRef = db.collection("users").document(uscid);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "Found document");
                            String temp = document.getString("password");
                            if (temp.equals(password)) {
                                //Toast.makeText(getActivity(), "password is valid", Toast.LENGTH_SHORT).show();
                                // Check password successful, implement logic for successful login HERE
                                // Consider calling a function here that does post-login logic
                                ((MainActivity) requireActivity()).setLoggedIn(true);
                                ((MainActivity) requireActivity()).setUsername(document.getId());
                                ProfileFragment profileFragment = new ProfileFragment();
                                FragmentManager fragmentManager = getParentFragmentManager();
                                fragmentManager.beginTransaction()
                                        .replace(R.id.frame_layout, profileFragment)
                                        .commit();
                            }
                        } else {
                            Log.d(TAG, "No such document");
                            TextView errorMessageTextView = rootView.findViewById(R.id.errorMessage);
                            errorMessageTextView.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                        TextView errorMessageTextView = rootView.findViewById(R.id.errorMessage);
                        errorMessageTextView.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }




}
