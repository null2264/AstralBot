package io.github.null2264.utils

import org.gradle.api.attributes.Attribute
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.DependencyHandlerScope

val patchedFMLModType = Attribute.of("patchedFMLModType", Boolean::class.javaObjectType)

// REF: https://github.com/object-Object/HexDebug/commit/3875f68e7220608c98d35ec60175ee9abdbad762
fun DependencyHandlerScope.kotlinForgeRuntimeLibrary(dependency: String) {
    "localRuntime"(dependency) {
        isTransitive = false
        attributes {
            attribute(patchedFMLModType, true)
        }
    }
}