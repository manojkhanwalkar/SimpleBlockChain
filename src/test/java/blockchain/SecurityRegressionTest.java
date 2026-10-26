package blockchain;

import data.Transaction;
import data.TransactionQuery;
import org.junit.Test;
import util.CryptUtil;
import util.JSONUtil;
import util.MerkleTreeUtil;
import util.SymKeyStringTuple;

import javax.validation.Validation;
import javax.validation.Validator;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static org.junit.Assert.*;

public class SecurityRegressionTest {

    private static File[] files(long... times) {
        File[] f = new File[times.length];
        for (int i = 0; i < times.length; i++)
            f[i] = new File("-" + times[i]);
        return f;
    }

    @Test(timeout = 5000)
    public void findFileTerminatesForFirstTransactionOfNewestFile() {
        // previously looped forever: two files and a query for the newest file's start time
        File[] f = files(100, 200);
        assertEquals("-200", FilePersistenceManager.findFile(f, 200).getName());
    }

    @Test(timeout = 5000)
    public void findFileReturnsFloorFile() {
        File[] f = files(100, 200, 300, 400, 500);
        assertNull(FilePersistenceManager.findFile(f, 50));
        assertEquals("-100", FilePersistenceManager.findFile(f, 100).getName());
        assertEquals("-200", FilePersistenceManager.findFile(f, 299).getName());
        assertEquals("-400", FilePersistenceManager.findFile(f, 400).getName());
        assertEquals("-500", FilePersistenceManager.findFile(f, 10_000).getName());
        for (long t = 0; t < 700; t += 7)
            FilePersistenceManager.findFile(f, t);
    }

    @Test
    public void parseTimeRejectsMalformedIds() {
        assertEquals(123L, FilePersistenceManager.parseTime("bcn1-123"));
        assertEquals(-1L, FilePersistenceManager.parseTime("bcn1"));
        assertEquals(-1L, FilePersistenceManager.parseTime("bcn1-"));
        assertEquals(-1L, FilePersistenceManager.parseTime("bcn1-abc"));
        assertEquals(-1L, FilePersistenceManager.parseTime(null));
    }

    @Test
    public void authenticatedEncryptionRoundTripsAndDetectsTampering() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.generateKeyPair();
        byte[] aad = "a-1|a-2".getBytes(StandardCharsets.UTF_8);

        SymKeyStringTuple t = CryptUtil.encryptAuthenticated("block contents", kp.getPublic(), aad);
        assertEquals("block contents", CryptUtil.decryptAuthenticated(t.message, t.key, kp.getPrivate(), aad));

        byte[] ct = Base64.getDecoder().decode(t.message);
        ct[ct.length - 1] ^= 1;
        String tampered = Base64.getEncoder().encodeToString(ct);
        try {
            CryptUtil.decryptAuthenticated(tampered, t.key, kp.getPrivate(), aad);
            fail("tampered ciphertext must not decrypt");
        } catch (GeneralSecurityException expected) {
        }
        try {
            CryptUtil.decryptAuthenticated(t.message, t.key, kp.getPrivate(), "b-1|b-2".getBytes(StandardCharsets.UTF_8));
            fail("ciphertext moved to another record must not decrypt");
        } catch (GeneralSecurityException expected) {
        }
    }

    @Test
    public void blockSurvivesRestrictedTypingRoundTrip() {
        List<Transaction> txns = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Transaction t = new Transaction();
            t.setTransactionId("bcn1-" + (1000 + i));
            t.setContents("c" + i);
            txns.add(t);
        }
        Block block = new Block(txns);
        Block copy = (Block) JSONUtil.fromJSON(JSONUtil.toJSON(block), Block.class);
        assertNotNull(copy);
        assertEquals(4, copy.transactions().size());
        assertEquals(block.getTree().getRoot().getHash(), copy.getTree().getRoot().getHash());
        assertNotNull(MerkleTreeUtil.fromJSON(MerkleTreeUtil.toJSON(block.getTree())));
    }

    @Test
    public void typingRejectsClassesOutsideAllowList() {
        // HashNode.left is the polymorphic Node interface, so a type id is read from the data there
        String allowed = "{\"tree\":{\"root\":{\"id\":\"1\",\"left\":[\"blockchain.merkletree.HashNode\",{\"id\":\"2\"}]}}}";
        assertNotNull(JSONUtil.fromJSON(allowed, Block.class));
        String foreign = "{\"tree\":{\"root\":{\"id\":\"1\",\"left\":[\"java.net.URL\",\"http://example.invalid/\"]}}}";
        assertNull(JSONUtil.fromJSON(foreign, Block.class));
    }

    @Test
    public void requestValidation() {
        Validator v = Validation.buildDefaultValidatorFactory().getValidator();

        TransactionQuery ok = new TransactionQuery();
        ok.setTransactionId("bcn1-26622479349110");
        assertTrue(v.validate(ok).isEmpty());
        for (String bad : new String[]{null, "bcn1", "../etc-1", "bcn1-1x", "bcn1-99999999999999999999"}) {
            TransactionQuery q = new TransactionQuery();
            q.setTransactionId(bad);
            assertFalse(bad, v.validate(q).isEmpty());
        }

        Transaction t = new Transaction();
        assertFalse(v.validate(t).isEmpty());
        t.setContents("x".repeat(Transaction.MAX_CONTENTS_LENGTH + 1));
        assertFalse(v.validate(t).isEmpty());
        t.setContents("{}");
        assertTrue(v.validate(t).isEmpty());
    }
}
