package blockchain;

import com.fasterxml.jackson.annotation.JsonProperty;
import data.Transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static util.MerkleTreeUtil.getSHA2HexValue;


public class BlockChain {




    public BlockChain()
    {


    }



    public void bootstrap()
    {
        Block genesis = Block.getGenesisBlock();
        blocks.add(genesis);

    }

    @JsonProperty
    List<Block> blocks = new ArrayList<>();

    public synchronized void add(Block block)
    {
        String hash = blocks.get(blocks.size()-1).rootHash;
        block.prevHash = hash;
      //TODO - fix this
        // block.rootHash = getSHA2HexValue(block.prevHash+block.tree.root.getHash());
        blocks.add(block);


    }

    public synchronized void restore(Block block)
    {
        blocks.add(block);

    }

/*
    public synchronized Transaction query(String id)
    {

            for (Block block : blocks) {

                Transaction transaction = block.transactionMap.get(id);
                if (transaction != null)
                    return transaction;


            }

            return null;
    }
*/
    public List<Block> getBlocks()
    {
        return Collections.unmodifiableList(blocks);
    }
}
