package wal;

import java.io.File;
import java.util.Iterator;

public class WALIterator implements Iterator<WALRecord> {

    final File[] files;
    DataRecordReader reader = null;
    int fileIndex=0;
    public WALIterator(File[] files, String transactionId)
    {
        this.files = files;
        position(transactionId);
    }

    private void position(String transactionId) {
        if (transactionId == null)
            reader = new DataRecordReader(files[fileIndex++]);
        else
        {
            reader = new DataRecordReader(files[fileIndex++]);
            while(hasNext())
            {
                WALRecord record = next();
                if (record.transactionId.equals(transactionId))
                    return;
            }
        }

    }

    WALRecord nextRecord =null;
    @Override
    public boolean hasNext() {

        if (files.length==0)
            return false;

        nextRecord = reader.readNext();
        if (nextRecord!=null)
            return true;
        if (fileIndex < files.length)
        {
            reader = new DataRecordReader(files[fileIndex++]);
            nextRecord = reader.readNext();

            return true;
        }

        return false;


    }

    @Override
    public WALRecord next() {
        return nextRecord;
    }
}
