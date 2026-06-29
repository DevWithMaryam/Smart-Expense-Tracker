package com.maryam.smartexpensetracker.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.auth.FirebaseUser;
import com.maryam.smartexpensetracker.repository.AuthRepository;
import com.maryam.smartexpensetracker.utils.Resource;

public class AuthViewModel extends ViewModel {

    private final AuthRepository authRepository;
    public MutableLiveData<Resource<FirebaseUser>> loginResult = new MutableLiveData<>();
    public MutableLiveData<Resource<String>> signupResult = new MutableLiveData<>();
    public MutableLiveData<Resource<String>> forgotPasswordResult = new MutableLiveData<>();

    public AuthViewModel() {
        authRepository = new AuthRepository();
    }

    public FirebaseUser getCurrentUser() {
        return authRepository.getCurrentUser();
    }

    public void login(String email, String password) {
        authRepository.login(email, password, loginResult);
    }

    public void signup(String fullName, String email, String password) {
        authRepository.signup(fullName, email, password, signupResult);
    }

    public void forgotPassword(String email) {
        authRepository.forgotPassword(email, forgotPasswordResult);
    }

    public void logout() {
        authRepository.logout();
    }
}