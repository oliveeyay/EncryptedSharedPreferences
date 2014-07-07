package com.og.encryptedsharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * EncryptedSharedPreferences allow to store encrypted objects into Android SharedPreferences.
 * Provide AES 128bits encryption and support for UTF-8 charset.
 * Created by Olivier Goutay on 07/07/2014.
 *
 * @author Olivier Goutay
 */
public class EncryptedSharedPreferences {
    private static final String TAG = "SHARED_PREFERENCES_ENCRYPTED_TAG";
    private static final String SHARED_PREFERENCES = "SHARED_PREFERENCES_ENCRYPTED";
    private static final String PRIVATE_KEY = "PRIVATE_KEY";
    private static final String UTF_8 = "UTF-8";
    private static final String AES = "AES";

    private Context mContext;

    /**
     * Public constructor of EncryptedSharedPreferences
     *
     * @param context The current context of the application
     */
    public EncryptedSharedPreferences(Context context) {
        this.mContext = context;
    }

    /**
     * Remove the SharedPreferences referenced by the key
     *
     * @param key The key we want to remove from SharedPreferences
     */
    public void removeString(String key) {
        SharedPreferences preferences = mContext.getSharedPreferences(SHARED_PREFERENCES,
                Context.MODE_PRIVATE);
        preferences.edit().remove(key).commit();
    }

    /**
     * Get the String for the given key.
     *
     * @param key The key we want to get from SharedPreferences
     * @return The String object referenced by the key
     */
    public String getString(String key) {
        try {
            SharedPreferences preferences = mContext.getSharedPreferences(SHARED_PREFERENCES,
                    Context.MODE_PRIVATE);
            String encrypted = preferences.getString(key, null);
            if (encrypted == null) {
                return null;
            } else {
                return decrypt(Base64.decode(encrypted, Base64.DEFAULT));
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception during getting encrypted String", e);
            return null;
        }
    }

    /**
     * Put the String for the given key.
     *
     * @param key   The key for which we want the String to be referenced
     * @param value The value that we want to put in SharedPreferences
     */
    public void putString(String key, String value) {
        try {
            SharedPreferences preferences = mContext.getSharedPreferences(SHARED_PREFERENCES,
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            editor.putString(key, Base64.encodeToString(encrypt(value), Base64.DEFAULT));
            editor.commit();
        } catch (Exception e) {
            Log.e(TAG, "Exception during putting encrypted String", e);
        }
    }

    /**
     * Encrypt a String and return a table of bytes to store in SharedPreferences.
     *
     * @param clear The clear String we want to store
     * @return A table of bytes corresponding to the encrypted String
     * @throws Exception If encryption not supported...
     */
    private byte[] encrypt(String clear) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(getKey(), AES);
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        byte[] encrypted = cipher.doFinal(clear.getBytes(UTF_8));
        return encrypted;
    }

    /**
     * Decrypt a table of byte and return a clear String
     *
     * @param encrypted The encrypted String formatted in byte array
     * @return The clear String decrypted from the byte array
     * @throws Exception If encryption not supported...
     */
    private String decrypt(byte[] encrypted) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(getKey(), AES);
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return new String(decrypted, UTF_8);
    }

    /**
     * Allows to generate and store an AES ket under SharedPreferences.
     * This key will be used to encrypt the other SharedPreferneces objects.
     *
     * @return The AES key
     * @throws Exception If encryption is not supported...
     */
    private byte[] getKey() throws Exception {
        SharedPreferences preferences = mContext.getSharedPreferences(SHARED_PREFERENCES,
                Context.MODE_PRIVATE);

        String key = preferences.getString(PRIVATE_KEY, null);

        if (key == null) {
            SharedPreferences.Editor editor = preferences.edit();

            KeyGenerator keyGenerator = KeyGenerator.getInstance(AES);
            keyGenerator.init(128);
            keyGenerator.generateKey();
            SecretKey secretKey = keyGenerator.generateKey();

            // Edit the saved preferences
            String stringKey = Base64.encodeToString(secretKey.getEncoded(), Base64.DEFAULT);
            editor.putString(PRIVATE_KEY, stringKey);
            editor.putInt("UserAge", 22);
            editor.commit();
            return secretKey.getEncoded();
        }

        byte[] encodedKey = Base64.decode(key, Base64.DEFAULT);
        SecretKey originalKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, AES);
        return originalKey.getEncoded();
    }
}
