package test.jsr223;

import java.util.List;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

public class ListScriptingEngines {
	public static void main(String[] args) {
		System.out.println();
		
		ScriptEngineManager manager = new ScriptEngineManager();
		List<ScriptEngineFactory> factories = manager.getEngineFactories();
		System.out.print("Found ");
		System.out.print(factories.size());
		System.out.print(" ");
		System.out.print((factories.size() == 1) ? "scripting engine" : "scripting engines");
		System.out.println(".");
		
		for (ScriptEngineFactory factory : factories) {
			System.out.println();
			System.out.println("Engine Name: " + factory.getEngineName());
			System.out.println("Engine Version: " + factory.getEngineVersion());
			System.out.println("Language Name: " + factory.getLanguageName());
			System.out.println("Language Version: " + factory.getLanguageVersion());
			System.out.println("Names:");
			for (String name : factory.getNames()) {
				System.out.println("\t" + name);
			}
			System.out.println("Extensions:");
			for (String extension : factory.getExtensions()) {
				System.out.println("\t" + extension);
			}
			System.out.println("MIME Types:");
			for (String mimeType : factory.getMimeTypes()) {
				System.out.println("\t" + mimeType);
			}
		}
		
		System.out.println();
	}
}
