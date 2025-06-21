package chatapp.client.service;

import chatapp.client.api.UserApi;
import chatapp.client.http.HttpService;
import chatapp.client.dto.RegisterRequest;
import chatapp.client.dto.RegisterResponse;
import chatapp.client.dto.LoginRequest;
import chatapp.client.dto.LoginResponse;
import chatapp.client.dto.GetUsernamesRequest;
import chatapp.client.dto.GetUsernamesResponse;
import chatapp.client.dto.RefreshRequest;
import chatapp.client.dto.RefreshResponse;
import chatapp.client.dto.LogoutRequest;
import chatapp.client.dto.LogoutResponse;
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

    @Override
    public ApiResponse<GetUsernamesResponse> getUsernames(GetUsernamesRequest req) {
        return http.post(baseUrl + "/getUsernames", req, GetUsernamesResponse.class);
    }

    @Override
    public ApiResponse<RefreshResponse> refresh(RefreshRequest req) {
        return http.post(baseUrl + "/refreshToken", req, RefreshResponse.class);
    }

    @Override
    public ApiResponse<LogoutResponse> logout(LogoutRequest req) {
        return  http.post(baseUrl + "/logout", req, LogoutResponse.class);
    }
}

