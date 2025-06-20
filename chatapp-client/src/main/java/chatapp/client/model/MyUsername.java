package chatapp.client.model;

public class MyUsername {
    private static String myUsername;

    public static String getMyUsername() { return myUsername; }
    public static void setMyUsername(String myUsername) { MyUsername.myUsername = myUsername; }
}