package mg.itu.prom16.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD) // tsy maintsy fonction no azo annoter na get (Method)
@Retention(RetentionPolicy.RUNTIME)
public @interface Get {
    String value();    
}
