/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.classloader;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardLocation;

public class AgentCompiler implements DiagnosticListener<JavaFileObject> {

	private JavaCompiler compiler;
	private SourceFileManager manager;
	
	public AgentCompiler(SimulationHandle source) {
		this(null, source);
	}

	public AgentCompiler(RemoteLoader simulationJar, SimulationHandle source) {
		this.compiler = ToolProvider.getSystemJavaCompiler();
		this.manager = new SourceFileManager(simulationJar, source, this);
	}
	
	public boolean alreadyCompiledClass(String name) {
		return manager.getByteCode(name) != null;
	}

	public byte[] findClass(String name) throws ClassNotFoundException {
		try {
			byte[] byteCode = manager.getByteCode(name);
			if (byteCode == null) {
				JavaFileObject object = manager.getJavaFileForInput(StandardLocation.SOURCE_PATH, name, Kind.SOURCE);
				System.out.println("Compiling " + name);
				boolean success = this.compiler.getTask(null, manager, this, Arrays.asList("-cp","loaded.jar","-g"), null, Collections.singleton(object)).call();
				assert success;
				byteCode = manager.getByteCode(name);
			}
			assert byteCode != null;
			return byteCode;
		} catch (IOException | RuntimeException e) { // CompilerRuntimeException
//			Previously, only CompilerRuntimeException was caught. But apparently, it sometimes also throws a RuntimeException:
//			java.lang.RuntimeException: java.lang.RuntimeException: com.agentecon.classloader.CompilerRuntimeException: com/agentecon/exercise5/StockPickingStrategy.java:15: error: package com.sun.xml.internal.ws.dump.LoggingDumpTube does not exist
//			import com.sun.xml.internal.ws.dump.LoggingDumpTube.Position;
//			        at sun.reflect.GeneratedConstructorAccessor54.newInstance(Unknown Source)
//			        at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)
//			        at java.lang.reflect.Constructor.newInstance(Constructor.java:423)
			throw new ClassNotFoundException("Could not load " + name, e);
		}
	}

	@Override
	public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
		if (diagnostic.getKind() == javax.tools.Diagnostic.Kind.ERROR) { 
			throw new CompilerRuntimeException(diagnostic.toString());
		} else {
			System.out.println("Compiler warns: " + diagnostic);
		}
	}

}
