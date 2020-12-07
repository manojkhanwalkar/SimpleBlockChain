package blockchain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EncryptedBlock {


    @JsonProperty
    String encryptedContents;

    @JsonProperty
    String encryptedKey;


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
