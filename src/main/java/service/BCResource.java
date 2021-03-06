package service;


import blockchain.TransactionManager;
import com.codahale.metrics.annotation.Timed;
import data.Transaction;
import data.TransactionQuery;
import data.TransactionResponse;
import util.JSONUtil;
import wal.WALIterator;
import wal.WALManager;
import wal.WALRecord;


import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Iterator;


@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class BCResource {
    private final String template;
    private final String defaultName;



    public BCResource(String template, String defaultName) {
        this.template = template;
        this.defaultName = defaultName;

        walManager.init();
        String transactionId = transactionManager.getLastTransactionId();

        System.out.println("Last transaction in the block chain is " + transactionId);
            var iterator = walManager.recovery(transactionId);

            // IF the Iterator is null , there ar no WAL files and hence nothing to recover.
            if (iterator != null) {
                System.out.println("Recovering from the WAL , transactions not yet committed to the block chain");
                int count =0;
                while(iterator.hasNext())
                {
                    count++;
                    var record = iterator.next().getTransaction();

                    transactionManager.submit((Transaction)JSONUtil.fromJSON(record,Transaction.class));
                }

                System.out.println("Recovered from WAL " + count);

            }

    }

    private void printWALS(Iterator<WALRecord> iterator)
    {
        while(iterator.hasNext())
        {
            System.out.println(iterator.next());
        }
    }

    String nodeName = "bcn1" ; //TODO - to take this from the configuration

    WALManager walManager = new WALManager(nodeName,"/home/manoj/data/sbc/wal/");


    TransactionManager transactionManager = TransactionManager.getInstance();

    @POST
    @Timed
    @Path("/submit")
    @Produces(MediaType.APPLICATION_JSON)
    public TransactionResponse submit(Transaction request) {

        String tranId = nodeName +"-" + System.nanoTime();
        request.setTransactionId(tranId);

        walManager.submit(request);
        transactionManager.submit(request);

        var response = new TransactionResponse();
        response.setTransactionId(tranId);
        return response;

    }

    @POST
    @Timed
    @Path("/query")
    @Produces(MediaType.APPLICATION_JSON)
    public Transaction verify(TransactionQuery request) {

        return transactionManager.query(request.getTransactionId());


    }









}
