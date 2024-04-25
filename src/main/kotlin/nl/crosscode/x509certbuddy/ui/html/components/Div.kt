package nl.crosscode.x509certbuddy.ui.html.components

class Div constructor(var raw: String) : Serializable {
    override fun serialize(): String {
        val sb = StringBuilder()
        return serialize(sb)
    }

    override fun serialize(sb: StringBuilder): String {
        sb.append("<div>")
        sb.append(raw)
        sb.append("</div>")
        return sb.toString()
    }
}

fun div(init: Div.() -> Unit): Div {
    val div = Div("")
    div.init()
    return div
}

fun HtmlBaseLayout.div(init: Div.() -> Unit) {
    val div = Div("")
    div.init()
    components += div
}