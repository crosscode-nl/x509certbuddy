package nl.crosscode.x509certbuddy.ui.html.components

interface Serializable {
    fun serialize(): String
    fun serialize(sb: StringBuilder): String
}