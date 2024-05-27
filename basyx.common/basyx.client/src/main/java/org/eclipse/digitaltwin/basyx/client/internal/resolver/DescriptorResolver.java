package org.eclipse.digitaltwin.basyx.client.internal.resolver;

public interface DescriptorResolver<I, O> {

	O resolveDescriptor(I descriptor);
	
}
