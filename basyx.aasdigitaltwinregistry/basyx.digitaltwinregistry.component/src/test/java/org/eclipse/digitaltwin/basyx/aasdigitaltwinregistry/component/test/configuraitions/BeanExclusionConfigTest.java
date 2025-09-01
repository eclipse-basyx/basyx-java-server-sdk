package org.eclipse.digitaltwin.basyx.aasdigitaltwinregistry.component.test.configuraitions;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.digitaltwin.basyx.aasdigitaltwinregistry.component.confguration.BeanExclusionConfig;
import org.junit.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.Assert.*;

@Slf4j
public class BeanExclusionConfigTest {

    @Test
    public void testRemoveDuplicateControllersWhenBeanExistsRemovesBeanDefinition() {
        log.info("Started unit test - testRemoveDuplicateControllersWhenBeanExistsRemovesBeanDefinition()");
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(BeanExclusionConfig.class);
        DefaultListableBeanFactory beanFactory = context.getDefaultListableBeanFactory();
        beanFactory.registerBeanDefinition("descriptionApiController", new RootBeanDefinition(Object.class));
        beanFactory.registerBeanDefinition("searchApiController", new RootBeanDefinition(Object.class));
        context.refresh();
        assertFalse("descriptionApiController should be removed", beanFactory.containsBean("descriptionApiController"));
        assertFalse("searchApiController should be removed", beanFactory.containsBean("searchApiController"));
        context.close();
        log.info("Successfully conducted unit test");
    }

    @Test
    public void testRemoveDuplicateControllersWhenBeanDoesNotExistNoExceptionThrown() {
        log.info("Started unit test - testRemoveDuplicateControllersWhenBeanDoesNotExistNoExceptionThrown()");
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(BeanExclusionConfig.class);
        DefaultListableBeanFactory beanFactory = context.getDefaultListableBeanFactory();
        context.refresh();
        assertFalse("descriptionApiController should not exist", beanFactory.containsBean("descriptionApiController"));
        assertFalse("searchApiController should not exist", beanFactory.containsBean("searchApiController"));
        context.close();
        log.info("Successfully conducted unit test");
    }

    @Test
    public void testRemoveDuplicateControllersRemovesAllSpecifiedBeans() {
        log.info("Started unit test - testRemoveDuplicateControllersRemovesAllSpecifiedBeans()");
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(BeanExclusionConfig.class);
        DefaultListableBeanFactory beanFactory = context.getDefaultListableBeanFactory();
        String[] duplicateBeans = {
                "descriptionApiController",
                "searchApiController",
                "shellDescriptorsApiController",
                "homeController",
                "registryEventLogSink"
        };

        for (String beanName : duplicateBeans) {
            beanFactory.registerBeanDefinition(beanName, new RootBeanDefinition(Object.class));
        }

        context.refresh();

        for (String beanName : duplicateBeans) {
            assertFalse(beanName + " should be removed", beanFactory.containsBean(beanName));
        }

        context.close();
        log.info("Unit Test conducted successfully");
    }

    @Test
    public void testRemoveDuplicateControllersDoesNotRemoveOtherBeans() {
        log.info("Started unit test - testRemoveDuplicateControllersDoesNotRemoveOtherBeans()" );
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(BeanExclusionConfig.class);
        DefaultListableBeanFactory beanFactory = context.getDefaultListableBeanFactory();
        beanFactory.registerBeanDefinition("descriptionApiController", new RootBeanDefinition(Object.class));
        beanFactory.registerBeanDefinition("validServiceBean", new RootBeanDefinition(Object.class));
        beanFactory.registerBeanDefinition("searchApiController", new RootBeanDefinition(Object.class));
        beanFactory.registerBeanDefinition("anotherValidBean", new RootBeanDefinition(Object.class));
        context.refresh();
        assertFalse("descriptionApiController should be removed", beanFactory.containsBean("descriptionApiController"));
        assertFalse("searchApiController should be removed", beanFactory.containsBean("searchApiController"));
        assertTrue("validServiceBean should remain", beanFactory.containsBean("validServiceBean"));
        assertTrue("anotherValidBean should remain", beanFactory.containsBean("anotherValidBean"));
        context.close();
        log.info("Unit test conducted successfully");
    }

    @Test
    public void testBeanExclusionConfigBeanFactoryPostProcessorIsStatic() {
        log.info("Started unit test - testBeanExclusionConfigBeanFactoryPostProcessorIsStatic()");
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(BeanExclusionConfig.class);
        assertTrue("Context should be active", context.isActive());
        context.close();
        log.info("Unit test conducted successfully");
    }
}
