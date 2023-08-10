package com.klaus.saas.gateway.secure;//package com.ctitc.cloud.gateway.secure;
//
//import javax.crypto.Cipher;
//import javax.crypto.spec.SecretKeySpec;
//import java.nio.charset.StandardCharsets;
//import java.security.KeyFactory;
//import java.security.interfaces.RSAPrivateKey;
//import java.security.spec.PKCS8EncodedKeySpec;
//import java.util.Base64;
//
///**
// * @author Klaus
// * @since 2021-08-27
// */
//public class DecryptUtils {
//
//	private static final String AES_ALGORITHM = "AES";
//	private static final String AES_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
//
//	/**
//	 * RSA解密
//	 *
//	 * @param str
//	 * @param sKey
//	 * @return
//	 * @throws Exception
//	 */
//	public static String decryptRSA(String str, String sKey) {
//		try {
//			byte[] dataByte = Base64.getDecoder().decode(str.getBytes(StandardCharsets.UTF_8));
//			RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(sKey)));
//			Cipher cipher = Cipher.getInstance("RSA");
//			cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey);
//			return new String(cipher.doFinal(dataByte), StandardCharsets.UTF_8);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//
//	/**
//	 * AES解密
//	 *
//	 * @param data
//	 * @param key
//	 * @return
//	 */
//	public static String decryptAES(String data, String key) throws Exception {
//		Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM);
//		SecretKeySpec keySpec = new SecretKeySpec(Base64.getDecoder().decode(key), AES_ALGORITHM);
//		cipher.init(Cipher.DECRYPT_MODE, keySpec);
//		return new String(cipher.doFinal(Base64.getDecoder().decode(data)), StandardCharsets.UTF_8);
//	}
//
//}
