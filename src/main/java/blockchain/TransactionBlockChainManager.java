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

    String persistenceDir;

    public String getPersistenceDir() {
        return persistenceDir;
    }

    public void setPersistenceDir(String persistenceDir) {
        this.persistenceDir = persistenceDir;
        // TODO - fix this
        //persistenceManager = new FilePersistenceManager(persistenceDir);

    }



    ExecutorService service = Executors.newCachedThreadPool();

//TODO - restore feature needs to be added

    @JsonProperty
    List<Block> blocks = new ArrayList<>();

    protected synchronized void add(Block block)
    {
        String hash = blocks.get(blocks.size()-1).rootHash;
        block.prevHash = hash;
        block.rootHash = getSHA2HexValue(block.prevHash+block.tree.getRoot().getHash());
        blocks.add(block);

        if (blocks.size()==MAXBLOCKS)
            persist(blocks);


        blocks.clear();
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








    static class BCHolder
    {
        static TransactionBlockChainManager INSTANCE = new TransactionBlockChainManager();
    }

     FilePersistenceManager persistenceManager;


    private TransactionBlockChainManager()
    {
    }

    public static TransactionBlockChainManager getInstance()
    {
        return BCHolder.INSTANCE;
    }


}
