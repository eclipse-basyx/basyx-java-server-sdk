# Basyx AAS Registry Plugins

This project provides a maven plugin that can be used to generate builder classes that create search paths for the registry-service-based POJO classes. The plugin traverses the referenced class and its fields and generates a class that can be used to set up these search paths.

As we use the same search path in our AAS registry client, this generator can also be used there. The main benefit is that we will avoid typos when using the generated client and do not need to specify the string directly. 

In addition, this plugin also generates a class that can be used to resolve a field of an object referenced by a path.

This is how you embed it into your POM file:

``` xml 
<plugin>
	<groupId>org.eclipse.digitaltwin.basyx</groupId>
	<artifactId>basyx.aasregistry</artifactId>
	<executions>
		<execution>
			<id>paths</id>
			<goals>
				<goal>simple-path-generator</goal>
			</goals>
		</execution>
	</executions>
	<configuration>
		<pathsTargetClassName>AasRegistryPaths</pathsTargetClassName>
		<processorTargetClassName>AasRegistryPathProcessor</processorTargetClassName>
		<className>org.eclipse.digitaltwin.basyx.aasregistry.model.AssetAdministrationShellDescriptor</className>
		<targetSourceFolder>${project.basedir}/src/generated/java</targetSourceFolder>
		<targetPackageName>org.eclipse.digitaltwin.basyx.aasregistry.client.api</targetPackageName>
	</configuration>
</plugin>
```

We use a mustache template for the Java class that will be generated. It will be thus quite easy to extend our plugin and add a mustache file for a different language.