package sinnet.grpc.reports.common;

import java.util.OptionalInt;

import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;

import lombok.experimental.UtilityClass;

/** Doxme. */
@UtilityClass
public class Fonts {

  static {
    // without the initialization font discovery (method: FontFactory.getFont) does not find proper fonts
    // and all fonts in the report will stay with some default font
    FontFactory.registerDirectories();
  }

  /** Doxme. */
  public static Font base() {
    // Fonts should be available because they are part of the project and they are included in proper
    // location in target docker image.
    var fontSize = 10;
    var baseFont = FontFactory.getFont("OpenSans");
    baseFont.setSize(fontSize);
    return baseFont;
  }

  /** Doxme. */
  public static Font bold() {
    // Fonts should be available because they are part of the project and they are included in proper
    // location in target docker image.
    var fontSize = 10;
    var baseFont = FontFactory.getFont("OpenSans", 10, Font.BOLD);
    baseFont.setSize(fontSize);
    return baseFont;
  }

  /** Doxme. */
  public static Font adjust(Font base, OptionalInt sizeAdjustment) {
    if (sizeAdjustment.isPresent()) {
      return new Font(base.getBaseFont(), base.getSize() + sizeAdjustment.getAsInt());
    }
    return base;
  }

}
