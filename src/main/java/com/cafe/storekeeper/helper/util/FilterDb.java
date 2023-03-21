package com.cafe.storekeeper.helper.util;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.SkipOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import com.cafe.storekeeper.helper.constant.ConstantsFields;
import com.cafe.storekeeper.helper.constant.ConstantsRest;

public final class FilterDb {

    private FilterDb() {
    }

    public static Aggregation getAggregationFilter(Map<String, String> params) {
        Criteria filterAgg = FilterDb.getCriteriaFilter(params);

        SortOperation operationSortField = FilterDb.getSortOperationField(params);
        LimitOperation operationLimit = FilterDb.getLimitOperation(params);

        if (operationLimit == null) {
            return newAggregation(
                    Aggregation.match(filterAgg),
                    operationSortField);
        } else {
            return newAggregation(
                    Aggregation.match(filterAgg),
                    operationSortField,
                    FilterDb.getSkipOperation(params),
                    operationLimit);
        }
    }

    public static LimitOperation getLimitOperation(Map<String, String> params) {
        LimitOperation limitOperation = null;
        if (params != null && !params.isEmpty()) {
            String count = params.get(ConstantsRest.COUNT);
            boolean flagCount = StringUtils.isNotBlank(count) && Util.isNumeric(count);

            if (flagCount) {
                long pageSize = Long.parseLong(count);
                if (pageSize > 0) {
                    limitOperation = Aggregation.limit(pageSize);
                }
            }
        }
        return limitOperation;
    }

    public static SkipOperation getSkipOperation(Map<String, String> params) {
        SkipOperation skipOperation;
        long numberPage = 0L;
        if (params != null && !params.isEmpty()) {
            String skip = params.get(ConstantsRest.SKIP);
            boolean flagSkip = StringUtils.isNotBlank(skip) && Util.isNumeric(skip);

            if (flagSkip) {
                numberPage = Long.parseLong(skip);
                if (numberPage < 0)
                    numberPage = 0;
            }
        }
        skipOperation = Aggregation.skip(numberPage);
        return skipOperation;
    }

    public static SortOperation getSortOperationField(Map<String, String> params) {
        Sort.Direction typeSortDirectionDefault = Sort.Direction.DESC;
        String fieldSortDefault = ConstantsFields.CREATE_DATE;

        if (params != null && !params.isEmpty()) {
            String shortField = params.get(ConstantsRest.SORT);
            boolean flagShort = StringUtils.isNotBlank(shortField);
            if (flagShort) {
                String typeSort = params.getOrDefault(ConstantsRest.TYPE_SORT, "ASC");
                fieldSortDefault = shortField;
                if (typeSort.equals("ASC")) {
                    typeSortDirectionDefault = Sort.Direction.ASC;
                }
            }
        }

        return Aggregation.sort(Sort.by(typeSortDirectionDefault, fieldSortDefault)
                .and(Sort.by(Sort.Direction.ASC, ConstantsFields.ID)));
    }

    public static Criteria getCriteriaFilter(Map<String, String> params) {
        List<Criteria> criteriaWhere = FilterDb.getCriteriaWhere(params);
        List<Criteria> criteriaWhereNot = FilterDb.getCriteriaWhereNot(params);

        List<Criteria> criteriaBetweenDate = FilterDb.getCriteriaBetweenDate(params, ConstantsFields.CREATE_DATE);
        Criteria criteriaLike = FilterDb.getCriteriaLike(params);

        Stream<Criteria> criteriaAnd = Stream.concat(
                criteriaWhere.stream(),
                criteriaBetweenDate.stream());

        List<Criteria> listCriteriaAnd = Stream.concat(
                criteriaAnd,
                criteriaWhereNot.stream())
                .collect(Collectors.toList());

        if (criteriaLike != null) {
            listCriteriaAnd = Stream.concat(
                    listCriteriaAnd.stream(),
                    Stream.of(criteriaLike))
                    .collect(Collectors.toList());
        }

        Criteria criteria = new Criteria();
        if (!listCriteriaAnd.isEmpty()) {
            criteria.andOperator(listCriteriaAnd);
        }

        return criteria;
    }

