package blockchain.merkletree;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.Objects;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class AttributeNode implements Node {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttributeNode that = (AttributeNode) o;
        return Objects.equals(name, that.name);
    }

    public enum Status { Valid , Fraud};

    Status status ;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    String name ;
    String value;


  //  @JsonManagedReference
    LeafHashNode parent;

    // Attribute has no children .

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Node getParent() {
        return parent;
    }

    @Override
    public void setParent(Node parent) {

        this.parent = (LeafHashNode)parent;

    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "AttributeNode{" +
                "status=" + status +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
