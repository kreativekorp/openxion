package test.jsr223;

import java.util.List;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ScriptDemo {
	public static void main(String[] args) {
		ScriptEngineManager manager = new ScriptEngineManager();
		List<ScriptEngineFactory> factories = manager.getEngineFactories();
		for (ScriptEngineFactory factory : factories) {
			System.out.println(factory.getEngineName() + " " + factory.getEngineVersion() + ":");
			String outputStatement = factory.getOutputStatement("Hello, world.");
			System.out.println(outputStatement);
			try {
				factory.getScriptEngine().eval(outputStatement);
			} catch (ScriptException e) {
				System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
			}
			System.out.println();
		}
	}
}
