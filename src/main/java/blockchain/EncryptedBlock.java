package blockchain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EncryptedBlock {


    @JsonProperty
    String encryptedContents;

    @JsonProperty
    String encryptedKey;

    @JsonProperty
    String startTransactionId;

    @JsonProperty
    String endTransactionId;

    public String getStartTransactionId() {
        return startTransactionId;
    }

    public void setStartTransactionId(String startTransactionId) {
        this.startTransactionId = startTransactionId;
    }

    public String getEndTransactionId() {
        return endTransactionId;
    }

    public void setEndTransactionId(String endTransactionId) {
        this.endTransactionId = endTransactionId;
    }

    public String getEncryptedContents() {
        return encryptedContents;
    }

    public void setEncryptedContents(String encryptedContents) {
        this.encryptedContents = encryptedContents;
    }

    public String getEncryptedKey() {
        return encryptedKey;
    }

    public void setEncryptedKey(String encryptedKey) {
        this.encryptedKey = encryptedKey;
    }
}
