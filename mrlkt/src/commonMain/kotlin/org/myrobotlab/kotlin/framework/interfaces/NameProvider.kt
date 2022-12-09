package org.myrobotlab.kotlin.framework.interfaces

import org.myrobotlab.kotlin.annotations.MrlClassMapping

@MrlClassMapping("org.myrobotlab.framework.interfaces.NameProvider")
interface NameProvider {

    val name: String

}