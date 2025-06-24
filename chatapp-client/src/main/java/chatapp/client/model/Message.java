package chatapp.client.model;


public class Message
{
    private String fromUser;
    private String toUser;
    private byte[] iv;
    private byte[] encryptedKey;
    private byte[] ciphertext;

    public Message() {}

    public Message(String fromUser, String toUser, byte[] iv, byte[] encryptedKey, byte[] ciphertext) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.iv = iv;
        this.encryptedKey = encryptedKey;
        this.ciphertext = ciphertext;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public byte[] getIv() {
        return iv;
    }

    public void setIv(byte[] iv) {
        this.iv = iv;
    }

    public byte[] getEncryptedKey() {
        return encryptedKey;
    }

    public void setEncryptedKey(byte[] encryptedKey) {
        this.encryptedKey = encryptedKey;
    }

    public byte[] getCiphertext() {
        return ciphertext;
    }

    public void setCiphertext(byte[] ciphertext) {
        this.ciphertext = ciphertext;
    }
}