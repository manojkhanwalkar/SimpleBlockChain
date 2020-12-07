package blockchain;

import com.fasterxml.jackson.annotation.JsonProperty;
import data.Transaction;
import shareablemerkletree.MerkleTree;
import util.MerkleTreeUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Block {


    @JsonProperty
    String rootHash;

    @JsonProperty
    String prevHash;

    @JsonProperty
    MerkleTree tree;




    public Block(List<Transaction> transactions)
    {
//TODO - fix this
        /*
        MerkleTreeUtil mtu = new MerkleTreeUtil(transactions);
        tree = mtu.merkle_tree();
*/
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
