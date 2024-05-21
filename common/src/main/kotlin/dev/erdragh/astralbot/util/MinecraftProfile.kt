package dev.erdragh.astralbot.util

import com.mojang.authlib.GameProfile
import java.util.*

data class MinecraftProfile(
    val javaId: UUID,
    val floodgateId: UUID? = null,
    val name: String,
    val javaProfile: GameProfile? = null,
) {
    val id: UUID
        get() = floodgateId ?: javaId

    constructor(javaId: UUID, floodgateId: UUID?, gameProfile: GameProfile?) :
            this(javaId, floodgateId, gameProfile?.name ?: UNNAMED_ACCOUNT, gameProfile)

    constructor(gameProfile: GameProfile?) :
            this(gameProfile?.id ?: UUID(0, 0), null, gameProfile)
}