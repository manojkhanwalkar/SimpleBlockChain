package blockchain;


import data.Transaction;

import java.util.ArrayList;
import java.util.List;


public class TransactionManager {

    public String getLastTransactionId() {
        return TransactionBlockChainManager.getInstance().getLastTransactionId();
    }

    public Transaction query(String transactionId) {
        return TransactionBlockChainManager.getInstance().getTransactionFromId(transactionId);

    }

    static class TMHolder
    {
        static TransactionManager INSTANCE = new TransactionManager();
    }

    public final static int MAX = 16;

    List<Transaction> transactionList = new ArrayList<>(MAX);
    private TransactionManager()
    {

    }

    public static TransactionManager getInstance()
    {
        return TMHolder.INSTANCE;
    }


    int count = 0;
    public synchronized void submit(Transaction transaction)
    {

        transactionList.add(transaction);
        count++;
        if (count >= MAX)
        {
            count=0;
            TransactionBlockChainManager.getInstance().submit(transactionList);
            transactionList = new ArrayList<>(MAX);
        }



    }
}
