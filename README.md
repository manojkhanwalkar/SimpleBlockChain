# **Simple Block Chain**

**Work In Progress**

The code implements a simplified version of the BlockChain and is intended for usage in applications like recording changes for audit purposes. 
The design pattern is independent of the nature of the data and assumes a generic transaction structure to store and query the data. 

**Actors**
The two actors in this design pattern are the 
1. BCC - Simple Block Chain Client. The client looks up the available BCN's from zookeeper and sends the transactions to an arbitrary node. It also provides support for querying the blockchain via a transaction id. 
2. BCN -  Block Chain Node. The BCN receives the transactions and queries from the clients. It is responsible for arranging the transactions into a blockchain and indexing and storing them periodically. Any BCN can be used to query the transactions that have been committed to disk.

**Interactions**
0. Setup 
   
a. Assumes ZK is up and running 
   
b. BCN's bind under a known structure 
   
c. BCC queries the ZK and gets all available nodes and randomly selects one of them for use in submission and queries.
   
d. Each BCN uses a (write ahead log) WAL and uses that to recover any unprocessed transactions into memory. 

1. Submission of transaction for recording 
   
a. The client generates a public/private key pair which will be used for signing every transaction in the current session. 
    
b. The  public key is submitted along with the Transaction to enable future verification of the transaction.
    
c. The BCN receives the transaction and does the following 
        
1. It writes the transaction to a WAL file to be used if required in recovery.
   
2. It generates a transaction id and sends it back to the client. The transaction id includes the node name and a time stamp. The transaction id is unique across all BCN's and is generated in a lockless manner. 
   
3. The transaction is stored in memory and when a configurable number of transactions are present in memory , they are all writted out to a file asynchronously from the client processing.
   
4. The in memory structure uses Merkle tree and disk based files form a block chain. The index is a bloom filter on the transactionid. The file naming conventions make it easy to narrow down the search for a transaction.


2. Querying the transaction details of a recorded transaction
   
a. The client submits the transaction id for the transaction that it wants to look at to any node.
    
b. Based on the transaction id , the node looks up the appropriate index file to see if the transaction exists and if it does it navigates the blockchain file to retreieve the transaction and send it to the client.
   
c. The client uses the public key stored with the transaction to confirm that the data has not been tampered with.

**BlockChain details**

a. Every transaction is stored in memory as the leaf of a merkle tree. 
   
b. Multiple transactions make a transaction block. The total is kept a pow of 2 to make it simpler to navigate the tree. (default value is 16)
   
c. When multiple transaction blocks are created (default 16 for a total of 256 transactions) , they are written out to a file and an index created for the id's.
   
d. The file names for both the data and index files follow the pattern <node name>-<start>-<end>.<extension>. This allows quick narrowing down of which files to search given a transaction id. 
   
e. Each file is independent of the other and the data is removed from memory when written to disk. 
   
f. For each query, the needed files are read and immediately discarded from memory.  

**Write Ahead Log (WAL) details**

a. The WAL is used to recover transactions that have not yet been written to the blockchain.

b. The blockchain is behind the WAL files by at the most the last two WAL files. 

c. A new WAL file is created when the file size is exceeded. (default 100MB). 

d. On recovery the node checks the last transaction id it has written to disk and then processes the last two WAL files in sequence to process the ones that are pending.

e. The WAL files follow a naming pattern of <node name>-<time>.wal


**Cryptography details**

1. The client generates an Elliptical curve public/private key pair on the NIST standard curve prime256v1 and uses the same curve for it's digital signature. (ECDSA)

2. The public key is stored with the transaction to enable easy verification.
   
3. The transaction is just a container that stores an application specific JSON string, the signature and public key. This enables any type of data to be stored as the system does not ake use of it , beyond providing a means of storing it with integrity.

**Constraints and Exclusions**
The code does not cater to the following areas, which would need to be provided by alternate mechanisms.
1. Mutual authentication between the client and the nodes.
2. A given transaction is submitted to one node only and there is no provision for a POW/POS or similar functionality.
3. S3 or HDFS would be a more suitable long term storage mechanism as they both provide the directory and file semantics that are used here.
4. For an additional layer of protection of data in transit (on top of TLS 1.2) look at the P2PE design pattern https://github.com/manojkhanwalkar/P2PE/blob/master/README.md
5. Query functionality is limited to supporting retrieval of a transaction by transaction id. 
6. Zookeeper is used to provide service discovery and usage is hardcoded into the client and node code.
7. The file system and service discovery using zookeeper are not cleanly abstracted awy to enable pluggable mechanisms.
8. Only transactions committed to disk can be queried. The ones still in memory and WAL are not accessible for querying.