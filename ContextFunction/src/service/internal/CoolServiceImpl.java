package service.internal;

import service.ICoolService;

public class CoolServiceImpl implements ICoolService{

	@Override
	public void coolAction() {
		System.err.println("Making a cool statement");
	}

}
