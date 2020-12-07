package blockchain;

import com.fasterxml.jackson.annotation.JsonProperty;
import data.Transaction;
import blockchain.merkletree.MerkleTree;
import util.MerkleTreeUtil;

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

        MerkleTreeUtil mtu = new MerkleTreeUtil();
        tree = mtu.build(transactions);

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
