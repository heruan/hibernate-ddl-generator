package to.lova;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.envers.Audited;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.SchemaExport.Action;
import org.hibernate.tool.schema.TargetType;

@Mojo(name = "generate-ddl", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class HibernateDDLGeneratorMojo extends AbstractMojo {

    @Entity
    @Audited
    public static class AuditedEntity {

        @Id
        @GeneratedValue
        private Long id;

    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder();
        Map<Object, Object> properties = new HashMap<>();
        properties.put(AvailableSettings.HBM2DDL_AUTO, "create");
        properties.put(AvailableSettings.DIALECT, H2Dialect.class.getName());
        registryBuilder.applySettings(properties);
        final StandardServiceRegistry registry = registryBuilder.build();
        final MetadataSources metadataSources = new MetadataSources(registry);
        metadataSources.addAnnotatedClass(AuditedEntity.class);
        final Metadata metadata = metadataSources.buildMetadata();
        final SchemaExport schema = new SchemaExport();
        schema.setDelimiter(";");
        schema.setOutputFile("target/ddl/h2/create.sql");
        schema.execute(EnumSet.of(TargetType.SCRIPT), Action.CREATE, metadata);
    }

}
