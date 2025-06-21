package chatapp.client.api;

import chatapp.client.dto.RegisterRequest;
import chatapp.client.dto.RegisterResponse;
import chatapp.client.dto.RefreshRequest;
import chatapp.client.dto.RefreshResponse;
import chatapp.client.dto.LoginRequest;
import chatapp.client.dto.LoginResponse;
import chatapp.client.dto.LogoutRequest;
import chatapp.client.dto.LogoutResponse;
import chatapp.client.dto.GetUsernamesRequest;
import chatapp.client.dto.GetUsernamesResponse;
import chatapp.client.model.ApiResponse;

public interface UserApi {
    ApiResponse<RegisterResponse> register(RegisterRequest req);
    ApiResponse<LoginResponse> login(LoginRequest req);
    ApiResponse<GetUsernamesResponse> getUsernames(GetUsernamesRequest req);
    ApiResponse<RefreshResponse> refresh(RefreshRequest req);
    ApiResponse<LogoutResponse> logout(LogoutRequest req);
}
