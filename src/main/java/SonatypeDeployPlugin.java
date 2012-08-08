package org.fogbeam.gradle.sonatype;

import org.gradle.api.*;
import com.google.common.collect.ImmutableMap;

public class SonatypeDeployPlugin implements Plugin<Project> {

  public void apply(Project target) {
    target.apply(ImmutableMap.of("apply", "maven"));
    target.apply(ImmutableMap.of("apply", "signing"));
  }

}
