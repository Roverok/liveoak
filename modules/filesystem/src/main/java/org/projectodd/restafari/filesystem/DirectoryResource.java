package org.projectodd.restafari.filesystem;

import org.projectodd.restafari.spi.Pagination;
import org.projectodd.restafari.spi.resource.Resource;
import org.projectodd.restafari.spi.resource.async.CollectionResource;
import org.projectodd.restafari.spi.resource.async.ResourceSink;
import org.projectodd.restafari.spi.resource.async.Responder;
import org.projectodd.restafari.spi.state.ResourceState;
import org.vertx.java.core.Vertx;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bob McWhirter
 */
public class DirectoryResource implements FSResource, CollectionResource {

    public DirectoryResource(FSResource parent, File file) {
        this.parent = parent;
        this.file = file;
    }

    public Vertx vertx() {
        return this.parent.vertx();
    }

    public File file() {
        return this.file;
    }

    @Override
    public void create(ResourceState state, Responder responder) {
        responder.createNotSupported(this);
    }

    @Override
    public void readContent(Pagination pagination, ResourceSink sink) {
        vertx().fileSystem().readDir(this.file.getPath(), (result) -> {
            if (result.failed()) {
                sink.close();
            } else {
                List<File> sorted = new ArrayList<>();

                for (String filename : result.result()) {
                    File child = new File(filename);
                    sorted.add( child );
                }

                sorted.sort((left, right) -> {
                    if ( left.isDirectory() && right.isDirectory()) {
                        return 0;
                    }

                    if ( left.isFile() && right.isFile() ) {
                        return 0;
                    }

                    if ( left.isDirectory() ) {
                        return -1;
                    }

                    if ( left.isFile() ) {
                        return 1;
                    }

                    return 0;

                });

                for ( File each : sorted ) {
                    if ( each.isDirectory() ) {
                        sink.accept( new DirectoryResource( this, each ));
                    } else {
                        sink.accept( new FileResource( this, each ));
                    }
                }
                sink.close();
            }
        });
    }

    @Override
    public Resource parent() {
        return this.parent;
    }

    @Override
    public String id() {
        return this.file.getName();
    }

    @Override
    public void read(String id, Responder responder) {
        File path = new File(this.file, id);
        System.err.println( "look for: " + path + " from " + this.file );
        vertx().fileSystem().exists(path.getPath(), (existResult) -> {
            System.err.println( "exists result: " + existResult );
            if (existResult.succeeded() && existResult.result()) {
                if (path.isDirectory()) {
                    System.err.println( "found dir: " + path );
                    responder.resourceRead(new DirectoryResource(this, path));
                } else {
                    System.err.println( "found file: " + path );
                    responder.resourceRead(new FileResource(this, path));
                }
            } else {
                System.err.println( "no such!" );
                responder.noSuchResource(id);
            }
        });
    }

    @Override
    public void delete(Responder responder) {
        responder.deleteNotSupported(this);
    }

    public String toString() {
        return "[DirectoryResource: file=" + this.file.getAbsolutePath() + "]";
    }

    private FSResource parent;
    protected File file;
}