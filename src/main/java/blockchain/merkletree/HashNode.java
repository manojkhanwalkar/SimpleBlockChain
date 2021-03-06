package blockchain.merkletree;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class HashNode implements Node {

    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    HashNode parent;
String hash;


Node left ;


Node right;


    @Override
    public Node getLeft() {
        return left;
    }

    @Override
    public Node getRight() {
        return right;
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
    public void setLeft(Node node) {
        left = node;
    }

    @Override
    public void setRight(Node node) {
            right = node;
    }

    @Override
    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public void setParent(Node parent) {
        this.parent = (HashNode)parent;
    }

  /*  @Override
    public String toString() {
        return "HashNode{" +
                "id=" + id +
                ", hash='" + hash + '\'' +
                ", left=" + left +
                ", right=" + right +
                '}';
    }*/
}
