package chatapp.client.crypto;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.math.BigInteger;
import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

public class KeyStoreManager {
    private static final String KEYSTORE_TYPE = "PKCS12";
    private static final String KEYSTORE_FILE = "keystore.p12";
    private static final char[] KEYSTORE_PASSWORD = "password".toCharArray();
    private static KeyStoreManager instance;
    private KeyStore keyStore;

    private KeyStoreManager() throws Exception {
        keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
        if (Files.exists(Path.of(KEYSTORE_FILE))) {
            try (FileInputStream fis = new FileInputStream(KEYSTORE_FILE)) {
                keyStore.load(fis, KEYSTORE_PASSWORD);
            }
        } else {
            keyStore.load(null, KEYSTORE_PASSWORD);
            try (FileOutputStream fos = new FileOutputStream(KEYSTORE_FILE)) {
                keyStore.store(fos, KEYSTORE_PASSWORD);
            }
        }
    }

    public static KeyStoreManager getInstance() throws Exception {
        if (instance == null) {
            instance = new KeyStoreManager();
        }
        return instance;
    }

    public void saveKeyPair(String alias, PrivateKey privateKey, PublicKey publicKey) throws Exception {
        X509Certificate cert = generateSelfSignedCertificate(privateKey, publicKey, alias);
        keyStore.setKeyEntry(alias, privateKey, KEYSTORE_PASSWORD, new Certificate[]{cert});
        try (FileOutputStream fos = new FileOutputStream(KEYSTORE_FILE)) {
            keyStore.store(fos, KEYSTORE_PASSWORD);
        }
    }

    public PrivateKey getPrivateKey(String alias) throws Exception {
        Key key = keyStore.getKey(alias, KEYSTORE_PASSWORD);
        if (key instanceof PrivateKey) {
            return (PrivateKey) key;
        }
        return null;
    }

    public PublicKey getPublicKey(String alias) throws Exception {
        Certificate cert = keyStore.getCertificate(alias);
        if (cert != null) {
            return cert.getPublicKey();
        }
        return null;
    }

    // do użycia KeyStore potrzeba mieć certyfikat który będzie przechowywał też klucz publiczny
    private X509Certificate generateSelfSignedCertificate(PrivateKey priv, PublicKey pub, String dn) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        long now = System.currentTimeMillis();
        Date start = new Date(now);
        Date end   = new Date(now + 365L*24*60*60*1000);

        X500Name subject = new X500Name("CN=" + dn);
        BigInteger serial  = BigInteger.valueOf(now);

        // Builder
        var certBuilder = new JcaX509v3CertificateBuilder(
                subject, serial, start, end, subject, pub
        );
        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA")
                .build(priv);

        X509CertificateHolder holder = certBuilder.build(signer);
        return new JcaX509CertificateConverter()
                .setProvider("BC")
                .getCertificate(holder);
    }


    public static class KeyGeneratorUtil {
        public static KeyPair generateRSAKeyPair(int keySize) throws NoSuchAlgorithmException {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(keySize);
            return keyGen.generateKeyPair();
        }
    }
}
