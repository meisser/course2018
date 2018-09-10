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
		} catch (CompilerRuntimeException e) {
			throw new ClassNotFoundException("Could not load " + name, e);
		} catch (IOException e) {
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