    public static Criteria getCriteriaLike(Map<String, String> params) {
        List<Criteria> listCriteria = new ArrayList<>();

        if (params == null || params.isEmpty())
            return null;

        String likes = params.get(ConstantsRest.LIKE);
        if (StringUtils.isEmpty(likes))
            return null;

        Criteria criteria = new Criteria();
        for (String item : likes.split(",")) {
            List<String> valuesAndField = Arrays.asList(item.split(":"));
            String nameField = valuesAndField.get(0);

            if (valuesAndField.size() >= 2) {
                List<String> values = valuesAndField.subList(1, valuesAndField.size());
                for (String value : values) {
                    String regex = "(?i).*" + value + ".*";
                    listCriteria.add(Criteria.where(nameField).regex(regex));
                }
            }
        }
        if (!listCriteria.isEmpty()) {
            criteria.orOperator(listCriteria);
        }
        return criteria;
    }

    public static List<Criteria> getCriteriaBetweenDate(Map<String, String> params, String nameField) {
        List<Criteria> listCriteria = new ArrayList<>();

        if (FilterDb.existsFilterDateUser(params)) {
            String from = params.get(ConstantsRest.FROM);
            String to = params.get(ConstantsRest.TO);

            LocalDateTime toDate = Instant
                    .ofEpochMilli(Long.parseLong(to))
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            toDate = LocalDateTime.of(toDate.toLocalDate(), LocalTime.MAX);

            LocalDateTime fromDate = Instant
                    .ofEpochMilli(Long.parseLong(from))
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            fromDate = LocalDateTime.of(fromDate.toLocalDate(), LocalTime.MIN);

            listCriteria.add(Criteria.where(nameField).gte(fromDate));
            listCriteria.add(Criteria.where(nameField).lte(toDate));
        }
        return listCriteria;
    }

    public static List<Criteria> getCriteriaWhere(Map<String, String> params) {
        List<Criteria> listCriteria = new ArrayList<>();

        if (params == null || params.isEmpty())
            return listCriteria;

        String where = params.get(ConstantsRest.WHERE);
        if (StringUtils.isEmpty(where))
            return listCriteria;

        for (String item : where.split(",")) {
            List<String> valuesAndField = Arrays.asList(item.split(":"));
            String nameField = valuesAndField.get(0);
            if (valuesAndField.size() >= 2) {
                List<String> values = valuesAndField.subList(1, valuesAndField.size());
                listCriteria.add(Criteria.where(nameField).in(values));
            }
        }
        Criteria criteria = new Criteria();
        criteria.andOperator(listCriteria);
        return List.of(criteria);
    }

    public static List<Criteria> getCriteriaWhereNot(Map<String, String> params) {
        List<Criteria> listCriteria = new ArrayList<>();

        if (params == null || params.isEmpty())
            return listCriteria;

        String whereNot = params.get(ConstantsRest.WHERE_NOT);
        if (StringUtils.isEmpty(whereNot))
            return listCriteria;

        for (String item : whereNot.split(",")) {
            List<String> valuesAndField = Arrays.asList(item.split(":"));
            String nameField = valuesAndField.get(0);
            if (valuesAndField.size() >= 2) {
                List<String> values = valuesAndField.subList(1, valuesAndField.size());
                listCriteria.add(Criteria.where(nameField).not().in(values));
            }
        }
        Criteria criteria = new Criteria();
        criteria.andOperator(listCriteria);
        return List.of(criteria);
    }

    public static boolean existsFilterDateUser(Map<String, String> params) {
        if (params != null && !params.isEmpty()) {
            String from = params.get(ConstantsRest.FROM);
            String to = params.get(ConstantsRest.TO);
            if (!StringUtils.isEmpty(from) && !StringUtils.isEmpty(to)) {
                return from.matches("\\d*") && to.matches("\\d*");
            }
        }
        return false;
    }

}
