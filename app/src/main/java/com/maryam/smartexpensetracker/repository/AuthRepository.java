package com.maryam.smartexpensetracker.repository;

import androidx.lifecycle.MutableLiveData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.maryam.smartexpensetracker.utils.Resource;

public class AuthRepository {

    private final FirebaseAuth firebaseAuth;

    public AuthRepository() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    public void login(String email, String password, MutableLiveData<Resource<FirebaseUser>> result) {
        result.setValue(Resource.loading());
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    if (user != null && user.isEmailVerified()) {
                        result.setValue(Resource.success(user));
                    } else {
                        result.setValue(Resource.error("Please verify your email first."));
                        firebaseAuth.signOut();
                    }
                })
                .addOnFailureListener(e ->
                        result.setValue(Resource.error(e.getMessage())));
    }

    public void signup(String fullName, String email, String password, MutableLiveData<Resource<String>> result) {
        result.setValue(Resource.loading());
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    if (user != null) {
                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(fullName)
                                .build();
                        user.updateProfile(profileUpdate);
                        user.sendEmailVerification()
                                .addOnSuccessListener(unused ->
                                        result.setValue(Resource.success("Verification email sent!")))
                                .addOnFailureListener(e ->
                                        result.setValue(Resource.error(e.getMessage())));
                    }
                })
                .addOnFailureListener(e ->
                        result.setValue(Resource.error(e.getMessage())));
    }

    public void forgotPassword(String email, MutableLiveData<Resource<String>> result) {
        result.setValue(Resource.loading());
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(unused ->
                        result.setValue(Resource.success("Password reset email sent!")))
                .addOnFailureListener(e ->
                        result.setValue(Resource.error(e.getMessage())));
    }

    public void logout() {
        firebaseAuth.signOut();
    }
}