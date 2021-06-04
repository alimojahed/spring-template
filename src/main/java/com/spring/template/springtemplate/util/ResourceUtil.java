package com.spring.template.springtemplate.util;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

/**
 * @author Ali Mojahed on 6/4/2021
 * @project spring-template
 **/
public class ResourceUtil {

    public InputStream getResource(String name) {
        return this.getClass().getResourceAsStream(name);
    }

    public String getResourcePath(String resourcePath) {
        URL resource = this.getClass().getResource(resourcePath);
        return resource == null ? null : resource.getFile().replaceAll("[%]20", " ");
    }

    public String getFileAbsoluteAddress(String address) {
        String resourcePath = getResourcePath(address);
        if (resourcePath != null) {
            File file = new File(resourcePath);
            if (file.exists()) {
                address = file.getAbsolutePath();
            }
        }
        return address;
    }

}