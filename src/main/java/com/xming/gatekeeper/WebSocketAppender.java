package com.xming.gatekeeper;


import com.xming.gatekeeper.api.ws.WsHub;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.Serializable;

import static org.apache.logging.log4j.core.Appender.*;

@Plugin(name = "WebSocketAppender", category = Core.CATEGORY_NAME, elementType = ELEMENT_TYPE, printObject = true)
public class WebSocketAppender extends AbstractAppender {

    private final WsHub hub;

    protected WebSocketAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions, WsHub hub) {
        super(name, filter, layout, ignoreExceptions, Property.EMPTY_ARRAY);
        this.hub = hub;
    }

    @PluginFactory
    public static WebSocketAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("Filter") Filter filter,
            @PluginElement("Layout") Layout<? extends Serializable> layout,
            @PluginAttribute("ignoreExceptions") boolean ignoreExceptions
    ) {
        return new WebSocketAppender(name, filter,
                layout != null ? layout : PatternLayout.createDefaultLayout(),
                ignoreExceptions, Gatekeeper.getPlugin().getGateway().getWsHub()
        );
    }

    @Override
    public void append(LogEvent event) {
        String message = new String(getLayout().toByteArray(event));
        hub.broadcast("gatekeeper", "/logger", message);
    }
}