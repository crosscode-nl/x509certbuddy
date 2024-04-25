package nl.crosscode.x509certbuddy.ui.html.components

class HtmlBaseLayout(components: MutableList<Serializable>) : Serializable {
    var components = components

    override fun serialize(): String {
        val sb = StringBuilder()
        return serialize(sb)
    }

    override fun serialize(sb: StringBuilder): String {
        sb.append("<html>")
        sb.append("<head>")
        sb.append("<style>")
        sb.append("body { font-family: monospace; background-color: #222222; }")
        sb.append("b { color: #0000FF; }")
        sb.append("td { vertical-align: top; text-style: strong; }")
        sb.append("</style></head>")
        sb.append("<body>")

        for (component in components) {
            component.serialize(sb)
        }

        sb.append("</body></html>")
        return sb.toString()
    }

}

fun htmlLayoutBody(init: HtmlBaseLayout.() -> Unit): HtmlBaseLayout {
    val layout = HtmlBaseLayout(mutableListOf())
    layout.init()
    return layout
}


fun t(a: HtmlBaseLayout.()->Unit) {
    val layout = HtmlBaseLayout(mutableListOf())
    layout.a()
}

fun test() {
    t {
        div {
            raw = "test"
        }
    }
}