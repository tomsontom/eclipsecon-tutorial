/*******************************************************************************
* Copyright (c) 2009 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.remoteservice.client;

import java.io.NotSerializableException;
import org.eclipse.ecf.remoteservice.IRemoteCall;

/**
 * Parameter serializer for String parameters.
 * 
 * @since 4.0
 *
 */
public class StringParameterSerializer implements IRemoteCallParameterSerializer {

	/**
	 * @throws NotSerializableException  
	 */
	public IRemoteCallParameter serializeParameter(String uri, IRemoteCall call, IRemoteCallable callable, IRemoteCallParameter paramDefault, Object paramToSerialize) throws NotSerializableException {
		if (paramToSerialize instanceof String)
			return new RemoteCallParameter(paramDefault.getName(), paramToSerialize);
		if (paramToSerialize == null) {
			Object defaultValue = paramDefault.getValue();
			if (defaultValue instanceof String)
				return new RemoteCallParameter(paramDefault.getName(), defaultValue);
		}
		return null;
	}

}
