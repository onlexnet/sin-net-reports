package sinnet.config

import com.lowagie.text.Font
import com.lowagie.text.FontFactory

object Fonts {

  {
    // without the initialization font discovery (method: FontFactory.getFont) does not find proper fonts
    // and all fonts in the report will stay with some default font
    FontFactory.registerDirectories()
  }

  def base: Font = {
    // Fonts should be available because they are part of the project and they are included in proper
    // location in target docker image.
    val fontSize = 10
    val baseFont = FontFactory.getFont("OpenSans")
    baseFont.setSize(fontSize)
    baseFont
  }

  def bold: Font = {
    // Fonts should be available because they are part of the project and they are included in proper
    // location in target docker image.
    val fontSize = 10
    val baseFont = FontFactory.getFont("OpenSans", 10, Font.BOLD)
    baseFont.setSize(fontSize)
    baseFont
  }

  def adjust(base: Font, sizeAdjustment: Option[Int]) = sizeAdjustment match {
    case Some(delta) => new Font(base.getBaseFont(), base.getSize() + delta)
    case None => base
  }
}
