package com.example.datastore

import android.content.Context
import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "secure_token_prefs")

@Singleton
class TokenManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("jwt_token")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val USERNAME_KEY = stringPreferencesKey("username")

        private const val KEYSET_NAME = "tink_token_keyset"
        private const val PREF_FILE_NAME = "tink_token_key_prefs"
        private const val MASTER_KEY_URI = "android-keystore://tink_token_master_key"
    }

    init {
        AeadConfig.register()
    }

    // Authenticated Encryption with Associated Data (연관 데이터가 있는 인증된 암호화)
    private val aead: Aead by lazy {
        AndroidKeysetManager.Builder()
            .withSharedPref(context, KEYSET_NAME, PREF_FILE_NAME)
            .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
            .withMasterKeyUri(MASTER_KEY_URI)
            .build()
            .keysetHandle
            .getPrimitive(Aead::class.java)
    }

    private fun encrypt(value: String): String {
        val ciphertext = aead.encrypt(value.toByteArray(Charsets.UTF_8), null)
        return Base64.encodeToString(ciphertext, Base64.NO_WRAP)
    }

    private fun decrypt(value: String?): String? {
        if (value == null) return null
        return try {
            val decoded = Base64.decode(value, Base64.NO_WRAP)
            val decrypted = aead.decrypt(decoded, null)
            String(decrypted, Charsets.UTF_8)
        } catch (e: Exception) {
            null
        }
    }

    val token: Flow<String?> = context.dataStore.data
        .map { preferences -> decrypt(preferences[TOKEN_KEY]) }

    val userId: Flow<Long?> = context.dataStore.data
        .map { preferences -> decrypt(preferences[USER_ID_KEY])?.toLongOrNull() }

    val username: Flow<String?> = context.dataStore.data
        .map { preferences -> decrypt(preferences[USERNAME_KEY]) }

    suspend fun saveToken(token: String) {
        withContext(Dispatchers.IO) {
            context.dataStore.edit { preferences ->
                preferences[TOKEN_KEY] = encrypt(token)
            }
        }
    }

    suspend fun saveUserInfo(id: Long, username: String) {
        withContext(Dispatchers.IO) {
            context.dataStore.edit { preferences ->
                preferences[USER_ID_KEY] = encrypt(id.toString())
                preferences[USERNAME_KEY] = encrypt(username)
            }
        }
    }

    suspend fun clearToken() {
        withContext(Dispatchers.IO) {
            context.dataStore.edit { preferences ->
                preferences.remove(TOKEN_KEY)
                preferences.remove(USER_ID_KEY)
                preferences.remove(USERNAME_KEY)
            }
        }
    }
}