package shareablemerkletree;

public class MerkleTree {

    HashNode root;

    public void setRoot(HashNode root)
    {
        this.root = root;
    }

    public HashNode getRoot()
    {
        return root;
    }

    @Override
    public String toString() {
        return "MerkleTree{" +
                "root=" + root +
                '}';
    }
}
