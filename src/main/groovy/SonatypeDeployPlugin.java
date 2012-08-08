package org.fogbeam.gradle.sonatype;

import org.gradle.api.*;
import com.google.common.collect.ImmutableMap;
import static org.fogbeam.gradle.sonatype.SonatypeDeployPluginHelper;

public class SonatypeDeployPlugin implements Plugin<Project> {

  public void apply(Project target) {
    // TODO Add a plugin to generate a javadoc and a source jar.
    target.apply(ImmutableMap.of("apply", "maven"));
    target.apply(ImmutableMap.of("apply", "signing"));
    target.getArtifacts().add("archives", "jar");
    configureSigning(target);
  }



}
