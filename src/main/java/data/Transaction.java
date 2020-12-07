package data;

public class Transaction {

    String signature;

    String transactionId;

    String contents;

    String publicKey;

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "signature='" + signature + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", contents='" + contents + '\'' +
                ", publicKey='" + publicKey + '\'' +
                '}';
    }
}
