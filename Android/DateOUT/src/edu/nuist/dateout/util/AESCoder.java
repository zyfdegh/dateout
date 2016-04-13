package edu.nuist.dateout.util;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AESCoder
{
    
    public static final String KEY_ALGORITHM = "AES";
    
    /**
     * 加密、解密/ 工作模式/ 填充方式
     */
    public static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
    
    /**
     * 转换秘钥
     * 
     * @param key 二进制秘钥
     * @return Key 秘钥
     * @throws Exception
     */
    private static Key toKey(byte[] key)
        throws Exception
    {
        // 实例化AES秘钥材料
        SecretKey secretKey = new SecretKeySpec(key, KEY_ALGORITHM);
        return secretKey;
    }
    
    /**
     * @param data 带解密数据
     * @param key 秘钥
     * @return byte[] 解密数据
     * @throws Exception
     */
    public static byte[] decrypt(byte[] data, byte[] key)
        throws Exception
    {
        // 还原秘钥
        Key k = toKey(key);
        // 实例化
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        // 初始化，设置解密模式
        cipher.init(Cipher.DECRYPT_MODE, k);
        // 执行操作
        return cipher.doFinal(data);
    }
    
    /**
     * 加密
     * 
     * @param data 带加密数据
     * @param key 秘钥
     * @return byte[] 加密数据
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data, byte[] key)
        throws Exception
    {
        // 还原秘钥
        Key k = toKey(key);
        // 实例化
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        // 初始化，设置加密模式
        cipher.init(Cipher.ENCRYPT_MODE, k);
        // 执行操作
        return cipher.doFinal(data);
    }
    
    /**
     * 生成秘密秘钥 java7 只支持56位密钥 Bouncy Castle 支持64位秘密
     * 
     * @return 二进制秘钥
     * @throws Exception
     */
    public static byte[] initKey()
        throws Exception
    {
        /**
         * 实例化秘钥生成器 如要使用64位秘钥需要替换为 KeyGenerator.getInstance(CIPHER__ALGORITHM, "BC")； "BC"是Bouncy Castle安全提供者的缩写。
         */
        KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);
        // 初始化秘钥生成器
        kg.init(128);
        // 生成秘密秘钥
        SecretKey secretKey = kg.generateKey();
        // 获得秘钥的二进制编码形式
        return secretKey.getEncoded();
    }
}