package chatapp.client.service;

import chatapp.client.api.UserApi;
import chatapp.client.http.HttpService;
import chatapp.client.dto.RegisterRequest;
import chatapp.client.dto.RegisterResponse;
import chatapp.client.dto.LoginRequest;
import chatapp.client.dto.LoginResponse;
import chatapp.client.dto.RefreshRequest;
import chatapp.client.dto.Tokens;
import chatapp.client.model.ApiResponse;

public class UserServiceClient implements UserApi {
    private final HttpService http;
    private final String baseUrl;

    public UserServiceClient(HttpService http, String baseUrl) {
        this.http = http;
        this.baseUrl = baseUrl;
    }

    @Override
    public ApiResponse<RegisterResponse> register(RegisterRequest req) {
        return http.post(baseUrl + "/register", req, RegisterResponse.class);
    }

    @Override
    public ApiResponse<LoginResponse> login(LoginRequest req) {
        return http.post(baseUrl + "/login", req, LoginResponse.class);
    }

    //logout on the server should always return the save type of object
    @Override
    public ApiResponse<Tokens> refresh(RefreshRequest req) {
        return http.post(baseUrl + "/refreshToken", req, Tokens.class);
    }


    //logout on the server should always return the save type of object
    @Override
    public void logout(RefreshRequest req) {
        http.post(baseUrl + "/logout", req, Void.class);
    }
}

