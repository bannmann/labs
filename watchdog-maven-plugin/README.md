
### Notes

The following log messages are not intercepted:

- `Installing <file> to <localrepo><file>` via logger `org.codehaus.plexus.PlexusContainer`
- `Unrecognized output from [<javacpath>, -version]: Picked up JAVA_TOOL_OPTIONS: -Duser.language=en<newline>javac 17.0.14` via logger `org.apache.maven.plugins.jar.ToolchainsJdkSpecification`
- `Plugin [INTERNAL, EXTERNAL] validation issues were detected in following plugin(s)` via logger `org.apache.maven.plugin.internal.DefaultPluginValidationManager`
