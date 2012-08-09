package org.fogbeam.gradle.sonatype

import org.apache.maven.model.Scm
import static org.fogbeam.gradle.sonatype.SonatypeDeployPluginHelper.configurate

class SonatypeScm extends Scm {

  static SonatypeScm create(Closure c) {
    return configurate(new SonatypeScm(), c)
  }

  static SonatypeScm github(String userName, String project) {
    return create {
      connection = "git://github.com/$userName/${project}.git"
      developerConnection = "git@github.com:$userName/${project}.git"
      url = "https://github.com/$userName/$project"
    }
  }
}
