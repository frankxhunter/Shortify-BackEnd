package com.frank.shortiy.shortify.repositories;

import com.frank.shortiy.shortify.models.InfoRequest;
import com.frank.shortiy.shortify.models.Url;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfoRequestRepository extends CrudRepository<InfoRequest, Long> {
    Iterable<InfoRequest> findByUrl(Url url);
}
