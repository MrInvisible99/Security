package be.pxl.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Decrypt {

	//Output file paths
	public final String ENCRYPTED_SYMMETRIC = System.getProperty("user.home") + "/documents/Security App Files/Encrypted Files/File_2";
	public final String ENCRYPTED_HASH = System.getProperty("user.home") + "/documents/Security App Files/Encrypted Files/File_3";

	// Files
	private File file1;

	// Cipher
	private Cipher pkCipher;
	private Cipher aesCipher;

	// Private and public key
	private PublicKey pub;
	private PrivateKey priv;

	// SecretKeySpec
	private SecretKeySpec aeskeySpec;

	// AES key size
	public final int AES_Key_Size = 128;

	// Byte aesKey
	private byte[] aesKey;
	private byte[] hashKey;

	// Secret Key
	private SecretKey key;
	
	//Decrypted hash
	private String dHash;

	//Constructor
	public Decrypt(String file1Name, String file2Name, String file3Name, String publicKeyName, String privateKeyName) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
		// create RSA public key cipher
		pkCipher = Cipher.getInstance("RSA");

		// create AES shared key cipher
		aesCipher = Cipher.getInstance("AES");

		//Sets file1 variable to a new file with path file1name
		this.file1 = new File(file1Name);

		readKeys(publicKeyName, privateKeyName);
		decryptAES(file2Name);
		decryptFile(file1);
		decryptHash(file3Name, publicKeyName);
	}

	@SuppressWarnings("resource")
	// Method reads public and private key and stores them in the variables
	// (tested and works)
	public void readKeys(String publicKeyPath, String privateKeyPath) {
		try {
			ObjectInputStream inputStream = null;

			// Read the public key
			inputStream = new ObjectInputStream(new FileInputStream(publicKeyPath));
			pub = (PublicKey) inputStream.readObject();

			// Read the private key
			inputStream = new ObjectInputStream(new FileInputStream(privateKeyPath));
			priv = (PrivateKey) inputStream.readObject();

			System.out.println("Keys loaded");
			System.out.println("Public Key: " + pub);
			System.out.println("Private Key: " + priv);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Method decrypts file using AES key (if having the right one)
	// (tested and works)
	public void decryptFile(File file) throws IOException, InvalidKeyException {
		//Get the index of the last "." at the end of the file path -> to get the extension easier
		int dot = file.getPath().lastIndexOf(".");
		aesCipher.init(Cipher.DECRYPT_MODE, aeskeySpec);

		//Constructs a CipherInputStream from an InputStream and a Cipher. 
		CipherInputStream is = new CipherInputStream(new FileInputStream(System.getProperty("user.home") + "/documents/Security App Files/Encrypted Files/File_1." + file.getPath().substring(dot + 1)), aesCipher);
		FileOutputStream os = new FileOutputStream(System.getProperty("user.home") + "/documents/Security App Files/Decrypted Files/File_1." + file.getPath().substring(dot + 1));

		int i;
		byte[] b = new byte[1024];
		while ((i = is.read(b)) != -1) {
			os.write(b, 0, i);
		}
		//System.out.println("File decrypted");
		is.close();
		os.close();
	}

	// Method decrypts AES key using private key (if having the right one)
	// (tested and works)
	public void decryptAES(String file2) throws IOException, InvalidKeyException {
		// read AES key
		pkCipher.init(Cipher.DECRYPT_MODE, priv);
		//AES_Key_Size / 8 --> 128 bit / 8 = 16 bytes
		aesKey = new byte[AES_Key_Size / 8];
		
		CipherInputStream is = new CipherInputStream(new FileInputStream(file2), pkCipher);
		is.read(aesKey);
		
		//Constructs a secret key from the given byte array. 
		aeskeySpec = new SecretKeySpec(aesKey, "AES");
		key = aeskeySpec;
		//System.out.println("AES encrypted");
		is.close();
	}

	// Method decrypts the hash
	// (tested and works)
	public void decryptHash(String file3path, String publicKeyFile) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, FileNotFoundException, IOException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		//The length of the hash that was created
		hashKey = new byte[32];
		
		pkCipher.init(Cipher.DECRYPT_MODE, pub);
		
		//Read the hash from the encrypted file (File_3)
		CipherInputStream is = new CipherInputStream(new FileInputStream(ENCRYPTED_HASH), pkCipher);
		is.read(hashKey);
		dHash = new String(hashKey);
		
		//System.out.println("Hash decrypted");
		//System.out.println("Digest(in hex format):: " + dHash);
		is.close();
	}
}
