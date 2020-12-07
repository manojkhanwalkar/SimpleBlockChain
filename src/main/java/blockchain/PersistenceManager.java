package blockchain;


public interface PersistenceManager {

     default void persist(Block block) {}
     default BlockChain restore() {
        return null;
    };
}
