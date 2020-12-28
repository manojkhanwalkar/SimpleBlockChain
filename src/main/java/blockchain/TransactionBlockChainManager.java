package blockchain;




import com.fasterxml.jackson.annotation.JsonProperty;
import data.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static util.MerkleTreeUtil.getSHA2HexValue;

public class TransactionBlockChainManager {


   // BlockChain blockChain ;

    final static String persistenceDir = "/home/manoj/data/sbc/bc/bcn1/";

    public String getPersistenceDir() {
        return persistenceDir;
    }

  /*  public void setPersistenceDir(String persistenceDir) {
        this.persistenceDir = persistenceDir;
        persistenceManager = new FilePersistenceManager(persistenceDir);

    }*/




//TODO - restore feature needs to be added

    @JsonProperty
    List<Block> blocks = new ArrayList<>();

    //TODO - fix the first prev hash to the last persisted block
    String prevHash="0";
    protected synchronized void add(Block block)
    {
        block.prevHash = prevHash;
        block.rootHash = getSHA2HexValue(block.prevHash+block.tree.getRoot().getHash());
        blocks.add(block);

        prevHash = block.rootHash;

        if (blocks.size()==MAXBLOCKS) {
            persist(blocks);
            blocks.clear();
        }



    }




    public synchronized void persist(List<Block> blocks)
    {
        persistenceManager.persist(blocks);
    }

    public synchronized void bootstrap()
    {
        //TODO - fix this
        //blockChain= new BlockChain();
        //blockChain.bootstrap();

        //persist(blockChain.getBlocks().get(0));
    }

    public synchronized void restore()
    {
        //TODO - fix this
       /* blockChain = persistenceManager.restore();
        if (blockChain==null)
        {
            bootstrap();
        }
        System.out.println(blockChain.getBlocks().size());*/
    }


    static final int MAXBLOCKS = 10;

    public void submit(List<Transaction> transactions)
    {
        // once max blocks are created , then persist to a new file .

        Block block = new Block(transactions);
        add(block);

      //  service.submit(new TransactionToBlocksProcessor(transactions));
    }

    public String getLastTransactionId() {

        Block lastBlock =  persistenceManager.getLastBlockWritten();
        if (lastBlock==null)
            return null;
        var list = lastBlock.transactions();

        var last = list.get(list.size()-1);

        return last.getTransactionId();

    }

    public Transaction getTransactionFromId(String transactionId)
    {

        Block lastBlock =  persistenceManager.getLikelyBlock(transactionId);
        if (lastBlock==null)
            return null;
        var list = lastBlock.transactions();



        var last = find(transactionId, list);

        return last;
    }

    private Transaction  find(String transactionId, List<Transaction> list) {

        for (Transaction transaction : list)
        {
            if (transaction.getTransactionId().equals(transactionId))
                return transaction;
        }

        return null;
    }


    static class BCHolder
    {
        static TransactionBlockChainManager INSTANCE = new TransactionBlockChainManager();
    }

     FilePersistenceManager persistenceManager;


    private TransactionBlockChainManager()
    {
        persistenceManager = new FilePersistenceManager(persistenceDir);

    }

    public static TransactionBlockChainManager getInstance()
    {
        return BCHolder.INSTANCE;
    }


}
