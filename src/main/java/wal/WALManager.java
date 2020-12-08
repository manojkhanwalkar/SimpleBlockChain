package wal;

import blockchain.TransactionManager;
import data.Transaction;

import java.util.ArrayList;
import java.util.List;

public class WALManager {
    //   /home/manoj/data/sbc/wal

    String nodeName;
    String directory;
    public WALManager(String nodeName , String directory)
    {
        this.nodeName = nodeName;
        this.directory = directory;

    }

    public void submit(Transaction transaction)
    {

    }

}
