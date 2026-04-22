package com.stroheim.app.auth;


import com.stroheim.app.auth.request.AuthenticationRequest;
import com.stroheim.app.auth.request.RefreshRequest;
import com.stroheim.app.auth.request.RegistrationRequest;
import com.stroheim.app.auth.response.AuthenticationResponse;

public interface AuthenticationService {

    AuthenticationResponse login(AuthenticationRequest request);

    void register(RegistrationRequest request);

    AuthenticationResponse refreshToken(RefreshRequest req);
}
