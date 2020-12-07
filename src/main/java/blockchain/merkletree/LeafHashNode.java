package blockchain.merkletree;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import data.Transaction;


@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class LeafHashNode implements Node {

    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


  //  @JsonManagedReference
    HashNode parent;
    String hash;

    Transaction transaction;

    public void setParent(HashNode parent) {
        this.parent = parent;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public Node getLeft() {
        return null;
    }

    @Override
    public String getHash() {
        return hash;
    }

    @Override
    public Node getParent() {
        return parent;
    }


    @Override
    public void setHash(String hash) {

        this.hash = hash;

    }

    @Override
    public void setParent(Node parent) {

        this.parent = (HashNode)parent;
    }

    @Override
    public String toString() {
        return "LeafHashNode{" +
                "id=" + id +
                ", parent=" + parent +
                ", hash='" + hash + '\'' +
                ", transaction=" + transaction +
                '}';
    }
}
