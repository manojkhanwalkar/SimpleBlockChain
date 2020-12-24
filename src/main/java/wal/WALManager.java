package wal;

import blockchain.TransactionManager;
import data.Transaction;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class WALManager {

    // rotate file if the record to be written will exceed the max size
    static final long MAXWALFILESIZE = 100000;
    final String nodeName;
    final String directory;

    DataRecordWriter dataRecordWriter ;

    final String fileName ="WAL";
    int counter=0;

    public WALManager(String nodeName , String directory)
    {
        this.nodeName = nodeName;
        this.directory = directory;

    }

    public void init()
    {
        /* if there is no file under the directory , create an empty file.
        If there are one or more files , check the validity of the last file.
        If file is invalid for any reason , delete that file and create a new one for writing.
        If file is valid and size not exceeded then set the file pointer to the end of the file.
        If file size is same or greater than max size then create a new file
         */
        File file = new File(directory+nodeName);
        if (!file.exists())
        {
            System.out.println("Invalid directory " + directory+nodeName);
            throw new RuntimeException("WAL Subsystem not initialized correctly");
        }

        File[] files = file.listFiles();
       if (files.length==0)
       {
           dataRecordWriter = new DataRecordWriter(directory+nodeName+"/"+fileName+counter++);
           return;
       }

        Arrays.sort(files, Comparator.comparingLong(File::lastModified));

       File latest = files[files.length-1];
       dataRecordWriter = new DataRecordWriter(latest);

       counter = files.length;

        //TODO - check file validity and recover data for the block chain .

    }

    public void recovery()
    {
        /* get the last transaction id from the block chain persistence subsystem. from that determine which files are to be processed for recovery
        Return an iterator that returns all transaction id's after the one that block chain has passed in.
         */
    }

    public void submit(Transaction transaction)
    {
        WALRecord walRecord = new WALRecord(transaction);
        String str = WALRecord.serialize(walRecord);

        synchronized (this) {
            if (dataRecordWriter.getPosition() + str.length() >= MAXWALFILESIZE) {

                dataRecordWriter.close();

                dataRecordWriter = new DataRecordWriter(directory + nodeName + "/" + fileName + counter++);
            }


        dataRecordWriter.write(str);

        }
    }

}
