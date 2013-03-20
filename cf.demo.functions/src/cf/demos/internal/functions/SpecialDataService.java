package cf.demos.internal.functions;

import cf.demo.functions.IDataService;

public class SpecialDataService implements IDataService {

	@Override
	public String getData() {
		
		return "This is special data from special data service";
		
	}

}
