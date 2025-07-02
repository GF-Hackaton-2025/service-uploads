package br.com.uploads.app.repositories;

import br.com.uploads.app.entities.File;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface FilesRepository extends ReactiveMongoRepository<File, ObjectId> {

  Mono<File> findByFileId(String fileId);

}
