package fr.parentsapariscentre.badgecreche.data.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class BadgeData(
    val serialNumber: String = UUID.randomUUID().toString(),
    val qrPayload: String,
    val showsChildName: Boolean = false,
    val childFirstName: String = "",
    val childLastName: String = "",
    val crecheName: String = "",
    val address: CrecheAddress,
    val updatedAt: Long = System.currentTimeMillis(),
    val lockScreenMeters: Int? = LockScreenDistance.STANDARD.rawValue
) {
    val lockScreenDistance: LockScreenDistance
        get() = LockScreenDistance.fromRawValue(lockScreenMeters)

    val displayedChildName: String?
        get() {
            if (!showsChildName) return null
            val fullName = listOf(childFirstName.trim(), childLastName.trim())
                .filter { it.isNotEmpty() }
                .joinToString(" ")
            return fullName.ifEmpty { null }
        }

    val displayedCrecheName: String?
        get() {
            val name = crecheName.trim()
            return name.ifEmpty { null }
        }
}
