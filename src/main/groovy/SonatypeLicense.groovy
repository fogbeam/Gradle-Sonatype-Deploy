package org.fogbeam.gradle.sonatype

import org.apache.maven.model.License
import static org.fogbeam.gradle.sonatype.SonatypeDeployPluginHelper.configurate

class SonatypeLicense extends License {

  static SonatypeLicense create(Closure c) {
    return configurate(new SonatypeLicense(), c)
  }

  static final SonatypeLicense CC0 = create {
    name = "Creative Commons Zero 1.0 Universal"
    distribution = "Any"
    url = "http://creativecommons.org/publicdomain/zero/1.0/"
  }

  static final SonatypeLicense GPL = create {
    name = "GNU GENERAL PUBLIC LICENSE, Version 3, 29 June 2007"
    distribution = "Binary with Source"
    url = "http://www.gnu.org/licenses/gpl-3.0.html"
  }
  static final SonatypeLicense GPLv3 = GPL

  public static final SonatypeLicense GPLv2 = create {
    name = "GNU GENERAL PUBLIC LICENSE, Version 2, June 1991"
    distribution = GPL.distribution
    url = "http://www.gnu.org/licenses/gpl-2.0.html"
  }

  public static final SonatypeLicense AGPL = create {
    name = "GNU AFFERO GENERAL PUBLIC LICENSE, Version 3, 19 November 2007"
    distribution = GPL.distribution
    url = "http://www.gnu.org/licenses/agpl-3.0.html"
  }

  static final SonatypeLicense LGPL = create {
    name = "GNU LESSER GENERAL PUBLIC LICENSE, Version 3, 29 June 2007"
    distribution = GPL.distribution
    url = "http://www.gnu.org/licenses/lgpl-3.0.html"
  }

  static final SonatypeLicense APACHE = create {
    name = "Apache License, Version 2.0"
    distribution = GPL.distribution
    url = "http://www.apache.org/licenses/LICENSE-2.0.html"
  }

  static final SonatypeLicense MIT = create {
    name = "The MIT License"
    distribution = GPL.distribution
    url = "http://opensource.org/licenses/mit-license.php/"
  }

  public String getDescription() {
    String desc = super.getDescription()
    if(desc == null) return "$name - See $url"
    return desc
  }

}
