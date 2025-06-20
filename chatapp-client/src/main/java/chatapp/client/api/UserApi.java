package chatapp.client.api;

import chatapp.client.dto.RegisterRequest;
import chatapp.client.dto.RegisterResponse;
import chatapp.client.dto.Tokens;
import chatapp.client.dto.RefreshRequest;
import chatapp.client.dto.LoginRequest;
import chatapp.client.dto.LoginResponse;
import chatapp.client.model.ApiResponse;

public interface UserApi {
    ApiResponse<RegisterResponse> register(RegisterRequest req);
    ApiResponse<LoginResponse> login(LoginRequest req);
    ApiResponse<Tokens> refresh(RefreshRequest req);
    void logout(RefreshRequest req);
}
