module mai_onsyn {
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.controls;
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;
    requires org.apache.logging.log4j.slf4j;
    requires java.desktop;
    requires fastjson;
    requires TarsosDSP;
    requires jlayer;
    requires org.jsoup;
    requires juniversalchardet;
    requires org.fxmisc.flowless;
    requires org.fxmisc.richtext;
    requires jacob;
    requires org.java_websocket;
    requires srt.library;
    requires epublib.core;

    opens mai_onsyn.AnimeFX.Module to org.apache.logging.log4j.core;
    opens mai_onsyn.AnimeFX;

    exports mai_onsyn.AnimeFX;
    exports mai_onsyn.VeloVoice;
}