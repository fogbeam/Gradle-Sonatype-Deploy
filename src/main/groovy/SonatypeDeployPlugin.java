package org.fogbeam.gradle.sonatype;

import org.gradle.api.logging.Logging;
import java.util.List;
import org.gradle.api.*;
import org.gradle.api.*;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableList;
import static org.fogbeam.gradle.sonatype.SonatypeDeployPluginHelper.*;
import org.gradle.api.artifacts.maven.MavenDeployer;
import org.gradle.api.artifacts.maven.MavenDeployment;
import org.gradle.api.tasks.Upload;
import org.gradle.plugins.signing.SigningExtension;
import org.apache.maven.model.Model;
import org.apache.maven.model.License;
import org.apache.maven.model.Developer;
import org.apache.maven.model.Scm;

public class SonatypeDeployPlugin implements Plugin<Project> {

  public static final String EXTENSION_NAME = "sonatype";

  public void apply(Project target) {
    // TODO Add a plugin to generate a javadoc and a source jar.
    target.getLogger().info(getClass().getSimpleName() + " is applying itself to " + target);
    target.apply(ImmutableMap.of("plugin", "maven"));
    target.apply(ImmutableMap.of("plugin", "signing"));
    target.getArtifacts().add("archives", "jar");
    configureSigning(target);
    configureDeployer(findMavenDeployer(target), findSigner(target));
    addPomConfigurationHook(target);
    configurePomToSetPackagingString(findMavenDeployer(target).getPom());
    target.getExtensions().create(EXTENSION_NAME, SonatypeConfiguration.class);
  }

  private static MavenDeployer findMavenDeployer(Project p) {
    Upload uploadArchives = p.getTasks().withType(Upload.class).getByName("uploadArchives");
    return uploadArchives.getRepositories().withType(MavenDeployer.class).getByName("mavenDeployer");
  }

  private static SigningExtension findSigner(Project p) {
    return p.getExtensions().findByType(SigningExtension.class);
  }

  private static void configureDeployer(final MavenDeployer mvn, final SigningExtension sign) {
    mvn.beforeDeployment(new Action<MavenDeployment>() {
      public void execute(MavenDeployment deploy) {
        sign.signPom(deploy);
      }
    });
    configureRepository(mvn);
  }

  private static Object die(String msg) {
    throw new GradleScriptException(msg, null);
  }

  private static void addPomConfigurationHook(Project project) {
    project.afterEvaluate(new Action<Project>() {
      public void execute(Project p) {
        SonatypeConfiguration data = p.getExtensions().findByType(SonatypeConfiguration.class);
        Model model = (Model)findMavenDeployer(p).getPom().getModel();

        if(model.getName() == null) model.setName(data.name == null ? p.getName() : data.name);
        model.setPackaging("jar");
        if(model.getDescription() == null) model.setDescription(data.description == null ?
          (model.getName()  + ", deployed by the Gradle Sonatype Deploy Plugin by Fogbeam Labs")
          : data.description
        );
        if(model.getUrl() == null) model.setUrl(data.url == null ?
          die("The URL for your project needs to be set on project." + EXTENSION_NAME + ".url").toString()
          : data.url
        );
        if(model.getScm() == null) model.setScm(createScm(data));
        if(model.getLicenses() == null || model.getLicenses().isEmpty()) {
          model.setLicenses(createLicenses(p, data));
        }
        if(model.getDevelopers() == null || model.getDevelopers().isEmpty()) {
          model.setDevelopers(createDevelopers(p, data));
        }
      }
    });
  }

  private static Scm createScm(SonatypeConfiguration data) {
    if(data.scm == null) {
      throw new GradleScriptException("Need to configure scm on project." + EXTENSION_NAME, null);
    }
    return data.scm;
  }

  private static List<License> createLicenses(Project project, SonatypeConfiguration data)  {
    if(data.licenses == null || data.licenses.isEmpty()) {
      project.getLogger().warn("Need to configure liscenses on project." + EXTENSION_NAME + "; assuming AGPL");
      data.getLicenses().add(SonatypeLicense.AGPL);
      return data.licenses;
    }
    return ImmutableList.copyOf(data.licenses);
  }

  private static List<Developer> createDevelopers(Project project, SonatypeConfiguration data) {
    if(data.developers == null || data.developers.isEmpty()) {
      throw new GradleScriptException("Need to configure developers on project." + EXTENSION_NAME, null);
    }
    return ImmutableList.copyOf(data.developers);
  }

}
