package com.klaus.saas.gateway.secure;//package com.ctitc.cloud.gateway.secure;
//
//import javax.crypto.KeyGenerator;
//import javax.crypto.SecretKey;
//import java.security.KeyPair;
//import java.security.KeyPairGenerator;
//import java.security.NoSuchAlgorithmException;
//import java.util.Base64;
//
///**
// * @author Klaus
// * @since 2021-08-27
// */
//public class KeyGenUtils {
//
//	private static final String AES_ALGORITHM = "AES";
//
//	/**
//	 * 生成RSA密钥对
//	 *
//	 * @return
//	 * @throws Exception
//	 */
//	public static RSAKeyPair genRSAKey() throws Exception {
//		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
//		keyPairGen.initialize(1024);
//		KeyPair keyPair = keyPairGen.generateKeyPair();
//
//		String privateKeyStr = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
//		String publicKeyStr = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
//
//		return new RSAKeyPair(publicKeyStr, privateKeyStr);
//	}
//
//	/**
//	 * 生成随机AES密钥
//	 *
//	 * @return
//	 * @throws Exception
//	 */
//	public static String genAESKey() {
//		try {
//			KeyGenerator kg = KeyGenerator.getInstance(AES_ALGORITHM);
//			kg.init(128);
//			SecretKey secretKey = kg.generateKey();
//			return Base64.getEncoder().encodeToString(secretKey.getEncoded());
//		} catch (NoSuchAlgorithmException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//
//}
