package com.nominanuda.zen.obj.wrap;

import static com.nominanuda.zen.obj.wrap.Wrap.WF;

import java.util.function.Function;

import com.nominanuda.zen.common.Check;
import com.nominanuda.zen.obj.Obj;
import com.nominanuda.zen.obj.Stru;

public class Wrapper2Function2Wrapper<WRAPPER extends ObjWrapper> implements Function<WRAPPER, WRAPPER> {
	private final Function<Obj, Stru> fnc;
	private final Class<WRAPPER> clz;
	
	
	public Wrapper2Function2Wrapper(Function<Obj, Stru> fnc, Class<WRAPPER> clz) {
		this.fnc = Check.notNull(fnc, "wrapped script is null!");
		this.clz = clz;
	}
	

	@Override
	public WRAPPER apply(WRAPPER t) {
		if (t != null) {
			Stru result = fnc.apply(t.unwrap());
			if (result != null) {
				return WF.wrap(result.asObj(), clz);
			}
		}
		return null;
	}
}
