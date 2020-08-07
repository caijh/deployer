package com.github.caijh.deployer.jinjava;

import com.hubspot.jinjava.interpret.JinjavaInterpreter;
import com.hubspot.jinjava.lib.filter.Filter;
import org.apache.commons.net.util.Base64;

public class Base64EncodeFilter implements Filter {

    private static final Base64EncodeFilter FILTER = new Base64EncodeFilter();

    public static Filter getInstance() {
        return FILTER;
    }

    @Override
    public Object filter(Object var, JinjavaInterpreter interpreter, String... args) {
        if (var instanceof String) {
            return new String(Base64.encodeBase64(((String) var).getBytes(interpreter.getConfig().getCharset())));
        }
        return var;
    }

    @Override
    public String getName() {
        return "b64encode";
    }

}
