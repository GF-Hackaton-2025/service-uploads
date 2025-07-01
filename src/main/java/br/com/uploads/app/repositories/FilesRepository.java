package br.com.uploads.app.repositories;

import br.com.uploads.app.entities.File;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilesRepository extends ReactiveMongoRepository<File, ObjectId> {
}
