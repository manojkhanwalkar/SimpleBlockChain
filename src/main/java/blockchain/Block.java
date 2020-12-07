package blockchain;

import com.fasterxml.jackson.annotation.JsonProperty;
import data.Transaction;
import blockchain.merkletree.MerkleTree;

import java.util.List;

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
