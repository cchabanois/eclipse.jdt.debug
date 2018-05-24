package org.eclipse.jdt.debug.tests.launching;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.debug.core.IJavaThread;
import org.eclipse.jdt.debug.testplugin.JavaProjectHelper;
import org.eclipse.jdt.debug.testplugin.JavaTestPlugin;
import org.eclipse.jdt.debug.tests.AbstractDebugTest;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;

public class LongClassPathTests extends AbstractDebugTest {

	private IJavaProject javaProject;
	private ILaunchConfiguration launchConfiguration;

	public LongClassPathTests(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		String projectName = "testVeryLongClasspath";
		javaProject = createProject(projectName, JavaProjectHelper.TEST_1_8_SRC_DIR.toString(), JavaProjectHelper.JAVA_SE_9_EE_NAME, false);
		launchConfiguration = createLaunchConfigurationStopInMain(javaProject, "LongClasspath");
	}

	@Override
	protected void tearDown() throws Exception {
		try {
			if (javaProject != null) {
				javaProject.getProject().delete(true, true, null);
			}
			if (launchConfiguration != null) {
				launchConfiguration.delete();
			}
		}
		catch (CoreException ce) {
			// ignore
		}
		finally {
			super.tearDown();
		}
	}

	public void testVeryLongClasspath() throws Exception {
		IJavaThread thread = null;
		try {
			// Given
			setLongClasspath(javaProject, launchConfiguration, 350000);
			waitForBuild();

			// When
			thread = launchAndSuspend(launchConfiguration);

			// Then
			refreshProject();
			if (useClasspathOnlyJarForLongClasspath()) {
				// assertEquals(1, getClasspathOnlyJars(javaProject).size());
			}
		}
		finally {
			terminateAndRemove(thread);
			refreshProject();
			// assertEquals(0, getClasspathOnlyJars(javaProject).size());
		}

	}

	private boolean useClasspathOnlyJarForLongClasspath() {
		switch (Platform.getOS()) {
			case Platform.OS_LINUX:
			case Platform.OS_MACOSX:
				return true;
			default:
				return false;
		}
	}

	private void refreshProject() throws CoreException {
		javaProject.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
	}

	private List<IFile> getClasspathOnlyJars(IJavaProject javaProject) throws CoreException {
		List<IFile> classpathOnlyJars = new ArrayList<>();
		for (IResource resource : javaProject.getProject().members()) {
			if (resource instanceof IFile && resource.getName().contains("-classpathOnly-")) {
				classpathOnlyJars.add((IFile) resource);
			}
		}
		return classpathOnlyJars;
	}

	private ILaunchConfiguration createLaunchConfigurationStopInMain(IJavaProject javaProject, String mainTypeName) throws Exception, CoreException {
		ILaunchConfiguration launchConfiguration;
		launchConfiguration = createLaunchConfiguration(javaProject, mainTypeName);
		ILaunchConfigurationWorkingCopy wc = launchConfiguration.getWorkingCopy();
		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_STOP_IN_MAIN, true);
		launchConfiguration = wc.doSave();
		return launchConfiguration;
	}

	private void setLongClasspath(IJavaProject javaProject, ILaunchConfiguration launchConfiguration, int minClassPathLength) throws Exception {
		StringBuilder sb = new StringBuilder(getClasspath(launchConfiguration));
		List<IClasspathEntry> classpathEntries = new ArrayList<>();
		int i = 0;
		File importRootDir = JavaTestPlugin.getDefault().getFileInPlugin(new Path("testjars"));
		IPath projectAbsolutePath = javaProject.getPath().makeAbsolute();
		while (sb.length() < minClassPathLength) {
			JavaProjectHelper.importFilesFromDirectory(importRootDir, projectAbsolutePath, null);
			String jarName = "library-" + i + ".jar";
			IPath targetPath = javaProject.getPath().append(jarName);
			javaProject.getProject().getFile("A.jar").move(targetPath, IResource.FORCE, new NullProgressMonitor());
			classpathEntries.add(JavaCore.newLibraryEntry(targetPath, null, null));
			sb.append(File.pathSeparator);
			sb.append(javaProject.getProject().getFile(jarName).getLocation().toString());
			i++;
		}
		// add previous classpath at the end, just to make sure classpath is not truncated
		classpathEntries.addAll(Arrays.asList(javaProject.getRawClasspath()));
		javaProject.setRawClasspath(classpathEntries.toArray(new IClasspathEntry[classpathEntries.size()]), null);

	}

	private String getClasspath(ILaunchConfiguration launchConfiguration) throws CoreException {
		StringBuilder sb = new StringBuilder();
		IRuntimeClasspathEntry[] entries = JavaRuntime.computeUnresolvedRuntimeClasspath(launchConfiguration);
		entries = JavaRuntime.resolveRuntimeClasspath(entries, launchConfiguration);
		int pathCount = 0;
		for (int i = 0; i < entries.length; i++) {
			if (entries[i].getClasspathProperty() == IRuntimeClasspathEntry.USER_CLASSES) {
				if (pathCount > 0) {
					sb.append(File.pathSeparator);
				}
				sb.append(entries[i].getLocation());
				pathCount++;
			}
		}
		return sb.toString();
	}

}
