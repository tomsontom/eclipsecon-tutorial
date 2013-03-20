/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package cf.demo.app.handlers;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;

import cf.demo.functions.IDataService;

public class AboutHandler {
	
	
	private static int i;

	@Execute
	public void execute(IDataService dataService, IEclipseContext sameContextThatGetsPassedToCF) {
		if (dataService== null){
			System.err.println("Error");
			return ;
		}
		String data = dataService.getData();
		System.out.println(data);
	}
}
