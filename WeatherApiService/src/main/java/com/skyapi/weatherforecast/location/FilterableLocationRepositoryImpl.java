package com.skyapi.weatherforecast.location;

import com.skyapi.weatherforecast.common.Location;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Repository
public class FilterableLocationRepositoryImpl implements FilterableLocationRepository{
    @Autowired
    EntityManager entityManager;

    @Override
    public Page<Location> listWithFilter(Pageable pageable, Map<String, Object> filterFields) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Location> entityQuery = builder.createQuery(Location.class);

        Root<Location> entityRoot = entityQuery.from(Location.class);

        Predicate[] predicates = createPredicates(filterFields, builder, entityRoot);
        if (predicates.length > 0) entityQuery.where(predicates);

        List<Order> listOrder = new ArrayList<>();
        pageable.getSort().stream().forEach(order -> {
            listOrder.add(builder.asc(entityRoot.get(order.getProperty())));
        });
        entityQuery.orderBy(listOrder);

        TypedQuery<Location> typedQuery = entityManager.createQuery(entityQuery);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<Location> listResult = new ArrayList<>();
        long totalRows = getTotalRows(filterFields);

        return new PageImpl<>(listResult, pageable, totalRows);
    }

    private Predicate[] createPredicates(Map<String, Object> filterFields, CriteriaBuilder builder, Root<Location> root) {
        Predicate[] predicates = new Predicate[filterFields.size() + 1];

        if (!filterFields.isEmpty()) {
            Iterator<String> iterator = filterFields.keySet().iterator();

            int i = 0;
            while (iterator.hasNext()) {
                String fieldName = iterator.next();
                Object filterValue = filterFields.get(fieldName);

                predicates[i++] = builder.equal(root.get(fieldName), filterValue);
            }
        }

        predicates[predicates.length - 1] = builder.equal(root.get("trashed"), false);

        return predicates;
    }

    private long getTotalRows(Map<String, Object> filterFields) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);

        Root<Location> countRoot = countQuery.from(Location.class);
        countQuery.select(builder.count(countRoot));

        Predicate[] predicates = createPredicates(filterFields, builder, countRoot);
        if (predicates.length > 0) countQuery.where(predicates);

        Long rowCount = entityManager.createQuery(countQuery).getSingleResult();

        return rowCount;
    }
}
