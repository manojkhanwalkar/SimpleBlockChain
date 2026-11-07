package data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class TransactionQuery {

    /** Transaction ids are issued as "<node name>-<time>". */
    @NotNull
    @Pattern(regexp = "[A-Za-z0-9_]{1,64}-[0-9]{1,18}")
    String transactionId;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
