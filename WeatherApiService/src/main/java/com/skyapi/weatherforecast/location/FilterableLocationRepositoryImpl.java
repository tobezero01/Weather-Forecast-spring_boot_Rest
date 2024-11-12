package com.skyapi.weatherforecast.location;

import com.skyapi.weatherforecast.common.Location;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Repository
public class FilterableLocationRepositoryImpl implements FilterableLocationRepository {

    @Autowired
    EntityManager entityManager;

    @Override
    public Page<Location> listWithFilter(Pageable pageable, Map<String, Object> filterFields) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Location> entityQuery = builder.createQuery(Location.class);

        Root<Location> entityRoot = entityQuery.from(Location.class);

        Predicate[] predicates = createPredicates(filterFields, builder, entityRoot);
        if (predicates.length > 0) {
            entityQuery.where(predicates);
        }

        List<Order> listOrder = new ArrayList<>();
        pageable.getSort().stream().forEach(order -> {
            if (order.getDirection() == Sort.Direction.ASC) {
                listOrder.add(builder.asc(entityRoot.get(order.getProperty())));
            }
            else {
                listOrder.add(builder.desc(entityRoot.get(order.getProperty())));
            }
        });
        entityQuery.orderBy(listOrder);

        TypedQuery<Location> typedQuery = entityManager.createQuery(entityQuery);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<Location> listResult = typedQuery.getResultList();
        long totalRows = getTotalRows(filterFields);

        return new PageImpl<>(listResult, pageable, totalRows);
    }

    private Predicate[] createPredicates(Map<String, Object> filterFields, CriteriaBuilder builder, Root<Location> root) {
        List<Predicate> predicatesList = new ArrayList<>();

        if (filterFields != null && !filterFields.isEmpty()) {
            filterFields.forEach((fieldName, filterValue) -> {
                predicatesList.add(builder.equal(root.get(fieldName), filterValue));
            });
        }

        predicatesList.add(builder.equal(root.get("trashed"), false));

        return predicatesList.toArray(new Predicate[0]);
    }

    private long getTotalRows(Map<String, Object> filterFields) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);

        Root<Location> countRoot = countQuery.from(Location.class);
        countQuery.select(builder.count(countRoot));

        Predicate[] predicates = createPredicates(filterFields, builder, countRoot);
        if (predicates.length > 0) {
            countQuery.where(predicates);
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }
}


