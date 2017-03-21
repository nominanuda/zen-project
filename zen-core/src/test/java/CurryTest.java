import static org.junit.Assert.*;

import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class CurryTest {

	@Test
	public void test() {
		fail("Not yet implemented");
	}
	<X,T> Supplier<T> curry(Function<X, T> f, X x) {
		return new Supplier<T>() {
			public T get() {
				return f.apply(x);
			}
		};
	}
}