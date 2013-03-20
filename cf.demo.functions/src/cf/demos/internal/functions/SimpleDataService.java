package cf.demos.internal.functions;

import cf.demo.functions.IDataService;

public class SimpleDataService implements IDataService {

	@Override
	public String getData() {
		return "This is simple data";
	}

}
