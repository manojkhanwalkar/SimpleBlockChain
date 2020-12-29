package blockchain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import data.Transaction;
import blockchain.merkletree.MerkleTree;
import util.MerkleTreeUtil;

import java.util.Comparator;
import java.util.List;

public class Block {


  //  @JsonProperty
    String rootHash;

  //  @JsonProperty
    String prevHash;

  //  @JsonProperty
    MerkleTree tree;

    @JsonIgnore
    String startTransactionId;
    @JsonIgnore
    String endTransactionId;

    public String getStartTransactionId() {
        return startTransactionId;
    }

    public void setStartTransactionId(String startTransactionId) {
        this.startTransactionId = startTransactionId;
    }

    public String getEndTransactionId() {
        return endTransactionId;
    }

    public void setEndTransactionId(String endTransactionId) {
        this.endTransactionId = endTransactionId;
    }

    public String getRootHash() {
        return rootHash;
    }

    public void setRootHash(String rootHash) {
        this.rootHash = rootHash;
    }

    public String getPrevHash() {
        return prevHash;
    }

    public void setPrevHash(String prevHash) {
        this.prevHash = prevHash;
    }

    public MerkleTree getTree() {
        return tree;
    }

    public void setTree(MerkleTree tree) {
        this.tree = tree;
    }

    @JsonIgnore
    public List<Transaction> transactions()
    {
        var transactions = MerkleTreeUtil.getTransactions(tree);
        transactions.sort(Comparator.comparing(Transaction::getTransactionId));

        return transactions;

    }

    @JsonIgnore
    public Block(List<Transaction> transactions)
    {

        MerkleTreeUtil mtu = new MerkleTreeUtil();
        tree = mtu.build(transactions);
        setStartTransactionId(transactions.get(0).getTransactionId());
        setEndTransactionId(transactions.get(transactions.size()-1).getTransactionId());

    }


    public Block()
    {

    }

    public static Block getGenesisBlock()
    {
        Block genesis = new Block();
        genesis.prevHash="-1";
        genesis.rootHash="0";
        genesis.tree = new MerkleTree();

        return genesis;
    }


}
