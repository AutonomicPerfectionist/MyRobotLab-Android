package org.myrobotlab.kotlin.service.interfaces

import org.myrobotlab.kotlin.annotations.MrlClassMapping

@MrlClassMapping("org.myrobotlab.service.interfaces.NameProvider")
interface NameProvider {

    val name: String

}