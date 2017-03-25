package com.soebes.maven.extensions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.maven.eventspy.AbstractEventSpy;
import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.execution.ExecutionEvent.Type;
import org.eclipse.aether.RepositoryEvent;
import org.eclipse.aether.RepositoryEvent.EventType;
import org.eclipse.aether.artifact.Artifact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Karl Heinz Marbaise <a href="mailto:khmarbaise@apache.org">khmarbaise@apache.org</a>
 */
@Named
@Singleton
public class DeploymentRecorderExtension
    extends AbstractEventSpy
{
    private static final String DPRE = "deployment-recorder-extension";
    
    private final Logger LOGGER = LoggerFactory.getLogger( getClass() );

    private File rootDirectory;

    private List<Artifact> recordedDeploys;

    public DeploymentRecorderExtension()
    {
        LOGGER.debug( "{} ctor called.", DPRE );
        this.recordedDeploys = new ArrayList<>();
    }

    @Override
    public void init( Context context )
        throws Exception
    {
        super.init( context );
        LOGGER.info( "{} Version {} started.", DPRE, DeploymentRecorderExtensionVersion.getVersion() );

        // Is this always in the context? Based on Maven Core yes.
        String workingDirectory = (String) context.getData().get( "workingDirectory" );
        LOGGER.debug( "{}: workingDirectory: {}", DPRE, workingDirectory );

        Properties systemProperties = (Properties) context.getData().get( "systemProperties" );
        // This is only available from 3.3.+
        String multiModuleProjectDirectory = systemProperties.getProperty( "maven.multiModuleProjectDirectory" );
        rootDirectory = new File( multiModuleProjectDirectory );
        LOGGER.debug( "{}: multiModuleProjectDirectory: {}", DPRE, multiModuleProjectDirectory );
    }

    @Override
    public void onEvent( Object event )
        throws Exception
    {
        try
        {
            if ( event instanceof ExecutionEvent )
            {
                executionEventHandler( (ExecutionEvent) event );
            }
            else if ( event instanceof org.eclipse.aether.RepositoryEvent )
            {
                repositoryEventHandler( (RepositoryEvent) event );
            }
            else
            {
                // TODO: What kind of event we haven't considered?
                LOGGER.debug( "{}: Event {}", DPRE, event.getClass().getCanonicalName() );
            }
        }
        catch ( Exception e )
        {
            LOGGER.error( DPRE + ": Exception", e );
        }
    }

    @Override
    public void close()
    {
        LOGGER.debug( "{}: done.", DPRE );
    }

    private String getId( Artifact artifact )
    {
        StringBuilder sb = new StringBuilder( artifact.getGroupId() );
        sb.append( ':' );
        sb.append( artifact.getArtifactId() );
        sb.append( ':' );
        sb.append( artifact.getExtension() );
        sb.append( ':' );
        sb.append( artifact.getVersion() );
        sb.append( ':' );
        sb.append( artifact.getBaseVersion() );
        // classifier can be != null but empty!
        if ( artifact.getClassifier() != null && artifact.getClassifier().trim().length() > 0)
        {
            sb.append( ':' );
            sb.append( artifact.getClassifier() );
        }
        return sb.toString();
    }

    private void repositoryEventHandler( org.eclipse.aether.RepositoryEvent repositoryEvent )
    {
        EventType type = repositoryEvent.getType();
        switch ( type )
        {
            case ARTIFACT_DEPLOYED:
                recordedDeploys.add( repositoryEvent.getArtifact() );
                break;

            case ARTIFACT_DOWNLOADING:
            case ARTIFACT_DOWNLOADED:
            case ARTIFACT_DEPLOYING:
            case ARTIFACT_INSTALLING:
            case ARTIFACT_INSTALLED:
            case METADATA_DEPLOYING:
            case METADATA_DEPLOYED:
            case METADATA_DOWNLOADING:
            case METADATA_DOWNLOADED:
            case METADATA_INSTALLING:
            case METADATA_INSTALLED:
            case ARTIFACT_RESOLVING:
            case ARTIFACT_RESOLVED:
            case ARTIFACT_DESCRIPTOR_INVALID:
            case ARTIFACT_DESCRIPTOR_MISSING:
            case METADATA_RESOLVED:
            case METADATA_RESOLVING:
            case METADATA_INVALID:
                // Those events are not recorded.
                break;

            default:
                LOGGER.error( "{}: repositoryEventHandler {}", DPRE, type );
                break;
        }
    }

    private void writeDeploymentRecorderFile()
    {
        //TODO: Make it configurable?
        File target = new File( this.rootDirectory, "target" );

        if ( !target.exists() )
        {
            target.mkdirs();
        }
        //TODO: Make this name configurable?
        File recorderFile = new File( target, "deploy-recorder.lst" );

        try ( FileWriter fw = new FileWriter( recorderFile ); BufferedWriter bos = new BufferedWriter( fw ) )
        {
            for ( Artifact artifact : recordedDeploys )
            {
                bos.write( getId( artifact ) );
                bos.newLine();
            }
        }
        catch ( IOException e )
        {
            LOGGER.error( "IOException", e );
        }
    }

    private void executionEventHandler( ExecutionEvent executionEvent )
    {
        Type type = executionEvent.getType();
        switch ( type )
        {
            case SessionEnded:
                writeDeploymentRecorderFile();
                break;

            case ProjectDiscoveryStarted:
            case SessionStarted:
            case ForkStarted:
            case ForkFailed:
            case ForkSucceeded:
            case ForkedProjectStarted:
            case ForkedProjectFailed:
            case ForkedProjectSucceeded:
            case MojoStarted:
            case MojoFailed:
            case MojoSucceeded:
            case MojoSkipped:
            case ProjectStarted:
            case ProjectFailed:
            case ProjectSucceeded:
            case ProjectSkipped:
                break;

            default:
                LOGGER.error( "{}: executionEventHandler: {}", DPRE, type );
                break;
        }

    }

}
