package org.eclipse.digitaltwin.basyx.core;

import java.io.IOException;
import java.util.List;
import org.springframework.boot.env.PropertiesPropertySourceLoader;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;

public class LoggingPropertySourceLoader implements PropertySourceLoader {
  
  private final PropertySourceLoader delegate = new PropertiesPropertySourceLoader();

  @Override
  public String[] getFileExtensions() {
      return delegate.getFileExtensions();
  }

  @Override
  public List<PropertySource<?>> load(String name, Resource resource) throws IOException {
      System.out.println("Loaded property file: " + resource.getURL());
      return delegate.load(name, resource);
  }
}
