package blockchain.merkletree;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public interface Node {

    public default Node getLeft()
    {
        return null;
    }
    public default Node getRight()
    {
        return null;
    }
    public default String getHash()
    {
        return null;
    }
    public default Node getParent()
    {
        return null;
    }

    public String getId();
    public void setId(String id);

    public default String getValue()
    {
        return null;
    }

    public default void setLeft(Node node)
    {

    }

    public default void setRight(Node node)
    {

    }

    public default void setHash(String hash)
    {

    }

    public default void setParent(Node parent)
    {

    }

    public default void setValue(String value)
    {

    }

    public default void setName(String name)
    {

    }

    public default String getName()
    {
        return null;
    }

}


