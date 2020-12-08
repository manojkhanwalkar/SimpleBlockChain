package blockchain;


import data.Transaction;

import java.util.ArrayList;
import java.util.List;


public class TransactionManager {

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
    public synchronized String submit(Transaction transaction, String nodeName)
    {
        String tranId = nodeName +"-" + System.nanoTime();
        transaction.setTransactionId(tranId);

        transactionList.add(transaction);
        count++;
        if (count >= MAX)
        {
            count=0;
            TransactionBlockChainManager.getInstance().submit(transactionList);
            transactionList = new ArrayList<>(MAX);
        }

        return tranId;

    }
}
