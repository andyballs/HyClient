package rage.pitclient.module;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ModuleInfo {
	String name();
	Category category();
	int key() default 0;
	boolean defaultEnabled() default false;
	String tooltip() default "";
	int permission() default 0;
}
