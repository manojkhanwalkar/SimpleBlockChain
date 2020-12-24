package wal;


import data.Transaction;
import util.JSONUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Objects;

public class WALRecord {
    /*WALRecord structure -
    length of transaction id
    transaction id
    length of transaction
    transaction.

     */

    static char EOR = (char)30;  // end of record
    static char EOF = (char)31;  // end of file - to show WAL has correctly been created and closed.

    final String transaction;
    final String transactionId;
    public WALRecord(Transaction transaction)
    {
        this.transaction = JSONUtil.toJSON(transaction);
        this.transactionId=transaction.getTransactionId();
    }


    public static String serialize(WALRecord record)
    {
        StringBuilder builder = new StringBuilder();
        builder.append(record.transaction);
        builder.append(EOR);
        return builder.toString();
    }


    public static WALRecord deserialize(String record)
    {

        return new WALRecord((Transaction)JSONUtil.fromJSON(record.substring(0,record.length()-1),Transaction.class));



    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WALRecord walRecord = (WALRecord) o;
        return transactionId.equals(walRecord.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId);
    }

    public static WALRecord deserialize(RandomAccessFile reader)
    {
        int keyStart=0;
        int keyEnd=0;
        int valStart=0;
        int valEnd=0;

        StringBuilder builder = new StringBuilder();

        int i=0;
        while(true) {

            char c = 0;
            try {
                c = reader.readChar();
            } catch (IOException e) {
                e.printStackTrace();
            }
            builder.append(c);


                if (c == EOF) {
                    keyEnd = i - 1;
                    break;
                }

                i++;
        }


            valStart = keyEnd + 2;

        i=valStart;

        while(true) {

            char c = 0;
            try {
                c = reader.readChar();
                builder.append(c);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (c == EOF) {
                    valEnd = i - 1;
                    break;
                }

                i++;
        }

        String record = builder.toString();

            String key = record.substring(keyStart, keyEnd + 1);
            String value = record.substring(valStart, valEnd + 1);

            return new WALRecord((Transaction)JSONUtil.fromJSON(value,Transaction.class));




    }



}
