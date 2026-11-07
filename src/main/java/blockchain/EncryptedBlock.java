package blockchain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EncryptedBlock {


    /** Encryption format version; null for blocks written by the legacy (unauthenticated) format. */
    @JsonProperty
    Integer version;

    @JsonProperty
    String encryptedContents;

    @JsonProperty
    String encryptedKey;

    @JsonProperty
    String startTransactionId;

    @JsonProperty
    String endTransactionId;

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

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
