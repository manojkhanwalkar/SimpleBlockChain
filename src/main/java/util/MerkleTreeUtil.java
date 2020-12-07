package util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import data.Transaction;
import shareablemerkletree.*;

import java.io.IOException;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


public class MerkleTreeUtil {

    static ObjectMapper mapper ;

    static {
        mapper = new ObjectMapper();
        mapper.enableDefaultTyping();


    }

  /*  public static MerkleTree buildPartial(MerkleTree input, Set<AttributeNode> nodes)
    {
        // make a copy
        String str = MerkleTreeUtil.toJSON(input);
        MerkleTree output = MerkleTreeUtil.fromJSON(str);

        process(output.getRoot(),nodes);

        return output;


    }*/

    /*

    private static void process(Node node,Set<AttributeNode> nodes)
    {
        if (node instanceof AttributeNode)
        {
            if (!nodes.contains(node))
            {
                node.getParent().setLeft(null);
            }
            return;
        }
        if (node.getLeft()!=null)
        {
            process(node.getLeft(),nodes);
        }
        if (node.getRight()!=null)
        {
            process(node.getRight(),nodes);
        }
    }

*/
    public MerkleTree build(List<Transaction> transactions)
    {
        var leaves = transactions.stream().map(t->{
           LeafHashNode leafHashNode = new LeafHashNode();
           leafHashNode.setTransaction(t);
            leafHashNode.setId(RandomIdGenerator.getId());
            leafHashNode.setHash(getSHA2HexValue(t.toString()));

            return leafHashNode;
        }).collect(Collectors.toList());

        return build1(leaves);
    }

    private MerkleTree build1(List<LeafHashNode> nodes) {

        List<LeafHashNode> attribNodes = new ArrayList<>();
        attribNodes.addAll(nodes);

        MerkleTree tree = new MerkleTree();

        Queue<Node>queue = new ArrayDeque();

        if (!isPowerof2(attribNodes))
            return null;

        /* put AttribNodes in a queue .  then process the queue

        If attribu node - add a LeafHashNode and put in the queue .
        if leaf hashnode , take another leaf has node - create a hashnode and put in the queue
        if hashnode and there is more in the queue , take another hash node and create a hashnode as parent and put in the queue
        if last hashnode - create a merkletree and set this node as the root and return the merkle tree so created.
         */

        queue.addAll(attribNodes);
        while(!queue.isEmpty())
        {
            Node node1 = queue.remove();

            if (node1 instanceof LeafHashNode)
            {
                buildHashNode(queue, node1);
            }
            if (node1 instanceof HashNode)
            {
                if (queue.isEmpty()) // root node
                {
                    tree.setRoot((HashNode)node1);
                    return tree;

                }
                buildHashNode(queue, node1);
            }
        }

        throw new RuntimeException("Error creating tree");

    }

    private void buildHashNode(Queue<Node> queue, Node node1) {
        Node node2 = queue.remove();
        HashNode hashNode = new HashNode();
        hashNode.setId(RandomIdGenerator.getId());
        node1.setParent(hashNode);
        node2.setParent(hashNode);
        hashNode.setLeft(node1);
        hashNode.setRight(node2);
        hashNode.setHash(getSHA2HexValue(node1.getHash()+ node2.getHash()));
        queue.add(hashNode);
        return;
    }

    public static MerkleTree merge(MerkleTree... trees)
    {

        Queue<MerkleTree>queue = new ArrayDeque();

        for (MerkleTree t : trees)
        {
            queue.add(t);
        }

        while (!queue.isEmpty())
        {
            MerkleTree tree1 = queue.remove();
            if (queue.isEmpty())
                return tree1;
            MerkleTree tree2 = queue.remove();

            MerkleTree tree = mergeTwoTrees(tree1,tree2);
            queue.add(tree);
        }

        System.err.println("Should not reach here ");
        return null;

    }




    private static MerkleTree mergeTwoTrees(MerkleTree tree1 , MerkleTree tree2)
    {
        HashNode root1 = tree1.getRoot();
        HashNode root2 = tree2.getRoot();


        HashNode root = new HashNode();
        root1.setParent(root);
        root2.setParent(root);
        root.setLeft(root1);
        root.setRight(root2);
        root.setId(RandomIdGenerator.getId());
        root.setHash(getSHA2HexValue(root1.getHash()+ root2.getHash()));

        MerkleTree tree = new MerkleTree();
        tree.setRoot(root);

        return tree;



    }

