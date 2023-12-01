package com.example.findaseat;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

public class CreateAccountFragment extends Fragment {

    private View rootView;
    private EditText name;
    private EditText email;
    private EditText USCID;
    private Spinner affiliation;
    private EditText password;
    private EditText confirmPassword;
    private Spinner profilePicture;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public CreateAccountFragment() {

    }

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.create_account_fragment, container, false);

        name = rootView.findViewById(R.id.newName);
        email = rootView.findViewById(R.id.newUsername);
        USCID = rootView.findViewById(R.id.newUSCID);
        affiliation = rootView.findViewById(R.id.newAffiliation);
        password = rootView.findViewById(R.id.newPasswordEditText);
        confirmPassword = rootView.findViewById(R.id.confrimPasswordEditText);
        profilePicture = rootView.findViewById(R.id.profilePictureSpinner);
        Button createAccountButton = rootView.findViewById(R.id.createAccountButton);

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle create account button click here
                // Perform account creation logic here
                String newName = name.getText().toString();
                String newEmail = email.getText().toString();
                String newUSCID = USCID.getText().toString();
                String newAffiliation = affiliation.getSelectedItem().toString();
                String newPassword = password.getText().toString();
                String newConfirmPassword = confirmPassword.getText().toString();
                String newProfilePicture = profilePicture.getSelectedItem().toString();
                String selectedProfilePicture = convertCat(newProfilePicture);

                createNewUserInFirestore(newName, newEmail, newUSCID, newAffiliation, newPassword, newConfirmPassword, selectedProfilePicture);
            }
        });

        return rootView;
    }

    public String convertCat(String catName) {
        if (Objects.equals(catName, "Polite Cat")) {
            return "cat1";
        } else if (Objects.equals(catName, "Blep Cat")) {
            return "cat2";
        } else if (Objects.equals(catName, "Loaf Cat")) {
            return "cat3";
        } else if (Objects.equals(catName, "Sandwich Cat")) {
            return "cat4";
        } else {
            return "cat5";
        }
    }

    public void setNewUserInfo(DocumentReference doc, String name, String username, String uscID, String affiliation, String password, String confirmPassword, String profilePicture) {
        Map<String, Object> data = new HashMap<>();
        data.put("USC_id", uscID);
        data.put("affiliation", affiliation);
        data.put("image", profilePicture);
        data.put("name", name);
        data.put("password", password);

        doc.set(data);
    }

    private void createNewUserInFirestore(String name, String username, String uscID, String affiliation, String password, String confirmPassword, String profilePicture) {
        String TAG = "myInfoTag";
        // Check that there are no blank fields
        if (name.isEmpty() || username.isEmpty() || uscID.isEmpty() || affiliation.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            TextView errorMessageTextView = rootView.findViewById(R.id.errorCreateAccountMessage);
            errorMessageTextView.setVisibility(View.VISIBLE);
            return;
        }

        // Check if email id usc.edu
        if (!username.contains("usc.edu")){
            Toast.makeText(getActivity(), "Invalid email, please enter USC email", Toast.LENGTH_SHORT).show();
            TextView errorMessageTextView = rootView.findViewById(R.id.errorCreateAccountMessage);
            errorMessageTextView.setVisibility(View.VISIBLE);
            return;
        }

        // Check if USCID is 10 digits
        if (uscID.length() != 10) {
            Toast.makeText(getActivity(), "USCID must be 10 digits", Toast.LENGTH_SHORT).show();
            TextView errormessageTextView = rootView.findViewById(R.id.errorCreateAccountMessage);
            errormessageTextView.setVisibility(View.VISIBLE);
            return;
        }

        // Check if the password and confirm password match
        if (!password.equals(confirmPassword)) {
            Toast.makeText(getActivity(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            TextView errorMessageTextView = rootView.findViewById(R.id.errorCreateAccountMessage);
            errorMessageTextView.setVisibility(View.VISIBLE);
            return;
        }

        DocumentReference docRef = db.collection("users").document(username);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Toast.makeText(getActivity(), "User or username already taken, please retry.", Toast.LENGTH_SHORT).show();
                        TextView errorMessageTextView = rootView.findViewById(R.id.errorCreateAccountMessage);
                        errorMessageTextView.setVisibility(View.VISIBLE);
                    } else {
                        setNewUserInfo(docRef, name, username, uscID, affiliation, password, confirmPassword, profilePicture);
                        if (!TestUtils.isRunningTest()) {
                            loginUser(username, password);
                        } else {
                            TextView errorMessageTextView = rootView.findViewById(R.id.successCreateAccountMessage);
                            errorMessageTextView.setVisibility(View.VISIBLE);
                        }

                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    public void loginUser(String username, String password) {
        DocumentReference docRef = db.collection("users").document(username);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "Found document");
                        String temp = document.getString("password");
                        assert temp != null;
                        if (temp.equals(password)) {
                            //Toast.makeText(getActivity(), "password is valid", Toast.LENGTH_SHORT).show();
                            // Check password successful, implement logic for successful login HERE
                            // Consider calling a function here that does post-login logic
                            MainActivity.setLoggedIn(true);
                            MainActivity.setUsername(document.getId());
                            ProfileFragment profileFragment = new ProfileFragment();
                            FragmentManager fragmentManager = getParentFragmentManager();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.frame_layout, profileFragment)
                                    .commit();
                        } else {
                            TextView errorMessageTextView = rootView.findViewById(R.id.errorMessage);
                            errorMessageTextView.setVisibility(View.VISIBLE);
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
