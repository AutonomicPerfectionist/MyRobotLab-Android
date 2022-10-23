package org.myrobotlab.kotlin.codec

/**
 * This class contains many utility methods related to foreign
 * processes that are written in other programming languages.
 *
 *
 * This class defines a format for describing "classes"
 * or types that originate from unknown programming languages.
 *
 *
 * The format consists of two simple parts: the language ID, that
 * describes what language the process is using, and the
 * language-specific type key. It is expected that each
 * language-specific type key maps to a set of runtime-static procedures,
 * so in the case of languages where a type's applicable procedures
 * can change during runtime, as in Python, it is recommended to
 * generate a new type key for every change in the procedure list.
 * This is because the set of known procedures is cached and is not regenerated
 * to improve performance.
 *
 *
 * Currently, this information is only used to determine when
 * to generate a [java.lang.reflect.Proxy], but it would
 * enable foreign processes in the future to instantiate the
 * correct proxy when dynamic proxies aren't possible such as
 * in fully compiled languages.
 *
 * @author AutonomicPerfectionist
 */
object ForeignProcessUtils {
    /**
     * The string used to separate the two parts of the
     * foreign process type specifier. The language ID
     * may not contain this string, but the language-specific
     * type key may. It is used in a regular expression
     * so escaping might be required.
     */
    const val LANGUAGE_ID_SEPARATOR = ":"

    /**
     * A pattern that both tests whether a string is a valid foreign
     * type key and splits the key on the language id separator.
     *
     *
     * For example, if the separator is a single colon (`':'`),
     * then "py:exampleService" would match and the two capture groups would be
     * "py" and "exampleService."
     *
     *
     * This pattern does not allow the separator in the language ID at all,
     * but does allow it in the language-specific type key (the second capture group).
     *
     *
     * This enables languages that use double-colons for package or module definition
     * to work seamlessly.
     */
    val FOREIGN_TYPE_KEY_PATTERN = Regex(
            "^([^%s${LANGUAGE_ID_SEPARATOR}]+)${LANGUAGE_ID_SEPARATOR}(.+)$"
    )

    /**
     * Java identifier pattern, using builtin Java regex "macros."
     * This is a string regex pattern that identifies a valid
     * Java identifier.
     */
    private const val JAVA_ID_PATTERN = "\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*"

    /**
     * Java fully-qualified class name pattern. This pattern is used
     * to determine if a type key is a valid Java class. If it is not
     * and the type key also does not match the [.FOREIGN_TYPE_KEY_PATTERN],
     * then the type key is malformed and should be rejected.
     */
    private val JAVA_FQCN_PATTERN =
        Regex("$JAVA_ID_PATTERN(\\.$JAVA_ID_PATTERN)*")

    /**
     * Checks whether the given string is a valid
     * fully-qualified class name.
     *
     * @param name The string to be checked
     * @return Whether name is a valid FQCN
     */
    val String.isValidJavaClassName: Boolean
        get() {
        return JAVA_FQCN_PATTERN.containsMatchIn(this)
    }

    /**
     * Checks whether a string is a valid Java class name
     * or a valid foreign type key.
     * @param typeKey The string to be checked for validity
     * @return Whether the string is a valid type keu
     */
    val String?.isValidTypeKey: Boolean
        get() {
        return this != null && (FOREIGN_TYPE_KEY_PATTERN.containsMatchIn(this)
                || isValidJavaClassName)
    }

    /**
     * Checks whether a type key is a Java type key or a foreign
     * key.
     * @param type The type key to check
     * @return Whether the string is a foreign key. If false, then it is a Java type key
     * @throws IllegalArgumentException if the string is an invalid type key
     */
    val String.isForeignTypeKey: Boolean
        get() {
        require(isValidTypeKey) { "Invalid type key: $this" }
        return FOREIGN_TYPE_KEY_PATTERN.containsMatchIn(this)
    }

    /**
     * Gets the language id of a foreign type key. The language ID
     * is the first part of the foreign type key, before the [.LANGUAGE_ID_SEPARATOR],
     *
     * @param typeKey The foreign type key to split
     * @return The language ID of the foreign key
     * @throws IllegalArgumentException if the string is not a foreign type key
     */
    val String.languageId: String
        get() {
            require(isForeignTypeKey) { "Type key $this is not a foreign key" }
            return FOREIGN_TYPE_KEY_PATTERN.find(this)?.groupValues?.get(1)
                ?: throw IllegalStateException("Invalid type key: $this")
        }

    /**
     * Gets the language-specific type key from a foreign type key.
     * The language-specific type key is the second part of the foreign type key.
     *
     * @return The language-specific type key contained in the foreign type key
     * @throws IllegalArgumentException if the string is not a foreign type key
     */
    val String.languageSpecificTypeKey: String
        get() {
            require(isForeignTypeKey) { "Type key $this is not a foreign key" }
            return FOREIGN_TYPE_KEY_PATTERN.find(this)?.groupValues?.get(2)
                ?: throw IllegalStateException("Invalid type key: $this")
        }


    fun main(args: Array<String>) {
        val foreignTypeKey = "py:exampleService"
        println("isValid: " + foreignTypeKey.isValidTypeKey)
        println("isValidJava: " + foreignTypeKey.isValidJavaClassName)
        println("isForeign: " + foreignTypeKey.isForeignTypeKey)
        println("languageId: " + foreignTypeKey.languageId)
        println("languageSpecificTypeKey: " + foreignTypeKey.languageSpecificTypeKey)
        val invalidKey = "^abcde"
        println("isValid (no): " + invalidKey.isValidTypeKey)
    }
}