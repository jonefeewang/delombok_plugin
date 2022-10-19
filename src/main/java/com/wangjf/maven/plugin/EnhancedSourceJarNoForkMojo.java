package com.wangjf.maven.plugin;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.source.SourceJarNoForkMojo;
import org.apache.maven.project.MavenProject;


/**
 * This goal bundles all the sources into a jar archive, but uses delomboked sources.
 */
@Mojo(name = "jar-no-fork", defaultPhase = LifecyclePhase.VERIFY, threadSafe = true)
public class EnhancedSourceJarNoForkMojo extends SourceJarNoForkMojo {

    @Parameter(property = "<some-prefix>.useDelombokSources", defaultValue = "true")
    protected boolean useDelombokSources;

    @Parameter(property = "<some-prefix>.delombokSourcesLocation", defaultValue = "delombok")
    protected String delombokSourcesLocation;

    @Override
    protected List<String> getSources(MavenProject p) {
        // if user doesn't want delomboked sources, use default algorithm
        List<String> sources = super.getSources(p);
        if (!useDelombokSources) {
            return sources;
        }

        // typically, sources' list will contain: [src/main/java, target/generated_sources].
        // replace src/main/java if it's present with delombok-generated sources
        String target = p.getBuild().getDirectory();
        return super.getSources(p)
                .stream()
                .map(s -> s.endsWith("java") ? String.format("%s/%s", target, delombokSourcesLocation) : s)
                .collect(Collectors.toList());
    }

}