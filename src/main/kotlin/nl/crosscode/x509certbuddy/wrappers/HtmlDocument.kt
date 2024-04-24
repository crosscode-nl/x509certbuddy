package nl.crosscode.x509certbuddy.wrappers

import kotlinx.html.*
import kotlinx.html.dom.createHTMLDocument
import org.w3c.dom.Document

fun createStyledHtmlDocument(): Document {
    return createHTMLDocument().html {
        head {
            style {
                type = "text/css"
                unsafe {
                    raw("""
                           body { font-family: monospace; background-color: #222222; }
                           b { color: #0000FF; }
                           td { vertical-align: top; text-style: strong; }
                    """)
                }
            }
        }
    }
}