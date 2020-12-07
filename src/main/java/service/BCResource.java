package service;


import com.codahale.metrics.annotation.Timed;
import data.Transaction;
import data.TransactionQuery;
import data.TransactionResponse;


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



    }






    @POST
    @Timed
    @Path("/submit")
    @Produces(MediaType.APPLICATION_JSON)
    public TransactionResponse submit(Transaction request) {


        return null;

    }

    @POST
    @Timed
    @Path("/query")
    @Produces(MediaType.APPLICATION_JSON)
    public Transaction verify(TransactionQuery request) {

        return null;

    }









}
