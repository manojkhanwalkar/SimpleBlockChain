import blockchain.TransactionBlockChainManager;
import blockchain.TransactionManager;
import data.Transaction;
import org.junit.Test;
import util.MerkleTreeUtil;

import java.util.List;
import java.util.UUID;

public class MerkleTreeTester {

    @Test
    public void createTree()
    {
        Transaction t1 = new Transaction();
        t1.setContents("Simple transacrtion");
        t1.setTransactionId(UUID.randomUUID().toString());
        t1.setPublicKey("Public key");
        t1.setSignature("Signed transaction");

        Transaction t2 = new Transaction();
        t2.setContents("Another Simple transacrtion");
        t2.setTransactionId(UUID.randomUUID().toString());
        t2.setPublicKey("Another Public key");
        t2.setSignature("Another Signed transaction");

        MerkleTreeUtil merkleTreeUtil = new MerkleTreeUtil();
        var tree = merkleTreeUtil.build(List.of(t1,t2));

        System.out.println(MerkleTreeUtil.toJSON(tree));



    }

    @Test
    public void createBlock()
    {
        TransactionBlockChainManager.getInstance().bootstrap();
        for (int i=0;i< TransactionManager.MAX+1;i++) {
            Transaction t1 = new Transaction();
            t1.setContents("Simple transacrtion");
            t1.setTransactionId(UUID.randomUUID().toString());
            t1.setPublicKey("Public key");
            t1.setSignature("Signed transaction");
            TransactionManager.getInstance().submit(t1);
        }

        try {
            Thread.sleep(500000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
