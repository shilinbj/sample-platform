package com.klaus.saas.gateway.secure;//package com.ctitc.cloud.gateway.secure;
//
//import javax.crypto.Cipher;
//import javax.crypto.spec.SecretKeySpec;
//import java.nio.charset.StandardCharsets;
//import java.security.KeyFactory;
//import java.security.interfaces.RSAPublicKey;
//import java.security.spec.X509EncodedKeySpec;
//import java.util.Base64;
//
///**
// * @author Klaus
// * @since 2021-08-27
// */
//public class EncryptUtils {
//
//	private static final String AES_ALGORITHM = "AES";
//	private static final String AES_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
//
//	/**
//	 * RSA加密
//	 *
//	 * @param data
//	 * @param sKey
//	 * @return
//	 * @throws Exception
//	 */
//	public static String encryptRSA(String data, String sKey) {
//		try {
//			RSAPublicKey rsaPublicKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(sKey)));
//			Cipher cipher = Cipher.getInstance("RSA");
//			cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
//			return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes(StandardCharsets.UTF_8)));
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//
//	/**
//	 * AES加密
//	 *
//	 * @param data
//	 * @param key
//	 * @return 返回Base64转码后的加密数据
//	 */
//	public static String encryptAES(String data, String key) {
//		try {
//			Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM);
//			byte[] byteContent = data.getBytes(StandardCharsets.UTF_8);
//			SecretKeySpec keySpec = new SecretKeySpec(Base64.getDecoder().decode(key), AES_ALGORITHM);
//			cipher.init(Cipher.ENCRYPT_MODE, keySpec);
//			return Base64.getEncoder().encodeToString(cipher.doFinal(byteContent));
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//
//}
