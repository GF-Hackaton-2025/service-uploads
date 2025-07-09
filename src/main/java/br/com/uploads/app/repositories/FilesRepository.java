package br.com.uploads.app.repositories;

import br.com.uploads.app.entities.File;
import br.com.uploads.utils.Pagination;
import br.com.uploads.utils.SpringContext;
import br.com.uploads.webui.domain.CustomPage;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.regex.Pattern;

import static br.com.uploads.utils.Strings.isNonEmpty;

@Repository
public interface FilesRepository extends ReactiveMongoRepository<File, ObjectId> {

  Mono<File> findByFileId(String fileId);

  default Mono<CustomPage<File>> findAllUserByPagination(String email, Integer page, Integer limit, String sort) {
    var pageReq = Pagination.getPageRequest(limit, page, sort, "uploadDate");

    var criteria = new Criteria();
    if (isNonEmpty(email)) {
      String regex = ".*" + Pattern.quote(email) + ".*";
      criteria = Criteria.where("email").regex(regex, "i");
    }

    var mongoTemplate = SpringContext.getBean(ReactiveMongoTemplate.class);
    var query = new Query(criteria).with(pageReq);

    Mono<List<File>> files = mongoTemplate.find(query, File.class).collectList();

    return files.map(fileList -> {
      boolean hasNext = fileList.size() >= limit;
      return new CustomPage<File>(fileList, page, fileList.size(), hasNext);
    });
  }
}
