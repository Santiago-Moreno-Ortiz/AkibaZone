package com.example.akibazone

import com.example.akibazone.data.preferences.ThemePreference
import org.junit.Assert.assertEquals
import org.junit.Test

class ThemePreferenceTest {

    @Test
    fun systemPreferenceRoundTrips() {
        assertEquals(
            ThemePreference.SYSTEM,
            ThemePreference.fromStoredValue(ThemePreference.SYSTEM.storedValue)
        )
    }

    @Test
    fun lightPreferenceRoundTrips() {
        assertEquals(
            ThemePreference.LIGHT,
            ThemePreference.fromStoredValue(ThemePreference.LIGHT.storedValue)
        )
    }

    @Test
    fun darkPreferenceRoundTrips() {
        assertEquals(
            ThemePreference.DARK,
            ThemePreference.fromStoredValue(ThemePreference.DARK.storedValue)
        )
    }

    @Test
    fun unknownPreferenceFallsBackToSystem() {
        assertEquals(ThemePreference.SYSTEM, ThemePreference.fromStoredValue("unknown"))
    }
}
