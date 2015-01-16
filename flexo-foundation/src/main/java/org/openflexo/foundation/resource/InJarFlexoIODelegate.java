package org.openflexo.foundation.resource;

import java.io.InputStream;
import java.io.OutputStream;

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.rm.InJarResourceImpl;

@ModelEntity
@ImplementationClass(InJarFlexoIODelegate.InJarFlexoIODelegateImpl.class)
@XMLElement
public interface InJarFlexoIODelegate extends FlexoIOStreamDelegate<InJarResourceImpl> {

	@PropertyIdentifier(type = InJarResourceImpl.class)
	public static final String IN_JAR_RESOURCE = "inJarResource";

	@Getter(value = IN_JAR_RESOURCE, ignoreType = true)
	public InJarResourceImpl getInJarResource();

	@Setter(IN_JAR_RESOURCE)
	public void setInJarResource(InJarResourceImpl inJarResource);

	public abstract class InJarFlexoIODelegateImpl extends FlexoIOStreamDelegateImpl<InJarResourceImpl> implements InJarFlexoIODelegate {

		public static InJarFlexoIODelegate makeInJarFlexoIODelegate(InJarResourceImpl inJarResource, ModelFactory factory) {
			InJarFlexoIODelegate delegate = factory.newInstance(InJarFlexoIODelegate.class);
			delegate.setInJarResource(inJarResource);
			return delegate;
		}

		@Override
		public InputStream getInputStream() {
			return getInJarResource().openInputStream();
		}

		@Override
		public void setInputStream(InputStream inputStream) {
			// TODO Auto-generated method stub
		}

		@Override
		public OutputStream getOutputStream() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setOutputStream(OutputStream outsputStream) {
			// TODO Auto-generated method stub
		}

		@Override
		public synchronized boolean hasWritePermission() {
			return false;
		}

		@Override
		public boolean exists() {
			return true;
		}

		@Override
		public boolean delete() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}

		@Override
		public String getParentPath() {
			// TODO Auto-generated method stub
			return null;
		}
	}

}
