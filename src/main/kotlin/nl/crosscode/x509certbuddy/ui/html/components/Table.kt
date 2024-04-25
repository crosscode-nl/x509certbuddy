package nl.crosscode.x509certbuddy.ui.html.components

class Table : Serializable {
    var rows = listOf<Row>()

    override fun serialize(): String {
        val sb = StringBuilder()
        return serialize(sb)
    }

    override fun serialize(sb: StringBuilder): String {
        sb.append("<table>")
        for (row in rows) {
           row.serialize(sb)
        }
        sb.append("</table>")
        return sb.toString()
    }
}
class Row : Serializable {
    var cells = listOf<Cell>()
    override fun serialize(): String {
        val sb = StringBuilder()
        return serialize(sb)
    }

    override fun serialize(sb: StringBuilder): String {
        sb.append("<tr>")
        for (cell in cells) {
            cell.serialize(sb)
        }
        sb.append("</tr>")
        return sb.toString()
    }
}
class Cell constructor(var text: String, var classNames: List<String>): Serializable {

    override fun serialize(): String {
        val sb = StringBuilder()
        return serialize(sb)
    }

    override fun serialize(sb: StringBuilder): String {
        sb.append("<td>")
        sb.append(text)
        sb.append("</td>")
        return sb.toString()
    }
}

fun table(init: Table.() -> Unit): Table {
    val table = Table()
    table.init()
    return table
}

fun HtmlBaseLayout.table(init: Table.() -> Unit) {
    val table = Table()
    table.init()
    components += table
}

fun Table.row(init: Row.() -> Unit) {
    val row = Row()
    row.init()
    rows += row
}

fun Row.cell(init: Cell.() -> Unit) {
    val cell = Cell("", listOf())
    cell.init()
    cells += cell
}

fun Row.cell(text: String, init: Cell.() -> Unit) {
    val cell = Cell(text, listOf())
    cell.init()
    cells += cell
}

fun Row.cell(text: String, classNames: List<String>, init: Cell.() -> Unit) {
    val cell = Cell(text, classNames)
    cell.init()
    cells += cell
}

fun Test() {
    val table = table {
        row {
            cell {
                text = "Hello"
            }
            cell {
                text = "World"
            }
        }
        row {
            cell("hello") {}
            cell("world") {}
        }
    }
    println(table.serialize())
}
