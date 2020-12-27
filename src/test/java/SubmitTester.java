import data.Transaction;
import org.junit.Test;
import util.Connection;
import util.JSONUtil;

public class SubmitTester {

    @Test
    public void submit()
    {
        Connection connection = new Connection("https://localhost:8180/");
        for (int i=0;i<1000;i++)
        {
            Transaction transaction = new Transaction();
            transaction.setSignature("Signature");
            transaction.setPublicKey("Pub Key");
            transaction.setContents("Some tran " + i);

            var response = connection.sendSimple(JSONUtil.toJSON(transaction),"submit");
            System.out.println(response);
        }

        System.out.println(Long.MAX_VALUE);
    }
}
