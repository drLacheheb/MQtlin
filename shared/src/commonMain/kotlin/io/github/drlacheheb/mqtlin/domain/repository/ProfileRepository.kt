package io.github.drlacheheb.mqtlin.domain.repository

import io.github.drlacheheb.mqtlin.domain.model.ConnectionConfig

/**
 * Domain repository abstraction for persisting and managing connection profiles.
 */
interface ProfileRepository {
    /**
     * Retrieves all saved connection profiles.
     */
    suspend fun getAllProfiles(): List<ConnectionConfig>

    /**
     * Saves a connection profile. If a profile with the same name exists, it is updated;
     * otherwise, it is inserted.
     */
    suspend fun saveProfile(profile: ConnectionConfig)

    /**
     * Deletes a connection profile by its unique name.
     */
    suspend fun deleteProfile(profileName: String)

    /**
     * Returns the name of the last selected/connected profile, or null if none.
     */
    suspend fun getLastSelectedProfileName(): String?

    /**
     * Records the name of the last selected profile.
     */
    suspend fun setLastSelectedProfileName(name: String)

    /**
     * Exports all profiles to a JSON formatted string.
     * @param includePasswords whether to include credentials in the export.
     */
    suspend fun exportProfilesJson(includePasswords: Boolean = true): String

    /**
     * Imports profiles from a JSON formatted string, returning the merged profiles list.
     */
    suspend fun importProfilesJson(jsonContent: String): List<ConnectionConfig>
}
