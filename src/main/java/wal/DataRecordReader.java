package wal;

import java.io.IOException;
import java.io.RandomAccessFile;



public class DataRecordReader {


    static final long InvalidLocation = -1 ;



    String fileName;  // = "/home/manoj/data/aero/datafile";



    RandomAccessFile reader;

    public DataRecordReader(String fileName)
    {

        this.fileName = fileName;
        try {
            reader = new RandomAccessFile(fileName,"r");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public WALRecord read(long start)
    {


        try {
            reader.seek(start);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return  WALRecord.deserialize(reader);



    }

    public WALRecord readNext()
    {


        return  WALRecord.deserialize(reader);



    }

    public long getCurrentPosition()
    {
        try {
            return reader.getFilePointer();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return InvalidLocation;
    }


    public void close()
    {
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
