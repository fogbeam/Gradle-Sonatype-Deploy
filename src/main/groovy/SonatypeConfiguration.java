package org.fogbeam.gradle.sonatype;

import groovy.lang.Closure;
import org.gradle.api.GradleScriptException;
import java.util.List;
import java.util.ArrayList;
import com.google.common.collect.ImmutableList;
import org.apache.maven.model.License;
import org.apache.maven.model.Scm;
import org.apache.maven.model.Developer;
import static org.fogbeam.gradle.sonatype.SonatypeDeployPluginHelper.configurate;
import static org.fogbeam.gradle.sonatype.SonatypeDeployPluginHelper.oneArgNopClosure;
import static org.fogbeam.gradle.sonatype.SonatypeDeployPluginHelper.lookupLicense;

public class SonatypeConfiguration {

  public String name = null;
  public String description = null;
  public String url = null;

  Scm scm = null;
  public Scm getScm() {
    if(scm == null) scm = new SonatypeScm();
    return scm;
  }
  public void setScm(Scm newScm) {
    if(newScm == null) {
      throw new GradleScriptException("Cannot assign a null SCM to Sonatype configuration", null);
    }
    this.scm = newScm;
  }
  public Scm scm(Closure c) {
    setScm(configurate(new Scm(), c));
    return scm;
  }

  List<License> licenses = null;
  public List<License> getLicenses() {
    if(licenses == null) licenses = new ArrayList<License>(2);
    return licenses;
  }
  public void setLicenses(List<License> newLicenses) {
    if(newLicenses == null) {
      throw new GradleScriptException("Cannot assign a null licenses collection to Sonatype configuration", null);
    }
    this.licenses = newLicenses;
  }
  public SonatypeLicense getLicense() {
    return license(oneArgNopClosure());
  }
  public SonatypeLicense license(String name) {
    SonatypeLicense it = lookupLicense(name);
    getLicenses().add(it);
    return it;
  }
  public SonatypeLicense license(Closure c) {
    SonatypeLicense it = configurate(new SonatypeLicense(), c);
    getLicenses().add(it);
    return it;
  }

  List<Developer> developers = null;
  public List<Developer> getDevelopers() {
    if(developers == null) developers = new ArrayList<Developer>(2);
    return developers;
  }
  public void setDevelopers(List<Developer> newDevs) {
    if(newDevs == null) {
      throw new GradleScriptException("Cannot assign a null developers collection to Sonatype configuration", null);
    }
    this.developers = newDevs;
  }
  public Developer getDeveloper() {
    return developer(oneArgNopClosure());
  }
  public Developer developer(Closure c) {
    Developer it = configurate(new Developer(), c);
    getDevelopers().add(it);
    return it;
  }
  public Developer dev(Closure c) {
    return developer(c);
  }


}
