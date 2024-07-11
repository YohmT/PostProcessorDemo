# 先上结论

**相似点:**

都是Spring框架中的扩展接口，允许开发者在Spring容器的生命周期的不同阶段对Bean进行处理

**不同点:**

- **操作对象不同:**
  - `BeanFactoryPostProcessor`处理的是`BeanDefinition`对象（配置元数据），而不是Bean实例，在容器实例化任何bean之前读取和可能更改配置元数据。
  - `BeanPostProcessor`处理的是bean实例，在bean实例化、配置和初始化之后进行自定义逻辑处理。
- **执行时机不同:**
  - `BeanFactoryPostProcessor`在bean实例化之前执行。
  - `BeanPostProcessor`在bean实例化之后执行。
- **应用场景不同:**
  - `BeanFactoryPostProcessor`适用于需要在bean实例化前修改bean定义信息的场景。
  - `BeanPostProcessor`适用于需要在bean实例化后进行的自定义处理逻辑。

# 定义

BeanFactoryPostProcessor和BeanPostProcessor是Spring中很重要的了两个接口。我们先来看看Spring的文档中对BeanPostProcessor的定义：

## BeanFactoryPostProcessor

The semantics of this interface are similar to those of the BeanPostProcessor, with one major difference:BeanFactoryPostProcessor operates on the bean configuration metadata; that is, the Spring IoC container allows a BeanFactoryPostProcessor to read the configuration metadata and potentially change it before the container instantiates any beans other than BeanFactoryPostProcessors.

BeanFactoryPostProcessor接口的语义与BeanPostProcessor类似，但有一个主要区别：BeanFactoryPostProcessor操作的是bean的配置元数据；也就是说，Spring IOC容器允许BeanFactoryPostProcessor读取配置元数据，并在容器实例化任何bean（除了BeanFactoryPostProcessor之外）之前可能更改这些元数据。

## BeanPostProcessor

The BeanPostProcessor interface defines callback methods that you can implement to provide your own (or override the container’s default) instantiation logic, dependency-resolution logic, and so forth. If you want to implement some custom logic after the Spring container finishes instantiating, configuring, and initializing a bean, you can plug in one or more BeanPostProcessor implementations.

BeanPostProcessor接口定义了回调方法，您可以实现这些方法来提供自己的（或覆盖容器的默认）实例化逻辑、依赖解析逻辑等。如果您希望在Spring容器完成实例化、配置和初始化bean之后实现一些自定义逻辑，您可以插入一个或多个BeanPostProcessor实现。

# Bean加载过程

- **Spring容器启动**：
  Spring容器启动时，会根据配置文件（如`applicationContext.xml`）或注解扫描包路径，加载定义在配置文件中的Bean定义或使用注解标注的Bean类。
- **Bean定义加载**：
  Spring首先解析配置文件或注解，并将每个Bean的定义（如类名、作用范围、依赖关系等）加载到`BeanDefinition`对象中。这个阶段只是在内存中建立一个Bean定义的描述信息，并没有实例化Bean。
- **BeanFactoryPostProcessor处理**：
  在Bean实例化之前，Spring容器会调用所有注册的`BeanFactoryPostProcessor`，例如`PropertyPlaceholderConfigurer`，用于修改`BeanDefinition`的属性。
- **实例化Bean**：
  当所有的Bean定义加载完毕并处理过后，Spring容器开始实例化Bean。实例化是通过调用无参构造器或带参构造器来完成的。如果一个Bean依赖于其他Bean，Spring会确保这些依赖先被实例化。
- **设置Bean属性**：
  在Bean实例化之后，Spring会设置Bean的属性（例如通过`setter`方法或直接注入字段），这一步会处理Bean之间的依赖注入。
- **Bean初始化**：
    Spring容器会调用注册的`BeanPostProcessor`，例如`@Autowired`和`@Value`注解的处理。`BeanPostProcessor`在Bean初始化的前后分别会有`postProcessBeforeInitialization`和`postProcessAfterInitialization`两个回调方法。
    - `postProcessBeforeInitialization`：在Bean的初始化方法（例如`afterPropertiesSet`或自定义初始化方法）之前调用。
    - 如果Bean实现了`InitializingBean`接口或定义了初始化方法（通过`init-method`属性），Spring会调用这些初始化方法。
    - `postProcessAfterInitialization`：在Bean的初始化方法之后调用。
- **Bean就绪使用**：
  当所有的Bean都被实例化、设置属性、并且经过初始化后，Spring容器就可以开始使用这些Bean了。
- **Bean关闭**：
  当应用程序关闭时，Spring容器会调用实现了`DisposableBean`接口的Bean的`destroy`方法或Bean定义中指定的销毁方法（通过`destroy-method`属性）。

# 示例
**源码地址：**
## 定义User类，实现InitializingBean, DisposableBean
User类从配置文件读取属性
```java
@Slf4j
@Component
@ConfigurationProperties(prefix = "user")
public class User implements InitializingBean, DisposableBean {

    private String name;

    private int age;

    public User() {
        log.warn("Bean of User: 实例化");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        log.warn("Bean of User: 设置属性");
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public void destroy() throws Exception {
        log.warn("Bean of User: DisposableBean执行");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.warn("Bean of User: InitializingBean执行");
    }
}
```
## 定义一个类实现BeanFactoryPostProcessor
重写`postProcessBeanFactory`方法
```java
@Slf4j
@Component
public class BeanFactoryProcessorTest implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
        for (String name : beanDefinitionNames) {
            if (name.contains("user")) {
                log.warn("Bean of User: BeanFactoryPostProcessor的postProcessBeanFactory执行");
            }
        }
    }

}
```
## 定义一个类实现BeanPostProcessor
重写写`postProcessBeforeInitialization`和`postProcessAfterInitialization`方法
```java
@Slf4j
@Component
public class PostProcessorTest implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (beanName.contains("user")) {
            log.warn("Bean of User: BeanPostProcessor中postProcessBeforeInitialization执行");
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (beanName.contains("user")) {
            log.warn("Bean of User: BeanPostProcessor中postProcessAfterInitialization执行");
        }
        return bean;
    }
}
```
## 结果
![在这里插入图片描述](https://i-blog.csdnimg.cn/direct/544b62f8c62a41788b807fe69f424634.jpeg#pic_center)