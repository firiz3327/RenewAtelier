package net.firiz.renewatelier.debug.annotations

import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Cmd(val desc: Array<String> = [], val args: Array<KClass<out Any>> = [])