package blockchain;




import data.Transaction;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransactionBlockChainManager {


    BlockChain blockChain ;

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

    protected synchronized void add(Block block)
    {
        blockChain.add(block);
        //TODO - fix this
       // persist(block);
    }


    public synchronized void persist(Block block)
    {
        System.out.println(blockChain.getBlocks().size());
        persistenceManager.persist(block);
    }

    public synchronized void bootstrap()
    {
        blockChain= new BlockChain();
        blockChain.bootstrap();
        //TODO - fix this
        //persist(blockChain.getBlocks().get(0));
    }

    public synchronized void restore()
    {
        blockChain = persistenceManager.restore();
        if (blockChain==null)
        {
            bootstrap();
        }
        System.out.println(blockChain.getBlocks().size());
    }




    public void submit(List<Transaction> transactions)
    {

        service.submit(new TransactionToBlocksProcessor(transactions));
    }




    class TransactionToBlocksProcessor implements Runnable
    {
        List<Transaction> transactions;
      //  BlockChainManager manager;
        public TransactionToBlocksProcessor(List<Transaction> transactions)
        {
            this.transactions = transactions;
         //   this.manager = manager;
        }

        public void run()
        {
            Block block = new Block(transactions);

            add(block);


        }
    }




    static class BCHolder
    {
        static TransactionBlockChainManager INSTANCE = new TransactionBlockChainManager();
    }

     PersistenceManager persistenceManager;


    private TransactionBlockChainManager()
    {
    }

    public static TransactionBlockChainManager getInstance()
    {
        return BCHolder.INSTANCE;
    }


}
