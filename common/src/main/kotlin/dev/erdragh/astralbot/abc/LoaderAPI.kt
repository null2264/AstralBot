package dev.erdragh.astralbot.abc

interface LoaderAPI {
    fun isModLoaded(modId: String): Boolean
}