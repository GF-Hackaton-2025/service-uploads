package br.com.uploads.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContext implements ApplicationContextAware {

  private static ApplicationContext context;

  public static <T> T getBean(final Class<T> beanClass) {
    return context.getBean(beanClass);
  }

  private static void setContext(final ApplicationContext context) {
    SpringContext.context = context;
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    setContext(applicationContext);
  }
}
