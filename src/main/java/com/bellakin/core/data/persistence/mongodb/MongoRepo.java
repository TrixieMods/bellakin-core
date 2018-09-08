package com.bellakin.core.data.persistence.mongodb;

import com.bellakin.core.data.model.Identifiable;
import com.bellakin.core.data.persistence.AbstractRepo;
import com.mongodb.bulk.BulkWriteResult;
import javafx.util.Pair;
import org.bson.types.ObjectId;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.SampleOperation;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.util.CloseableIterator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

public class MongoRepo<T extends Identifiable> extends AbstractRepo<T> implements MongoRepository<T, String> {

  private final MongoTemplate template;
  private final String collection;

  public MongoRepo(Class<T> type, MongoDbFactory factory, String collection) {
    this(type, new MongoTemplate(factory), collection);
  }

  public MongoRepo(Class<T> type, MongoTemplate template, String collection) {
    super(type);
    this.template = template;
    this.collection = collection;
  }

  public void delete(Iterable<? extends T> iterable) {
    this.deleteAll(iterable);
  }

  /**
   * It needs to be closed!
   *
   * @return
   */
  public CloseableIterator<T> iterator() {
    return iterator(new Query());
  }

  /**
   * It needs to be closed!
   *
   * @param query
   * @return
   */
  public CloseableIterator<T> iterator(Query query) {
    return stream(query);
  }

  public CloseableIterator<T> stream(Query query) {
    return template.stream(query, getType(), collection);
  }

  @Override
  public void deleteAll() {
    template.remove(new Query(), getType(), collection);
  }

  public BulkWriteResult deleteAll(List<ObjectId> list) {
    BulkOperations bulkOps =
      template.bulkOps(BulkOperations.BulkMode.UNORDERED, collection);
    List<Query> listOfQueries =
      list.stream().map(id -> query(where("_id").is(id))).collect(Collectors.toList());
    bulkOps.remove(listOfQueries);
    return bulkOps.execute();
  }

  @Override
  public void delete(T entity) {
    template.remove(entity, collection);
  }

  @Override
  public void deleteAll(Iterable<? extends T> iterable) {
    for (T entity : iterable) {
      template.remove(entity, collection);
    }
  }

  public void delete(Query query) {
    template.remove(query, getType(), collection);
  }

  public T findOne() {
    return template.findOne(new Query(), getType(), collection);
  }

  public T findOne(String id) {
    return this.findById(id).orElse(null);
  }

  @Override
  public <S extends T> S save(S entity) {
    template.save(entity, collection);
    return entity;
  }

  @Override
  public <S extends T> List<S> saveAll(Iterable<S> entities) {
    List<S> saved = new ArrayList<>();
    entities.forEach(s -> saved.add(save(s)));
    return saved;
  }

  @Override
  public Optional<T> findById(String objectId) {
    T found = (!ObjectId.isValid(objectId)) ? null :
      template.findOne(query(where("_id").is(objectId)), getType(), collection);
    return Optional.ofNullable(found);
  }

  @Override
  public boolean existsById(String objectId) {
    return (ObjectId.isValid(objectId)) && template.exists(query(where("_id").is(objectId)), getType(), collection);
  }

  @Override
  public List<T> findAll() {
    return find(new Query());
  }

  @Override
  public Iterable<T> findAllById(Iterable<String> iterable) {
    throw new UnsupportedOperationException("Method not implemented");
  }

  public List<T> find(Query query) {
    return template.find(query, getType(), collection);
  }

  @Override
  public long count() {
    return count(new Query());
  }

  @Override
  public void deleteById(String objectId) {
    if (ObjectId.isValid(objectId)) {
      template.remove(query(where("_id").is(objectId)), getType(), collection);
    }
  }

  public long count(Query query) {
    return template.count(query, getType(), collection);
  }

  @Override
  public List<T> findAll(Sort sort) {
    return template.find(new Query().with(sort), getType(), collection);
  }

  @Override
  public Page<T> findAll(Pageable pageable) {
    Long count = count();
    List<T> list = find(new Query().with(pageable));

    return new PageImpl<>(list, pageable, count);
  }

  @Override
  public <S extends T> S insert(S entity) {
    template.insert(entity, collection);
    return entity;
  }

  @Override
  public <S extends T> List<S> insert(Iterable<S> entities) {

    List<S> list = new ArrayList<S>();
    for (S entity : entities) {
      list.add(insert(entity));
    }

    return list;
  }

  public <S extends T> boolean exists(String id) {
    return existsById(id);
  }

  public <S> AggregationResults<S> aggregate(Aggregation aggregation, Class<S> outputType) {
    return template.aggregate(aggregation, collection, outputType);
  }

  public T findOne(Query query) {
    return template.findOne(query, getType(), collection);
  }

  public void update(ObjectId id, Update update) {
    Query query = query(where("_id").is(id));
    updateMulti(query, update, false);
  }

  public void update(Query query, Update update) {
    updateMulti(query, update, false);
  }

  public BulkWriteResult updateMulti(Query query, Update update, boolean multi) {
    // TODO - look for a way to update this method to allow acknowledgement
    if (multi) {
      return template.bulkOps(BulkOperations.BulkMode.UNORDERED, collection)
        .updateMulti(query, update).execute();
    }
    return template.bulkOps(BulkOperations.BulkMode.UNORDERED, collection)
      .updateOne(query, update).execute();

  }

  public List<T> sample(int sampleSize) {
    return template.aggregate(
      Aggregation.newAggregation(Collections.singletonList(new SampleOperation(sampleSize))),
      collection, getType()).getMappedResults();
  }

  public BulkWriteResult writeInBulk(List<T> list) {
    BulkOperations bulkOperations =
      template.bulkOps(BulkOperations.BulkMode.UNORDERED, collection);
    for (T object : list) {
      bulkOperations.insert(object);
    }
    return bulkOperations.execute();
  }

  public BulkWriteResult updateInBulk(List<Pair<ObjectId, Update>> list) {
    BulkOperations bulkOperations =
      template.bulkOps(BulkOperations.BulkMode.UNORDERED, collection);
    for (Pair<ObjectId, Update> item : list) {
      bulkOperations.updateOne(query(where("_id").is(item.getKey())), item.getValue());
    }
    return bulkOperations.execute();
  }

  @Override
  public <S extends T> Optional<S> findOne(Example<S> example) {
    throw new UnsupportedOperationException("Operation not implemented.");
  }

  @Override
  public <S extends T> List<S> findAll(Example<S> example) {
    throw new UnsupportedOperationException("Operation not implemented.");
  }

  @Override
  public <S extends T> List<S> findAll(Example<S> example, Sort sort) {
    throw new UnsupportedOperationException("Operation not implemented.");
  }

  @Override
  public <S extends T> Page<S> findAll(Example<S> example, Pageable pageable) {
    throw new UnsupportedOperationException("Operation not implemented.");
  }

  @Override
  public <S extends T> long count(Example<S> example) {
    throw new UnsupportedOperationException("Operation not implemented.");
  }

  @Override
  public <S extends T> boolean exists(Example<S> example) {
    throw new UnsupportedOperationException("Operation not implemented.");
  }
}
