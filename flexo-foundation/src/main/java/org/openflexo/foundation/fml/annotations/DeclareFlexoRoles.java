package org.openflexo.foundation.fml.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.openflexo.foundation.fml.FlexoRole;

/**
 * Annotation used to provide to a {@link ModelSLot} the list of all {@link FlexoRole} to consider
 * 
 * @author sylvain
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(value = ElementType.TYPE)
public @interface DeclareFlexoRoles {

	public Class<? extends FlexoRole>[] value();
}
