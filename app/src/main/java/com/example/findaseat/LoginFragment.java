package com.example.findaseat;

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


public class LoginFragment extends Fragment {

    private EditText uscidEditText;
    private EditText passwordEditText;
    private Button loginButton;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference users = db.collection("users");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.login_fragment, container, false);
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
                DocumentReference docRef = db.collection("users").document(uscid);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

                            if (document.exists()) {
                                String string = document.get("password").toString();
                                Toast.makeText(getActivity(), string, Toast.LENGTH_SHORT).show();
                                // Check password is correct
                                if (document.get("password").toString() == password) {
                                    // Navigate to the ProfileFragment or any other fragment
                                    ((MainActivity) requireActivity()).setLoggedIn(true);

                                    ProfileFragment profileFragment = new ProfileFragment();
                                    FragmentManager fragmentManager = getParentFragmentManager();
                                    fragmentManager.beginTransaction()
                                            .replace(R.id.frame_layout, profileFragment)
                                            .commit();
                                    Toast.makeText(getActivity(), "Logged in successful", Toast.LENGTH_SHORT).show();
                                }
//                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            } else {
                                // Document doesn't exist => Username is incorrect or user doesn't exist
//                        Log.d(TAG, "No such document");
//                        TextView errorMessageTextView = rootView.findViewById(R.id.errorMessage);
//                        errorMessageTextView.setVisibility(View.VISIBLE);
                                Toast.makeText(getActivity(), "Username is incorrect or user doesn't exist", Toast.LENGTH_SHORT).show();
                            }
                        } else {
//                    Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });

                // Check login credentials, and if login is successful
//                if (isLoginSuccessful(uscid, password)) {
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
//                isLoginSuccessful(uscid, password);
            }
        });

        return rootView;
    }

    // Example login logic (replace with your actual login logic)
    private void isLoginSuccessful(String uscid, String password) {
//        DocumentReference docRef = users.document(uscid);
        DocumentReference docRef = db.collection("users").document(uscid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        String string = document.get("password").toString();
                        Toast.makeText(getActivity(), string, Toast.LENGTH_SHORT).show();
                        // Check password is correct
                        if (document.get("password").toString() == password) {
                            // Navigate to the ProfileFragment or any other fragment
                            ((MainActivity) requireActivity()).setLoggedIn(true);

                            ProfileFragment profileFragment = new ProfileFragment();
                            FragmentManager fragmentManager = getParentFragmentManager();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.frame_layout, profileFragment)
                                    .commit();
                            Toast.makeText(getActivity(), "Logged in successful", Toast.LENGTH_SHORT).show();
                        }
//                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        // Document doesn't exist => Username is incorrect or user doesn't exist
//                        Log.d(TAG, "No such document");
//                        TextView errorMessageTextView = rootView.findViewById(R.id.errorMessage);
//                        errorMessageTextView.setVisibility(View.VISIBLE);
                        Toast.makeText(getActivity(), "Username is incorrect or user doesn't exist", Toast.LENGTH_SHORT).show();
                    }
                } else {
//                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        // Replace this with your actual login logic (e.g., Firebase Authentication)
        // For this example, let's assume a simple check for a valid USC ID and password.
//        return uscid.equals("123456789") && password.equals("password");
    }


}
