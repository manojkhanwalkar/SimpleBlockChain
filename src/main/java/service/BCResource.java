package service;


import blockchain.TransactionManager;
import com.codahale.metrics.annotation.Timed;
import data.Transaction;
import data.TransactionQuery;
import data.TransactionResponse;
import wal.WALManager;


import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class BCResource {
    private final String template;
    private final String defaultName;



    public BCResource(String template, String defaultName) {
        this.template = template;
        this.defaultName = defaultName;

        walManager.init();

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
        transactionManager.submit(request,nodeName);

        var response = new TransactionResponse();
        response.setTransactionId(tranId);
        return response;

    }

    @POST
    @Timed
    @Path("/query")
    @Produces(MediaType.APPLICATION_JSON)
    public Transaction verify(TransactionQuery request) {

        return null;

    }









}