    public static MerkleTree copy(MerkleTree src)
    {
        String str = toJSON(src);
        MerkleTree dest = fromJSON(str);
        return dest;
    }

    public static String toJSON(MerkleTree tree)
    {
        try {
            String str = mapper.writeValueAsString(tree);
            return str;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;

    }

    public static MerkleTree fromJSON(String str)
    {
        try {
            MerkleTree tree = mapper.readValue(str,MerkleTree.class);
            return tree;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static boolean validateTree(MerkleTree tree)
    {
        // if left and right are not null - then compute the hash of the children and compare to value stored .
        // if not same , then return false , if same then continue checking on both left and right side of the subtree .

        return traverseAndValidateHashInTree(tree.getRoot());


    }

 /*   public static Identity getIdentityAttributes(MerkleTree tree)
    {
        List<AttributeNode> attributeNodes = new ArrayList<>();
        getIdentityAttributes(tree.getRoot(),attributeNodes);

        Identity identity = new Identity();
        attributeNodes.stream().forEach((n-> {
            if (n.getName().equals(FirstName.toString()))
            {
                identity.setFirstName(n.getValue());
                return;
            }
            if (n.getName().equals(LastName.toString()))
            {
                identity.setLastName(n.getValue());
                return;
            }
            if (n.getName().equals(SSN.toString()))
            {
                identity.setSsn(n.getValue());
                return;
            }
            if (n.getName().equals(Email.toString()))
            {
                identity.setEmail(n.getValue());
                return;
            }
            if (n.getName().equals(Address.toString()))
            {
                identity.setAddress(n.getValue());
                return;
            }
            if (n.getName().equals(BirthDate.toString()))
            {
                identity.setBirthDate(LocalDate.parse(n.getValue()));
                return;
            }
            if (n.getName().equals(Zip.toString()))
            {
                identity.setZip(n.getValue());
                return;
            }



        }));

        return identity;

    }

    private static void getIdentityAttributes(Node node , List<AttributeNode> attributeNodes)
    {
        if (node==null)
            return;
        if (node instanceof AttributeNode)
        {
            attributeNodes.add((AttributeNode)node);
            return;
        }
        getIdentityAttributes(node.getLeft(),attributeNodes);
        getIdentityAttributes(node.getRight(),attributeNodes);
    }*/

    private static boolean traverseAndValidateHashInTree(Node root)
    {
        if (root==null)
        {
            return true;
        }

        if (root.getLeft()==null && root.getRight()==null) // AttributeNode or LeafHashNode that has its attribute Notshared.
        {
            return true;   // end of tree
        }

        String sha2HexValue;
        if (root.getLeft()!=null && root.getRight()==null) // LeafHashNode
        {
             sha2HexValue = getSHA2HexValue(root.getLeft().getName()+root.getLeft().getValue());

        }
        else
        {
            sha2HexValue = getSHA2HexValue(root.getLeft().getHash() + root.getRight().getHash());

        }
        String hash1 = root.getHash();

        if (hash1.equals(sha2HexValue))
        {
            return true && traverseAndValidateHashInTree(root.getLeft()) && traverseAndValidateHashInTree(root.getRight());
        }
        else
        {
            return false;
        }



    }




    //     String sha2HexValue = getSHA2HexValue(left.tuple.value + right.tuple.value);

  public static String getSHA2HexValue(String str) {
        byte[] cipher_byte;
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(str.getBytes());
            cipher_byte = md.digest();
            StringBuilder sb = new StringBuilder(2 * cipher_byte.length);
            for(byte b: cipher_byte) {
                sb.append(String.format("%02x", b&0xff) );
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }


    private boolean isPowerof2(List<?> attributeNodes)
    {
        int size = attributeNodes.size();

        int count = 1;
        while(count<size)
        {
            count = count*2;
        }

        int diff = count-size;
        return diff==0;


    }


    // transaction List
















    public static Set<AttributeNode> getAttributes(MerkleTree tree)
    {
        Set<AttributeNode> fields = new HashSet<>();

        traverseTreeAndCollectFields(tree.getRoot(),fields);
        return fields;

    }


    private static void traverseTreeAndCollectFields(Node node , Set<AttributeNode> fields)
    {

        if (node instanceof AttributeNode)
        {
            fields.add((AttributeNode)node);
        }

        if (node.getLeft()!=null)
        {
            traverseTreeAndCollectFields(node.getLeft(),fields);
        }
        if (node.getRight()!=null)
        {
            traverseTreeAndCollectFields(node.getRight(),fields);
        }



    }







}
