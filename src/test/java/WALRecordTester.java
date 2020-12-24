import blockchain.TransactionBlockChainManager;
import blockchain.TransactionManager;
import data.Transaction;
import org.junit.Test;
import util.MerkleTreeUtil;
import wal.WALRecord;

import java.util.List;
import java.util.UUID;

public class WALRecordTester {

    @Test
    public void serDeSerWAL()
    {
        Transaction t1 = new Transaction();
        t1.setContents("Simple transacrtion");
        t1.setTransactionId(UUID.randomUUID().toString());
        t1.setPublicKey("Public key");
        t1.setSignature("Signed transaction");


        WALRecord record = new WALRecord(t1);

        String ser = WALRecord.serialize(record) ;

        WALRecord record1 = WALRecord.deserialize(ser);

        assert(record.equals(record1));





    }


}
