package org.myrobotlab.android

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * A container for injecting a [Context]
 * via Koin. For whatever reason injecting
 * the context directly causes crashes, this is a workaround
 * for services that absolutely need a context.
 * However, check first to ensure the object that requires
 * the context cannot be constructed by the framework and
 * injected by Koin instead.
 */
data class ContextContainer(val context: Context)
