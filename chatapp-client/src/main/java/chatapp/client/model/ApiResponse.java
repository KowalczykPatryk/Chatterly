package chatapp.client.model;

public class ApiResponse<T> {
    private final int status;
    private final T body;

    public ApiResponse(int status, T body) {
        this.status = status;
        this.body = body;
    }
    public int getStatus() {return status;}
    public T getBody() {return body;}
}
