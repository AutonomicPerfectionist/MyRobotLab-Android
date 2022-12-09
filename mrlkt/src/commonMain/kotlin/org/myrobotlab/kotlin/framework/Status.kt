package org.myrobotlab.kotlin.framework


import org.myrobotlab.kotlin.annotations.MrlClassMapping
import org.myrobotlab.kotlin.framework.StatusLevel.DEBUG
import org.myrobotlab.kotlin.framework.StatusLevel.ERROR
import org.myrobotlab.kotlin.framework.StatusLevel.INFO
import org.myrobotlab.kotlin.framework.StatusLevel.SUCCESS
import org.myrobotlab.kotlin.framework.StatusLevel.WARN

object StatusLevel {
  const val DEBUG = "debug"
  const val INFO = "info"
  const val WARN = "warn"
  const val ERROR = "error"
  const val SUCCESS = "success"
}

/**
 * Represents a status of some remote operation.
 * Required for handshake procedure but unused otherwise.
 *
 */
@MrlClassMapping("org.myrobotlab.framework.Status")
data class Status(
  var name: String? = null,
  var level: String? = null,
  var key: String? = null,
  var detail: String? = null,
  var source: Any? = null
) {


  constructor(e: Exception): this() {
    level = ERROR
    try {

      e.printStackTrace()
      detail = e.stackTraceToString()
    } catch (_: Exception) {
    }
    key = "${e::class.simpleName} - ${e.message}"
  }

  constructor(s: Status): this(s.name, s.level, s.key, s.detail)

  /**
   * for minimal amount of information error is assumed, and info is detail of
   * an ERROR
   *
   * @param detail
   * d
   */
  constructor(detail: String): this(detail = detail, level = ERROR)

  constructor(name: String?, level: String?, key: String?, detail: String?): this(
    name, level, key, detail, null
  )

  val isDebug: Boolean
    get() = DEBUG == level
  val isError: Boolean
    get() = ERROR == level
  val isInfo: Boolean
    get() = INFO == level
  val isWarn: Boolean
    get() = WARN == level

  override fun toString(): String = buildString {
      if (name != null) {
        append(name)
        append(" ")
      }
      if (level != null) {
        append(level)
        append(" ")
      }
      if (key != null) {
        append(key)
        append(" ")
      }
      if (detail != null) {
        append(detail)
      }
    }


  val isSuccess: Boolean
    get() = SUCCESS == level

  companion object {
    private const val serialVersionUID = 1L
    val log = MrlClient.logger

    // --- static creation of typed Status objects ----
    fun debug(msg: String): Status {
      val status = Status(msg)
      status.level = DEBUG
      return status
    }

    fun error(e: Exception): Status {
      val s = Status(e)
      s.level = ERROR
      return s
    }

    fun error(msg: String?): Status {
      val s = Status(msg)
      s.level = ERROR
      return s
    }

    fun warn(msg: String): Status {
      val status = Status(msg)
      status.level = WARN
      return status
    }

    fun info(msg: String?): Status {
      val s = Status(msg)
      s.level = INFO
      return s
    }


    fun stackToString(e: Throwable): String {
      try {
      } catch (e2: Exception) {
        return "bad stackToString"
      }
      return """
               ------
               ${e.stackTraceToString()}------
               
               """.trimIndent()
    }

    fun success(): Status {
      val s = Status(SUCCESS)
      s.level = SUCCESS
      return s
    }

    fun success(detail: String): Status {
      val s = Status(SUCCESS)
      s.level = SUCCESS
      s.detail = detail
      return s
    }
  }
}
