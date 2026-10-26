package data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Transaction {



    String transactionId;

    /** Upper bound on the stored payload, so a single request cannot exhaust memory, the WAL or block files. */
    public static final int MAX_CONTENTS_LENGTH = 1_048_576;

    @NotNull
    @Size(max = MAX_CONTENTS_LENGTH)
    String contents;


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

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId='" + transactionId + '\'' +
                ", contents='" + contents + '\'' +
                '}';
    }
}
