# Spring Beans:
There are a couple of ways to define a bean, works similar to a java object but a bean is used by
spring application to make sure it works under dependency injection (IOC - Inversion of Control).

## 1. Basic Setup:

- In this, all the beans are defined in a separate class (usually called AppConfig with a
@Configuration) which lets the spring application know that here all the beans would be defined.
(we can define multiple appconfig classess too ig but didn't tried).

- now to define a bean inside this AppConfig, create a public function and lets suppose the class
for which we need to define the bean is named Trial, so the AppConfig would look something like
this:
```java
    @Configuration
    public class AppConfig {
        @Bean
        public Trial trial(){
            return new Trial();
        }
    }
```
One can name the function whatever they want unless it has the @Bean annotation in it.
- Now whenever in the application we need to get a bean, or use class trial, we won't create an
object of that class normally like Trial obj = new Trial(); No! We're gonna work with beans which
means have to inject it. In this case either one can use context.getBean method, or use a
constructor like this:
```java
    public class_where_we_use_bean(Trial trial){
        this.trial = trial;
    }
```
> **Note:** define the object in the class too, like:
> ```java
>   private final Trial trial;
>```

- and thats it, no annotation to be used cause spring will automatically check for this name when it
starts, and we defined the bean with the name so it will inject automatically. And then one can use
the trial object.

** What if we make 2 beans of the same class Trial in the AppConfig, here is where @Qualifier and
@Bean("name") comes into play. @Qualifier is used to define a unique ID, when using the constructor
just define @qualifier there as well as when you define bean, on the other hand if you use name
property in the @bean, you can call that particular bean with the name using the function context
.getbean(name, classtype).
Also add @primary annotation

## 2. Using diff bean annotations

- write about how to use @Annotation (known as field injection) -> not recommended (why? dont know) 
  also can use @qualifier after @autowired to define which bean is to be used.

- Another way is to use method injection, In the class where need to use the bean, do this:
```java
      private Trial trial;

      @Autowired
      public void injectDependencies(@Qualifier("Q_name") Trial trial){
        this.trial = trial;
      }
```
Once done need not to call this method anywhere it will automatically inject the bean and then 
can use the object anywhere in the class. (Make sure to add @Autowired which is used by the 
application to do injection).

- and diff bean 
  types like @service, @repository, 
  @transactional