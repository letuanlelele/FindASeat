package com.example.findaseat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class LoginFragment extends Fragment {

    private EditText uscidEditText;
    private EditText passwordEditText;
    private Button loginButton;


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

                // Check login credentials, and if login is successful
                if (isLoginSuccessful(uscid, password)) {
                    // Navigate to the ProfileFragment or any other fragment
                    ((MainActivity) requireActivity()).setLoggedIn(true);

                    ProfileFragment profileFragment = new ProfileFragment();
                    FragmentManager fragmentManager = getParentFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.frame_layout, profileFragment)
                            .commit();
                }
                else{
                    TextView errorMessageTextView = rootView.findViewById(R.id.errorMessage);
                    errorMessageTextView.setVisibility(View.VISIBLE);
                }
            }
        });

        return rootView;
    }

    // Example login logic (replace with your actual login logic)
    private boolean isLoginSuccessful(String uscid, String password) {
        // Replace this with your actual login logic (e.g., Firebase Authentication)
        // For this example, let's assume a simple check for a valid USC ID and password.
        return uscid.equals("123456789") && password.equals("password");
    }
}
