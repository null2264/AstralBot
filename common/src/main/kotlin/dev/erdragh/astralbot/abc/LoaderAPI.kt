package dev.erdragh.astralbot.abc

interface LoaderAPI {
    fun isModLoaded(modId: String): Boolean
    fun getBrand(): Brand

    fun isFabricLike(): Boolean = getBrand() == Brand.FABRIC || getBrand() == Brand.QUILT
    fun isForgeLike(): Boolean = getBrand() == Brand.FORGE || getBrand() == Brand.NEO

    enum class Brand {
        FABRIC,
        FORGE,
        QUILT,
        NEO,
    }
}