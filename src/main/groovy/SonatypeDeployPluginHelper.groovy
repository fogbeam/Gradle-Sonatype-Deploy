package org.fogbeam.gradle.sonatype

import org.gradle.api.*;
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.maven.MavenDeployer
import org.gradle.api.artifacts.maven.MavenPom;
import org.gradle.api.logging.Logging;
import static org.apache.commons.lang.StringUtils.getLevenshteinDistance;

class SonatypeDeployPluginHelper {

  static MavenDeployer createMavenDeployer(Project p) {
    def x = null
    p.uploadArchives {
      repositories {
        x = mavenDeployer {

        }
      }
    }
    return x
  }

  static Closure oneArgNopClosure() { return {-> } }

  static SonatypeLicense lookupLicense(String name) {
    String prop = SonatypeLicense.metaClass.properties.findAll {
      it.type == SonaTypeLicense
    }.inject(null) { currentProperty,bestGuess ->
      String currentName = currentProperty.name;
      if(currentName.contains(name)) {
        if(name.equalsIgnoreCase(bestGuess)) {
          return bestGuess
        } else if(name.equalsIgnoreCase(currentName)) {
          return currentName
        } else {
          int guessLev = getLevenshteinDistance(
            bestGuess.toLowerCase(), name.toLowerCase()
          )
          int currLev = getLevenshteinDistance(
            currentName.toLowerCase(), name.toLowerCase()
          )
          if(guessLev == currLev) {
            if(guessLev > currLev) return guessLev
            return currLev
          } else if(guessLev < currLev) {
            return bestGuess
          } else {
            return currentName
          }
        }
      }
    }
    if(!prop.equalsIgnoreCase(name)) {
      Logging.getLogger(SonatypeDeployPluginHelper).warn(
        "Could not find exact match for liscense ($name): guessing $prop"
      )
    }
    Logging.getLogger(SonatypeDeployPluginHelper).info("Looked up liscense $prop")
    return SonatypeLicense."$prop"
  }

  static <T> T configurate(T target, Closure c) {
    target.with(c)
    return target;
  }

  static void configureSigning(Project target) {
    target.signing {
      sign target.configurations.archives
    }
  }

  static void configureRepository(Project project, MavenDeployer mvn) {
    mvn.repository(url:"https://oss.sonatype.org/service/local/staging/deploy/maven2/") {
      authentication(userName: project.sonatypeUsername, password: project.sonatypePassword)
    }
  }

  static void configurePomToSetPackagingString(MavenPom pom) {
    pom.withXml { XmlProvider xmlProvider ->
      def xml = xmlProvider.asString()
      def pomXml = new XmlParser().parse(new ByteArrayInputStream(xml.toString().bytes))

      pomXml.version[0] + { packaging('jar') }

      def newXml = new StringWriter()
      def printer = new XmlNodePrinter(new PrintWriter(newXml))
      printer.preserveWhitespace = true
      printer.print(pomXml)
      xml.setLength(0)
      xml.append(newXml.toString())
    }
  }

}
