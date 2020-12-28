import data.Transaction;
import data.TransactionQuery;
import org.junit.Test;
import util.Connection;
import util.JSONUtil;

public class SubmitTester {

    @Test
    public void submit()
    {
        Connection connection = new Connection("https://localhost:8180/");
        for (int i=0;i<1001;i++)
        {
            Transaction transaction = new Transaction();
            transaction.setSignature("Signature");
            transaction.setPublicKey("Pub Key");
            transaction.setContents("Some tran " + i);

            var response = connection.sendSimple(JSONUtil.toJSON(transaction),"submit");
            System.out.println(response);
        }


    }

    @Test
    public void query()
    {
        Connection connection = new Connection("https://localhost:8180/");
        TransactionQuery transactionQuery = new TransactionQuery();
        transactionQuery.setTransactionId("bcn1-3757071982160");

            var response = connection.sendSimple(JSONUtil.toJSON(transactionQuery),"query");
            System.out.println(response);

    }
}
