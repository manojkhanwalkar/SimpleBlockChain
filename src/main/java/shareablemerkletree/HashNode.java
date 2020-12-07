package shareablemerkletree;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class HashNode implements Node {

    int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

  //  @JsonManagedReference
    HashNode parent;
String hash;

//@JsonBackReference
Node left ;

//@JsonBackReference
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

    @Override
    public String toString() {
        return "HashNode{" +
                "id=" + id +
                ", hash='" + hash + '\'' +
                ", left=" + left +
                ", right=" + right +
                '}';
    }
}
