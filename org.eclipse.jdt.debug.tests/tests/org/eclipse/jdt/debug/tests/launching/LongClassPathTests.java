package org.eclipse.jdt.debug.tests.launching;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assume.assumeThat;
import static org.junit.Assume.assumeTrue;

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
import org.eclipse.jdt.launching.AbstractVMInstall;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.IVMInstall;
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

	/*
	 * When classpathOnlyJar is enabled, a classpath-only jar is created.
	 */
	public void testVeryLongClasspathWithClasspathOnlyJar() throws Exception {
		IJavaThread thread = null;
		try {
			// Given
			setLongClasspath(javaProject, launchConfiguration, 350000);
			launchConfiguration = enableClasspathOnlyJar(launchConfiguration);
			waitForBuild();

			// When
			thread = launchAndSuspend(launchConfiguration);

			// Then
			refreshProject();
			assertEquals(1, getClasspathOnlyJars(javaProject).size());
		}
		finally {
			terminateAndRemove(thread);
			refreshProject();
			assertEquals(0, getClasspathOnlyJars(javaProject).size());
		}
	}

	/*
	 * When JVM > 9, an argument file for the classpath is created when classpath is too long
	 */
	public void testVeryLongClasspathWithArgumentFile() throws Exception {
		assumeThat(Platform.getOS(), is(not(equalTo(Platform.OS_WIN32))));
		assumeTrue(isArgumentFileSupported(launchConfiguration));
		IJavaThread thread = null;
		try {
			// Given
			setLongClasspath(javaProject, launchConfiguration, 350000);
			waitForBuild();

			// When
			thread = launchAndSuspend(launchConfiguration);

			// Then
			refreshProject();
			assertEquals(1, getClasspathArgumentFiles(javaProject).size());
		} finally {
			terminateAndRemove(thread);
			refreshProject();
			assertEquals(0, getClasspathArgumentFiles(javaProject).size());
		}
	}

	/*
	 * On Windows, the CLASSPATH env variable is used if classpath is too long
	 */
	public void testVeryLongClasspathWithEnvironmentVariable() throws Exception {
		assumeThat(Platform.getOS(), is(equalTo(Platform.OS_WIN32)));
		IJavaThread thread = null;
		try {
			// Given
			setLongClasspath(javaProject, launchConfiguration, 350000);
			waitForBuild();

			// When/Then
			thread = launchAndSuspend(launchConfiguration);
		} finally {
			terminateAndRemove(thread);
		}
	}

	private boolean isArgumentFileSupported(ILaunchConfiguration launchConfiguration) throws CoreException {
		IVMInstall vmInstall = JavaRuntime.computeVMInstall(launchConfiguration);
		if (vmInstall instanceof AbstractVMInstall) {
			AbstractVMInstall install = (AbstractVMInstall) vmInstall;
			String vmver = install.getJavaVersion();
			if (JavaCore.compareJavaVersions(vmver, JavaCore.VERSION_9) >= 0) {
				return true;
			}
		}
		return false;
	}

	private ILaunchConfiguration enableClasspathOnlyJar(ILaunchConfiguration launchConfiguration) throws CoreException {
		ILaunchConfigurationWorkingCopy configurationWorkingCopy = launchConfiguration.getWorkingCopy();
		configurationWorkingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_USE_CLASSPATH_ONLY_JAR, true);
		return configurationWorkingCopy.doSave();
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

	private List<IFile> getClasspathArgumentFiles(IJavaProject javaProject) throws CoreException {
		List<IFile> classpathFiles = new ArrayList<>();
		for (IResource resource : javaProject.getProject().members()) {
			if (resource instanceof IFile && resource.getName().contains("-classpath-arg-")) {
				classpathFiles.add((IFile) resource);
			}
		}
		return classpathFiles;
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
