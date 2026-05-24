package com.kingdom.mobile.state

import android.content.SharedPreferences

interface GameLayoutPreferenceStore {
    fun get(): GameLayoutPreference
    fun set(preference: GameLayoutPreference)
}

class SharedPreferencesGameLayoutPreferenceStore(
    private val sharedPreferences: SharedPreferences
) : GameLayoutPreferenceStore {
    override fun get(): GameLayoutPreference {
        return GameLayoutPreference.fromStoredValue(sharedPreferences.getString(KEY_LAYOUT_PREFERENCE, null))
    }

    override fun set(preference: GameLayoutPreference) {
        sharedPreferences.edit().putString(KEY_LAYOUT_PREFERENCE, preference.name).apply()
    }

    private companion object {
        const val KEY_LAYOUT_PREFERENCE = "game_layout_preference"
    }
}
